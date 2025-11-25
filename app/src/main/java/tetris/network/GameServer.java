package tetris.network;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isRunning = false;
    private Thread listenerThread;
    private Thread pingThread;
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void onMessageReceived(Object message);
        void onClientConnected();
        void onClientDisconnected();
        void onError(Exception e);
        void onRttUpdate(long rtt);
    }

    public GameServer(int port) throws IOException {
        System.out.println("[SERVER] Creating server on port " + port);
        serverSocket = new ServerSocket(port);
        isRunning = true;
        System.out.println("[SERVER] Server socket created successfully");
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public void start() {
        new Thread(() -> {
            try {
                System.out.println("[SERVER] Server started, waiting for client connection...");
                clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Client connected from: " + clientSocket.getInetAddress());

                // ObjectOutputStream을 먼저 생성하고 flush (헤더 전송)
                System.out.println("[SERVER] Creating ObjectOutputStream...");
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                System.out.println("[SERVER] ObjectOutputStream created and flushed");

                // 클라이언트가 OutputStream을 생성할 시간 확보
                try {
                    System.out.println("[SERVER] Waiting 100ms for client to create stream...");
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                // ObjectInputStream 생성
                System.out.println("[SERVER] Creating ObjectInputStream...");
                in = new ObjectInputStream(clientSocket.getInputStream());
                System.out.println("[SERVER] ObjectInputStream created successfully");

                if (messageHandler != null) {
                    messageHandler.onClientConnected();
                }

                startListening();
                startPinging();
            } catch (IOException e) {
                System.err.println("[SERVER] Error during connection: " + e.getMessage());
                e.printStackTrace();
                if (messageHandler != null) {
                    messageHandler.onError(e);
                }
            }
        }).start();
    }

    private void startListening() {
        System.out.println("[SERVER] Starting message listener thread...");
        listenerThread = new Thread(() -> {
            int consecutiveErrors = 0;
            final int MAX_CONSECUTIVE_ERRORS = 5;

            while (isRunning && clientSocket != null && !clientSocket.isClosed()) {
                try {
                    Object msg = in.readObject();
                    consecutiveErrors = 0; // 성공적으로 읽으면 에러 카운트 리셋

                    if (msg instanceof NetworkMessage) {
                        NetworkMessage netMsg = (NetworkMessage) msg;
                        if (netMsg.getType() == NetworkMessage.MessageType.PING) {
                            System.out.println("[SERVER] PING received, sending PONG");
                            sendMessage(new NetworkMessage(NetworkMessage.MessageType.PONG, netMsg.getData()));
                        } else if (netMsg.getType() == NetworkMessage.MessageType.PONG) {
                            long rtt = System.currentTimeMillis() - (long) netMsg.getData();
                            System.out.println("[SERVER] PONG received, RTT: " + rtt + "ms");
                            if (messageHandler != null) {
                                messageHandler.onRttUpdate(rtt);
                            } else {
                                System.err.println("[SERVER] WARNING: messageHandler is null, cannot update RTT");
                            }
                        } else {
                            System.out.println("[SERVER] Received message: " + netMsg.getType());
                            if (messageHandler != null) {
                                messageHandler.onMessageReceived(msg);
                            }
                        }
                    } else {
                        System.out.println("[SERVER] Received non-NetworkMessage object");
                        if (messageHandler != null) {
                            messageHandler.onMessageReceived(msg);
                        }
                    }
                } catch (EOFException | SocketException e) {
                    System.out.println("[SERVER] Client connection closed");
                    if (messageHandler != null) {
                        messageHandler.onClientDisconnected();
                    }
                    break;
                } catch (IOException e) {
                    consecutiveErrors++;
                    System.err.println("[SERVER] IO Error reading message (" + consecutiveErrors + "/" + MAX_CONSECUTIVE_ERRORS + "): " + e.getMessage());

                    if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                        System.err.println("[SERVER] Too many consecutive errors, stopping listener");
                        if (isRunning && messageHandler != null) {
                            messageHandler.onError(e);
                        }
                        break;
                    }
                    // readObject()는 블로킹이므로 sleep 없이 다음 시도
                    System.out.println("[SERVER] Retrying read...");
                } catch (ClassNotFoundException e) {
                    System.err.println("[SERVER] Class not found error: " + e.getMessage());
                    // ClassNotFoundException은 심각한 에러이므로 중단
                    if (isRunning && messageHandler != null) {
                        messageHandler.onError(e);
                    }
                    break;
                }
            }
            System.out.println("[SERVER] Listener thread stopped");
        });
        listenerThread.start();
        System.out.println("[SERVER] Listener thread started");
    }

    private void startPinging() {
        System.out.println("[SERVER] Starting ping thread...");
        pingThread = new Thread(() -> {
            while (isRunning) {
                try {
                    long timestamp = System.currentTimeMillis();
                    System.out.println("[SERVER] Sending PING at timestamp: " + timestamp);
                    sendMessage(new NetworkMessage(NetworkMessage.MessageType.PING, timestamp));
                    Thread.sleep(1000); // 1초마다 PING 전송
                } catch (IOException e) {
                    System.err.println("[SERVER] Ping failed: " + e.getMessage());
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("[SERVER] Ping thread stopped");
        });
        pingThread.start();
        System.out.println("[SERVER] Ping thread started");
    }

    public synchronized void sendMessage(Object message) throws IOException {
        if (out != null) {
            if (message instanceof NetworkMessage) {
                NetworkMessage netMsg = (NetworkMessage) message;
                if (netMsg.getType() != NetworkMessage.MessageType.PONG && netMsg.getType() != NetworkMessage.MessageType.PING) {
                    System.out.println("[SERVER] Sending message: " + netMsg.getType());
                }
            }
            out.writeObject(message);
            out.flush();
        }
    }

    public String getServerIP() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            return InetAddress.getLocalHost().getHostAddress(); // Fallback
        } catch (UnknownHostException e) {
            return "Unknown"; // Final fallback
        }
    }

    public void close() {
        System.out.println("[SERVER] Closing server...");
        isRunning = false;
        if (pingThread != null && pingThread.isAlive()) {
            pingThread.interrupt();
            System.out.println("[SERVER] Ping thread interrupted");
        }
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
            System.out.println("[SERVER] Listener thread interrupted");
        }
        try {
            if (in != null) {
                in.close();
                System.out.println("[SERVER] Input stream closed");
            }
            if (out != null) {
                out.close();
                System.out.println("[SERVER] Output stream closed");
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("[SERVER] Client socket closed");
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[SERVER] Server socket closed");
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Error during cleanup: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[SERVER] Server closed successfully");
    }

    public boolean isClientConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }
}
