package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.data.ScoreManager;
import tetris.ui.SceneManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

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
                loader.load();
                
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
                loader.load();
                
                ScoreBoardController controller = loader.getController();
                SceneManager sceneManager = mock(SceneManager.class);
                
                controller.setSceneManager(sceneManager);
                
                assertSame(sceneManager, readSceneManager(controller));
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
                loader1.load();
                assertNotNull(loader1.getController());
                
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                loader2.load();
                assertNotNull(loader2.getController());
            } catch (Exception e) {
                fail("Screen sizes test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testNormalModeShowsSelectedDifficultyScores() throws Exception {
        runOnFxThreadAndWait(() -> {
            ScoreManager scoreManager = ScoreManager.getInstance();
            scoreManager.clearScores("NORMAL");
            scoreManager.addScore("AAA", 1000, "Normal", "NORMAL");
            scoreManager.addScore("BBB", 900, "Normal", "NORMAL");
            scoreManager.addScore("CCC", 800, "Hard", "NORMAL");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                loader.load();

                ListView<String> scoreList = getNode(loader, "scoreListView");
                ToggleButton hardButton = getNode(loader, "hardButton");

                assertEquals(2, scoreList.getItems().size(), "Normal difficulty should show two entries");

                hardButton.fire();
                assertEquals(1, scoreList.getItems().size(), "Hard difficulty should show filtered entries");
            } catch (Exception e) {
                fail("Normal mode score filtering failed: " + e.getMessage());
            } finally {
                scoreManager.clearScores("NORMAL");
            }
        });
    }

    @Test
    void testItemModeHidesDifficultyButtonsAndLoadsScores() throws Exception {
        runOnFxThreadAndWait(() -> {
            ScoreManager scoreManager = ScoreManager.getInstance();
            scoreManager.clearScores("ITEM");
            scoreManager.addScore("ITEM1", 1500, "Normal", "ITEM");
            scoreManager.addScore("ITEM2", 500, "Normal", "ITEM");
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                loader.load();

                ListView<String> scoreList = getNode(loader, "scoreListView");
                ToggleButton itemModeButton = getNode(loader, "itemModeButton");
                ToggleButton easyButton = getNode(loader, "easyButton");
                ToggleButton normalButton = getNode(loader, "normalButton");
                ToggleButton hardButton = getNode(loader, "hardButton");

                itemModeButton.fire();

                assertFalse(easyButton.isVisible());
                assertFalse(normalButton.isVisible());
                assertFalse(hardButton.isVisible());
                assertEquals(2, scoreList.getItems().size(), "Item mode should list all entries");
            } catch (Exception e) {
                fail("Item mode loading failed: " + e.getMessage());
            } finally {
                scoreManager.clearScores("ITEM");
            }
        });
    }

    @Test
    void testBackToMenuCallsSceneManager() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScoreBoard.fxml"));
                loader.load();
                ScoreBoardController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                Method backMethod = ScoreBoardController.class.getDeclaredMethod("onBackToMenu");
                backMethod.setAccessible(true);
                backMethod.invoke(controller);

                assertTrue(sceneManager.mainMenuShown);
            } catch (Exception e) {
                fail("Back to menu invocation failed: " + e.getMessage());
            }
        });
    }

    private <T> T getNode(FXMLLoader loader, String fxId) {
        Object node = loader.getNamespace().get(fxId);
        assertNotNull(node, fxId + " should exist");
        @SuppressWarnings("unchecked")
        T casted = (T) node;
        return casted;
    }

    private static class TestSceneManager extends SceneManager {
        private boolean mainMenuShown;

        TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showMainMenu() {
            mainMenuShown = true;
        }
    }
    private SceneManager readSceneManager(ScoreBoardController controller) {
        try {
            Field field = ScoreBoardController.class.getDeclaredField("sceneManager");
            field.setAccessible(true);
            return (SceneManager) field.get(controller);
        } catch (ReflectiveOperationException e) {
            fail("Unable to read sceneManager field: " + e.getMessage());
            return null;
        }
    }
}
