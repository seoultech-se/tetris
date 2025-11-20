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
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void onMessageReceived(Object message);
        void onClientConnected();
        void onClientDisconnected();
        void onError(Exception e);
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
            while (isRunning && clientSocket != null && !clientSocket.isClosed()) {
                try {
                    Object msg = in.readObject();
                    if (msg instanceof NetworkMessage) {
                        NetworkMessage netMsg = (NetworkMessage) msg;
                        if (netMsg.getType() == NetworkMessage.MessageType.PING) {
                            sendMessage(new NetworkMessage(NetworkMessage.MessageType.PONG, netMsg.getData()));
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
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("[SERVER] Error reading message: " + e.getMessage());
                    if (isRunning && messageHandler != null) {
                        messageHandler.onError(e);
                    }
                    break;
                }
            }
        });
        listenerThread.start();
        System.out.println("[SERVER] Listener thread started");
    }

    public void sendMessage(Object message) throws IOException {
        if (out != null) {
            if (message instanceof NetworkMessage) {
                NetworkMessage netMsg = (NetworkMessage) message;
                if (netMsg.getType() != NetworkMessage.MessageType.PONG) {
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
        isRunning = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClientConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }
}
