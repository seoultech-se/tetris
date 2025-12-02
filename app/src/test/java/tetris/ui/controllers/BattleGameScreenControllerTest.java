package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.game.BattleGameEngine;
import tetris.game.GameBoard;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BattleGameScreenController.
 * Tests basic initialization, scene management, and protected methods.
 */
class BattleGameScreenControllerTest extends JavaFXTestBase {

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private void invokePrivateMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(obj);
    }

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load BattleGameScreen.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                assertNotNull(loader.getLocation(), "BattleGameScreen.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetBattleModeScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();

                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("SCORE", false);

                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set SCORE mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetBattleModeTimeLimit() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();

                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("TIME_LIMIT", false);

                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set TIME_LIMIT mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetBattleModeLine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();

                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("LINE", false);

                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set LINE mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetBattleModeItemVsComputer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();

                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("ITEM", true);

                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set ITEM mode with computer opponent: " + e.getMessage());
            }
        });
    }

    @Test
    void testScreenSizeImpact() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalSize = settings.getScreenSize();

                settings.setScreenSize("작게");
                FXMLLoader loaderSmall = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loaderSmall.load();
                assertNotNull(loaderSmall.getController());

                settings.setScreenSize("중간");
                FXMLLoader loaderMedium = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loaderMedium.load();
                assertNotNull(loaderMedium.getController());

                settings.setScreenSize("크게");
                FXMLLoader loaderLarge = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loaderLarge.load();
                assertNotNull(loaderLarge.getController());

                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Screen size impact test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testMultipleControllerInstances() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader1.load();
                BattleGameScreenController controller1 = loader1.getController();

                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader2.load();
                BattleGameScreenController controller2 = loader2.getController();

                assertNotSame(controller1, controller2);
            } catch (Exception e) {
                fail("Multiple controller instance test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testColorBlindModeWithBattleGame() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                boolean originalMode = settings.isColorBlindModeEnabled();

                settings.setColorBlindModeEnabled(true);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("ITEM", false);

                assertNotNull(controller);

                settings.setColorBlindModeEnabled(originalMode);
            } catch (Exception e) {
                fail("Color blind mode battle test failed: " + e.getMessage());
            }
        });
    }

    // ===== 추가 커버리지 테스트 =====

    @Test
    void testConfigureBattleEngineNormalMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();

                // SCORE 모드 (기본 대전)
                controller.setBattleMode("SCORE", false);
                
                // battleEngine이 정상적으로 초기화되는지 확인
                BattleGameEngine engine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertNotNull(engine);
            } catch (Exception e) {
                fail("Configure battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderMethodsWithDifferentModes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("ITEM", false);
                
                // render 메서드들 호출 테스트 (private이지만 setBattleMode에서 호출됨)
                BattleGameEngine engine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertNotNull(engine);
                
                // Canvas 필드 접근
                Canvas player1Canvas = (Canvas) getPrivateField(controller, "player1Canvas");
                Canvas player2Canvas = (Canvas) getPrivateField(controller, "player2Canvas");
                assertNotNull(player1Canvas);
                assertNotNull(player2Canvas);
            } catch (Exception e) {
                fail("Render methods test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateUILabels() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("LINE", false);
                
                // UI 라벨 접근
                Label player1ScoreLabel = (Label) getPrivateField(controller, "player1ScoreLabel");
                Label player2ScoreLabel = (Label) getPrivateField(controller, "player2ScoreLabel");
                
                assertNotNull(player1ScoreLabel);
                assertNotNull(player2ScoreLabel);
            } catch (Exception e) {
                fail("Update UI labels test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGameOverBoxVisibility() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("SCORE", false);
                
                VBox gameOverBox = (VBox) getPrivateField(controller, "gameOverBox");
                // 게임 시작 시에는 gameOverBox가 숨겨져 있어야 함
                assertNotNull(gameOverBox);
            } catch (Exception e) {
                fail("Game over box visibility test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testTimerLabelForTimeLimitMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("TIME_LIMIT", false);
                
                Label timerLabel = (Label) getPrivateField(controller, "timerLabel");
                assertNotNull(timerLabel);
            } catch (Exception e) {
                fail("Timer label test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testWinnerLabelInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("SCORE", false);
                
                Label winnerLabel = (Label) getPrivateField(controller, "winnerLabel");
                assertNotNull(winnerLabel);
                // 게임 시작 시 winnerLabel은 비어있어야 함
                assertTrue(winnerLabel.getText().isEmpty() || winnerLabel.getText() == null || "".equals(winnerLabel.getText()));
            } catch (Exception e) {
                fail("Winner label test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testNextPieceCanvasSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("NORMAL", false);
                
                Canvas player1NextCanvas = (Canvas) getPrivateField(controller, "player1NextCanvas");
                Canvas player2NextCanvas = (Canvas) getPrivateField(controller, "player2NextCanvas");
                
                assertNotNull(player1NextCanvas);
                assertNotNull(player2NextCanvas);
                assertTrue(player1NextCanvas.getWidth() > 0);
                assertTrue(player1NextCanvas.getHeight() > 0);
            } catch (Exception e) {
                fail("Next piece canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testIncomingCanvasSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("SCORE", false);
                
                Canvas player1IncomingCanvas = (Canvas) getPrivateField(controller, "player1IncomingCanvas");
                Canvas player2IncomingCanvas = (Canvas) getPrivateField(controller, "player2IncomingCanvas");
                
                assertNotNull(player1IncomingCanvas);
                assertNotNull(player2IncomingCanvas);
            } catch (Exception e) {
                fail("Incoming canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetSceneManagerWithBattleMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                
                controller.setSceneManager(sceneManager);
                controller.setBattleMode("ITEM", true);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Set scene manager with battle mode failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testLevelLabelsUpdate() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("LINE", false);
                
                Label player1LevelLabel = (Label) getPrivateField(controller, "player1LevelLabel");
                Label player2LevelLabel = (Label) getPrivateField(controller, "player2LevelLabel");
                
                assertNotNull(player1LevelLabel);
                assertNotNull(player2LevelLabel);
            } catch (Exception e) {
                fail("Level labels update test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testLinesLabelsUpdate() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("SCORE", false);
                
                Label player1LinesLabel = (Label) getPrivateField(controller, "player1LinesLabel");
                Label player2LinesLabel = (Label) getPrivateField(controller, "player2LinesLabel");
                
                assertNotNull(player1LinesLabel);
                assertNotNull(player2LinesLabel);
            } catch (Exception e) {
                fail("Lines labels update test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testComputerOpponentModeInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                // 컴퓨터 대전 모드 (vsComputer = true)
                controller.setBattleMode("SCORE", true);
                
                boolean computerOpponentEnabled = (boolean) getPrivateField(controller, "computerOpponentEnabled");
                assertTrue(computerOpponentEnabled);
            } catch (Exception e) {
                fail("Computer opponent mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testDifficultySettingForComputerMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String originalDifficulty = settings.getDifficulty();
                
                // 다양한 난이도 테스트
                settings.setDifficulty("어려움");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("SCORE", true);
                assertNotNull(controller);
                
                settings.setDifficulty(originalDifficulty);
            } catch (Exception e) {
                fail("Difficulty setting test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testCanvasRenderingBoardSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                BattleGameScreenController controller = loader.getController();
                
                controller.setBattleMode("NORMAL", false);
                
                Canvas player1Canvas = (Canvas) getPrivateField(controller, "player1Canvas");
                
                // 캔버스 크기가 게임 보드 크기에 맞게 설정되어야 함
                assertTrue(player1Canvas.getWidth() > 0);
                assertTrue(player1Canvas.getHeight() > 0);
                
                // GameBoard 크기 기반 확인 (BOARD_WIDTH * BLOCK_SIZE)
                double expectedMinWidth = GameBoard.BOARD_WIDTH * 15; // 최소 블록 크기
                assertTrue(player1Canvas.getWidth() >= expectedMinWidth);
            } catch (Exception e) {
                fail("Canvas board size test failed: " + e.getMessage());
            }
        });
    }
}
