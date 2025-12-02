package tetris.ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.controllers.JavaFXTestBase;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SceneManager.
 * Tests basic initialization and field setup.
 */
class SceneManagerTest extends JavaFXTestBase {

    private static Object getStaticField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    @Test
    void testConstructor() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                SceneManager sceneManager = new SceneManager(stage);
                
                assertNotNull(sceneManager, "SceneManager should be created");
                assertEquals(stage, sceneManager.getPrimaryStage(), "PrimaryStage should be stored");
            } catch (Exception e) {
                fail("Constructor test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGetPrimaryStage() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                SceneManager sceneManager = new SceneManager(stage);
                
                Stage result = sceneManager.getPrimaryStage();
                assertSame(stage, result, "getPrimaryStage should return the original stage");
            } catch (Exception e) {
                fail("getPrimaryStage test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetupStage() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                new SceneManager(stage);
                
                // Stage should be configured
                assertEquals("Tetris Game", stage.getTitle(), "Stage title should be set");
                assertFalse(stage.isResizable(), "Stage should not be resizable");
            } catch (Exception e) {
                fail("setupStage test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testScreenSizeConstantsSmall() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                double smallWidth = (double) getStaticField(SceneManager.class, "SMALL_WIDTH");
                double smallHeight = (double) getStaticField(SceneManager.class, "SMALL_HEIGHT");
                
                assertEquals(480.0, smallWidth, "SMALL_WIDTH should be 480");
                assertEquals(720.0, smallHeight, "SMALL_HEIGHT should be 720");
            } catch (Exception e) {
                fail("Screen size constants test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testScreenSizeConstantsMedium() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                double mediumWidth = (double) getStaticField(SceneManager.class, "MEDIUM_WIDTH");
                double mediumHeight = (double) getStaticField(SceneManager.class, "MEDIUM_HEIGHT");
                
                assertEquals(600.0, mediumWidth, "MEDIUM_WIDTH should be 600");
                assertEquals(900.0, mediumHeight, "MEDIUM_HEIGHT should be 900");
            } catch (Exception e) {
                fail("Screen size constants test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testScreenSizeConstantsLarge() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                double largeWidth = (double) getStaticField(SceneManager.class, "LARGE_WIDTH");
                double largeHeight = (double) getStaticField(SceneManager.class, "LARGE_HEIGHT");
                
                assertEquals(720.0, largeWidth, "LARGE_WIDTH should be 720");
                assertEquals(1080.0, largeHeight, "LARGE_HEIGHT should be 1080");
            } catch (Exception e) {
                fail("Screen size constants test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testMultipleSceneManagerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage1 = new Stage();
                Stage stage2 = new Stage();
                
                SceneManager sm1 = new SceneManager(stage1);
                SceneManager sm2 = new SceneManager(stage2);
                
                assertNotSame(sm1.getPrimaryStage(), sm2.getPrimaryStage(), 
                    "Different SceneManagers should have different stages");
            } catch (Exception e) {
                fail("Multiple SceneManager creation test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testStageNotNull() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                SceneManager sceneManager = new SceneManager(stage);
                
                assertNotNull(sceneManager.getPrimaryStage(), "Stage should not be null");
            } catch (Exception e) {
                fail("Stage not null test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testStageCloseRequestHandler() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                new SceneManager(stage);
                
                // Close request handler should be set (not null)
                assertNotNull(stage.getOnCloseRequest(), 
                    "Close request handler should be set");
            } catch (Exception e) {
                fail("Stage close request handler test failed: " + e.getMessage());
            }
        });
    }
}
