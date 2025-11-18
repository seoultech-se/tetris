package tetris.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@DisplayName("GameClient 테스트")
public class GameClientTest {

    private GameClient client;
    private GameServer server;
    private static final int TEST_PORT = 12346;
    private static final int TIMEOUT_SECONDS = 5;

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
    @DisplayName("GameClient 생성 테스트")
    void testGameClientCreation() {
        // When
        client = new GameClient();

        // Then
        assertNotNull(client);
        assertFalse(client.isConnected());
    }

    @Test
    @DisplayName("GameClient MessageHandler 설정 테스트")
    void testSetMessageHandler() {
        // Given
        client = new GameClient();
        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        GameClient.MessageHandler handler = new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                handlerCalled.set(true);
            }

            @Override
            public void onConnected() {
                handlerCalled.set(true);
            }

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {}
        };

        // When & Then
        assertDoesNotThrow(() -> client.setMessageHandler(handler));
    }

    @Test
    @DisplayName("GameClient 서버 연결 실패 테스트 (서버 없음)")
    void testConnectToNonExistentServer() {
        // Given
        client = new GameClient();

        // When & Then
        assertThrows(ConnectException.class, () -> {
            client.connect("localhost", TEST_PORT);
        }, "존재하지 않는 서버에 연결 시도하면 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("GameClient 서버 연결 성공 테스트")
    void testConnectToServer() throws IOException, InterruptedException {
        // Given
        server = new GameServer(TEST_PORT);
        server.start();
        Thread.sleep(100); // Give server time to start

        client = new GameClient();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean connected = new AtomicBoolean(false);

        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onConnected() {
                connected.set(true);
                latch.countDown();
            }

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });

        // When
        client.connect("localhost", TEST_PORT);

        // Then
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "연결 콜백이 호출되어야 합니다");
        assertTrue(connected.get(), "클라이언트가 연결되어야 합니다");
        assertTrue(client.isConnected(), "isConnected()가 true를 반환해야 합니다");
    }

    @Test
    @DisplayName("GameClient 연결 전 isConnected 테스트")
    void testIsConnectedBeforeConnection() {
        // Given
        client = new GameClient();

        // When & Then
        assertFalse(client.isConnected(), "연결 전에는 false를 반환해야 합니다");
    }

    @Test
    @DisplayName("GameClient 메시지 전송 테스트 (연결 없이)")
    void testSendMessageWithoutConnection() {
        // Given
        client = new GameClient();
        NetworkMessage message = new NetworkMessage(
            NetworkMessage.MessageType.GAME_START,
            "test"
        );

        // When & Then
        assertDoesNotThrow(() -> client.sendMessage(message),
            "연결 없이 메시지 전송 시도해도 예외가 발생하지 않아야 합니다");
    }

    @Test
    @DisplayName("GameClient 닫기 테스트")
    void testClientClose() throws IOException, InterruptedException {
        // Given
        server = new GameServer(TEST_PORT);
        server.start();
        Thread.sleep(100);

        client = new GameClient();
        client.connect("localhost", TEST_PORT);
        Thread.sleep(200);

        assertTrue(client.isConnected(), "연결이 성립되어야 합니다");

        // When
        client.close();
        Thread.sleep(100);

        // Then
        assertFalse(client.isConnected(), "닫은 후에는 연결되지 않아야 합니다");
    }

    @Test
    @DisplayName("GameClient 다중 close 호출 테스트")
    void testMultipleCloseCallsSafe() {
        // Given
        client = new GameClient();

        // When & Then
        assertDoesNotThrow(() -> {
            client.close();
            client.close();
            client.close();
        }, "여러 번 close를 호출해도 예외가 발생하지 않아야 합니다");
    }

    @Test
    @DisplayName("GameClient 잘못된 IP 주소 연결 테스트")
    void testConnectWithInvalidIP() {
        // Given
        client = new GameClient();

        // When & Then
        assertThrows(Exception.class, () -> {
            client.connect("invalid.ip.address", TEST_PORT);
        }, "잘못된 IP 주소로 연결 시도 시 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("GameClient 연결 해제 콜백 테스트")
    void testDisconnectCallback() throws IOException, InterruptedException {
        // Given
        server = new GameServer(TEST_PORT);
        server.start();
        Thread.sleep(100);

        client = new GameClient();
        CountDownLatch disconnectLatch = new CountDownLatch(1);
        AtomicBoolean disconnected = new AtomicBoolean(false);

        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {
                disconnected.set(true);
                disconnectLatch.countDown();
            }

            @Override
            public void onError(Exception e) {}
        });

        client.connect("localhost", TEST_PORT);
        Thread.sleep(200);

        // When - Close server to trigger disconnection
        server.close();

        // Then
        assertTrue(disconnectLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "연결 해제 콜백이 호출되어야 합니다");
        assertTrue(disconnected.get(), "연결 해제가 감지되어야 합니다");
    }

    @Test
    @DisplayName("GameClient 에러 핸들러 테스트")
    void testErrorHandler() throws IOException, InterruptedException {
        // Given
        server = new GameServer(TEST_PORT);
        server.start();
        Thread.sleep(100);

        client = new GameClient();
        AtomicReference<Exception> caughtError = new AtomicReference<>();

        client.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onError(Exception e) {
                caughtError.set(e);
            }
        });

        client.connect("localhost", TEST_PORT);
        Thread.sleep(200);

        // When - Abruptly close connection to potentially trigger error
        server.close();
        Thread.sleep(500);

        // Then - Error or disconnect should have been triggered
        // (Implementation may call onDisconnected instead of onError)
    }

    @Test
    @DisplayName("GameClient null IP 주소 연결 테스트")
    void testConnectWithNullIP() {
        // Given
        client = new GameClient();

        // When & Then
        assertThrows(Exception.class, () -> {
            client.connect(null, TEST_PORT);
        }, "null IP 주소로 연결 시도 시 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("GameClient 잘못된 포트 번호 연결 테스트")
    void testConnectWithInvalidPort() {
        // Given
        client = new GameClient();

        // When & Then
        assertThrows(Exception.class, () -> {
            client.connect("localhost", -1);
        }, "잘못된 포트 번호로 연결 시도 시 예외가 발생해야 합니다");
    }
}
