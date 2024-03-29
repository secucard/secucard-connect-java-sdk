/*
 * Copyright (c) 2015. hp.weber GmbH & Co secucard KG (www.secucard.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.secucard.connect.net.stomp.client;

import com.secucard.connect.client.ClientError;
import com.secucard.connect.client.NetworkError;
import com.secucard.connect.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Minimal stomp messaging support.
 */
public class StompClient {
  private SSLSocket socket;
  private Thread receiver;
  private final Listener eventListener;
  private BufferedReader reader;
  private volatile boolean stopReceiver;
  private volatile boolean shutdown;
  private volatile boolean initial;
  private volatile boolean connected;
  private volatile Frame error;
  private final String id;
  private final Config config;
  private final Set<String> receipts = new HashSet<>();
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

  public static final int DEFAULT_SOCKET_TIMEOUT_S = 30;
  public static final int DEFAULT_CONNECT_TIMEOUT_S = 30;
  public static final int DEFAULT_MESSAGE_TIMEOUT_S = 30;
  public static final int DEFAULT_HEARTBEAT_S = 30;
  public static final String CONNECT = "CONNECT";
  public static final String DISCONNECT = "DISCONNECT";
  public static final String DISCONNECTED = "DISCONNECTED"; // not a real stomp frame just for internal usage
  public static final String CONNECTED = "CONNECTED";
  public static final String RECEIPT = "RECEIPT";
  public static final String MESSAGE = "MESSAGE";
  public static final String ERROR = "ERROR";
  public static final String SEND = "SEND";

  private static final List<String> SERVER_FRAMES = Arrays.asList(CONNECTED, RECEIPT, MESSAGE, ERROR);

  private final static Log LOG = new Log(StompClient.class);

  public StompClient(String id, Config config, Listener eventListener) {
    this.eventListener = eventListener;
    this.id = id;
    this.config = config;

    LOG.info("STOMP client created, ", config);
  }

  /**
   * Connect to STOMP server.
   * Blocks until success or failure when used without callback.
   * In all failure cases all resources are properly closed, no need to call disconnect().
   *
   * @param user     User name.
   * @param password User password.
   * @throws StompError                               if the server responds with an ERROR frame, the frame details are set.
   * @throws com.secucard.connect.client.NetworkError if the network failed.
   * @throws com.secucard.connect.client.ClientError  if an general error happened.
   */
  public synchronized void connect(String user, String password) {
    if (connected) {
      return;
    }

    try {
      initConnection();
      sendConnect(user, password);
    } catch (Throwable e) {
      closeConnection(true);
      if (e instanceof IOException) {
        throw new NetworkError(e);
      }
      throw new ClientError(e);
    }

    awaitConnect();
  }

  /**
   * Disconnect connection to STOMP server.
   * The event listener {@link StompClient.Listener#onDisconnect()} gets called after disconnect.
   *
   * @throws NoReceiptException Only if {@link StompClient.Config#requestDISCONNECTReceipt} is true
   *                            (default: false) and no receipt could be received in time.
   * @throws StompError         If an error happens during disconnect attempt. Get details by
   *                            inspecting the properties.
   */
  public synchronized void disconnect() {
    doDisconnect();
  }

  private void doDisconnect() {
    if (!connected) {
      return;
    }
    shutdown = true;
    connected = false;

    if (initial) {
      // just initial state, no connect performed yet, just close all resources
      closeConnection(true);
      return;
    }

    // if connect was performed successfully before must send disconnect
    // closing of all further resources will be done by the receiver thread which gets an error on disconnect

    final String id = config.requestDISCONNECTReceipt ? createReceiptId("disconnect") : null;
    try {
      sendDisconnect(id);
    } catch (IOException | InterruptedException e) {
      // ignore an just return
      return;
    }


    if (config.requestDISCONNECTReceipt) {
      awaitReceipt(id, false, null);  // no disconnect because we already sent it
    }

    dispatchFrame(new Frame(DISCONNECTED));
  }


