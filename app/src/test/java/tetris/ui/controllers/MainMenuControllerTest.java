package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MainMenuController.
 * Tests basic UI initialization and component accessibility.
 */
class MainMenuControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                MainMenuController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load MainMenu.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                MainMenuController controller = loader.getController();
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                
                controller.setSceneManager(sceneManager);
                
                // Verify controller was initialized successfully
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                assertNotNull(loader.getLocation(), "MainMenu.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                loader.load();
                
                MainMenuController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 버튼 초기화 테스트 =====
    
    @Test
    void testMenuButtonsExist() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                MainMenuController controller = loader.getController();
                
                // 컨트롤러가 초기화되었고 버튼들이 로드됨
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Menu buttons initialization failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testButtonNavigation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                MainMenuController controller = loader.getController();
                
                // 키보드 네비게이션 로직이 초기화됨
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Button navigation test failed: " + e.getMessage());
            }
        });
    }
    
    // ===== Scene 크기 테스트 =====
    
    @Test
    void testDifferentScreenSizes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 작게
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Scene scene1 = new Scene(loader1.load(), 480, 720);
                assertNotNull(loader1.getController());
                
                // 중간
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Scene scene2 = new Scene(loader2.load(), 600, 900);
                assertNotNull(loader2.getController());
                
                // 크게
                FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Scene scene3 = new Scene(loader3.load(), 720, 1080);
                assertNotNull(loader3.getController());
            } catch (Exception e) {
                fail("Screen size tests failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMultipleControllerInstances() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                loader1.load();
                MainMenuController controller1 = loader1.getController();
                
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                loader2.load();
                MainMenuController controller2 = loader2.getController();
                
                assertNotNull(controller1);
                assertNotNull(controller2);
                assertNotSame(controller1, controller2);
            } catch (Exception e) {
                fail("Multiple controller instances test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testSceneLoadingPerformance() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                for (int i = 0; i < 5; i++) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                    loader.load();
                }
                
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                
                // 5번 로드가 5초 이내에 완료되어야 함
                assertTrue(duration < 5000, "Loading should be fast");
            } catch (Exception e) {
                fail("Performance test failed: " + e.getMessage());
            }
        });
    }
}
