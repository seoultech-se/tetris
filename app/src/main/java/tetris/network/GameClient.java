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
        socket = new Socket(serverIP, port);

        // ObjectOutputStream을 먼저 생성하고 flush (헤더 전송)
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

        // 서버가 ObjectOutputStream을 생성할 시간 확보
        try {
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        // ObjectInputStream 생성
        in = new ObjectInputStream(socket.getInputStream());
        isRunning = true;

        if (messageHandler != null) {
            messageHandler.onConnected();
        }

        startListening();
        startPinging();
    }

    private void startListening() {
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
                            if (messageHandler != null) {
                                messageHandler.onMessageReceived(msg);
                            }
                        }
                    } else {
                        if (messageHandler != null) {
                            messageHandler.onMessageReceived(msg);
                        }
                    }
                } catch (EOFException | SocketException e) {
                    System.out.println("서버 연결 종료");
                    if (messageHandler != null) {
                        messageHandler.onDisconnected();
                    }
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    if (isRunning && messageHandler != null) {
                        messageHandler.onError(e);
                    }
                    break;
                }
            }
        });
        listenerThread.start();
    }

    private void startPinging() {
        pingThread = new Thread(() -> {
            while (isRunning) {
                try {
                    sendMessage(new NetworkMessage(NetworkMessage.MessageType.PING, System.currentTimeMillis()));
                    Thread.sleep(1000); // 1초마다 PING 전송
                } catch (IOException e) {
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        pingThread.start();
    }

    public void sendMessage(Object message) throws IOException {
        if (out != null) {
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
