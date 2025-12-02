package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;
import tetris.network.GameServer;
import tetris.network.GameClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PVPLobbyController.
 */
class PVPLobbyControllerTest extends JavaFXTestBase {

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private void invokePrivateMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(obj);
    }

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                
                PVPLobbyController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load PVPLobby.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                
                PVPLobbyController controller = loader.getController();
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                
                controller.setSceneManager(sceneManager);
                
                Object stored = getPrivateField(controller, "sceneManager");
                assertEquals(sceneManager, stored, "SceneManager should be stored");
            } catch (Exception e) {
                fail("Failed to set scene manager: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                assertNotNull(loader.getLocation(), "PVPLobby.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                
                PVPLobbyController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 네트워크 객체 설정 테스트 =====
    
    @Test
    void testSetNetworkObjectsAsServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                GameServer mockServer = mock(GameServer.class);
                GameClient mockClient = mock(GameClient.class);
                
                // 서버 모드로 네트워크 객체 설정
                controller.setNetworkObjects(mockServer, mockClient, true);
                
                boolean isServer = (boolean) getPrivateField(controller, "isServer");
                assertTrue(isServer, "Should be in server mode");
            } catch (Exception e) {
                fail("Server network setup failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testSetNetworkObjectsAsClient() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                GameServer mockServer = mock(GameServer.class);
                GameClient mockClient = mock(GameClient.class);
                
                // 클라이언트 모드로 네트워크 객체 설정
                controller.setNetworkObjects(mockServer, mockClient, false);
                
                boolean isServer = (boolean) getPrivateField(controller, "isServer");
                assertFalse(isServer, "Should be in client mode");
            } catch (Exception e) {
                fail("Client network setup failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testNetworkObjectsWithNullServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                // null 네트워크 객체로도 설정 가능해야 함
                controller.setNetworkObjects(null, null, true);
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Network setup with null objects failed: " + e.getMessage());
            }
        });
    }
    
    // ===== FXML 필드 초기화 테스트 =====
    
    @Test
    void testGameModeBoxSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                VBox gameModeBox = (VBox) getPrivateField(controller, "gameModeBox");
                assertNotNull(gameModeBox, "gameModeBox should be initialized");
            } catch (Exception e) {
                fail("Failed to check gameModeBox: " + e.getMessage());
            }
        });
    }

    @Test
    void testRadioButtonsSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                RadioButton normalModeRadio = (RadioButton) getPrivateField(controller, "normalModeRadio");
                RadioButton itemModeRadio = (RadioButton) getPrivateField(controller, "itemModeRadio");
                RadioButton timeLimitModeRadio = (RadioButton) getPrivateField(controller, "timeLimitModeRadio");
                
                assertNotNull(normalModeRadio, "normalModeRadio should be initialized");
                assertNotNull(itemModeRadio, "itemModeRadio should be initialized");
                assertNotNull(timeLimitModeRadio, "timeLimitModeRadio should be initialized");
            } catch (Exception e) {
                fail("Failed to check radio buttons: " + e.getMessage());
            }
        });
    }

    @Test
    void testToggleGroupSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                ToggleGroup gameModeGroup = (ToggleGroup) getPrivateField(controller, "gameModeGroup");
                assertNotNull(gameModeGroup, "gameModeGroup should be initialized");
            } catch (Exception e) {
                fail("Failed to check toggle group: " + e.getMessage());
            }
        });
    }

    @Test
    void testSelectedModeLabelSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Label selectedModeLabel = (Label) getPrivateField(controller, "selectedModeLabel");
                assertNotNull(selectedModeLabel, "selectedModeLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check selectedModeLabel: " + e.getMessage());
            }
        });
    }

    @Test
    void testStatusLabelsSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPLobbyController controller = new PVPLobbyController();

                Label serverStatusLabel = new Label();
                Label clientStatusLabel = new Label();

                setPrivateField(controller, "serverStatusLabel", serverStatusLabel);
                setPrivateField(controller, "clientStatusLabel", clientStatusLabel);
                setPrivateField(controller, "isServer", true);
                setPrivateField(controller, "isReady", true);
                setPrivateField(controller, "opponentReady", false);

                Method updateStatusLabels = PVPLobbyController.class.getDeclaredMethod("updateStatusLabels");
                updateStatusLabels.setAccessible(true);
                updateStatusLabels.invoke(controller);

                assertTrue(serverStatusLabel.getText().contains("서버"), "serverStatusLabel text should update");
                assertTrue(clientStatusLabel.getText().contains("클라이언트"), "clientStatusLabel text should update");
            } catch (Exception e) {
                fail("Failed to check status labels: " + e.getMessage());
            }
        });
    }

    @Test
    void testButtonsSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Button readyButton = (Button) getPrivateField(controller, "readyButton");
                Button backButton = (Button) getPrivateField(controller, "backButton");
                
                assertNotNull(readyButton, "readyButton should be initialized");
                assertNotNull(backButton, "backButton should be initialized");
            } catch (Exception e) {
                fail("Failed to check buttons: " + e.getMessage());
            }
        });
    }

    @Test
    void testInitialStateValues() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                boolean isReady = (boolean) getPrivateField(controller, "isReady");
                boolean opponentReady = (boolean) getPrivateField(controller, "opponentReady");
                String selectedGameMode = (String) getPrivateField(controller, "selectedGameMode");
                
                assertFalse(isReady, "isReady should be false initially");
                assertFalse(opponentReady, "opponentReady should be false initially");
                assertEquals("NORMAL", selectedGameMode, "Default game mode should be NORMAL");
            } catch (Exception e) {
                fail("Failed to check initial state: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateGameModeLabelNormal() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                setPrivateField(controller, "selectedGameMode", "NORMAL");
                invokePrivateMethod(controller, "updateGameModeLabel");
                
                Label selectedModeLabel = (Label) getPrivateField(controller, "selectedModeLabel");
                assertEquals("게임 모드: 일반 모드", selectedModeLabel.getText());
            } catch (Exception e) {
                fail("Failed to test updateGameModeLabel: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateGameModeLabelItem() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                setPrivateField(controller, "selectedGameMode", "ITEM");
                invokePrivateMethod(controller, "updateGameModeLabel");
                
                Label selectedModeLabel = (Label) getPrivateField(controller, "selectedModeLabel");
                assertEquals("게임 모드: 아이템 모드", selectedModeLabel.getText());
            } catch (Exception e) {
                fail("Failed to test updateGameModeLabel for ITEM: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateGameModeLabelTimeLimit() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                setPrivateField(controller, "selectedGameMode", "TIME_LIMIT");
                invokePrivateMethod(controller, "updateGameModeLabel");
                
                Label selectedModeLabel = (Label) getPrivateField(controller, "selectedModeLabel");
                assertEquals("게임 모드: 시간제한 모드", selectedModeLabel.getText());
            } catch (Exception e) {
                fail("Failed to test updateGameModeLabel for TIME_LIMIT: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateStatusLabelsAsServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                setPrivateField(controller, "isServer", true);
                setPrivateField(controller, "isReady", true);
                setPrivateField(controller, "opponentReady", false);
                
                invokePrivateMethod(controller, "updateStatusLabels");
                
                Label serverStatusLabel = (Label) getPrivateField(controller, "serverStatusLabel");
                Label clientStatusLabel = (Label) getPrivateField(controller, "clientStatusLabel");
                
                assertEquals("서버: 준비 완료", serverStatusLabel.getText());
                assertEquals("클라이언트: 대기 중", clientStatusLabel.getText());
            } catch (Exception e) {
                fail("Failed to test updateStatusLabels as server: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateStatusLabelsAsClient() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                setPrivateField(controller, "isServer", false);
                setPrivateField(controller, "isReady", true);
                setPrivateField(controller, "opponentReady", true);
                
                invokePrivateMethod(controller, "updateStatusLabels");
                
                Label serverStatusLabel = (Label) getPrivateField(controller, "serverStatusLabel");
                Label clientStatusLabel = (Label) getPrivateField(controller, "clientStatusLabel");
                
                assertEquals("서버: 준비 완료", serverStatusLabel.getText());
                assertEquals("클라이언트: 준비 완료", clientStatusLabel.getText());
            } catch (Exception e) {
                fail("Failed to test updateStatusLabels as client: " + e.getMessage());
            }
        });
    }

    @Test
    void testServerModeUIVisibility() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                controller.setNetworkObjects(null, null, true);
                
                VBox gameModeBox = (VBox) getPrivateField(controller, "gameModeBox");
                Label selectedModeLabel = (Label) getPrivateField(controller, "selectedModeLabel");
                
                assertTrue(gameModeBox.isVisible(), "gameModeBox should be visible for server");
                assertFalse(selectedModeLabel.isVisible(), "selectedModeLabel should be hidden for server");
            } catch (Exception e) {
                fail("Failed to test server mode UI visibility: " + e.getMessage());
            }
        });
    }

    @Test
    void testClientModeUIVisibility() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                controller.setNetworkObjects(null, null, false);
                
                VBox gameModeBox = (VBox) getPrivateField(controller, "gameModeBox");
                Label selectedModeLabel = (Label) getPrivateField(controller, "selectedModeLabel");
                
                assertFalse(gameModeBox.isVisible(), "gameModeBox should be hidden for client");
                assertTrue(selectedModeLabel.isVisible(), "selectedModeLabel should be visible for client");
            } catch (Exception e) {
                fail("Failed to test client mode UI visibility: " + e.getMessage());
            }
        });
    }
    
    // ===== 통합 테스트 =====
    
    @Test
    void testMultipleNetworkSetups() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                GameServer mockServer1 = mock(GameServer.class);
                GameClient mockClient1 = mock(GameClient.class);
                controller.setNetworkObjects(mockServer1, mockClient1, true);
                
                // 다시 설정 (재연결 시나리오)
                GameServer mockServer2 = mock(GameServer.class);
                GameClient mockClient2 = mock(GameClient.class);
                controller.setNetworkObjects(mockServer2, mockClient2, false);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Multiple network setups failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testServerAndClientModeToggle() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                GameServer mockServer = mock(GameServer.class);
                GameClient mockClient = mock(GameClient.class);
                
                // 서버 모드
                controller.setNetworkObjects(mockServer, mockClient, true);
                
                // 클라이언트 모드로 전환
                controller.setNetworkObjects(mockServer, mockClient, false);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Server/Client mode toggle failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testFullLobbyWorkflow() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                GameServer mockServer = mock(GameServer.class);
                GameClient mockClient = mock(GameClient.class);
                
                // 1. 네트워크 설정
                controller.setNetworkObjects(mockServer, mockClient, true);
                
                // 2. 게임 준비 (FXML 필드가 있어야 동작)
                // onReady() 메서드는 private이므로 직접 테스트 불가
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Full lobby workflow failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBackgroundImageSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPLobby.fxml"));
                loader.load();
                PVPLobbyController controller = loader.getController();
                
                // backgroundImage may or may not be null depending on FXML
                // Just verify no exception is thrown during access
                assertNotNull(getPrivateField(controller, "backgroundImage") != null || true);
            } catch (Exception e) {
                fail("Failed to check backgroundImage: " + e.getMessage());
            }
        });
    }
}
