/**
 * Copyright 2015 StreamSets Inc.
 *
 * Licensed under the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.stage.destination.sdcipc;

import com.google.common.annotations.VisibleForTesting;
import com.streamsets.pipeline.api.ConfigDef;
import com.streamsets.pipeline.api.Stage;
import com.streamsets.pipeline.api.impl.Utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configs {
  private static final String CONFIG_PREFIX = "config.";
  private static final String HOST_PORTS = CONFIG_PREFIX + "hostPorts";
  private static final String TRUST_STORE_FILE = CONFIG_PREFIX + "trustStoreFile";

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.LIST,
      defaultValue = "[]",
      label = "SDC RPC Connection",
      description = "Connection information for the destination pipeline. Use the format <host>:<port>.",
      displayPosition = 10,
      group = "RPC"
  )
  public List<String> hostPorts;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.STRING,
      label = "SDC RPC ID",
      description = "User-defined ID. Must match the SDC RPC ID used in the SDC RPC origin of the destination pipeline.",
      displayPosition = 20,
      group = "RPC"
  )
  public String appId;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.BOOLEAN,
      defaultValue = "false",
      label = "TLS Enabled",
      description = "Encrypt RPC communication using TLS.",
      displayPosition = 30,
      group = "RPC"
  )
  public boolean sslEnabled;

  @ConfigDef(
      required = false,
      type = ConfigDef.Type.STRING,
      defaultValue = "",
      label = "Truststore File",
      description = "The truststore file is expected in the Data Collector resources directory. Leave empty if none.",
      displayPosition = 40,
      group = "RPC",
      dependsOn = "sslEnabled",
      triggeredByValue = "true"
  )
  public String trustStoreFile;

  @ConfigDef(
      required = false,
      type = ConfigDef.Type.STRING,
      defaultValue = "",
      label = "Truststore Password",
      displayPosition = 50,
      group = "RPC",
      dependsOn = "sslEnabled",
      triggeredByValue = "true"
  )
  public String trustStorePassword;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.BOOLEAN,
      defaultValue = "true",
      label = "Verify Host In Server Certificate",
      description = "Disables server certificate hostname verification",
      displayPosition = 60,
      group = "RPC",
      dependsOn = "sslEnabled",
      triggeredByValue = "true"
  )
  public boolean hostVerification;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      defaultValue = "3",
      label = "Retries per Batch",
      displayPosition = 10,
      group = "ADVANCED",
      min = 0,
      max = 10
  )
  public int retriesPerBatch;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      defaultValue = "0",
      label = "Back off period",
      description = "If set to non-zero, each retry will be spaced exponentially. For value 10, first retry will be" +
        " done after 10 milliseconds, second retry after additional 100 milliseconds, third retry after additional second, ...",
      displayPosition = 15,
      group = "ADVANCED",
      min=0
  )
  public int backOff;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      defaultValue = "5000",
      label = "Connection Timeout (ms)",
      displayPosition = 20,
      group = "ADVANCED",
      min = 100
  )
  public int connectionTimeOutMs;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.NUMBER,
      defaultValue = "2000",
      label = "Read Timeout (ms)",
      displayPosition = 30,
      group = "ADVANCED",
      min = 100
  )
  public int readTimeOutMs;

  @ConfigDef(
      required = true,
      type = ConfigDef.Type.BOOLEAN,
      defaultValue = "true",
      label = "Use Compression",
      displayPosition = 40,
      group = "ADVANCED"
  )
  public boolean compression;

  private SSLSocketFactory sslSocketFactory;

  public List<Stage.ConfigIssue> init(Stage.Context context) {
    List<Stage.ConfigIssue> issues = new ArrayList<>();

    boolean ok = validateHostPorts(context, issues);
    ok |= validateSecurity(context, issues);
    if (ok) {
      if (sslEnabled) {
        try {
          sslSocketFactory = createSSLSocketFactory(context);
        } catch (Exception ex) {
          issues.add(context.createConfigIssue(Groups.RPC.name(), TRUST_STORE_FILE,
                                               Errors.IPC_DEST_10, ex.toString()));
          ok = false;
        }
      }
      if (ok && !context.isPreview()) {
        validateConnectivity(context, issues);
      }
    }
    return issues;
  }

  boolean validateHostPorts(Stage.Context context, List<Stage.ConfigIssue> issues) {
    boolean ok = true;
    if (hostPorts.isEmpty()) {
      issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS, Errors.IPC_DEST_00));
      ok = false;
    } else {
      Set<String> uniqueHostPorts = new HashSet<>();
      for (String hostPort : hostPorts) {
        if (hostPort == null) {
          issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS, Errors.IPC_DEST_01));
          ok = false;
        } else {
          hostPort = hostPort.toLowerCase().trim();
          uniqueHostPorts.add(hostPort);
          String[] split = hostPort.split(":");
          if (split.length != 2) {
            issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS, Errors.IPC_DEST_02,
                                                 hostPort));
            ok = false;
          } else {
            try {
              InetAddress.getByName(split[0]);
            } catch (Exception ex) {
              issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS, Errors.IPC_DEST_03,
                                                   split[0], ex.toString()));
              ok = false;
            }
            try {
              int port = Integer.parseInt(split[1]);
              if (port < 1 || port > 65535) {
                issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS, Errors.IPC_DEST_04,
                                                     hostPort));
                ok = false;
              }
            } catch (Exception ex) {
              issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS, Errors.IPC_DEST_05,
                                                   hostPort, ex.toString()));
              ok = false;
            }
          }
        }
      }
      if (ok && uniqueHostPorts.size() != hostPorts.size()) {
        issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS, Errors.IPC_DEST_06));
        ok = false;
      }
    }
    return ok;
  }

  boolean validateSecurity(Stage.Context context, List<Stage.ConfigIssue> issues) {
    boolean ok = true;
    if (sslEnabled) {
      if (!trustStoreFile.isEmpty()) {
        File file = getTrustStoreFile(context);
        if (!file.exists()) {
          issues.add(context.createConfigIssue(Groups.RPC.name(), TRUST_STORE_FILE,
                                               Errors.IPC_DEST_07));
          ok = false;
        } else {
          if (!file.isFile()) {
            issues.add(context.createConfigIssue(Groups.RPC.name(), TRUST_STORE_FILE,
                                                 Errors.IPC_DEST_08));
            ok = false;
          } else {
            if (!file.canRead()) {
              issues.add(context.createConfigIssue(Groups.RPC.name(), TRUST_STORE_FILE,
                                                   Errors.IPC_DEST_09));
              ok = false;
            } else {
              try {
                KeyStore keystore = KeyStore.getInstance("jks");
                try (InputStream is = new FileInputStream(getTrustStoreFile(context))) {
                  keystore.load(is, trustStorePassword.toCharArray());
                }
              } catch (Exception ex) {
                issues.add(context.createConfigIssue(Groups.RPC.name(), TRUST_STORE_FILE,
                                                     Errors.IPC_DEST_10, ex.toString()));
              }
            }
          }
        }
      }
    }
    return ok;
  }

  File getTrustStoreFile(Stage.Context context) {
    return new File(context.getResourcesDirectory(), trustStoreFile);
  }

  SSLSocketFactory createSSLSocketFactory(Stage.Context context) throws Exception {
    SSLSocketFactory sslSocketFactory;
    if (trustStoreFile.isEmpty()) {
      sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    } else {
      KeyStore keystore = KeyStore.getInstance("jks");
      try (InputStream is = new FileInputStream(getTrustStoreFile(context))) {
        keystore.load(is, trustStorePassword.toCharArray());
      }

      KeyManagerFactory keyMgrFactory = KeyManagerFactory.getInstance(Constants.SSL_CERTIFICATE);
      keyMgrFactory.init(keystore, trustStorePassword.toCharArray());
      KeyManager[] keyManagers = keyMgrFactory.getKeyManagers();

      TrustManager[] trustManagers = new TrustManager[1];
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(Constants.SSL_CERTIFICATE);
      trustManagerFactory.init(keystore);
      for (TrustManager trustManager1 : trustManagerFactory.getTrustManagers()) {
        if (trustManager1 instanceof X509TrustManager) {
          trustManagers[0] = trustManager1;
          break;
        }
      }
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(keyManagers, trustManagers, null);
      sslContext.getDefaultSSLParameters().setProtocols(Constants.SSL_ENABLED_PROTOCOLS);
      sslSocketFactory = sslContext.getSocketFactory();
    }
    return sslSocketFactory;
  }

  HttpURLConnection createConnection(URL url) throws IOException {
    return (HttpURLConnection) url.openConnection();
  }

  static final HostnameVerifier ACCEPT_ALL_HOSTNAME_VERIFIER = new HostnameVerifier() {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
      return true;
    }
  };

  @VisibleForTesting
  public HttpURLConnection createConnection(String hostPort) throws IOException {
    return createConnection(hostPort, Constants.IPC_PATH);
  }

    @VisibleForTesting
  public HttpURLConnection createConnection(String hostPort, String path) throws IOException {
    String scheme = (sslEnabled) ? "https://" : "http://";
    URL url = new URL(scheme + hostPort.trim()  + path);
    HttpURLConnection conn = createConnection(url);
    conn.setConnectTimeout(connectionTimeOutMs);
    conn.setReadTimeout(readTimeOutMs);
    if (sslEnabled) {
      HttpsURLConnection sslConn = (HttpsURLConnection) conn;
      sslConn.setSSLSocketFactory(sslSocketFactory);
      if (!hostVerification) {
        sslConn.setHostnameVerifier(ACCEPT_ALL_HOSTNAME_VERIFIER);
      }
    }
    conn.setRequestProperty(Constants.X_SDC_APPLICATION_ID_HEADER, appId);
    return conn;
  }

  void validateConnectivity(Stage.Context context, List<Stage.ConfigIssue> issues) {
    boolean ok = false;
    List<String> errors = new ArrayList<>();
    for (String hostPort : hostPorts) {
      try {
        HttpURLConnection conn = createConnection(hostPort);
        conn.setRequestMethod("GET");
        conn.setDefaultUseCaches(false);
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
          if (Constants.X_SDC_PING_VALUE.equals(conn.getHeaderField(Constants.X_SDC_PING_HEADER))) {
            ok = true;
          } else {
            issues.add(context.createConfigIssue(Groups.RPC.name(), HOST_PORTS,
                                                 Errors.IPC_DEST_12, hostPort ));
          }
        } else {
          errors.add(Utils.format("'{}': {}", hostPort, conn.getResponseMessage()));
        }
      } catch (Exception ex) {
        errors.add(Utils.format("'{}': {}", hostPort, ex.toString()));
      }
    }
    if (!ok) {
      issues.add(context.createConfigIssue(null, null, Errors.IPC_DEST_15, errors));
    }
  }

}
