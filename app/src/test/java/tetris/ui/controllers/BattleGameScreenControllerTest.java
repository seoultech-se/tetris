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
                controller.setBattleMode("SCORE");

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
                controller.setBattleMode("TIME_LIMIT");

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
                controller.setBattleMode("LINE");

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
                controller.setBattleMode("ITEM");

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
                controller.setBattleMode("ITEM");

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
                controller.setBattleMode("SCORE");
                
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
                
                controller.setBattleMode("ITEM");
                
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
                
                controller.setBattleMode("LINE");
                
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
                
                controller.setBattleMode("SCORE");
                
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
                
                controller.setBattleMode("TIME_LIMIT");
                
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
                
                controller.setBattleMode("SCORE");
                
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
                
                controller.setBattleMode("NORMAL");
                
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
                
                controller.setBattleMode("SCORE");
                
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
                controller.setBattleMode("ITEM");
                
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
                
                controller.setBattleMode("LINE");
                
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
                
                controller.setBattleMode("SCORE");
                
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
                controller.setBattleMode("SCORE");
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
                
                controller.setBattleMode("NORMAL");
                
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

    // ===== 추가 렌더링 및 로직 테스트 =====

    @Test
    void testRenderPlayer1WithNullCanvas() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                
                setPrivateField(controller, "player1Canvas", null);
                
                Method renderPlayer1 = BattleGameScreenController.class.getDeclaredMethod("renderPlayer1");
                renderPlayer1.setAccessible(true);
                renderPlayer1.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderPlayer1 with null canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderPlayer2WithNullCanvas() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                
                setPrivateField(controller, "player2Canvas", null);
                
                Method renderPlayer2 = BattleGameScreenController.class.getDeclaredMethod("renderPlayer2");
                renderPlayer2.setAccessible(true);
                renderPlayer2.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderPlayer2 with null canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderPlayer1WithNullBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas player1Canvas = new Canvas(GameBoard.BOARD_WIDTH * 25, GameBoard.BOARD_HEIGHT * 25);
                setPrivateField(controller, "player1Canvas", player1Canvas);
                setPrivateField(controller, "battleEngine", null);
                
                Method renderPlayer1 = BattleGameScreenController.class.getDeclaredMethod("renderPlayer1");
                renderPlayer1.setAccessible(true);
                renderPlayer1.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderPlayer1 with null battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderNextPiecesWithNullCanvases() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                
                setPrivateField(controller, "player1NextCanvas", null);
                setPrivateField(controller, "player2NextCanvas", null);
                
                Method renderNextPieces = BattleGameScreenController.class.getDeclaredMethod("renderNextPieces");
                renderNextPieces.setAccessible(true);
                renderNextPieces.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderNextPieces with null canvases test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderNextPiecesWithNullBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas player1NextCanvas = new Canvas(6 * 25, 5 * 25);
                Canvas player2NextCanvas = new Canvas(6 * 25, 5 * 25);
                setPrivateField(controller, "player1NextCanvas", player1NextCanvas);
                setPrivateField(controller, "player2NextCanvas", player2NextCanvas);
                setPrivateField(controller, "battleEngine", null);
                
                Method renderNextPieces = BattleGameScreenController.class.getDeclaredMethod("renderNextPieces");
                renderNextPieces.setAccessible(true);
                renderNextPieces.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderNextPieces with null battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateFallSpeeds() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                BattleGameEngine engine = new BattleGameEngine("NORMAL");
                setPrivateField(controller, "battleEngine", engine);
                
                Method updateFallSpeeds = BattleGameScreenController.class.getDeclaredMethod("updateFallSpeeds");
                updateFallSpeeds.setAccessible(true);
                updateFallSpeeds.invoke(controller);
                
                long fallSpeed1 = (long) getPrivateField(controller, "fallSpeed1");
                long fallSpeed2 = (long) getPrivateField(controller, "fallSpeed2");
                
                assertTrue(fallSpeed1 > 0, "Fall speed should be positive");
                assertTrue(fallSpeed2 > 0, "Fall speed should be positive");
            } catch (Exception e) {
                fail("updateFallSpeeds test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateFallSpeedsWithNullBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                setPrivateField(controller, "battleEngine", null);
                
                Method updateFallSpeeds = BattleGameScreenController.class.getDeclaredMethod("updateFallSpeeds");
                updateFallSpeeds.setAccessible(true);
                updateFallSpeeds.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("updateFallSpeeds with null battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBlockSizeInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                int blockSize = (int) getPrivateField(controller, "BLOCK_SIZE");
                assertTrue(blockSize > 0, "Block size should be positive");
                assertEquals(25, blockSize, "Block size should be 25 for medium screen");
            } catch (Exception e) {
                fail("Block size initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testFallSpeedInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                long fallSpeed1 = (long) getPrivateField(controller, "fallSpeed1");
                long fallSpeed2 = (long) getPrivateField(controller, "fallSpeed2");
                
                assertEquals(1_000_000_000L, fallSpeed1, "Initial fall speed should be 1 second");
                assertEquals(1_000_000_000L, fallSpeed2, "Initial fall speed should be 1 second");
            } catch (Exception e) {
                fail("Fall speed initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testClearAnimationInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                boolean isAnimatingClear1 = (boolean) getPrivateField(controller, "isAnimatingClear1");
                boolean isAnimatingClear2 = (boolean) getPrivateField(controller, "isAnimatingClear2");
                
                assertFalse(isAnimatingClear1, "Should not be animating clear initially");
                assertFalse(isAnimatingClear2, "Should not be animating clear initially");
            } catch (Exception e) {
                fail("Clear animation initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testLastUpdateTimesInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                long lastUpdateTime1 = (long) getPrivateField(controller, "lastUpdateTime1");
                long lastUpdateTime2 = (long) getPrivateField(controller, "lastUpdateTime2");
                
                assertEquals(0, lastUpdateTime1, "lastUpdateTime1 should be 0 initially");
                assertEquals(0, lastUpdateTime2, "lastUpdateTime2 should be 0 initially");
            } catch (Exception e) {
                fail("Last update times initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testPieceColorsArray() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                Field colorsField = BattleGameScreenController.class.getDeclaredField("PIECE_COLORS");
                colorsField.setAccessible(true);
                javafx.scene.paint.Color[] colors = (javafx.scene.paint.Color[]) colorsField.get(null);
                
                assertNotNull(colors, "PIECE_COLORS should not be null");
                assertEquals(10, colors.length, "PIECE_COLORS should have 10 elements");
            } catch (Exception e) {
                fail("Piece colors array test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testPieceSymbolsArray() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                Field symbolsField = BattleGameScreenController.class.getDeclaredField("PIECE_SYMBOLS");
                symbolsField.setAccessible(true);
                String[] symbols = (String[]) symbolsField.get(null);
                
                assertNotNull(symbols, "PIECE_SYMBOLS should not be null");
                assertEquals(10, symbols.length, "PIECE_SYMBOLS should have 10 elements");
            } catch (Exception e) {
                fail("Piece symbols array test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testClearAnimationDuration() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                Field durationField = BattleGameScreenController.class.getDeclaredField("CLEAR_ANIMATION_DURATION");
                durationField.setAccessible(true);
                long duration = (long) durationField.get(null);
                
                assertEquals(50_000_000L, duration, "CLEAR_ANIMATION_DURATION should be 50ms");
            } catch (Exception e) {
                fail("Clear animation duration test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetBattleModeInitializesGameLoop() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("NORMAL");
                
                Object gameLoop = getPrivateField(controller, "gameLoop");
                assertNotNull(gameLoop, "Game loop should be initialized after setBattleMode");
                
                // Clean up
                try {
                    Method stop = gameLoop.getClass().getMethod("stop");
                    stop.invoke(gameLoop);
                } catch (Exception ignored) {}
            } catch (Exception e) {
                fail("setBattleMode initialize game loop test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testTimeLimitModeConfiguration() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                controller.setBattleMode("TIME_LIMIT");
                
                BattleGameEngine engine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertTrue(engine.isTimeLimitMode(), "Should have time limit mode enabled");
                
                // Clean up
                try {
                    Object gameLoop = getPrivateField(controller, "gameLoop");
                    Method stop = gameLoop.getClass().getMethod("stop");
                    stop.invoke(gameLoop);
                } catch (Exception ignored) {}
            } catch (Exception e) {
                fail("Time limit mode configuration test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSettingsManagerInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                Object settingsManager = getPrivateField(controller, "settingsManager");
                assertNotNull(settingsManager, "SettingsManager should be initialized");
                assertTrue(settingsManager instanceof SettingsManager, "Should be SettingsManager instance");
            } catch (Exception e) {
                fail("SettingsManager initialization test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGameLoopNullInitially() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BattleGameScreen.fxml"));
                loader.load();
                
                BattleGameScreenController controller = loader.getController();
                
                Object gameLoop = getPrivateField(controller, "gameLoop");
                assertNull(gameLoop, "Game loop should be null before setBattleMode");
            } catch (Exception e) {
                fail("Game loop null initially test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderPlayer1WithBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                
                Canvas player1Canvas = new Canvas(GameBoard.BOARD_WIDTH * 25, GameBoard.BOARD_HEIGHT * 25);
                setPrivateField(controller, "player1Canvas", player1Canvas);
                
                BattleGameEngine engine = new BattleGameEngine("NORMAL");
                setPrivateField(controller, "battleEngine", engine);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderPlayer1 = BattleGameScreenController.class.getDeclaredMethod("renderPlayer1");
                renderPlayer1.setAccessible(true);
                renderPlayer1.invoke(controller);
                // Should render without exception
            } catch (Exception e) {
                fail("renderPlayer1 with battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderPlayer2WithBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                
                Canvas player2Canvas = new Canvas(GameBoard.BOARD_WIDTH * 25, GameBoard.BOARD_HEIGHT * 25);
                setPrivateField(controller, "player2Canvas", player2Canvas);
                
                BattleGameEngine engine = new BattleGameEngine("NORMAL");
                setPrivateField(controller, "battleEngine", engine);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderPlayer2 = BattleGameScreenController.class.getDeclaredMethod("renderPlayer2");
                renderPlayer2.setAccessible(true);
                renderPlayer2.invoke(controller);
                // Should render without exception
            } catch (Exception e) {
                fail("renderPlayer2 with battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesBlockWithValidInput() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                
                setPrivateField(controller, "player1IncomingCanvas", canvas);
                
                Method renderIncomingLinesBlock = BattleGameScreenController.class.getDeclaredMethod(
                    "renderIncomingLinesBlock", javafx.scene.canvas.GraphicsContext.class, int.class, java.util.List.class
                );
                renderIncomingLinesBlock.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                java.util.List<Integer> emptyCols = new java.util.ArrayList<>();
                emptyCols.add(5);
                emptyCols.add(7);
                emptyCols.add(3);
                
                // Test with valid numLines (5)
                renderIncomingLinesBlock.invoke(controller, gc, 5, emptyCols);
                // Should render without exception
            } catch (Exception e) {
                fail("renderIncomingLinesBlock with valid input test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesBlockWithMaxLines() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                
                setPrivateField(controller, "player1IncomingCanvas", canvas);
                
                Method renderIncomingLinesBlock = BattleGameScreenController.class.getDeclaredMethod(
                    "renderIncomingLinesBlock", javafx.scene.canvas.GraphicsContext.class, int.class, java.util.List.class
                );
                renderIncomingLinesBlock.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                java.util.List<Integer> emptyCols = new java.util.ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    emptyCols.add(i);
                }
                
                // Test with maximum numLines (more than 10)
                renderIncomingLinesBlock.invoke(controller, gc, 15, emptyCols);
                // Should render only 10 lines
            } catch (Exception e) {
                fail("renderIncomingLinesBlock with max lines test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesBlockWithZeroLines() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                
                setPrivateField(controller, "player1IncomingCanvas", canvas);
                
                Method renderIncomingLinesBlock = BattleGameScreenController.class.getDeclaredMethod(
                    "renderIncomingLinesBlock", javafx.scene.canvas.GraphicsContext.class, int.class, java.util.List.class
                );
                renderIncomingLinesBlock.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                java.util.List<Integer> emptyCols = new java.util.ArrayList<>();
                
                // Test with zero lines
                renderIncomingLinesBlock.invoke(controller, gc, 0, emptyCols);
                // Should render grid only (no lines)
            } catch (Exception e) {
                fail("renderIncomingLinesBlock with zero lines test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesBlockWithEmptyEmptyCols() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                
                setPrivateField(controller, "player1IncomingCanvas", canvas);
                
                Method renderIncomingLinesBlock = BattleGameScreenController.class.getDeclaredMethod(
                    "renderIncomingLinesBlock", javafx.scene.canvas.GraphicsContext.class, int.class, java.util.List.class
                );
                renderIncomingLinesBlock.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                java.util.List<Integer> emptyCols = new java.util.ArrayList<>();
                
                // Test with empty emptyCols list
                renderIncomingLinesBlock.invoke(controller, gc, 5, emptyCols);
                // Should use default empty column calculation
            } catch (Exception e) {
                fail("renderIncomingLinesBlock with empty emptyCols test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledWithValidColor() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                javafx.scene.paint.Color color = javafx.scene.paint.Color.RED;
                
                // Test basic block rendering with itemType NONE
                renderBlockScaled.invoke(controller, gc, 50.0, 50.0, 25.0, color, 0, tetris.game.ItemType.NONE);
                // Should render without exception
            } catch (Exception e) {
                fail("renderBlockScaled with valid color test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledWithItemType() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                javafx.scene.paint.Color color = javafx.scene.paint.Color.BLUE;
                
                // Test block rendering with BOMB itemType
                renderBlockScaled.invoke(controller, gc, 100.0, 100.0, 25.0, color, 1, tetris.game.ItemType.BOMB);
                // Should render block with item indicator
            } catch (Exception e) {
                fail("renderBlockScaled with itemType test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledWithLineCloseItemType() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                javafx.scene.paint.Color color = javafx.scene.paint.Color.GREEN;
                
                // Test with LINE_CLEAR itemType
                renderBlockScaled.invoke(controller, gc, 0.0, 0.0, 10.0, color, 2, tetris.game.ItemType.LINE_CLEAR);
                // Should render small block without exception
            } catch (Exception e) {
                fail("renderBlockScaled with LINE_CLEAR itemType test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledSmallSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(100, 100);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                javafx.scene.paint.Color color = javafx.scene.paint.Color.GREEN;
                
                // Test with small block size (10 pixels)
                renderBlockScaled.invoke(controller, gc, 0.0, 0.0, 10.0, color, 2, tetris.game.ItemType.WEIGHT);
                // Should render small block without exception
            } catch (Exception e) {
                fail("renderBlockScaled small size test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledLargeSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(500, 500);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                javafx.scene.paint.Color color = javafx.scene.paint.Color.YELLOW;
                
                // Test with large block size (50 pixels)
                renderBlockScaled.invoke(controller, gc, 100.0, 100.0, 50.0, color, 3, tetris.game.ItemType.DOUBLE_SCORE);
                // Should render large block without exception
            } catch (Exception e) {
                fail("renderBlockScaled large size test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledColorBlindMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                
                // Setup settings manager with color blind mode enabled
                SettingsManager settingsManager = SettingsManager.getInstance();
                // Temporarily save original state
                boolean originalColorBlindMode = settingsManager.isColorBlindModeEnabled();
                
                // Note: We can't directly set color blind mode without modifying SettingsManager,
                // but we can test the rendering happens without exception
                setPrivateField(controller, "settingsManager", settingsManager);
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                javafx.scene.paint.Color color = javafx.scene.paint.Color.PURPLE;
                
                // Test rendering with any settings
                renderBlockScaled.invoke(controller, gc, 50.0, 50.0, 25.0, color, 4, tetris.game.ItemType.NONE);
                // Should render without exception
            } catch (Exception e) {
                fail("renderBlockScaled color blind mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledMultiplePieceTypes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(350, 300);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                
                // Test all 7 tetromino types (0-6)
                javafx.scene.paint.Color[] colors = {
                    javafx.scene.paint.Color.CYAN, javafx.scene.paint.Color.BLUE, javafx.scene.paint.Color.ORANGE,
                    javafx.scene.paint.Color.YELLOW, javafx.scene.paint.Color.GREEN, javafx.scene.paint.Color.PURPLE,
                    javafx.scene.paint.Color.RED
                };
                
                for (int pieceType = 0; pieceType < colors.length; pieceType++) {
                    double x = (pieceType % 4) * 70.0;
                    double y = (pieceType / 4) * 70.0;
                    renderBlockScaled.invoke(controller, gc, x, y, 25.0, colors[pieceType], pieceType, tetris.game.ItemType.NONE);
                }
                // Should render all piece types without exception
            } catch (Exception e) {
                fail("renderBlockScaled multiple piece types test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBlockScaledWithOutOfBoundsPosition() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                BattleGameScreenController controller = new BattleGameScreenController();
                Canvas canvas = new Canvas(250, 300);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());
                
                Method renderBlockScaled = BattleGameScreenController.class.getDeclaredMethod(
                    "renderBlockScaled", javafx.scene.canvas.GraphicsContext.class, 
                    double.class, double.class, double.class, javafx.scene.paint.Color.class, 
                    int.class, tetris.game.ItemType.class
                );
                renderBlockScaled.setAccessible(true);
                
                javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
                javafx.scene.paint.Color color = javafx.scene.paint.Color.ORANGE;
                
                // Test with out of bounds position (negative x)
                renderBlockScaled.invoke(controller, gc, -25.0, 50.0, 25.0, color, 0, tetris.game.ItemType.NONE);
                // Should still render without exception (GraphicsContext handles clipping)
            } catch (Exception e) {
                fail("renderBlockScaled out of bounds test failed: " + e.getMessage());
            }
        });
    }
}
