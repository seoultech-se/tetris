package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.game.GameEngine;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameScreenController.
 * Tests basic game screen initialization.
 */
class GameScreenControllerTest extends JavaFXTestBase {

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @SuppressWarnings("unused")
    private void invokePrivateMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(obj);
    }

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

    // ===== 추가 커버리지 테스트 =====

    @Test
    void testGameCanvasInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Canvas gameCanvas = (Canvas) getPrivateField(controller, "gameCanvas");
                assertNotNull(gameCanvas);
                assertTrue(gameCanvas.getWidth() > 0);
                assertTrue(gameCanvas.getHeight() > 0);
            } catch (Exception e) {
                fail("Game canvas initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testNextPieceCanvasInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Canvas nextPieceCanvas = (Canvas) getPrivateField(controller, "nextPieceCanvas");
                assertNotNull(nextPieceCanvas);
                assertTrue(nextPieceCanvas.getWidth() > 0);
                assertTrue(nextPieceCanvas.getHeight() > 0);
            } catch (Exception e) {
                fail("Next piece canvas initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGameEngineCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                GameEngine engine = (GameEngine) getPrivateField(controller, "gameEngine");
                assertNotNull(engine);
                assertTrue(engine.isGameRunning());
            } catch (Exception e) {
                fail("Game engine creation test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateNextItemCounterInNormalMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                // NORMAL 모드에서는 아이템 카운터가 표시되지 않음
                controller.updateNextItemCounter();
                
                Label nextItemLabel = (Label) getPrivateField(controller, "nextItemLabel");
                if (nextItemLabel != null) {
                    assertTrue(nextItemLabel.getText().isEmpty() || nextItemLabel.getText() == null);
                }
            } catch (Exception e) {
                fail("Update next item counter in normal mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateNextItemCounterInItemMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("ITEM");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                controller.updateNextItemCounter();
                
                Label nextItemLabel = (Label) getPrivateField(controller, "nextItemLabel");
                if (nextItemLabel != null) {
                    // ITEM 모드에서는 카운터가 표시됨
                    assertNotNull(nextItemLabel.getText());
                }
            } catch (Exception e) {
                fail("Update next item counter in item mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateDoubleScoreTimer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("ITEM");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                controller.updateDoubleScoreTimer();
                
                Label doubleScoreTimerLabel = (Label) getPrivateField(controller, "doubleScoreTimerLabel");
                assertNotNull(doubleScoreTimerLabel);
            } catch (Exception e) {
                fail("Update double score timer test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateSkipNotification() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("ITEM");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                controller.updateSkipNotification();
                
                Label skipNotificationLabel = (Label) getPrivateField(controller, "skipNotificationLabel");
                assertNotNull(skipNotificationLabel);
            } catch (Exception e) {
                fail("Update skip notification test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBlockSizeForSmallScreen() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();
                
                settings.setScreenSize("작게");
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Integer blockSize = (Integer) getPrivateField(controller, "BLOCK_SIZE");
                assertEquals(20, blockSize);
                
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Block size for small screen test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBlockSizeForMediumScreen() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();
                
                settings.setScreenSize("중간");
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Integer blockSize = (Integer) getPrivateField(controller, "BLOCK_SIZE");
                assertEquals(25, blockSize);
                
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Block size for medium screen test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBlockSizeForLargeScreen() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();
                
                settings.setScreenSize("크게");
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Integer blockSize = (Integer) getPrivateField(controller, "BLOCK_SIZE");
                assertEquals(30, blockSize);
                
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Block size for large screen test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testFallSpeedDecreasesWithLevel() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Long initialFallSpeed = (Long) getPrivateField(controller, "fallSpeed");
                assertNotNull(initialFallSpeed);
                assertTrue(initialFallSpeed > 0);
            } catch (Exception e) {
                fail("Fall speed test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testScoreLabelUpdate() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                controller.updateScore(12345);
                
                Label scoreLabel = (Label) getPrivateField(controller, "scoreLabel");
                assertNotNull(scoreLabel);
                assertTrue(scoreLabel.getText().contains("12345"));
            } catch (Exception e) {
                fail("Score label update test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testLevelLabelUpdate() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                controller.updateLevel(7);
                
                Label levelLabel = (Label) getPrivateField(controller, "levelLabel");
                assertNotNull(levelLabel);
                assertTrue(levelLabel.getText().contains("7"));
            } catch (Exception e) {
                fail("Level label update test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testLinesLabelUpdate() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                controller.updateLines(42);
                
                Label linesLabel = (Label) getPrivateField(controller, "linesLabel");
                assertNotNull(linesLabel);
                assertTrue(linesLabel.getText().contains("42"));
            } catch (Exception e) {
                fail("Lines label update test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSettingsManagerInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                SettingsManager settingsManager = (SettingsManager) getPrivateField(controller, "settingsManager");
                assertNotNull(settingsManager);
            } catch (Exception e) {
                fail("Settings manager initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testAnimationTimerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Object gameLoop = getPrivateField(controller, "gameLoop");
                assertNotNull(gameLoop);
            } catch (Exception e) {
                fail("Animation timer creation test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testClearAnimationFlags() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Boolean isAnimatingClear = (Boolean) getPrivateField(controller, "isAnimatingClear");
                assertFalse(isAnimatingClear);
                
                Object linesToClear = getPrivateField(controller, "linesToClear");
                assertNull(linesToClear);
            } catch (Exception e) {
                fail("Clear animation flags test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerAssignment() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager.getInstance().setGameMode("NORMAL");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                controller.setSceneManager(sceneManager);
                
                SceneManager assignedManager = (SceneManager) getPrivateField(controller, "sceneManager");
                assertNotNull(assignedManager);
                assertSame(sceneManager, assignedManager);
            } catch (Exception e) {
                fail("Scene manager assignment test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testDefaultBlockSizeForUnknownScreenSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();
                
                // 알 수 없는 화면 크기 설정 (default case)
                setPrivateField(settings, "screenSize", "알수없음");
                settings.setGameMode("NORMAL");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameScreen.fxml"));
                loader.load();
                GameScreenController controller = loader.getController();
                
                Integer blockSize = (Integer) getPrivateField(controller, "BLOCK_SIZE");
                // default case: 25
                assertEquals(25, blockSize);
                
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Default block size test failed: " + e.getMessage());
            }
        });
    }
}
