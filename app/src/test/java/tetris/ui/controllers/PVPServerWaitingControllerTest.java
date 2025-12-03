package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tetris.network.GameServer;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for PVPServerWaitingController.
 */
class PVPServerWaitingControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPServerWaiting.fxml"));
                loader.load();
                
                PVPServerWaitingController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load PVPServerWaiting.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPServerWaiting.fxml"));
                loader.load();
                
                PVPServerWaitingController controller = loader.getController();
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                
                controller.setSceneManager(sceneManager);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set scene manager: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPServerWaiting.fxml"));
                assertNotNull(loader.getLocation(), "PVPServerWaiting.fxml should exist");
                
                Object root = loader.load();
                assertNotNull(root, "FXML root should not be null");
            } catch (Exception e) {
                fail("Failed to load FXML: " + e.getMessage());
            }
        });
    }

    @Test
    void testControllerInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPServerWaiting.fxml"));
                loader.load();
                
                PVPServerWaitingController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetServerInfoRegistersHandlerAndHandlesCallbacks() throws Exception {
        GameServer mockServer = mock(GameServer.class);
        Label[] ipLabelHolder = new Label[1];
        Label[] statusLabelHolder = new Label[1];
        TestSceneManager[] sceneManagerHolder = new TestSceneManager[1];

        runOnFxThreadAndWait(() -> {
            try {
                PVPServerWaitingController controller = new PVPServerWaitingController();
                Label ipLabel = new Label();
                Label statusLabel = new Label("대기 중");
                ipLabelHolder[0] = ipLabel;
                statusLabelHolder[0] = statusLabel;

                setPrivateField(controller, "serverIpLabel", ipLabel);
                setPrivateField(controller, "statusLabel", statusLabel);

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                sceneManagerHolder[0] = sceneManager;
                controller.setSceneManager(sceneManager);

                controller.setServerInfo(mockServer, "10.0.0.1");
            } catch (Exception e) {
                fail("Server info setup failed: " + e.getMessage());
            }
        });

        assertEquals("10.0.0.1", ipLabelHolder[0].getText());

        ArgumentCaptor<GameServer.MessageHandler> captor = ArgumentCaptor.forClass(GameServer.MessageHandler.class);
        verify(mockServer).setMessageHandler(captor.capture());
        GameServer.MessageHandler handler = captor.getValue();
        doNothing().when(mockServer).sendMessage(any());

        handler.onClientConnected();
        waitFor(500);
        runOnFxThreadAndWait(() -> {
            String text = statusLabelHolder[0].getText();
            assertTrue(
                text.contains("연결되었습니다") || text.contains("로비로 이동 중"),
                "Status label should reflect connection progress"
            );
        });
        waitFor(1500);
        assertTrue(sceneManagerHolder[0].lobbyShown, "Lobby should be requested after client connection");

        handler.onClientDisconnected();
        waitFor(100);
        runOnFxThreadAndWait(() -> assertTrue(statusLabelHolder[0].getText().contains("연결 끊김")));

        handler.onError(new IOException("boom"));
        waitFor(100);
        runOnFxThreadAndWait(() -> assertTrue(statusLabelHolder[0].getText().contains("오류")));
    }

    @Test
    void testInitializeAdjustsBackgroundImageForScreenSizes() {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            String originalSize = settings.getScreenSize();
            try {
                assertBackgroundSizeForSetting(settings, "작게", 480, 720);
                assertBackgroundSizeForSetting(settings, "중간", 600, 900);
                assertBackgroundSizeForSetting(settings, "크게", 720, 1080);
                assertBackgroundSizeForSetting(settings, "알수없음", 600, 900);
            } finally {
                settings.setScreenSize(originalSize);
            }
        });
    }

    @Test
    void testHandleMessageAcceptsNetworkMessagesGracefully() {
        runOnFxThreadAndWait(() -> {
            try {
                PVPServerWaitingController controller = new PVPServerWaitingController();
                invokeHandleMessage(controller, new NetworkMessage(NetworkMessage.MessageType.LOBBY_READY, "data"));
                invokeHandleMessage(controller, "not a network message");
            } catch (Exception e) {
                fail("handleMessage invocation failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnBackWithoutServerStillNavigatesToModeSelection() throws Exception {
        TestSceneManager[] sceneManagerHolder = new TestSceneManager[1];

        runOnFxThreadAndWait(() -> {
            try {
                PVPServerWaitingController controller = new PVPServerWaitingController();
                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                sceneManagerHolder[0] = sceneManager;
                controller.setSceneManager(sceneManager);

                invokePrivate(controller, "onBack");
            } catch (Exception e) {
                fail("Back action without server failed: " + e.getMessage());
            }
        });

        assertTrue(sceneManagerHolder[0].modeSelectionShown,
            "Mode selection should still be shown even when no server is active");
    }

    @Test
    void testOnBackClosesServerAndNavigatesToModeSelection() throws Exception {
        GameServer mockServer = mock(GameServer.class);
        TestSceneManager[] sceneManagerHolder = new TestSceneManager[1];

        runOnFxThreadAndWait(() -> {
            try {
                PVPServerWaitingController controller = new PVPServerWaitingController();
                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                sceneManagerHolder[0] = sceneManager;
                controller.setSceneManager(sceneManager);
                setPrivateField(controller, "gameServer", mockServer);

                invokePrivate(controller, "onBack");
            } catch (Exception e) {
                fail("Back action test failed: " + e.getMessage());
            }
        });

        verify(mockServer).close();
        assertTrue(sceneManagerHolder[0].modeSelectionShown, "Back action should return to mode selection");
    }

    private void assertBackgroundSizeForSetting(SettingsManager settings, String size, double expectedWidth, double expectedHeight) {
        settings.setScreenSize(size);
        PVPServerWaitingController controller = new PVPServerWaitingController();
        ImageView imageView = new ImageView();
        setPrivateField(controller, "backgroundImage", imageView);
        controller.initialize(null, null);
        assertEquals(expectedWidth, imageView.getFitWidth(), 0.01, "Width mismatch for " + size);
        assertEquals(expectedHeight, imageView.getFitHeight(), 0.01, "Height mismatch for " + size);
    }

    private void setPrivateField(PVPServerWaitingController controller, String fieldName, Object value) {
        try {
            var field = PVPServerWaitingController.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(controller, value);
        } catch (ReflectiveOperationException e) {
            fail("Failed to set field " + fieldName + ": " + e.getMessage());
        }
    }

    private void invokePrivate(PVPServerWaitingController controller, String methodName) {
        try {
            var method = PVPServerWaitingController.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(controller);
        } catch (ReflectiveOperationException e) {
            fail("Failed to invoke " + methodName + ": " + e.getMessage());
        }
    }

    private void invokeHandleMessage(PVPServerWaitingController controller, Object message) throws ReflectiveOperationException {
        var method = PVPServerWaitingController.class.getDeclaredMethod("handleMessage", Object.class);
        method.setAccessible(true);
        method.invoke(controller, message);
    }

    private static class TestSceneManager extends SceneManager {
        private boolean lobbyShown;
        private boolean modeSelectionShown;

        TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showPVPLobby(Object gameServer, Object gameClient, boolean isServer) {
            lobbyShown = true;
        }

        @Override
        public void showPVPModeSelection() {
            modeSelectionShown = true;
        }
    }
}
