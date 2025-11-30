package tetris.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GameClientTest {
    
    private GameClient client;
    private ServerSocket mockServer;
    private static int testPortCounter = 18880;
    private int currentTestPort;
    
    @BeforeEach
    void setUp() {
        client = new GameClient();
        // Each test uses a different port
        currentTestPort = testPortCounter++;
    }
    
    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (mockServer != null && !mockServer.isClosed()) {
            try {
                mockServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Wait for port to be fully released
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    @Timeout(5)
    void testClientCreation() {
        assertNotNull(client);
    }
    
    @Test
    @Timeout(5)
    void testIsConnected_NotConnected() {
        assertFalse(client.isConnected());
    }
    
    @Test
    @Timeout(10)
    void testConnect() throws Exception {
        CountDownLatch connectedLatch = new CountDownLatch(1);
        AtomicBoolean connected = new AtomicBoolean(false);
        
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onConnected() {
                connected.set(true);
                connectedLatch.countDown();
            }
            
            @Override
            public void onDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        // Start mock server
        mockServer = new ServerSocket(currentTestPort);
        new Thread(() -> {
            try {
                Socket serverSocket = mockServer.accept();
                ObjectInputStream serverIn = new ObjectInputStream(serverSocket.getInputStream());
                Thread.sleep(100);
                ObjectOutputStream serverOut = new ObjectOutputStream(serverSocket.getOutputStream());
                serverOut.flush();
                
                // Keep connection alive
                while (!serverSocket.isClosed()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }).start();
        
        client.connect("localhost", currentTestPort);
        
        assertTrue(connectedLatch.await(3, TimeUnit.SECONDS));
        assertTrue(connected.get());
        assertTrue(client.isConnected());
    }
    
    @Test
    @Timeout(10)
    void testSendMessage() throws Exception {
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<Object> receivedMessage = new AtomicReference<>();
        
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onConnected() {}
            
            @Override
            public void onDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        // Start mock server
        mockServer = new ServerSocket(currentTestPort);
        new Thread(() -> {
            try {
                Socket serverSocket = mockServer.accept();
                ObjectInputStream serverIn = new ObjectInputStream(serverSocket.getInputStream());
                Thread.sleep(100);
                ObjectOutputStream serverOut = new ObjectOutputStream(serverSocket.getOutputStream());
                serverOut.flush();
                
                // Read message from client
                Object msg = serverIn.readObject();
                receivedMessage.set(msg);
                messageLatch.countDown();
            } catch (Exception e) {
                // Ignore
            }
        }).start();
        
        client.connect("localhost", currentTestPort);
        Thread.sleep(200);
        
        // Send message from client
        NetworkMessage testMessage = new NetworkMessage(NetworkMessage.MessageType.GAME_START, "test");
        client.sendMessage(testMessage);
        
        assertTrue(messageLatch.await(3, TimeUnit.SECONDS));
        assertNotNull(receivedMessage.get());
    }
    
    @Test
    @Timeout(10)
    void testReceiveMessage() throws Exception {
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<Object> receivedMessage = new AtomicReference<>();
        
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                receivedMessage.set(message);
                messageLatch.countDown();
            }
            
            @Override
            public void onConnected() {}
            
            @Override
            public void onDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        // Start mock server
        mockServer = new ServerSocket(currentTestPort);
        new Thread(() -> {
            try {
                Socket serverSocket = mockServer.accept();
                ObjectInputStream serverIn = new ObjectInputStream(serverSocket.getInputStream());
                Thread.sleep(100);
                ObjectOutputStream serverOut = new ObjectOutputStream(serverSocket.getOutputStream());
                serverOut.flush();
                
                // Send message to client
                NetworkMessage testMessage = new NetworkMessage(NetworkMessage.MessageType.GAME_STATE, "server message");
                serverOut.writeObject(testMessage);
                serverOut.flush();
            } catch (Exception e) {
                // Ignore
            }
        }).start();
        
        client.connect("localhost", currentTestPort);
        
        assertTrue(messageLatch.await(3, TimeUnit.SECONDS));
        assertNotNull(receivedMessage.get());
    }
    
    @Test
    @Timeout(10)
    void testPingPong() throws Exception {
        CountDownLatch rttLatch = new CountDownLatch(1);
        AtomicLong receivedRtt = new AtomicLong(-1);
        
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onConnected() {}
            
            @Override
            public void onDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {
                receivedRtt.set(rtt);
                rttLatch.countDown();
            }
        });
        
        // Start mock server
        mockServer = new ServerSocket(currentTestPort);
        new Thread(() -> {
            try {
                Socket serverSocket = mockServer.accept();
                ObjectInputStream serverIn = new ObjectInputStream(serverSocket.getInputStream());
                Thread.sleep(100);
                ObjectOutputStream serverOut = new ObjectOutputStream(serverSocket.getOutputStream());
                serverOut.flush();
                
                // Wait for PING and send PONG
                Object msg = serverIn.readObject();
                if (msg instanceof NetworkMessage) {
                    NetworkMessage netMsg = (NetworkMessage) msg;
                    if (netMsg.getType() == NetworkMessage.MessageType.PING) {
                        NetworkMessage pong = new NetworkMessage(NetworkMessage.MessageType.PONG, netMsg.getData());
                        serverOut.writeObject(pong);
                        serverOut.flush();
                    }
                }
            } catch (Exception e) {
                // Ignore
            }
        }).start();
        
        client.connect("localhost", currentTestPort);
        
        assertTrue(rttLatch.await(3, TimeUnit.SECONDS));
        assertTrue(receivedRtt.get() >= 0);
    }
    
    @Test
    @Timeout(10)
    void testDisconnection() throws Exception {
        CountDownLatch disconnectedLatch = new CountDownLatch(1);
        AtomicBoolean disconnected = new AtomicBoolean(false);
        
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onConnected() {}
            
            @Override
            public void onDisconnected() {
                disconnected.set(true);
                disconnectedLatch.countDown();
            }
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        // Start mock server
        mockServer = new ServerSocket(currentTestPort);
        Socket[] serverSocketHolder = new Socket[1];
        new Thread(() -> {
            try {
                Socket serverSocket = mockServer.accept();
                serverSocketHolder[0] = serverSocket;
                ObjectInputStream serverIn = new ObjectInputStream(serverSocket.getInputStream());
                Thread.sleep(100);
                ObjectOutputStream serverOut = new ObjectOutputStream(serverSocket.getOutputStream());
                serverOut.flush();
                
                // Keep connection alive
                Thread.sleep(500);
            } catch (Exception e) {
                // Ignore
            }
        }).start();
        
        client.connect("localhost", currentTestPort);
        Thread.sleep(300);
        
        // Close server connection
        if (serverSocketHolder[0] != null) {
            serverSocketHolder[0].close();
        }
        
        assertTrue(disconnectedLatch.await(3, TimeUnit.SECONDS));
        assertTrue(disconnected.get());
    }
    
    @Test
    @Timeout(5)
    void testClose() throws IOException {
        client.close();
        assertFalse(client.isConnected());
    }
    
    @Test
    @Timeout(5)
    void testSetMessageHandler() {
        GameClient.MessageHandler handler = new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onConnected() {}
            
            @Override
            public void onDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        };
        
        client.setMessageHandler(handler);
        assertNotNull(client);
    }
}
