package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;
import tetris.network.GameServer;
import tetris.network.GameClient;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PVPLobbyController.
 */
class PVPLobbyControllerTest extends JavaFXTestBase {

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
                assertNotNull(controller);
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
                assertNotNull(controller);
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
}
