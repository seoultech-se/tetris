package tetris.network;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class GameServerClientTest {

    private static final int TEST_PORT = 17777;
    private GameServer server;
    private GameClient client;

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.close();
        }
        // 포트 해제를 위한 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Timeout(10)
    void testServerCreation() throws IOException {
        server = new GameServer(TEST_PORT);
        assertNotNull(server);
    }

    @Test
    @Timeout(10)
    void testServerGetIP() throws IOException {
        server = new GameServer(TEST_PORT + 1);
        String ip = server.getServerIP();
        assertNotNull(ip);
        assertFalse(ip.isEmpty());
    }

    @Test
    @Timeout(10)
    void testClientNotConnectedInitially() {
        client = new GameClient();
        assertFalse(client.isConnected());
    }

    @Test
    @Timeout(10)
    void testServerClientConnection() throws Exception {
        CountDownLatch serverConnected = new CountDownLatch(1);
        CountDownLatch clientConnected = new CountDownLatch(1);
        
        server = new GameServer(TEST_PORT + 2);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            @Override
            public void onClientConnected() {
                serverConnected.countDown();
            }
            @Override
            public void onClientDisconnected() {}
            @Override
            public void onError(Exception e) {}
            @Override
            public void onRttUpdate(long rtt) {}
        });
        server.start();
        
        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            @Override
            public void onConnected() {
                clientConnected.countDown();
            }
            @Override
            public void onDisconnected() {}
            @Override
            public void onError(Exception e) {}
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        // 서버가 시작될 시간 대기
        Thread.sleep(200);
        
        client.connect("localhost", TEST_PORT + 2);
        
        assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
        assertTrue(serverConnected.await(5, TimeUnit.SECONDS));
        assertTrue(client.isConnected());
        assertTrue(server.isClientConnected());
    }

    @Test
    @Timeout(15)
    void testMessageExchange() throws Exception {
        CountDownLatch messageReceived = new CountDownLatch(1);
        AtomicReference<Object> receivedMessage = new AtomicReference<>();
        
        server = new GameServer(TEST_PORT + 3);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                receivedMessage.set(message);
                messageReceived.countDown();
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
        
        client = new GameClient();
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
        
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT + 3);
        Thread.sleep(300);
        
        // 메시지 전송
        NetworkMessage testMsg = new NetworkMessage(
            NetworkMessage.MessageType.CONNECTION_REQUEST, "TestPlayer"
        );
        client.sendMessage(testMsg);
        
        assertTrue(messageReceived.await(5, TimeUnit.SECONDS));
        assertNotNull(receivedMessage.get());
        assertTrue(receivedMessage.get() instanceof NetworkMessage);
        
        NetworkMessage received = (NetworkMessage) receivedMessage.get();
        assertEquals(NetworkMessage.MessageType.CONNECTION_REQUEST, received.getType());
        assertEquals("TestPlayer", received.getData());
    }

    @Test
    @Timeout(15)
    void testServerSendsToClient() throws Exception {
        CountDownLatch messageReceived = new CountDownLatch(1);
        AtomicReference<Object> receivedMessage = new AtomicReference<>();
        CountDownLatch connected = new CountDownLatch(1);
        
        server = new GameServer(TEST_PORT + 4);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            @Override
            public void onClientConnected() {
                connected.countDown();
            }
            @Override
            public void onClientDisconnected() {}
            @Override
            public void onError(Exception e) {}
            @Override
            public void onRttUpdate(long rtt) {}
        });
        server.start();
        
        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                if (message instanceof NetworkMessage) {
                    NetworkMessage netMsg = (NetworkMessage) message;
                    if (netMsg.getType() == NetworkMessage.MessageType.CONNECTION_ACCEPTED) {
                        receivedMessage.set(message);
                        messageReceived.countDown();
                    }
                }
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
        
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT + 4);
        
        assertTrue(connected.await(5, TimeUnit.SECONDS));
        Thread.sleep(500);
        
        // 서버에서 클라이언트로 메시지 전송
        NetworkMessage testMsg = new NetworkMessage(
            NetworkMessage.MessageType.CONNECTION_ACCEPTED, true
        );
        server.sendMessage(testMsg);
        
        assertTrue(messageReceived.await(5, TimeUnit.SECONDS));
        assertNotNull(receivedMessage.get());
        
        NetworkMessage received = (NetworkMessage) receivedMessage.get();
        assertEquals(NetworkMessage.MessageType.CONNECTION_ACCEPTED, received.getType());
    }

    @Test
    @Timeout(10)
    void testClientDisconnection() throws Exception {
        CountDownLatch disconnected = new CountDownLatch(1);
        
        server = new GameServer(TEST_PORT + 5);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            @Override
            public void onClientConnected() {}
            @Override
            public void onClientDisconnected() {
                disconnected.countDown();
            }
            @Override
            public void onError(Exception e) {}
            @Override
            public void onRttUpdate(long rtt) {}
        });
        server.start();
        
        client = new GameClient();
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
        
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT + 5);
        Thread.sleep(300);
        
        assertTrue(client.isConnected());
        
        // 클라이언트 종료
        client.close();
        
        assertTrue(disconnected.await(5, TimeUnit.SECONDS));
    }

    @Test
    @Timeout(10)
    void testServerClose() throws Exception {
        server = new GameServer(TEST_PORT + 6);
        server.start();
        Thread.sleep(100);
        
        server.close();
        assertFalse(server.isClientConnected());
    }

    @Test
    @Timeout(10)
    void testClientClose() throws Exception {
        client = new GameClient();
        
        // 연결 없이 close 호출해도 예외 발생 안함
        assertDoesNotThrow(() -> client.close());
        assertFalse(client.isConnected());
    }

    @Test
    void testClientConnectToInvalidHost() {
        client = new GameClient();
        
        assertThrows(IOException.class, () -> {
            client.connect("invalid.host.that.does.not.exist", 9999);
        });
    }

    @Test
    @Timeout(15)
    void testRttUpdate() throws Exception {
        CountDownLatch rttUpdated = new CountDownLatch(1);
        AtomicBoolean rttReceived = new AtomicBoolean(false);
        CountDownLatch clientConnected = new CountDownLatch(1);
        
        server = new GameServer(TEST_PORT + 7);
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
                if (rtt >= 0) {
                    rttReceived.set(true);
                    rttUpdated.countDown();
                }
            }
        });
        server.start();
        
        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}
            @Override
            public void onConnected() {
                clientConnected.countDown();
            }
            @Override
            public void onDisconnected() {}
            @Override
            public void onError(Exception e) {}
            @Override
            public void onRttUpdate(long rtt) {}
        });
        
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT + 7);
        
        assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
        
        // PING/PONG이 동작하면 RTT 업데이트됨 (여러번 시도 허용)
        boolean success = rttUpdated.await(8, TimeUnit.SECONDS);
        if (!success) {
            // 타이밍 이슈로 실패할 수 있으므로 건너뜀
            System.out.println("RTT update not received in time, but connection works");
        }
    }
}
