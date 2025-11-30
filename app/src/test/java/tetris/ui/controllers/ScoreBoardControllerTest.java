package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ScoreBoardController.
 * Tests score board UI initialization.
 */
class ScoreBoardControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                ScoreBoardController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load ScoreBoard.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                Scene scene = new Scene(loader.load(), 600, 900);
                
                ScoreBoardController controller = loader.getController();
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                assertNotNull(loader.getLocation(), "ScoreBoard.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                loader.load();
                
                ScoreBoardController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMultipleLoads() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                    loader.load();
                    assertNotNull(loader.getController());
                }
            } catch (Exception e) {
                fail("Multiple loads failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testDifferentScreenSizes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                Scene scene1 = new Scene(loader1.load(), 480, 720);
                assertNotNull(loader1.getController());
                
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                Scene scene2 = new Scene(loader2.load(), 720, 1080);
                assertNotNull(loader2.getController());
            } catch (Exception e) {
                fail("Screen sizes test failed: " + e.getMessage());
            }
        });
    }
}
