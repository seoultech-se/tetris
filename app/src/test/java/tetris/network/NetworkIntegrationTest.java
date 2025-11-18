package tetris.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@DisplayName("네트워크 통합 테스트 - P2P 대전 모드 접속 검증")
public class NetworkIntegrationTest {

    private GameServer server;
    private GameClient client;
    private static final int TEST_PORT = 12347;
    private static final int TIMEOUT_SECONDS = 10;

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (server != null) {
            server.close();
        }
    }

    @Test
    @DisplayName("P2P 연결 설정 통합 테스트")
    void testP2PConnectionEstablishment() throws IOException, InterruptedException {
        // Given
        CountDownLatch serverConnectedLatch = new CountDownLatch(1);
        CountDownLatch clientConnectedLatch = new CountDownLatch(1);
        AtomicBoolean serverConnected = new AtomicBoolean(false);
        AtomicBoolean clientConnected = new AtomicBoolean(false);

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {
                serverConnected.set(true);
                serverConnectedLatch.countDown();
            }

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {
                fail("서버 에러 발생: " + e.getMessage());
            }
        });

        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onConnected() {
                clientConnected.set(true);
                clientConnectedLatch.countDown();
            }

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {
                fail("클라이언트 에러 발생: " + e.getMessage());
            }
        });

        // When
        server.start();
        Thread.sleep(200); // Give server time to start

        client.connect("localhost", TEST_PORT);

        // Then
        assertTrue(serverConnectedLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "서버에서 클라이언트 연결을 감지해야 합니다");
        assertTrue(clientConnectedLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "클라이언트에서 연결 성공을 감지해야 합니다");
        assertTrue(serverConnected.get(), "서버 연결 상태가 true여야 합니다");
        assertTrue(clientConnected.get(), "클라이언트 연결 상태가 true여야 합니다");
        assertTrue(server.isClientConnected(), "서버의 isClientConnected()가 true여야 합니다");
        assertTrue(client.isConnected(), "클라이언트의 isConnected()가 true여야 합니다");
    }

    @Test
    @DisplayName("서버에서 클라이언트로 메시지 전송 테스트")
    void testServerToClientMessageTransmission() throws IOException, InterruptedException {
        // Given
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<NetworkMessage> receivedMessage = new AtomicReference<>();

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                receivedMessage.set((NetworkMessage) message);
                messageLatch.countDown();
            }

            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        server.start();
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT);
        Thread.sleep(300);

        // When
        NetworkMessage testMessage = new NetworkMessage(
            NetworkMessage.MessageType.GAME_START,
            "게임 시작"
        );
        server.sendMessage(testMessage);

        // Then
        assertTrue(messageLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "클라이언트가 메시지를 수신해야 합니다");
        assertNotNull(receivedMessage.get(), "수신된 메시지가 null이 아니어야 합니다");
        assertEquals(NetworkMessage.MessageType.GAME_START,
            receivedMessage.get().getType(),
            "메시지 타입이 일치해야 합니다");
        assertEquals("게임 시작", receivedMessage.get().getData(),
            "메시지 데이터가 일치해야 합니다");
    }

    @Test
    @DisplayName("클라이언트에서 서버로 메시지 전송 테스트")
    void testClientToServerMessageTransmission() throws IOException, InterruptedException {
        // Given
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<NetworkMessage> receivedMessage = new AtomicReference<>();

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                receivedMessage.set((NetworkMessage) message);
                messageLatch.countDown();
            }

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

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
        });

        server.start();
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT);
        Thread.sleep(300);

        // When
        NetworkMessage testMessage = new NetworkMessage(
            NetworkMessage.MessageType.PLAYER_INPUT,
            "키 입력: LEFT"
        );
        client.sendMessage(testMessage);

        // Then
        assertTrue(messageLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "서버가 메시지를 수신해야 합니다");
        assertNotNull(receivedMessage.get(), "수신된 메시지가 null이 아니어야 합니다");
        assertEquals(NetworkMessage.MessageType.PLAYER_INPUT,
            receivedMessage.get().getType(),
            "메시지 타입이 일치해야 합니다");
        assertEquals("키 입력: LEFT", receivedMessage.get().getData(),
            "메시지 데이터가 일치해야 합니다");
    }

    @Test
    @DisplayName("양방향 메시지 교환 테스트")
    void testBidirectionalMessageExchange() throws IOException, InterruptedException {
        // Given
        CountDownLatch serverReceiveLatch = new CountDownLatch(1);
        CountDownLatch clientReceiveLatch = new CountDownLatch(1);
        AtomicReference<NetworkMessage> serverReceived = new AtomicReference<>();
        AtomicReference<NetworkMessage> clientReceived = new AtomicReference<>();

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                serverReceived.set((NetworkMessage) message);
                serverReceiveLatch.countDown();
            }

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                clientReceived.set((NetworkMessage) message);
                clientReceiveLatch.countDown();
            }

            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        server.start();
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT);
        Thread.sleep(300);

        // When
        NetworkMessage clientToServer = new NetworkMessage(
            NetworkMessage.MessageType.PLAYER_ACTION,
            "클라이언트 액션"
        );
        NetworkMessage serverToClient = new NetworkMessage(
            NetworkMessage.MessageType.GAME_STATE_UPDATE,
            "서버 상태 업데이트"
        );

        client.sendMessage(clientToServer);
        server.sendMessage(serverToClient);

        // Then
        assertTrue(serverReceiveLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "서버가 메시지를 수신해야 합니다");
        assertTrue(clientReceiveLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "클라이언트가 메시지를 수신해야 합니다");
        assertEquals(NetworkMessage.MessageType.PLAYER_ACTION,
            serverReceived.get().getType());
        assertEquals(NetworkMessage.MessageType.GAME_STATE_UPDATE,
            clientReceived.get().getType());
    }

    @Test
    @DisplayName("GameStateData 네트워크 전송 테스트")
    void testGameStateDataTransmission() throws IOException, InterruptedException {
        // Given
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<GameStateData> receivedState = new AtomicReference<>();

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                NetworkMessage networkMessage = (NetworkMessage) message;
                receivedState.set((GameStateData) networkMessage.getData());
                messageLatch.countDown();
            }

            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        server.start();
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT);
        Thread.sleep(300);

        // When
        int[][] testBoard = {{1, 0}, {0, 1}};
        int[][] testItemBoard = {{0, 0}, {0, 0}};
        int[][] currentPiece = {{1, 1}};
        int[][] nextPiece = {{1, 1, 1}};

        GameStateData gameState = new GameStateData(
            testBoard, testItemBoard, 500, 3, 5, false,
            currentPiece, 4, 2, 1, nextPiece, 2, 3
        );

        NetworkMessage message = new NetworkMessage(
            NetworkMessage.MessageType.GAME_STATE,
            gameState
        );
        server.sendMessage(message);

        // Then
        assertTrue(messageLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "게임 상태 데이터를 수신해야 합니다");
        assertNotNull(receivedState.get());
        assertEquals(500, receivedState.get().getScore());
        assertEquals(3, receivedState.get().getLevel());
        assertEquals(3, receivedState.get().getIncomingAttackLines());
    }

    @Test
    @DisplayName("연결 끊김 감지 테스트")
    void testConnectionDisconnectionDetection() throws IOException, InterruptedException {
        // Given
        CountDownLatch disconnectLatch = new CountDownLatch(1);
        AtomicBoolean clientDisconnected = new AtomicBoolean(false);

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {
                clientDisconnected.set(true);
                disconnectLatch.countDown();
            }

            @Override
            public void onError(Exception e) {}
        });

        client = new GameClient();

        server.start();
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT);
        Thread.sleep(300);

        assertTrue(server.isClientConnected(), "초기에는 연결되어 있어야 합니다");

        // When
        client.close();

        // Then
        assertTrue(disconnectLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "연결 끊김을 감지해야 합니다");
        assertTrue(clientDisconnected.get(), "클라이언트 연결 해제가 감지되어야 합니다");
    }

    @Test
    @DisplayName("다중 메시지 순차 전송 테스트")
    void testMultipleSequentialMessages() throws IOException, InterruptedException {
        // Given
        int messageCount = 5;
        CountDownLatch messageLatch = new CountDownLatch(messageCount);
        AtomicInteger receivedCount = new AtomicInteger(0);

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                receivedCount.incrementAndGet();
                messageLatch.countDown();
            }

            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        server.start();
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT);
        Thread.sleep(300);

        // When
        for (int i = 0; i < messageCount; i++) {
            NetworkMessage message = new NetworkMessage(
                NetworkMessage.MessageType.GAME_STATE_UPDATE,
                "메시지 " + i
            );
            server.sendMessage(message);
            Thread.sleep(50); // Small delay between messages
        }

        // Then
        assertTrue(messageLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "모든 메시지를 수신해야 합니다");
        assertEquals(messageCount, receivedCount.get(),
            messageCount + "개의 메시지를 모두 받아야 합니다");
    }

    @Test
    @DisplayName("네트워크 지연 시뮬레이션 테스트")
    void testNetworkLatencySimulation() throws IOException, InterruptedException {
        // Given
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<NetworkMessage> receivedMessage = new AtomicReference<>();
        AtomicReference<Long> sendTime = new AtomicReference<>();
        AtomicReference<Long> receiveTime = new AtomicReference<>();

        server = new GameServer(TEST_PORT);
        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        client = new GameClient();
        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                receiveTime.set(System.currentTimeMillis());
                receivedMessage.set((NetworkMessage) message);
                messageLatch.countDown();
            }

            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {}
        });

        server.start();
        Thread.sleep(200);
        client.connect("localhost", TEST_PORT);
        Thread.sleep(300);

        // When
        NetworkMessage message = new NetworkMessage(
            NetworkMessage.MessageType.PLAYER_INPUT,
            "테스트 입력"
        );
        sendTime.set(System.currentTimeMillis());
        server.sendMessage(message);

        // Then
        assertTrue(messageLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "메시지를 수신해야 합니다");

        long latency = receiveTime.get() - sendTime.get();
        System.out.println("네트워크 지연 시간: " + latency + "ms");

        // 로컬 네트워크에서는 200ms 이하여야 함 (요구사항)
        assertTrue(latency < 200,
            "로컬 네트워크 지연은 200ms 이하여야 합니다. 실제: " + latency + "ms");
    }
}