  /**
   * Send a message to the stomp server.
   * Blocks until a receipt is received or receipt timeout if no callback is provided.
   * The connection is automatically closed when no receipt could be received in time if
   * {@link Config#disconnectOnSENDReceiptTimeout} is true (default).
   *
   * @param destination The destination string.
   * @param body        The message body or null.
   * @param headers     The message headers or null.
   * @param timeoutSec  Timeout for awaiting receipt. Pass null to use the config value.
   * @throws StompError                               If an error happens during sending.
   *                                                  Get details by inspecting the properties.
   *                                                  All resources are closed properly, no need to call disconnect().
   * @throws NoReceiptException                       If no receipt is received after
   *                                                  {@link Config#receiptTimeoutSec}. Will NEVER be
   *                                                  thrown when a callback is provided.
   * @throws com.secucard.connect.client.NetworkError if the networks failed.
   */
  public synchronized void send(String destination, String body, Map<String, String> headers, Integer timeoutSec) {
    if (headers == null) {
      headers = new HashMap<>();
    }
    String id = createReceiptId(body);
    headers.put("destination", destination);
    headers.put("content-length", Integer.toString(body.getBytes(StandardCharsets.UTF_8).length));
    if (config.requestSENDReceipt) {
      headers.put("receipt", id);
    }

    try {
      sendFrame(SEND, headers, body);
    } catch (Throwable t) {
      closeConnection(true);
      if (t instanceof IOException) {
        throw new NetworkError(t);
      }
      throw new ClientError(t);
    }

    try {
      awaitReceipt(id, config.disconnectOnSENDReceiptTimeout, 15);
    } catch (NoReceiptException e) {
      try {
        sendFrame(SEND, headers, body);
      } catch (Throwable t) {
        closeConnection(true);
        if (t instanceof IOException) {
          throw new NetworkError(t);
        }
        throw new ClientError(t);
      }

      awaitReceipt(id, config.disconnectOnSENDReceiptTimeout, (timeoutSec - 15));
    }
  }

  public boolean isConnected() {
    return connected;
  }

  private void onConnected() {
    connected = true;
    initial = false;
  }

  private void onDisconnected() {
    connected = false;
    initial = false;
    eventListener.onDisconnect();
  }

  private void onReceipt(Frame frame) {
    String receiptId = frame.getHeaders() == null ? null : frame.getHeaders().get("receipt-id");
    if (receiptId != null) {
      synchronized (receipts) {
        receipts.add(receiptId);
      }
    }
  }

  private void onMessage(Frame frame) {
    eventListener.onMessage(frame);
  }

  private void onError(Frame frame) {
    // always treat as response to a message
    // just set as current error, must be handled when waiting for connection or receipt
    error = frame;
  }


  private void awaitConnect() {
    final long maxWaitTime = System.currentTimeMillis() + config.connectionTimeoutSec * 1000L;
    while (System.currentTimeMillis() <= maxWaitTime) {
      if (connected || error != null) {
        break;
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // will be stopped anyway
      }
    }

    if (connected) {
      return;
    }

    if (error != null) {
      closeConnection(true);
      String body = error.getBody();
      Map<String, String> headers = error.getHeaders();
      error = null;
      throw new StompError(body, headers);
    } else {
      closeConnection(true);
      throw new NetworkError("Timout waiting for connection.");
    }
  }

  private void awaitReceipt(final String receiptId, final boolean disconnect, Integer timeoutSec) {
    // check if receipt was received
    if (timeoutSec == null) {
      timeoutSec = config.receiptTimeoutSec;
    }
    boolean found = false;
    long maxWaitTime = System.currentTimeMillis() + timeoutSec * 1000;
    outer:
    while (System.currentTimeMillis() <= maxWaitTime && connected && error == null) {
      synchronized (receipts) {
        Iterator<String> it = receipts.iterator();
        while (it.hasNext()) {
          String next = it.next();
          if (next.equals(receiptId)) {
            it.remove();
            found = true;
            break outer;
          }
        }
      }

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // will be stopped anyway
      }
    }

    // we can treat error as reason to disconnect

