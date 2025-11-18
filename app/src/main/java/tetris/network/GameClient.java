package tetris.network;

import java.io.*;
import java.net.*;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isRunning = false;
    private Thread listenerThread;
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void onMessageReceived(Object message);
        void onConnected();
        void onDisconnected();
        void onError(Exception e);
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public void connect(String serverIP, int port) throws IOException {
        socket = new Socket(serverIP, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        isRunning = true;
        
        if (messageHandler != null) {
            messageHandler.onConnected();
        }
        
        startListening();
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            while (isRunning && socket != null && !socket.isClosed()) {
                try {
                    Object message = in.readObject();
                    if (messageHandler != null) {
                        messageHandler.onMessageReceived(message);
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

    public void sendMessage(Object message) throws IOException {
        if (out != null) {
            out.writeObject(message);
            out.flush();
        }
    }

    public void close() {
        isRunning = false;
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
