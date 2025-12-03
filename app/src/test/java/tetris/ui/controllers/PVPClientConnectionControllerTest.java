package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tetris.data.RecentIPManager;
import tetris.network.GameClient;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PVPClientConnectionControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPClientConnection.fxml"));
                loader.load();
                PVPClientConnectionController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load PVPClientConnection.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnBackAction() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPClientConnection.fxml"));
                loader.load();
                PVPClientConnectionController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                GameClient mockClient = mock(GameClient.class);
                setField(controller, "gameClient", mockClient);

                invoke(controller, "onBack");

                assertTrue(sceneManager.pvpModeSelectionShown, "Should navigate back to PVP Mode Selection");
                verify(mockClient).close();

            } catch (Exception e) {
                fail("onBack action test failed", e);
            }
        });
    }

    @Test
    void testOnConnectWithEmptyIp() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPClientConnection.fxml"));
                loader.load();
                PVPClientConnectionController controller = loader.getController();

                invoke(controller, "onConnect");

                Label statusLabel = (Label) loader.getNamespace().get("statusLabel");
                assertEquals("IP 주소를 입력하세요", statusLabel.getText());

            } catch (Exception e) {
                fail("onConnect with empty IP test failed", e);
            }
        });
    }

    @Test
    void testHandleMessageConnectionAccepted() throws Exception {
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        try (var mockedManager = mockStatic(RecentIPManager.class)) {
            RecentIPManager ipManagerInstance = mock(RecentIPManager.class);
            mockedManager.when(RecentIPManager::getInstance).thenReturn(ipManagerInstance);

            runOnFxThreadAndWait(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPClientConnection.fxml"));
                    loader.load();
                    PVPClientConnectionController controller = loader.getController();

                    TestSceneManager sceneManager = new TestSceneManager(new Stage());
                    controller.setSceneManager(sceneManager);

                    TextField ipField = (TextField) loader.getNamespace().get("serverIpField");
                    ipField.setText("127.0.0.1");

                    Method handleMessage = PVPClientConnectionController.class.getDeclaredMethod("handleMessage", Object.class);
                    handleMessage.setAccessible(true);
                    handleMessage.invoke(controller, new NetworkMessage(NetworkMessage.MessageType.CONNECTION_ACCEPTED, ""));

                    Platform.runLater(() -> {
                        try {
                            assertTrue(sceneManager.pvpLobbyShown);
                            verify(ipManagerInstance).addRecentIP("127.0.0.1");
                        } finally {
                            latch.countDown();
                        }
                    });

                } catch (Exception e) {
                    fail("handleMessage CONNECTION_ACCEPTED test failed", e);
                    latch.countDown();
                }
            });
            assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS), "Test timed out");
        }
    }
    
    @Test
    void testClientHandlerOnDisconnected() throws Exception {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPClientConnection.fxml"));
                loader.load();
                PVPClientConnectionController controller = loader.getController();

                Button connectButton = (Button) loader.getNamespace().get("connectButton");
                connectButton.setDisable(true);

                GameClient.MessageHandler handler = createClientMessageHandler(controller);
                handler.onDisconnected();
                
                Platform.runLater(() -> {
                    try {
                        Label statusLabel = (Label) loader.getNamespace().get("statusLabel");
                        assertEquals("서버 연결 끊김", statusLabel.getText());
                        assertFalse(connectButton.isDisabled());
                    } finally {
                        latch.countDown();
                    }
                });

            } catch (Exception e) {
                fail("onDisconnected handler test failed", e);
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS), "Test timed out");
    }
    
    @Test
    void testClientHandlerOnError() throws Exception {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPClientConnection.fxml"));
                loader.load();
                PVPClientConnectionController controller = loader.getController();

                Button connectButton = (Button) loader.getNamespace().get("connectButton");
                connectButton.setDisable(true);
                
                GameClient.MessageHandler handler = createClientMessageHandler(controller);
                handler.onError(new Exception("Test Error"));

                Platform.runLater(() -> {
                    try {
                        Label statusLabel = (Label) loader.getNamespace().get("statusLabel");
                        assertEquals("연결 실패: Test Error", statusLabel.getText());
                        assertFalse(connectButton.isDisabled());
                    } finally {
                        latch.countDown();
                    }
                });

            } catch (Exception e) {
                fail("onError handler test failed", e);
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, java.util.concurrent.TimeUnit.SECONDS), "Test timed out");
    }

    private void invoke(Object controller, String methodName) throws Exception {
        Method method = controller.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(controller);
    }

    private void setField(Object controller, String fieldName, Object value) throws Exception {
        Field field = controller.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private GameClient.MessageHandler createClientMessageHandler(PVPClientConnectionController controller) throws Exception {
        Class<?> handlerClass = Class.forName("tetris.ui.controllers.PVPClientConnectionController$1");
        Constructor<?> constructor = handlerClass.getDeclaredConstructor(PVPClientConnectionController.class);
        constructor.setAccessible(true);
        return (GameClient.MessageHandler) constructor.newInstance(controller);
    }

    private static class TestSceneManager extends tetris.ui.SceneManager {
        boolean pvpModeSelectionShown = false;
        boolean pvpLobbyShown = false;

        public TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showPVPModeSelection() {
            pvpModeSelectionShown = true;
        }

        @Override
        public void showPVPLobby(Object gameServer, Object gameClient, boolean isServer) {
            pvpLobbyShown = true;
        }
    }

    private void waitFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