    if (error != null || !found) {
      if (connected && disconnect) {
        try {
          disconnect();
        } catch (Throwable t) {
          // ignore all
          LOG.error("Error disconnecting due receipt timeout or error.", t);
        }
      }
      if (error != null) {
        String body = error.getBody();
        Map<String, String> headers = error.getHeaders();
        error = null;
        throw new StompError(body, headers);
      } else {
        throw new NoReceiptException("No receipt frame received for sent message.");
      }
    }
    // consider receipt as successful disconnect
  }

  /**
   * Utility method to build header map from given strings.
   *
   * @param args Key and value string pairs (first key, second value). Obviously the numbers of args must be even.
   * @return The header map.
   */
  public static Map<String, String> createHeader(String... args) {
    Map<String, String> headers = new HashMap<>(args.length / 2);
    for (int i = 0; i < args.length - 1; i += 2) {
      headers.put(args[i], args[i + 1]);
    }
    return headers;
  }


  private void sendDisconnect(String receiptId) throws IOException, InterruptedException {
    sendFrame(DISCONNECT, receiptId == null ? null : createHeader("receipt", receiptId), null);
  }

  private void sendConnect(String login, String password) throws IOException, InterruptedException {
    LOG.debug("sendConnect called");
    Map<String, String> header = new HashMap<>();

    String log = login == null ? config.login : login;
    if (log != null) {
      header.put("login", log);
    }
    String pwd = password == null ? config.password : password;
    if (pwd != null) {
      header.put("passcode", pwd);
    }
    if (config.virtualHost != null) {
      header.put("host", config.virtualHost);
    }
    if (config.heartbeatMs > 0) {
      header.put("heart-beat", config.heartbeatMs + "," + config.heartbeatMs);
    }
    header.put("accept-version", "1.2"); // STOMP protocol version
    sendFrame(CONNECT, header, null);
  }


  private void sendFrame(String command, Map<String, String> header, String body) throws IOException, InterruptedException {
    LOG.debug("Frame try to sent: command=", command);
    StringBuilder frame = new StringBuilder();
    frame.append(command).append("\n");

    if (header != null) {
      for (Map.Entry<String, String> entry : header.entrySet()) {
        frame.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
      }
    }
    frame.append("\n");

    if (body != null) {
      frame.append(body);
    }

    frame.append("\000");

    byte[] bytes = frame.toString().getBytes(StandardCharsets.UTF_8);

    try {
      write(bytes);
    } catch (IOException e) {
      // try again with a new connection
      initConnection();
      write(bytes);
    }

    LOG.debug("Frame sent: command=", command, ", header=", header, ", body=", body);
  }

  private void dispatchFrame(Frame frame) {
    try {
      switch (frame.getCommand()) {
        case CONNECTED:
          onConnected();
          break;
        case DISCONNECTED:
          onDisconnected();
          break;
        case RECEIPT:
          onReceipt(frame);
          break;
        case MESSAGE:
          onMessage(frame);
          break;
        case ERROR:
          onError(frame);
          break;
      }
    } catch (Exception e) {
      // ignore to not let exceptions produced in callbacks exit the receiver loop
      LOG.error("Client error happened.", e);
    }
  }

  private void write(byte[] bytes) throws IOException, InterruptedException {
    LOG.trace("write() -> lock.writeLock().tryLock()");
    if (!lock.writeLock().tryLock(30, TimeUnit.SECONDS))
    {
      throw new InterruptedException("Could not write to socket within 30 seconds (it's locked by another thread).");
    }

    try {
      OutputStream out = socket.getOutputStream();
      if (bytes == null) {
        LOG.debug("Writing NULL-Byte to socket...");
        out.write(0);
      } else {
        out.write(bytes);
      }
      out.flush();
    } finally {
      LOG.trace("write() -> lock.writeLock().unlock()");
      lock.writeLock().unlock();
    }
  }

  private void initConnection() throws IOException {
    shutdown = true;
    initial = false;
    receipts.clear();
    error = null;

    closeConnection(true);

    LOG.trace("initConnection() -> lock.writeLock().lock()");
    lock.writeLock().lock();
    try {
      socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(config.host, config.port);
      String[] supportedProtocols = {"TLSv1.2"};
      socket.setEnabledProtocols(supportedProtocols);
      socket.setSoTimeout(config.socketTimeoutSec * 1000);
      socket.setKeepAlive(true);

      if (!socket.isConnected()) {
        LOG.info("Socket is not connected yet, starting handshake...");
        socket.connect(new InetSocketAddress(config.host, config.port), config.connectionTimeoutSec);
        socket.startHandshake();
      }

      reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

      stopReceiver = false;
      shutdown = false;
      receiver = new Thread(this::receive);
      receiver.start();
      initial = true;
      LOG.debug("initConnection finished");
    }
    finally {
      LOG.trace("initConnection() -> lock.writeLock().unlock()");
      lock.writeLock().unlock();
    }
  }

  private void receive() {
    LOG.info("Receiver started.");

    while (!stopReceiver) {
      try {
        // socket is supposed to have timeout set!
        // that means reading will block until line is read or timeout,
        // this is to avoid endless blocking since it gives time to react on stopReceiver flag
        String line = reader.readLine();
        LOG.trace("line={", line, "}");
        if (line == null) {
          // indicates connection is dropped
          // write test to see if it's the case - if write throws IOException we must close
          write(null);
        } else {
          line = line.trim();
          if (SERVER_FRAMES.contains(line)) {
            Frame frame = new Frame(line, reader);
            LOG.debug("Frame received: ", frame);
            dispatchFrame(frame);
          }
        }
      } catch (SocketTimeoutException e) {
        // just regular configured socket timeout, ignore and go on
        LOG.trace("receive() got no message yet: " + e.getMessage());
      } catch (Exception e) {
        LOG.trace("Exception happened: ", e);
        // in most cases this would be an IOException, coming from dropped connection
        // very unlikely that the receiver loop would work after, so quit and close all
        closeConnection(false);
        if (!shutdown) {
          // exception is expected on shutdown (disconnect was sent), report only in other cases
          // just log, client can't do something meaningful usually
          LOG.debug("Error in receiver loop.", e);
          dispatchFrame(new Frame(DISCONNECTED));
        }
        break;
      }
    }
    LOG.info("Receiver stopped.");
  }

  /**
   * Closing all resources silently, no exception is raised because we can do nothing.
   *
   * @param stopReceiver True if receiver loop should also be stopped, false else.
   */
  private void closeConnection(boolean stopReceiver) {
    connected = false;
    if (stopReceiver && receiver != null && receiver.isAlive()) {
      LOG.debug("closeConnection: stopping receiver...");
      this.stopReceiver = true;
      try {
        receiver.join();
      } catch (InterruptedException e) {
        // ignore
      }
    }

    if (socket != null) {
      LOG.debug("closeConnection: closing socket...");
      try {
        socket.close();
      } catch (IOException e) {
        LOG.error("Error closing socket", e);
      }
    }
  }

  private String createReceiptId(String str) {
    return "rcpt-" + UUID.randomUUID().toString();
  }

  /**
   * Listener interface to get notified when stuff happened.
   * Note: Exceptions thrown by methods are swallowed (just logged) to not break this clients message receiver loop by
   * any exception caused by client code.
   */
  public static interface Listener {

    /**
     * Called when a message frame was received.
     */
    void onMessage(Frame frame);

    /**
     * Called when the connection state has changed to disconnect.
     * Just called when the connection is broken or so, not when disconnecting by intention.
     */
    void onDisconnect();
  }

  public static class Config {
    // send DISCONNECT when a SEND receipt times out
    private final boolean disconnectOnSENDReceiptTimeout = true;

    // request a receipt on SEND
    private final boolean requestSENDReceipt = true;

    // request a receipt on DISCONNECT
    private final boolean requestDISCONNECTReceipt = false;

    private final String host;
    private final int port;
    private final String virtualHost;
    private final String login;
    private final String password;
    private final int socketTimeoutSec;
    private final int receiptTimeoutSec;
    private final int connectionTimeoutSec;

    /*
    This is the client side heart beat interval the server can expect, server closes connection if not fulfilled.
    Note: the the stomp client itself will not deliver heartbeat, this must be done by user sending messages.
    Server side heart beat not supported.
    0 for no heart beat.
    */
    private final int heartbeatMs;

    /**
     * @deprecated The config attribute "useSsl" is not used any more
     */
    public Config(String host, int port, String virtualHost, String login, String password, int heartbeatMs,
                  boolean useSsl, int socketTimeoutSec, int receiptTimeoutSec, int connectionTimeoutSec) {
      this(host, port, virtualHost, login, password, heartbeatMs, socketTimeoutSec, receiptTimeoutSec, connectionTimeoutSec);
    }

    public Config(String host, int port, String virtualHost, String login, String password,
        int heartbeatMs, int socketTimeoutSec, int receiptTimeoutSec, int connectionTimeoutSec) {

      if (heartbeatMs < 1000 || heartbeatMs > 120000) {
        heartbeatMs = DEFAULT_HEARTBEAT_S * 1000;
      }

      if (socketTimeoutSec <= 1) {
        socketTimeoutSec = DEFAULT_SOCKET_TIMEOUT_S;
      }

      if (receiptTimeoutSec <= 1) {
        receiptTimeoutSec = DEFAULT_MESSAGE_TIMEOUT_S;
      }

      if (connectionTimeoutSec <= 1) {
        connectionTimeoutSec = DEFAULT_CONNECT_TIMEOUT_S;
      }

      this.host = host;
      this.port = port;
      this.virtualHost = virtualHost;
      this.login = login;
      this.password = password;
      this.heartbeatMs = heartbeatMs;
      this.socketTimeoutSec = socketTimeoutSec;
      this.receiptTimeoutSec = receiptTimeoutSec;
      this.connectionTimeoutSec = connectionTimeoutSec;
    }

    @Override
    public String toString() {
      return "Config{" +
          "disconnectOnSENDReceiptTimeout=" + disconnectOnSENDReceiptTimeout +
          ", requestSENDReceipt=" + requestSENDReceipt +
          ", requestDISCONNECTReceipt=" + requestDISCONNECTReceipt +
          ", host='" + host + '\'' +
          ", port=" + port +
          ", virtualHost='" + virtualHost + '\'' +
          ", socketTimeoutSec=" + socketTimeoutSec +
          ", receiptTimeoutSec=" + receiptTimeoutSec +
          ", connectionTimeoutSec=" + connectionTimeoutSec +
          ", heartbeatMs=" + heartbeatMs +
          '}';
    }
  }
}
