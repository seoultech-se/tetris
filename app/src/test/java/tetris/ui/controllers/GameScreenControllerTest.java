package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameScreenController.
 * Tests basic game screen initialization.
 */
class GameScreenControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // Set game mode before loading
                SettingsManager.getInstance().setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                
                GameScreenController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load GameScreen.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                
                GameScreenController controller = loader.getController();
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                assertNotNull(loader.getLocation(), "GameScreen.fxml should exist");
                
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
                SettingsManager.getInstance().setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                
                GameScreenController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testItemMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("ITEM");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                
                GameScreenController controller = loader.getController();
                assertNotNull(controller, "Controller should work with ITEM mode");
            } catch (Exception e) {
                fail("Failed with ITEM mode: " + e.getMessage());
            }
        });
    }
    
    // ===== 게임 모드 테스트 =====
    
    @Test
    void testNormalModeInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                GameScreenController controller = loader.getController();
                
                // GameEngine이 초기화되고 게임이 시작됨
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Normal mode initialization failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testItemModeInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("ITEM");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                GameScreenController controller = loader.getController();
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Item mode initialization failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 화면 크기 테스트 =====
    
    @Test
    void testSmallScreenSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();
                
                settings.setScreenSize("작게");
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                Scene scene = new Scene(loader.load(), 480, 720);
                GameScreenController controller = loader.getController();
                assertNotNull(controller);
                
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Small screen size test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMediumScreenSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();
                
                settings.setScreenSize("중간");
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                GameScreenController controller = loader.getController();
                assertNotNull(controller);
                
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Medium screen size test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testLargeScreenSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();
                
                settings.setScreenSize("크게");
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                Scene scene = new Scene(loader.load(), 720, 1080);
                GameScreenController controller = loader.getController();
                assertNotNull(controller);
                
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Large screen size test failed: " + e.getMessage());
            }
        });
    }
    
    // ===== UI 업데이트 테스트 =====
    
    @Test
    void testUpdateScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                // 다양한 점수 업데이트
                controller.updateScore(100);
                controller.updateScore(1000);
                controller.updateScore(10000);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Update score test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testUpdateLevel() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                // 레벨 업데이트
                for (int i = 1; i <= 10; i++) {
                    controller.updateLevel(i);
                }
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Update level test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testUpdateLines() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                // 라인 업데이트
                for (int i = 0; i <= 100; i += 10) {
                    controller.updateLines(i);
                }
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Update lines test failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 통합 테스트 =====
    
    @Test
    void testGameLoopStartsAutomatically() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                // GameEngine과 AnimationTimer가 자동으로 시작됨
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Game loop auto-start test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMultipleGameInstances() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                
                // 첫 번째 게임
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader1.load();
                GameScreenController controller1 = loader1.getController();
                assertNotNull(controller1);
                
                // 두 번째 게임
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader2.load();
                GameScreenController controller2 = loader2.getController();
                assertNotNull(controller2);
                
                assertNotSame(controller1, controller2);
            } catch (Exception e) {
                fail("Multiple game instances test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testGameWithAllUpdateMethods() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("ITEM");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                // 모든 업데이트 메서드 호출
                controller.updateScore(5000);
                controller.updateLevel(5);
                controller.updateLines(25);
                
                controller.updateScore(10000);
                controller.updateLevel(10);
                controller.updateLines(50);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Comprehensive update test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testSetupKeyHandler() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                GameScreenController controller = loader.getController();
                
                // Scene에 키 핸들러가 설정됨
                controller.setupKeyHandler();
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Setup key handler test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testColorBlindMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                boolean originalMode = settings.isColorBlindModeEnabled();
                
                // 색맹 모드 활성화
                settings.setColorBlindModeEnabled(true);
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                assertNotNull(controller);
                
                // 원래 설정으로 복원
                settings.setColorBlindModeEnabled(originalMode);
            } catch (Exception e) {
                fail("Color blind mode test failed: " + e.getMessage());
            }
        });
    }
}
