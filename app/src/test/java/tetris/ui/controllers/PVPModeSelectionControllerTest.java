package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PVPModeSelectionController.
 */
class PVPModeSelectionControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load PVPModeSelection.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                assertNotNull(loader.getLocation(), "PVPModeSelection.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMultipleInstances() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                    loader.load();
                    assertNotNull(loader.getController());
                }
            } catch (Exception e) {
                fail("Multiple instances test failed: " + e.getMessage());
            }
        });
    }
}
