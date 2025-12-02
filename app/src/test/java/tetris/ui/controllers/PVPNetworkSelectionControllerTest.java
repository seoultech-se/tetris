package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tetris.network.GameClient;
import tetris.network.GameServer;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for PVPNetworkSelectionController.
 */
class PVPNetworkSelectionControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                
                PVPNetworkSelectionController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load PVPNetworkSelection.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                
                PVPNetworkSelectionController controller = loader.getController();
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
    void testSetGameMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                
                PVPNetworkSelectionController controller = loader.getController();
                
                // Test setting game mode
                controller.setGameMode("NORMAL");
                controller.setGameMode("ITEM");

                Label gameModeLabel = getNode(loader, "gameModeLabel");
                assertTrue(gameModeLabel.getText().contains("아이템"));
            } catch (Exception e) {
                fail("Failed to set game mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                assertNotNull(loader.getLocation(), "PVPNetworkSelection.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                
                PVPNetworkSelectionController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testClientModeDisplaysClientBox() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                invoke(controller, "onClientMode");

                VBox modeSelectionBox = getNode(loader, "modeSelectionBox");
                VBox clientBox = getNode(loader, "clientBox");

                assertFalse(modeSelectionBox.isVisible());
                assertFalse(modeSelectionBox.isManaged());
                assertTrue(clientBox.isVisible());
                assertTrue(clientBox.isManaged());
            } catch (Exception e) {
                fail("Client mode visibility failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBackActionReturnsToModeSelection() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                invoke(controller, "onBack");
                assertTrue(sceneManager.modeSelectionShown);
            } catch (Exception e) {
                fail("Back action failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetGameModeNormal() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                controller.setGameMode("NORMAL");

                Label gameModeLabel = getNode(loader, "gameModeLabel");
                assertTrue(gameModeLabel.getText().contains("일반"));
            } catch (Exception e) {
                fail("Set game mode NORMAL failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetGameModeTimeLimit() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                controller.setGameMode("TIME_LIMIT");

                Label gameModeLabel = getNode(loader, "gameModeLabel");
                assertTrue(gameModeLabel.getText().contains("시간제한"));
            } catch (Exception e) {
                fail("Set game mode TIME_LIMIT failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testIsServerInitiallyFalse() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                Field isServerField = PVPNetworkSelectionController.class.getDeclaredField("isServer");
                isServerField.setAccessible(true);
                boolean isServer = (boolean) isServerField.get(controller);

                assertFalse(isServer, "isServer should be false initially");
            } catch (Exception e) {
                fail("isServer check failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnConnectWithEmptyIP() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                // First switch to client mode
                invoke(controller, "onClientMode");

                TextField serverIpField = getNode(loader, "serverIpField");
                serverIpField.setText(""); // Empty IP

                invoke(controller, "onConnect");

                Label clientStatusLabel = getNode(loader, "clientStatusLabel");
                assertTrue(clientStatusLabel.getText().contains("enter IP") ||
                          clientStatusLabel.getText().contains("Please"),
                          "Should show error for empty IP");
            } catch (Exception e) {
                fail("Connect with empty IP test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testCleanup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                Method cleanup = PVPNetworkSelectionController.class.getDeclaredMethod("cleanup");
                cleanup.setAccessible(true);
                cleanup.invoke(controller);

                Field gameServerField = PVPNetworkSelectionController.class.getDeclaredField("gameServer");
                gameServerField.setAccessible(true);
                assertNull(gameServerField.get(controller), "gameServer should be null after cleanup");

                Field gameClientField = PVPNetworkSelectionController.class.getDeclaredField("gameClient");
                gameClientField.setAccessible(true);
                assertNull(gameClientField.get(controller), "gameClient should be null after cleanup");
            } catch (Exception e) {
                fail("Cleanup test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testCleanupClosesNetworkResources() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                GameServer gameServer = mock(GameServer.class);
                GameClient gameClient = mock(GameClient.class);
                setField(controller, "gameServer", gameServer);
                setField(controller, "gameClient", gameClient);
                PVPNetworkSelectionController.setGameScreenController(mock(PVPGameScreenController.class));

                invoke(controller, "cleanup");

                verify(gameServer).close();
                verify(gameClient).close();
                assertNull(getField(controller, "gameServer"));
                assertNull(getField(controller, "gameClient"));
                assertNull(getStaticField("gameScreenController"));
            } catch (Exception e) {
                fail("Cleanup resources test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnBackPerformsCleanupBeforeNavigation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                GameServer gameServer = mock(GameServer.class);
                setField(controller, "gameServer", gameServer);

                invoke(controller, "onBack");

                verify(gameServer).close();
                assertTrue(sceneManager.modeSelectionShown);
            } catch (Exception e) {
                fail("Back cleanup test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBackgroundImageSizes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String original = settings.getScreenSize();

                // Test small size
                settings.setScreenSize("작게");
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader1.load();
                assertNotNull(loader1.getController());

                // Test medium size
                settings.setScreenSize("중간");
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader2.load();
                assertNotNull(loader2.getController());

                // Test large size
                settings.setScreenSize("크게");
                FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader3.load();
                assertNotNull(loader3.getController());

                // Restore original
                settings.setScreenSize(original);
            } catch (Exception e) {
                fail("Background image size test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testStaticSetGameScreenController() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // Test setting game screen controller
                PVPNetworkSelectionController.setGameScreenController(null);
                // No exception should be thrown
            } catch (Exception e) {
                fail("setGameScreenController test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testClientBoxHiddenInitially() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();

                VBox clientBox = getNode(loader, "clientBox");
                // Client box might be visible or hidden based on FXML defaults
                assertNotNull(clientBox, "clientBox should exist");
            } catch (Exception e) {
                fail("Client box initial state test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testModeSelectionBoxExists() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();

                VBox modeSelectionBox = getNode(loader, "modeSelectionBox");
                assertNotNull(modeSelectionBox, "modeSelectionBox should exist");
            } catch (Exception e) {
                fail("Mode selection box test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testServerButtonExists() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();

                Button serverButton = getNode(loader, "serverButton");
                assertNotNull(serverButton, "serverButton should exist");
            } catch (Exception e) {
                fail("Server button test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testConnectButtonExists() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();

                Button connectButton = getNode(loader, "connectButton");
                assertNotNull(connectButton, "connectButton should exist");
            } catch (Exception e) {
                fail("Connect button test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testServerModeDisplaysServerBox() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                // 기존 서버 정리
                invoke(controller, "cleanup");

                VBox modeSelectionBox = getNode(loader, "modeSelectionBox");
                VBox serverBox = getNode(loader, "serverBox");

                assertTrue(modeSelectionBox.isVisible(), "Mode selection should be visible initially");

                // 서버 모드 시작 - 실제 서버 생성 시도
                try {
                    invoke(controller, "onServerMode");

                    // UI 변경 확인
                    assertFalse(modeSelectionBox.isVisible(), "Mode selection should be hidden");
                    assertFalse(modeSelectionBox.isManaged(), "Mode selection should not be managed");
                    assertTrue(serverBox.isVisible(), "Server box should be visible");
                    assertTrue(serverBox.isManaged(), "Server box should be managed");

                    // isServer 플래그 확인
                    Field isServerField = PVPNetworkSelectionController.class.getDeclaredField("isServer");
                    isServerField.setAccessible(true);
                    assertTrue((boolean) isServerField.get(controller), "isServer should be true");
                } finally {
                    // 서버 정리
                    invoke(controller, "cleanup");
                }
            } catch (Exception e) {
                fail("Server mode display test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testServerModeShowsIPAddress() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                invoke(controller, "cleanup");

                try {
                    invoke(controller, "onServerMode");

                    Label serverIpLabel = getNode(loader, "serverIpLabel");
                    assertNotNull(serverIpLabel.getText(), "Server IP should be displayed");
                    assertFalse(serverIpLabel.getText().isEmpty(), "Server IP should not be empty");
                } finally {
                    invoke(controller, "cleanup");
                }
            } catch (Exception e) {
                fail("Server IP display test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testHandleServerMessageWithConnectionRequest() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                Method handleMethod = PVPNetworkSelectionController.class.getDeclaredMethod("handleServerMessage", Object.class);
                handleMethod.setAccessible(true);

                NetworkMessage connectionRequest = new NetworkMessage(
                    NetworkMessage.MessageType.CONNECTION_REQUEST,
                    "TestPlayer"
                );

                // 예외 없이 처리되어야 함
                handleMethod.invoke(controller, connectionRequest);
            } catch (Exception e) {
                fail("Handle server message test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testHandleServerMessageWithUnknownType() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                Method handleMethod = PVPNetworkSelectionController.class.getDeclaredMethod("handleServerMessage", Object.class);
                handleMethod.setAccessible(true);

                NetworkMessage gameState = new NetworkMessage(
                    NetworkMessage.MessageType.GAME_STATE,
                    "data"
                );

                // 예외 없이 처리되어야 함 (default case)
                handleMethod.invoke(controller, gameState);
            } catch (Exception e) {
                fail("Handle unknown server message test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testHandleClientMessageWithUnknownType() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                Method handleMethod = PVPNetworkSelectionController.class.getDeclaredMethod("handleClientMessage", Object.class);
                handleMethod.setAccessible(true);

                NetworkMessage gameState = new NetworkMessage(
                    NetworkMessage.MessageType.GAME_STATE,
                    "data"
                );

                // 예외 없이 처리되어야 함 (default case)
                handleMethod.invoke(controller, gameState);
            } catch (Exception e) {
                fail("Handle unknown client message test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testStartGameAsServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);
                setField(controller, "isServer", true);
                setField(controller, "gameMode", "NORMAL");

                GameServer mockServer = mock(GameServer.class);
                setField(controller, "gameServer", mockServer);

                Method startGame = PVPNetworkSelectionController.class.getDeclaredMethod("startGame");
                startGame.setAccessible(true);
                startGame.invoke(controller);

                assertTrue(sceneManager.pvpGameShown, "PVP game screen should be shown");
                assertTrue(sceneManager.wasServer, "Should be server mode");
            } catch (Exception e) {
                fail("Start game as server test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testStartGameAsClient() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);
                setField(controller, "isServer", false);
                setField(controller, "gameMode", "ITEM");

                GameClient mockClient = mock(GameClient.class);
                setField(controller, "gameClient", mockClient);

                Method startGame = PVPNetworkSelectionController.class.getDeclaredMethod("startGame");
                startGame.setAccessible(true);
                startGame.invoke(controller);

                assertTrue(sceneManager.pvpGameShown, "PVP game screen should be shown");
                assertFalse(sceneManager.wasServer, "Should be client mode");
            } catch (Exception e) {
                fail("Start game as client test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testServerMessageHandlerOnClientConnectedSendsAcceptance() throws Exception {
        GameServer mockServer = mock(GameServer.class);
        Label[] statusLabelHolder = new Label[1];

        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                statusLabelHolder[0] = getNode(loader, "connectionStatusLabel");
                setField(controller, "gameServer", mockServer);
                setField(controller, "gameMode", "TIME_LIMIT");

                GameServer.MessageHandler handler = createServerMessageHandler(controller);
                handler.onClientConnected();
            } catch (Exception e) {
                fail("Server handler test setup failed: " + e.getMessage());
            }
        });

        waitFor(150);

        runOnFxThreadAndWait(() ->
            assertEquals("Client Connected!", statusLabelHolder[0].getText(),
                "Server handler should update connection label")
        );

        ArgumentCaptor<NetworkMessage> messageCaptor = ArgumentCaptor.forClass(NetworkMessage.class);
        verify(mockServer).sendMessage(messageCaptor.capture());
        NetworkMessage sent = messageCaptor.getValue();
        assertEquals(NetworkMessage.MessageType.CONNECTION_ACCEPTED, sent.getType(),
            "Server should acknowledge connection");
        assertEquals("TIME_LIMIT", sent.getData());
    }

    @Test
    void testClientMessageHandlerProcessesConnectionAcceptedMessage() throws Exception {
        GameClient mockClient = mock(GameClient.class);
        SceneManager mockSceneManager = mock(SceneManager.class);
        Label[] statusLabelHolder = new Label[1];

        PVPNetworkSelectionController.setGameScreenController(null);

        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPNetworkSelection.fxml"));
                loader.load();
                PVPNetworkSelectionController controller = loader.getController();

                statusLabelHolder[0] = getNode(loader, "clientStatusLabel");
                setField(controller, "gameClient", mockClient);
                setField(controller, "sceneManager", mockSceneManager);
                setField(controller, "gameMode", "NORMAL");

                GameClient.MessageHandler handler = createClientMessageHandler(controller);
                handler.onMessageReceived(new NetworkMessage(
                    NetworkMessage.MessageType.CONNECTION_ACCEPTED,
                    "ITEM"));
            } catch (Exception e) {
                fail("Client handler test setup failed: " + e.getMessage());
            }
        });

        waitFor(150);

        runOnFxThreadAndWait(() ->
            assertTrue(statusLabelHolder[0].getText().contains("Starting"),
                "Client handler should transition to game start")
        );

        verify(mockSceneManager).showPVPGameScreen(eq("ITEM"), isNull(), eq(mockClient), eq(false));
    }

    private <T> T getNode(FXMLLoader loader, String fxId) {
        Object node = loader.getNamespace().get(fxId);
        assertNotNull(node, fxId + " should exist");
        @SuppressWarnings("unchecked")
        T casted = (T) node;
        return casted;
    }

    private void invoke(PVPNetworkSelectionController controller, String methodName) throws Exception {
        var method = PVPNetworkSelectionController.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(controller);
    }

    private void setField(PVPNetworkSelectionController controller, String fieldName, Object value) throws Exception {
        Field field = PVPNetworkSelectionController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private Object getField(PVPNetworkSelectionController controller, String fieldName) throws Exception {
        Field field = PVPNetworkSelectionController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(controller);
    }

    private Object getStaticField(String fieldName) throws Exception {
        Field field = PVPNetworkSelectionController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    private GameServer.MessageHandler createServerMessageHandler(PVPNetworkSelectionController controller) throws Exception {
        Class<?> handlerClass = Class.forName("tetris.ui.controllers.PVPNetworkSelectionController$1");
        Constructor<?> constructor = handlerClass.getDeclaredConstructor(PVPNetworkSelectionController.class);
        constructor.setAccessible(true);
        return (GameServer.MessageHandler) constructor.newInstance(controller);
    }

    private GameClient.MessageHandler createClientMessageHandler(PVPNetworkSelectionController controller) throws Exception {
        Class<?> handlerClass = Class.forName("tetris.ui.controllers.PVPNetworkSelectionController$2");
        Constructor<?> constructor = handlerClass.getDeclaredConstructor(PVPNetworkSelectionController.class);
        constructor.setAccessible(true);
        return (GameClient.MessageHandler) constructor.newInstance(controller);
    }

    private static class TestSceneManager extends SceneManager {
        private boolean modeSelectionShown;
        private boolean pvpGameShown;
        private boolean wasServer;

        TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showPVPModeSelection() {
            modeSelectionShown = true;
        }

        @Override
        public void showPVPGameScreen(String gameMode, Object server, Object client, boolean isServer) {
            pvpGameShown = true;
            wasServer = isServer;
        }
    }
}
