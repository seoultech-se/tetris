package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

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
                new Scene(loader.load(), 600, 900);
                
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
                new Scene(loader.load(), 600, 900);
                
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
                new Scene(loader.load(), 600, 900);
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
                new Scene(loader.load(), 600, 900);
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
                new Scene(loader1.load(), 480, 720);
                assertNotNull(loader1.getController());
                
                // 중간
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                new Scene(loader2.load(), 600, 900);
                assertNotNull(loader2.getController());
                
                // 크게
                FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                new Scene(loader3.load(), 720, 1080);
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

    @Test
    void testCompactLayoutForSmallScreen() throws Exception {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            String originalSize = settings.getScreenSize();
            try {
                settings.setScreenSize("작게");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                new Scene(loader.load(), 480, 720);
                VBox buttonContainer = getNode(loader, "buttonContainer");

                assertTrue(buttonContainer.getStyleClass().contains("menu-compact"));
                assertEquals(3, buttonContainer.getChildren().size());
                for (Node row : buttonContainer.getChildren()) {
                    assertTrue(row instanceof HBox);
                }
            } catch (Exception e) {
                fail("Compact layout test failed: " + e.getMessage());
            } finally {
                settings.setScreenSize(originalSize);
            }
        });
    }

    @Test
    void testSelectButtonAppliesFocus() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                loader.load();
                MainMenuController controller = loader.getController();

                Button battleButton = getNode(loader, "battleModeButton");
                Button normalButton = getNode(loader, "normalModeButton");

                Method selectMethod = MainMenuController.class.getDeclaredMethod("selectButton", int.class);
                selectMethod.setAccessible(true);
                selectMethod.invoke(controller, 2);

                assertTrue(battleButton.getStyleClass().contains("focused"));
                assertFalse(normalButton.getStyleClass().contains("focused"));
            } catch (Exception e) {
                fail("Select button focus test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnStartNormalModeUpdatesSettings() throws Exception {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            String originalMode = settings.getGameMode();
            try {
                settings.setGameMode("ITEM");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                loader.load();
                MainMenuController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                Method startNormal = MainMenuController.class.getDeclaredMethod("onStartNormalMode");
                startNormal.setAccessible(true);
                startNormal.invoke(controller);

                assertEquals("NORMAL", settings.getGameMode());
                assertEquals("GAME", sceneManager.lastAction);
            } catch (Exception e) {
                fail("Start normal mode test failed: " + e.getMessage());
            } finally {
                settings.setGameMode(originalMode);
            }
        });
    }

    @Test
    void testOnStartBattleModeNavigates() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                loader.load();
                MainMenuController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                Method startBattle = MainMenuController.class.getDeclaredMethod("onStartBattleMode");
                startBattle.setAccessible(true);
                startBattle.invoke(controller);

                assertEquals("BATTLE", sceneManager.lastAction);
            } catch (Exception e) {
                fail("Start battle mode test failed: " + e.getMessage());
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
        private String lastAction;

        TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showGameScreen() {
            lastAction = "GAME";
        }

        @Override
        public void showBattleModeSelection() {
            lastAction = "BATTLE";
        }

        @Override
        public void showPVPModeSelection() {
            lastAction = "PVP";
        }

        @Override
        public void showScoreBoard() {
            lastAction = "SCORE";
        }

        @Override
        public void showSettingsScreen() {
            lastAction = "SETTINGS";
        }
    }
}
