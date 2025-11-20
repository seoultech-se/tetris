package tetris.network;

import java.io.*;
import java.net.*;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isRunning = false;
    private Thread listenerThread;
    private Thread pingThread;
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void onMessageReceived(Object message);
        void onConnected();
        void onDisconnected();
        void onError(Exception e);
        void onRttUpdate(long rtt);
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public void connect(String serverIP, int port) throws IOException {
        System.out.println("[CLIENT] Attempting to connect to " + serverIP + ":" + port);
        socket = new Socket(serverIP, port);
        System.out.println("[CLIENT] Socket connected successfully");

        // ObjectOutputStream을 먼저 생성하고 flush (헤더 전송)
        System.out.println("[CLIENT] Creating ObjectOutputStream...");
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        System.out.println("[CLIENT] ObjectOutputStream created and flushed");

        // 서버가 ObjectOutputStream을 생성할 시간 확보
        try {
            System.out.println("[CLIENT] Waiting 100ms for server to create stream...");
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        // ObjectInputStream 생성
        System.out.println("[CLIENT] Creating ObjectInputStream...");
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("[CLIENT] ObjectInputStream created successfully");
        isRunning = true;

        if (messageHandler != null) {
            messageHandler.onConnected();
        }

        startListening();
        startPinging();
        System.out.println("[CLIENT] Connection established and ready");
    }

    private void startListening() {
        System.out.println("[CLIENT] Starting message listener thread...");
        listenerThread = new Thread(() -> {
            while (isRunning && socket != null && !socket.isClosed()) {
                try {
                    Object msg = in.readObject();
                    if (msg instanceof NetworkMessage) {
                        NetworkMessage netMsg = (NetworkMessage) msg;
                        if (netMsg.getType() == NetworkMessage.MessageType.PONG) {
                            long rtt = System.currentTimeMillis() - (long) netMsg.getData();
                            if (messageHandler != null) {
                                messageHandler.onRttUpdate(rtt);
                            }
                        } else {
                            System.out.println("[CLIENT] Received message: " + netMsg.getType());
                            if (messageHandler != null) {
                                messageHandler.onMessageReceived(msg);
                            }
                        }
                    } else {
                        System.out.println("[CLIENT] Received non-NetworkMessage object");
                        if (messageHandler != null) {
                            messageHandler.onMessageReceived(msg);
                        }
                    }
                } catch (EOFException | SocketException e) {
                    System.out.println("[CLIENT] Server connection closed");
                    if (messageHandler != null) {
                        messageHandler.onDisconnected();
                    }
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("[CLIENT] Error reading message: " + e.getMessage());
                    if (isRunning && messageHandler != null) {
                        messageHandler.onError(e);
                    }
                    break;
                }
            }
        });
        listenerThread.start();
        System.out.println("[CLIENT] Listener thread started");
    }

    private void startPinging() {
        System.out.println("[CLIENT] Starting ping thread...");
        pingThread = new Thread(() -> {
            while (isRunning) {
                try {
                    sendMessage(new NetworkMessage(NetworkMessage.MessageType.PING, System.currentTimeMillis()));
                    Thread.sleep(1000); // 1초마다 PING 전송
                } catch (IOException e) {
                    System.err.println("[CLIENT] Ping failed: " + e.getMessage());
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        pingThread.start();
        System.out.println("[CLIENT] Ping thread started");
    }

    public void sendMessage(Object message) throws IOException {
        if (out != null) {
            if (message instanceof NetworkMessage) {
                NetworkMessage netMsg = (NetworkMessage) message;
                if (netMsg.getType() != NetworkMessage.MessageType.PING) {
                    System.out.println("[CLIENT] Sending message: " + netMsg.getType());
                }
            }
            out.writeObject(message);
            out.flush();
        }
    }

    public void close() {
        isRunning = false;
        if (pingThread != null) {
            pingThread.interrupt();
        }
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
