package tetris.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GameServerTest {
    
    private GameServer server;
    private static int testPortCounter = 19990;
    private int currentTestPort;
    
    @BeforeEach
    void setUp() throws IOException {
        // 각 테스트마다 다른 포트 사용
        currentTestPort = testPortCounter++;
        server = new GameServer(currentTestPort);
    }
    
    @AfterEach
    void tearDown() {
        if (server != null) {
            server.close();
        }
        // 포트가 완전히 닫힐 때까지 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @Test
    @Timeout(5)
    void testServerCreation() {
        assertNotNull(server);
    }
    
    @Test
    @Timeout(5)
    void testGetServerIP() {
        String ip = server.getServerIP();
        assertNotNull(ip);
        assertFalse(ip.isEmpty());
        assertNotEquals("Unknown", ip);
    }
    
    @Test
    @Timeout(5)
    void testIsClientConnected_NoClient() {
        assertFalse(server.isClientConnected());
    }
    
    @Test
    @Timeout(10)
    void testClientConnection() throws Exception {
        CountDownLatch connectedLatch = new CountDownLatch(1);
        AtomicBoolean connected = new AtomicBoolean(false);
        
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onClientConnected() {
                connected.set(true);
                connectedLatch.countDown();
            }
            
            @Override
            public void onClientDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        server.start();
        
        // Give server time to start
        Thread.sleep(200);
        
        // Connect as client
        try (Socket clientSocket = new Socket("localhost", currentTestPort)) {
            ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
            clientOut.flush();
            
            Thread.sleep(100);
            
            ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream());
            
            assertTrue(connectedLatch.await(3, TimeUnit.SECONDS));
            assertTrue(connected.get());
            assertTrue(server.isClientConnected());
        }
    }
    
    @Test
    @Timeout(10)
    void testSendMessage() throws Exception {
        CountDownLatch connectedLatch = new CountDownLatch(1);
        
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onClientConnected() {
                connectedLatch.countDown();
            }
            
            @Override
            public void onClientDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        server.start();
        Thread.sleep(200);
        
        try (Socket clientSocket = new Socket("localhost", currentTestPort)) {
            ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
            clientOut.flush();
            Thread.sleep(100);
            
            ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream());
            
            assertTrue(connectedLatch.await(3, TimeUnit.SECONDS));
            
            // Send message from server to client
            NetworkMessage testMessage = new NetworkMessage(NetworkMessage.MessageType.GAME_START, "test");
            server.sendMessage(testMessage);
            
            // Read message on client side
            Object received = clientIn.readObject();
            assertNotNull(received);
            assertTrue(received instanceof NetworkMessage);
            NetworkMessage receivedMsg = (NetworkMessage) received;
            assertEquals(NetworkMessage.MessageType.GAME_START, receivedMsg.getType());
        }
    }
    
    @Test
    @Timeout(10)
    void testReceiveMessage() throws Exception {
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<Object> receivedMessage = new AtomicReference<>();
        
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                receivedMessage.set(message);
                messageLatch.countDown();
            }
            
            @Override
            public void onClientConnected() {}
            
            @Override
            public void onClientDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        server.start();
        Thread.sleep(200);
        
        try (Socket clientSocket = new Socket("localhost", currentTestPort)) {
            ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
            clientOut.flush();
            Thread.sleep(100);
            
            ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream());
            
            // Send message from client to server
            NetworkMessage testMessage = new NetworkMessage(NetworkMessage.MessageType.GAME_STATE, "client message");
            clientOut.writeObject(testMessage);
            clientOut.flush();
            
            assertTrue(messageLatch.await(3, TimeUnit.SECONDS));
            assertNotNull(receivedMessage.get());
        }
    }
    
    @Test
    @Timeout(10)
    void testPingPong() throws Exception {
        CountDownLatch rttLatch = new CountDownLatch(1);
        AtomicLong receivedRtt = new AtomicLong(-1);
        
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onClientConnected() {}
            
            @Override
            public void onClientDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {
                receivedRtt.set(rtt);
                rttLatch.countDown();
            }
        });
        
        server.start();
        Thread.sleep(200);
        
        try (Socket clientSocket = new Socket("localhost", currentTestPort)) {
            ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
            clientOut.flush();
            Thread.sleep(100);
            
            ObjectInputStream clientIn = new ObjectInputStream(clientSocket.getInputStream());
            
            // Wait for PING and send PONG
            Object msg = clientIn.readObject();
            if (msg instanceof NetworkMessage) {
                NetworkMessage netMsg = (NetworkMessage) msg;
                if (netMsg.getType() == NetworkMessage.MessageType.PING) {
                    NetworkMessage pong = new NetworkMessage(NetworkMessage.MessageType.PONG, netMsg.getData());
                    clientOut.writeObject(pong);
                    clientOut.flush();
                }
            }
            
            assertTrue(rttLatch.await(3, TimeUnit.SECONDS));
            assertTrue(receivedRtt.get() >= 0);
        }
    }
    
    @Test
    @Timeout(10)
    void testClientDisconnection() throws Exception {
        CountDownLatch disconnectedLatch = new CountDownLatch(1);
        AtomicBoolean disconnected = new AtomicBoolean(false);
        
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onClientConnected() {}
            
            @Override
            public void onClientDisconnected() {
                disconnected.set(true);
                disconnectedLatch.countDown();
            }
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        server.start();
        Thread.sleep(200);
        
        Socket clientSocket = new Socket("localhost", currentTestPort);
        ObjectOutputStream clientOut = new ObjectOutputStream(clientSocket.getOutputStream());
        clientOut.flush();
        Thread.sleep(100);
        
        new ObjectInputStream(clientSocket.getInputStream());
        
        // Close client connection
        clientSocket.close();
        
        assertTrue(disconnectedLatch.await(3, TimeUnit.SECONDS));
        assertTrue(disconnected.get());
    }
    
    @Test
    @Timeout(5)
    void testClose() throws IOException {
        server.close();
        assertFalse(server.isClientConnected());
    }
    
    @Test
    @Timeout(5)
    void testSetMessageHandler() {
        GameServer.MessageHandler handler = new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            
            @Override
            public void onClientConnected() {}
            
            @Override
            public void onClientDisconnected() {}
            
            @Override
            public void onError(Exception e) {}
            
            @Override
            public void onRttUpdate(long rtt) {}
        };
        
        server.setMessageHandler(handler);
        assertNotNull(server);
    }
}
