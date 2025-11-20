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
        serverSocket = new ServerSocket(port);
        isRunning = true;
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public void start() {
        new Thread(() -> {
            try {
                System.out.println("서버 시작, 클라이언트 대기 중...");
                clientSocket = serverSocket.accept();
                System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());

                // ObjectOutputStream을 먼저 생성하고 flush (헤더 전송)
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();

                // 클라이언트가 OutputStream을 생성할 시간 확보
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                // ObjectInputStream 생성
                in = new ObjectInputStream(clientSocket.getInputStream());

                if (messageHandler != null) {
                    messageHandler.onClientConnected();
                }

                startListening();
            } catch (IOException e) {
                if (messageHandler != null) {
                    messageHandler.onError(e);
                }
            }
        }).start();
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            while (isRunning && clientSocket != null && !clientSocket.isClosed()) {
                try {
                    Object msg = in.readObject();
                    if (msg instanceof NetworkMessage) {
                        NetworkMessage netMsg = (NetworkMessage) msg;
                        if (netMsg.getType() == NetworkMessage.MessageType.PING) {
                            sendMessage(new NetworkMessage(NetworkMessage.MessageType.PONG, netMsg.getData()));
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
                    System.out.println("클라이언트 연결 종료");
                    if (messageHandler != null) {
                        messageHandler.onClientDisconnected();
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
