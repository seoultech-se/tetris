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
            int consecutiveErrors = 0;
            final int MAX_CONSECUTIVE_ERRORS = 5;

            while (isRunning && socket != null && !socket.isClosed()) {
                try {
                    Object msg = in.readObject();
                    consecutiveErrors = 0; // 성공적으로 읽으면 에러 카운트 리셋

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
                } catch (IOException e) {
                    consecutiveErrors++;
                    System.err.println("[CLIENT] IO Error reading message (" + consecutiveErrors + "/" + MAX_CONSECUTIVE_ERRORS + "): " + e.getMessage());

                    if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                        System.err.println("[CLIENT] Too many consecutive errors, stopping listener");
                        if (isRunning && messageHandler != null) {
                            messageHandler.onError(e);
                        }
                        break;
                    }
                    // readObject()는 블로킹이므로 sleep 없이 다음 시도
                    System.out.println("[CLIENT] Retrying read...");
                } catch (ClassNotFoundException e) {
                    System.err.println("[CLIENT] Class not found error: " + e.getMessage());
                    // ClassNotFoundException은 심각한 에러이므로 중단
                    if (isRunning && messageHandler != null) {
                        messageHandler.onError(e);
                    }
                    break;
                }
            }
            System.out.println("[CLIENT] Listener thread stopped");
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

    public synchronized void sendMessage(Object message) throws IOException {
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
        System.out.println("[CLIENT] Closing client...");
        isRunning = false;
        if (pingThread != null && pingThread.isAlive()) {
            pingThread.interrupt();
            System.out.println("[CLIENT] Ping thread interrupted");
        }
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
            System.out.println("[CLIENT] Listener thread interrupted");
        }
        try {
            if (in != null) {
                in.close();
                System.out.println("[CLIENT] Input stream closed");
            }
            if (out != null) {
                out.close();
                System.out.println("[CLIENT] Output stream closed");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[CLIENT] Socket closed");
            }
        } catch (IOException e) {
            System.err.println("[CLIENT] Error during cleanup: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[CLIENT] Client closed successfully");
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
