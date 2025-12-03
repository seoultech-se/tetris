package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BattleModeSelectionController.
 */
class BattleModeSelectionControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                loader.load();
                
                BattleModeSelectionController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load BattleModeSelection.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                loader.load();
                
                BattleModeSelectionController controller = loader.getController();
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                assertNotNull(loader.getLocation(), "BattleModeSelection.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                loader.load();
                
                BattleModeSelectionController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 다중 인스턴스 테스트 =====
    
    @Test
    void testMultipleControllerInstances() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                loader1.load();
                BattleModeSelectionController controller1 = loader1.getController();
                
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                loader2.load();
                BattleModeSelectionController controller2 = loader2.getController();
                
                assertNotNull(controller1);
                assertNotNull(controller2);
                assertNotSame(controller1, controller2);
            } catch (Exception e) {
                fail("Multiple instances test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testDifferentScreenSizes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                new Scene(loader1.load(), 480, 720);
                assertNotNull(loader1.getController());
                
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                new Scene(loader2.load(), 600, 900);
                assertNotNull(loader2.getController());
                
                FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                new Scene(loader3.load(), 720, 1080);
                assertNotNull(loader3.getController());
            } catch (Exception e) {
                fail("Screen size tests failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testRepeatedLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                    loader.load();
                    assertNotNull(loader.getController());
                }
            } catch (Exception e) {
                fail("Repeated loading test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testNormalBattleNavigates() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                loader.load();
                BattleModeSelectionController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                invoke(controller, "onNormalBattle");
                assertEquals("NORMAL", sceneManager.lastBattleMode);
                assertFalse(sceneManager.vsComputer);
            } catch (Exception e) {
                fail("Normal battle navigation failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBackButtonReturnsToMenu() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleModeSelection.fxml"));
                loader.load();
                BattleModeSelectionController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                invoke(controller, "onBack");
                assertTrue(sceneManager.mainMenuShown);
            } catch (Exception e) {
                fail("Back button navigation failed: " + e.getMessage());
            }
        });
    }

    private void invoke(BattleModeSelectionController controller, String methodName) throws Exception {
        var method = BattleModeSelectionController.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(controller);
    }

    private static class TestSceneManager extends SceneManager {
        private String lastBattleMode;
        private boolean vsComputer;
        private boolean mainMenuShown;

        TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showBattleGameScreen(String battleMode) {
            lastBattleMode = battleMode;
            vsComputer = false;
        }

        // @Override
        // public void showBattleGameScreenAgainstComputer(String battleMode) {
        //     lastBattleMode = battleMode;
        //     vsComputer = true;
        // }

        @Override
        public void showMainMenu() {
            mainMenuShown = true;
        }
    }
}
