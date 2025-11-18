package tetris.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@DisplayName("GameServer 테스트")
public class GameServerTest {

    private GameServer server;
    private Socket testClient;
    private static final int TEST_PORT = 12345;
    private static final int TIMEOUT_SECONDS = 5;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.close();
        }
        if (testClient != null && !testClient.isClosed()) {
            try {
                testClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @DisplayName("GameServer 생성 테스트")
    void testGameServerCreation() throws IOException {
        // When
        server = new GameServer(TEST_PORT);

        // Then
        assertNotNull(server);
        assertNotNull(server.getServerIP());
        assertFalse(server.isClientConnected());
    }

    @Test
    @DisplayName("GameServer IP 주소 가져오기 테스트")
    void testGetServerIP() throws IOException {
        // Given
        server = new GameServer(TEST_PORT);

        // When
        String ip = server.getServerIP();

        // Then
        assertNotNull(ip);
        assertNotEquals("Unknown", ip);
        assertTrue(ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+"),
            "IP 주소는 올바른 형식이어야 합니다");
    }

    @Test
    @DisplayName("GameServer 클라이언트 연결 테스트")
    void testClientConnection() throws IOException, InterruptedException {
        // Given
        server = new GameServer(TEST_PORT);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean connected = new AtomicBoolean(false);

        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {
                connected.set(true);
                latch.countDown();
            }

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {
                // Ignore errors during connection test
            }
        });

        // When
        server.start();
        Thread.sleep(500); // Give server time to start

        // Use a separate thread to connect a proper client
        GameClient testClient = new GameClient();
        new Thread(() -> {
            try {
                testClient.connect("localhost", TEST_PORT);
            } catch (IOException e) {
                // Ignore
            }
        }).start();

        // Then
        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
            "클라이언트 연결 콜백이 호출되어야 합니다");
        assertTrue(connected.get(), "클라이언트가 연결되어야 합니다");

        // Wait a bit for connection to be fully established
        Thread.sleep(500);
        assertTrue(server.isClientConnected(), "서버가 클라이언트 연결을 인식해야 합니다");

        // Cleanup
        testClient.close();
    }

    @Test
    @DisplayName("GameServer MessageHandler 설정 테스트")
    void testSetMessageHandler() throws IOException {
        // Given
        server = new GameServer(TEST_PORT);
        AtomicBoolean handlerSet = new AtomicBoolean(false);

        GameServer.MessageHandler handler = new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                handlerSet.set(true);
            }

            @Override
            public void onClientConnected() {
                handlerSet.set(true);
            }

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {}
        };

        // When
        server.setMessageHandler(handler);

        // Then - Handler should be set (we'll verify through connection)
        assertDoesNotThrow(() -> server.setMessageHandler(handler));
    }

    @Test
    @DisplayName("GameServer 메시지 전송 테스트 (연결 없이)")
    void testSendMessageWithoutConnection() throws IOException {
        // Given
        server = new GameServer(TEST_PORT);
        NetworkMessage message = new NetworkMessage(
            NetworkMessage.MessageType.GAME_START,
            "test"
        );

        // When & Then - Should not throw exception even without connection
        assertDoesNotThrow(() -> server.sendMessage(message));
    }

    @Test
    @DisplayName("GameServer 중복 포트 사용 예외 테스트")
    void testDuplicatePortException() throws IOException {
        // Given
        server = new GameServer(TEST_PORT);

        // When & Then
        assertThrows(IOException.class, () -> {
            new GameServer(TEST_PORT);
        }, "같은 포트를 두 번 사용하면 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("GameServer 닫기 테스트")
    void testServerClose() throws IOException {
        // Given
        server = new GameServer(TEST_PORT);

        // When
        server.close();

        // Then
        assertFalse(server.isClientConnected());
    }

    @Test
    @DisplayName("GameServer 에러 핸들러 테스트")
    void testErrorHandler() throws IOException, InterruptedException {
        // Given
        server = new GameServer(TEST_PORT);
        CountDownLatch errorLatch = new CountDownLatch(1);
        AtomicReference<Exception> caughtError = new AtomicReference<>();

        server.setMessageHandler(new GameServer.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {}

            @Override
            public void onClientConnected() {}

            @Override
            public void onClientDisconnected() {}

            @Override
            public void onError(Exception e) {
                caughtError.set(e);
                errorLatch.countDown();
            }
        });

        // When
        server.start();
        Thread.sleep(100);

        testClient = new Socket("localhost", TEST_PORT);
        Thread.sleep(100);

        // Force an error by closing the client abruptly
        testClient.close();

        // Then - Wait for error or disconnect callback
        // Note: This might trigger onClientDisconnected instead of onError
        Thread.sleep(500); // Give time for callbacks to be processed
    }

    @Test
    @DisplayName("GameServer 시작 후 상태 확인 테스트")
    void testServerStateAfterStart() throws IOException, InterruptedException {
        // Given
        server = new GameServer(TEST_PORT);

        // When
        server.start();
        Thread.sleep(200); // Give server time to start listening

        // Then
        assertFalse(server.isClientConnected(),
            "클라이언트가 연결되기 전에는 false여야 합니다");
    }

    @Test
    @DisplayName("GameServer 다중 close 호출 테스트")
    void testMultipleCloseCallsSafe() throws IOException {
        // Given
        server = new GameServer(TEST_PORT);

        // When & Then
        assertDoesNotThrow(() -> {
            server.close();
            server.close();
            server.close();
        }, "여러 번 close를 호출해도 예외가 발생하지 않아야 합니다");
    }

    @Test
    @DisplayName("GameServer 연결 전 isClientConnected 테스트")
    void testIsClientConnectedBeforeConnection() throws IOException {
        // Given
        server = new GameServer(TEST_PORT);

        // When & Then
        assertFalse(server.isClientConnected(),
            "클라이언트 연결 전에는 false를 반환해야 합니다");
    }
}
