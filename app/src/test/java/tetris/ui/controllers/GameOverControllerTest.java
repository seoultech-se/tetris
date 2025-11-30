package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameOverController.
 * Tests game over screen initialization and score display.
 */
class GameOverControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load GameOverScreen.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
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
    void testSetFinalScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
                
                // Test setting final score
                int testScore = 5000;
                controller.setFinalScore(testScore);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set final score: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                assertNotNull(loader.getLocation(), "GameOverScreen.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                
                GameOverController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testMultipleScoreValues() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
                
                // Test various score values
                int[] testScores = {0, 100, 1000, 10000, 99999};
                for (int score : testScores) {
                    controller.setFinalScore(score);
                    // Just verify no exception is thrown
                }
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to handle multiple scores: " + e.getMessage());
            }
        });
    }
    
    // ===== 추가 점수 테스트 =====
    
    @Test
    void testZeroScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();
                
                controller.setFinalScore(0);
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Zero score test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMaxScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();
                
                controller.setFinalScore(Integer.MAX_VALUE);
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Max score test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testRepeatedScoreSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();
                
                // 여러 번 점수 설정
                for (int i = 0; i < 10; i++) {
                    controller.setFinalScore(i * 1000);
                }
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Repeated score setting failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testDifferentScreenSizes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 다양한 화면 크기에서 로드
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                Scene scene1 = new Scene(loader1.load(), 480, 720);
                assertNotNull(loader1.getController());
                
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                Scene scene2 = new Scene(loader2.load(), 600, 900);
                assertNotNull(loader2.getController());
                
                FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                Scene scene3 = new Scene(loader3.load(), 720, 1080);
                assertNotNull(loader3.getController());
            } catch (Exception e) {
                fail("Screen size tests failed: " + e.getMessage());
            }
        });
    }
}
