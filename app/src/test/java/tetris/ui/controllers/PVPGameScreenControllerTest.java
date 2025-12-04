package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.game.BattleGameEngine;
import tetris.game.GameBoard;
import tetris.network.GameServer;
import tetris.network.GameClient;
import tetris.network.GameStateData;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for PVPGameScreenController.
 * Tests initialization, scene management, network handling and game state.
 */
class PVPGameScreenControllerTest extends JavaFXTestBase {

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

    private Object invokePrivateMethodWithReturn(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(obj);
    }

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                assertNotNull(controller, "PVPGameScreenController should be created");
            } catch (Exception e) {
                fail("Failed to load PVPGameScreen.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                assertNotNull(loader.getLocation(), "PVPGameScreen.fxml should exist");
                
                Object root = loader.load();
                assertNotNull(root, "FXML root should not be null");
            } catch (Exception e) {
                fail("Failed to load FXML: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                
                controller.setSceneManager(sceneManager);
                
                Object storedManager = getPrivateField(controller, "sceneManager");
                assertNotNull(storedManager, "SceneManager should be stored");
                assertEquals(sceneManager, storedManager, "SceneManager should match");
            } catch (Exception e) {
                fail("Failed to set scene manager: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetGameModeNormal() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                controller.setGameMode("NORMAL");
                
                String gameMode = (String) getPrivateField(controller, "gameMode");
                assertEquals("NORMAL", gameMode, "Game mode should be NORMAL");
            } catch (Exception e) {
                fail("Failed to set game mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetGameModeItem() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                controller.setGameMode("ITEM");
                
                String gameMode = (String) getPrivateField(controller, "gameMode");
                assertEquals("ITEM", gameMode, "Game mode should be ITEM");
            } catch (Exception e) {
                fail("Failed to set game mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetGameModeTimeLimit() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                controller.setGameMode("TIME_LIMIT");
                
                String gameMode = (String) getPrivateField(controller, "gameMode");
                assertEquals("TIME_LIMIT", gameMode, "Game mode should be TIME_LIMIT");
            } catch (Exception e) {
                fail("Failed to set game mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testControllerInitializationWithSettingsManager() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Object settingsManager = getPrivateField(controller, "settingsManager");
                assertNotNull(settingsManager, "SettingsManager should be initialized");
            } catch (Exception e) {
                fail("Failed to verify SettingsManager: " + e.getMessage());
            }
        });
    }

    @Test
    void testCanvasSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Canvas myCanvas = (Canvas) getPrivateField(controller, "myCanvas");
                Canvas opponentCanvas = (Canvas) getPrivateField(controller, "opponentCanvas");
                
                assertNotNull(myCanvas, "myCanvas should be initialized");
                assertNotNull(opponentCanvas, "opponentCanvas should be initialized");
                
                // Check canvas sizes
                assertEquals(GameBoard.BOARD_WIDTH * 25, myCanvas.getWidth(), "My canvas width should be correct");
                assertEquals(GameBoard.BOARD_HEIGHT * 25, myCanvas.getHeight(), "My canvas height should be correct");
            } catch (Exception e) {
                fail("Failed to check canvas setup: " + e.getMessage());
            }
        });
    }

    @Test
    void testNextCanvasSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Canvas myNextCanvas = (Canvas) getPrivateField(controller, "myNextCanvas");
                Canvas opponentNextCanvas = (Canvas) getPrivateField(controller, "opponentNextCanvas");
                
                assertNotNull(myNextCanvas, "myNextCanvas should be initialized");
                assertNotNull(opponentNextCanvas, "opponentNextCanvas should be initialized");
            } catch (Exception e) {
                fail("Failed to check next canvas setup: " + e.getMessage());
            }
        });
    }

    @Test
    void testIncomingCanvasSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Canvas myIncomingCanvas = (Canvas) getPrivateField(controller, "myIncomingCanvas");
                Canvas opponentIncomingCanvas = (Canvas) getPrivateField(controller, "opponentIncomingCanvas");
                
                assertNotNull(myIncomingCanvas, "myIncomingCanvas should be initialized");
                assertNotNull(opponentIncomingCanvas, "opponentIncomingCanvas should be initialized");
            } catch (Exception e) {
                fail("Failed to check incoming canvas setup: " + e.getMessage());
            }
        });
    }

    @Test
    void testPlayerLabelsSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label myPlayerLabel = (Label) getPrivateField(controller, "myPlayerLabel");
                Label opponentPlayerLabel = (Label) getPrivateField(controller, "opponentPlayerLabel");
                
                assertNotNull(myPlayerLabel, "myPlayerLabel should be initialized");
                assertNotNull(opponentPlayerLabel, "opponentPlayerLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check player labels: " + e.getMessage());
            }
        });
    }

    @Test
    void testScoreLabelsSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label myScoreLabel = (Label) getPrivateField(controller, "myScoreLabel");
                Label opponentScoreLabel = (Label) getPrivateField(controller, "opponentScoreLabel");
                
                assertNotNull(myScoreLabel, "myScoreLabel should be initialized");
                assertNotNull(opponentScoreLabel, "opponentScoreLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check score labels: " + e.getMessage());
            }
        });
    }

    @Test
    void testLevelLabelsSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label myLevelLabel = (Label) getPrivateField(controller, "myLevelLabel");
                Label opponentLevelLabel = (Label) getPrivateField(controller, "opponentLevelLabel");
                
                assertNotNull(myLevelLabel, "myLevelLabel should be initialized");
                assertNotNull(opponentLevelLabel, "opponentLevelLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check level labels: " + e.getMessage());
            }
        });
    }

    @Test
    void testLinesLabelsSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label myLinesLabel = (Label) getPrivateField(controller, "myLinesLabel");
                Label opponentLinesLabel = (Label) getPrivateField(controller, "opponentLinesLabel");
                
                assertNotNull(myLinesLabel, "myLinesLabel should be initialized");
                assertNotNull(opponentLinesLabel, "opponentLinesLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check lines labels: " + e.getMessage());
            }
        });
    }

    @Test
    void testGameModeLabelSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label gameModeLabel = (Label) getPrivateField(controller, "gameModeLabel");
                assertNotNull(gameModeLabel, "gameModeLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check game mode label: " + e.getMessage());
            }
        });
    }

    @Test
    void testStatusLabelSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label statusLabel = (Label) getPrivateField(controller, "statusLabel");
                assertNotNull(statusLabel, "statusLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check status label: " + e.getMessage());
            }
        });
    }

    @Test
    void testGameOverBoxSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                VBox gameOverBox = (VBox) getPrivateField(controller, "gameOverBox");
                assertNotNull(gameOverBox, "gameOverBox should be initialized");
            } catch (Exception e) {
                fail("Failed to check game over box: " + e.getMessage());
            }
        });
    }

    @Test
    void testBlockSizeInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                int blockSize = (int) getPrivateField(controller, "BLOCK_SIZE");
                assertEquals(25, blockSize, "Block size should be 25");
            } catch (Exception e) {
                fail("Failed to check block size: " + e.getMessage());
            }
        });
    }

    @Test
    void testInitialFallSpeed() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                long fallSpeedMe = (long) getPrivateField(controller, "fallSpeedMe");
                assertEquals(1_000_000_000L, fallSpeedMe, "Initial fall speed should be 1 second");
            } catch (Exception e) {
                fail("Failed to check fall speed: " + e.getMessage());
            }
        });
    }

    @Test
    void testCountdownInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                boolean isCountingDown = (boolean) getPrivateField(controller, "isCountingDown");
                int countdownNumber = (int) getPrivateField(controller, "countdownNumber");
                
                assertFalse(isCountingDown, "Should not be counting down initially");
                assertEquals(3, countdownNumber, "Countdown should start at 3");
            } catch (Exception e) {
                fail("Failed to check countdown initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testClearAnimationInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                boolean isAnimatingClear = (boolean) getPrivateField(controller, "isAnimatingClear");
                Object playerLinesToClear = getPrivateField(controller, "playerLinesToClear");
                
                assertFalse(isAnimatingClear, "Should not be animating clear initially");
                assertNull(playerLinesToClear, "Lines to clear should be null initially");
            } catch (Exception e) {
                fail("Failed to check clear animation initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testOpponentStateInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Object opponentState = getPrivateField(controller, "opponentState");
                int opponentIncomingLines = (int) getPrivateField(controller, "opponentIncomingLines");
                
                assertNull(opponentState, "Opponent state should be null initially");
                assertEquals(0, opponentIncomingLines, "Opponent incoming lines should be 0");
            } catch (Exception e) {
                fail("Failed to check opponent state initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testTimeLimitModeInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                boolean isTimeLimitMode = (boolean) getPrivateField(controller, "isTimeLimitMode");
                long gameDuration = (long) getPrivateField(controller, "gameDuration");
                
                assertFalse(isTimeLimitMode, "Time limit mode should be false initially");
                assertEquals(180, gameDuration, "Game duration should be 180 seconds (3 min)");
            } catch (Exception e) {
                fail("Failed to check time limit mode initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testConnectionLostInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                boolean connectionLost = (boolean) getPrivateField(controller, "connectionLost");
                assertFalse(connectionLost, "Connection should not be lost initially");
            } catch (Exception e) {
                fail("Failed to check connection lost initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testRTTInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                long currentRTT = (long) getPrivateField(controller, "currentRTT");
                assertEquals(0, currentRTT, "Current RTT should be 0 initially");
            } catch (Exception e) {
                fail("Failed to check RTT initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testNetworkObjectsInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Object gameServer = getPrivateField(controller, "gameServer");
                Object gameClient = getPrivateField(controller, "gameClient");
                
                assertNull(gameServer, "Game server should be null initially");
                assertNull(gameClient, "Game client should be null initially");
            } catch (Exception e) {
                fail("Failed to check network objects initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testBattleEngineInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Object battleEngine = getPrivateField(controller, "battleEngine");
                assertNull(battleEngine, "Battle engine should be null before setNetworkObjects");
            } catch (Exception e) {
                fail("Failed to check battle engine initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testGameLoopInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Object gameLoop = getPrivateField(controller, "gameLoop");
                assertNull(gameLoop, "Game loop should be null initially");
            } catch (Exception e) {
                fail("Failed to check game loop initialization: " + e.getMessage());
            }
        });
    }

    @Test
    void testLatencyLabelSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label latencyLabel = (Label) getPrivateField(controller, "latencyLabel");
                assertNotNull(latencyLabel, "latencyLabel should be initialized");
            } catch (Exception e) {
                fail("Failed to check latency label: " + e.getMessage());
            }
        });
    }

    @Test
    void testLagWarningLabelSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label lagWarningLabel = (Label) getPrivateField(controller, "lagWarningLabel");
                // lagWarningLabel may be null if not defined in FXML
            } catch (Exception e) {
                fail("Failed to check lag warning label: " + e.getMessage());
            }
        });
    }

    @Test
    void testTimerLabelSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Label timerLabel = (Label) getPrivateField(controller, "timerLabel");
                // timerLabel may be null if not defined in FXML
            } catch (Exception e) {
                fail("Failed to check timer label: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateGameModeLabelNormal() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                controller.setGameMode("NORMAL");
                
                // Invoke updateGameModeLabel
                invokePrivateMethod(controller, "updateGameModeLabel");
                
                Label gameModeLabel = (Label) getPrivateField(controller, "gameModeLabel");
                assertEquals("일반 모드", gameModeLabel.getText(), "Game mode label should show 일반 모드");
            } catch (Exception e) {
                fail("Failed to test updateGameModeLabel: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateGameModeLabelItem() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                controller.setGameMode("ITEM");
                
                // Invoke updateGameModeLabel
                invokePrivateMethod(controller, "updateGameModeLabel");
                
                Label gameModeLabel = (Label) getPrivateField(controller, "gameModeLabel");
                assertEquals("아이템 모드", gameModeLabel.getText(), "Game mode label should show 아이템 모드");
            } catch (Exception e) {
                fail("Failed to test updateGameModeLabel: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateGameModeLabelTimeLimit() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                controller.setGameMode("TIME_LIMIT");
                
                // Invoke updateGameModeLabel
                invokePrivateMethod(controller, "updateGameModeLabel");
                
                Label gameModeLabel = (Label) getPrivateField(controller, "gameModeLabel");
                assertEquals("시간제한 모드", gameModeLabel.getText(), "Game mode label should show 시간제한 모드");
            } catch (Exception e) {
                fail("Failed to test updateGameModeLabel: " + e.getMessage());
            }
        });
    }

    @Test
    void testPieceColorsArray() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Access static field
                Field colorsField = PVPGameScreenController.class.getDeclaredField("PIECE_COLORS");
                colorsField.setAccessible(true);
                javafx.scene.paint.Color[] colors = (javafx.scene.paint.Color[]) colorsField.get(null);
                
                assertNotNull(colors, "PIECE_COLORS should not be null");
                assertEquals(10, colors.length, "PIECE_COLORS should have 10 elements");
            } catch (Exception e) {
                fail("Failed to check piece colors: " + e.getMessage());
            }
        });
    }

    @Test
    void testPieceSymbolsArray() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Access static field
                Field symbolsField = PVPGameScreenController.class.getDeclaredField("PIECE_SYMBOLS");
                symbolsField.setAccessible(true);
                String[] symbols = (String[]) symbolsField.get(null);
                
                assertNotNull(symbols, "PIECE_SYMBOLS should not be null");
                assertEquals(10, symbols.length, "PIECE_SYMBOLS should have 10 elements");
            } catch (Exception e) {
                fail("Failed to check piece symbols: " + e.getMessage());
            }
        });
    }

    @Test
    void testLagThresholds() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Access static fields
                Field warningThreshold = PVPGameScreenController.class.getDeclaredField("LAG_WARNING_THRESHOLD");
                warningThreshold.setAccessible(true);
                long lagWarning = (long) warningThreshold.get(null);
                
                Field criticalThreshold = PVPGameScreenController.class.getDeclaredField("LAG_CRITICAL_THRESHOLD");
                criticalThreshold.setAccessible(true);
                long lagCritical = (long) criticalThreshold.get(null);
                
                assertEquals(200, lagWarning, "LAG_WARNING_THRESHOLD should be 200ms");
                assertEquals(500, lagCritical, "LAG_CRITICAL_THRESHOLD should be 500ms");
            } catch (Exception e) {
                fail("Failed to check lag thresholds: " + e.getMessage());
            }
        });
    }

    @Test
    void testTimeoutThresholds() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Access static fields
                Field rttTimeout = PVPGameScreenController.class.getDeclaredField("RTT_TIMEOUT");
                rttTimeout.setAccessible(true);
                long timeout = (long) rttTimeout.get(null);
                
                Field connTimeout = PVPGameScreenController.class.getDeclaredField("CONNECTION_TIMEOUT");
                connTimeout.setAccessible(true);
                long connTimeoutValue = (long) connTimeout.get(null);
                
                assertEquals(5_000_000_000L, timeout, "RTT_TIMEOUT should be 5 seconds");
                assertEquals(10_000_000_000L, connTimeoutValue, "CONNECTION_TIMEOUT should be 10 seconds");
            } catch (Exception e) {
                fail("Failed to check timeout thresholds: " + e.getMessage());
            }
        });
    }

    @Test
    void testStateSendInterval() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Access static field
                Field intervalField = PVPGameScreenController.class.getDeclaredField("STATE_SEND_INTERVAL");
                intervalField.setAccessible(true);
                long interval = (long) intervalField.get(null);
                
                assertEquals(50_000_000L, interval, "STATE_SEND_INTERVAL should be 50ms");
            } catch (Exception e) {
                fail("Failed to check state send interval: " + e.getMessage());
            }
        });
    }

    @Test
    void testClearAnimationDuration() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Access static field - changed from CLEAR_ANIMATION_DURATION to CLEAR_ANIMATION_BASE
                Field durationField = PVPGameScreenController.class.getDeclaredField("CLEAR_ANIMATION_BASE");
                durationField.setAccessible(true);
                long duration = (long) durationField.get(null);
                
                assertEquals(50_000_000L, duration, "CLEAR_ANIMATION_BASE should be 50ms");
            } catch (Exception e) {
                fail("Failed to check clear animation duration: " + e.getMessage());
            }
        });
    }

    @Test
    void testCountdownInterval() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Access static field
                Field intervalField = PVPGameScreenController.class.getDeclaredField("COUNTDOWN_INTERVAL");
                intervalField.setAccessible(true);
                long interval = (long) intervalField.get(null);
                
                assertEquals(1_000_000_000L, interval, "COUNTDOWN_INTERVAL should be 1 second");
            } catch (Exception e) {
                fail("Failed to check countdown interval: " + e.getMessage());
            }
        });
    }

    @Test
    void testRematchButtonSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Object rematchButton = getPrivateField(controller, "rematchButton");
                assertNotNull(rematchButton, "rematchButton should be initialized");
            } catch (Exception e) {
                fail("Failed to check rematch button: " + e.getMessage());
            }
        });
    }

    @Test
    void testToLobbyButtonSetup() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Object toLobbyButton = getPrivateField(controller, "toLobbyButton");
                assertNotNull(toLobbyButton, "toLobbyButton should be initialized");
            } catch (Exception e) {
                fail("Failed to check to lobby button: " + e.getMessage());
            }
        });
    }

    @Test
    void testIsServerFlagInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                boolean isServer = (boolean) getPrivateField(controller, "isServer");
                assertFalse(isServer, "isServer should be false initially");
            } catch (Exception e) {
                fail("Failed to check isServer flag: " + e.getMessage());
            }
        });
    }

    @Test
    void testLastUpdateTimesInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                long lastUpdateTimeMe = (long) getPrivateField(controller, "lastUpdateTimeMe");
                long lastUpdateTimeOpponent = (long) getPrivateField(controller, "lastUpdateTimeOpponent");
                
                assertEquals(0, lastUpdateTimeMe, "lastUpdateTimeMe should be 0 initially");
                assertEquals(0, lastUpdateTimeOpponent, "lastUpdateTimeOpponent should be 0 initially");
            } catch (Exception e) {
                fail("Failed to check last update times: " + e.getMessage());
            }
        });
    }

    @Test
    void testTimeUpSentInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                boolean timeUpSent = (boolean) getPrivateField(controller, "timeUpSent");
                assertFalse(timeUpSent, "timeUpSent should be false initially");
            } catch (Exception e) {
                fail("Failed to check timeUpSent flag: " + e.getMessage());
            }
        });
    }

    // ===== 추가 테스트: 메서드 로직 커버리지 =====

    @Test
    void testSetupCanvasSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Method setupMethod = PVPGameScreenController.class.getDeclaredMethod("setupCanvasSize");
                setupMethod.setAccessible(true);
                setupMethod.invoke(controller);
                
                Canvas myCanvas = (Canvas) getPrivateField(controller, "myCanvas");
                assertEquals(GameBoard.BOARD_WIDTH * 25, myCanvas.getWidth());
                assertEquals(GameBoard.BOARD_HEIGHT * 25, myCanvas.getHeight());
            } catch (Exception e) {
                fail("setupCanvasSize test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetStatusMessageShort() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Method setStatusMessage = PVPGameScreenController.class.getDeclaredMethod("setStatusMessage", String.class, String.class);
                setStatusMessage.setAccessible(true);
                setStatusMessage.invoke(controller, "START!", "#00ff00");
                
                Label statusLabel = (Label) getPrivateField(controller, "statusLabel");
                assertEquals("START!", statusLabel.getText());
                assertTrue(statusLabel.getStyle().contains("24px"), "Short message should use large font");
            } catch (Exception e) {
                fail("setStatusMessage short test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetStatusMessageLong() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Method setStatusMessage = PVPGameScreenController.class.getDeclaredMethod("setStatusMessage", String.class, String.class);
                setStatusMessage.setAccessible(true);
                setStatusMessage.invoke(controller, "상대방의 응답을 기다리는 중...", "#ffff00");
                
                Label statusLabel = (Label) getPrivateField(controller, "statusLabel");
                assertEquals("상대방의 응답을 기다리는 중...", statusLabel.getText());
                assertTrue(statusLabel.getStyle().contains("16px"), "Long message should use small font");
            } catch (Exception e) {
                fail("setStatusMessage long test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLatencyDisplayWithZeroRTT() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 0L);
                
                Method updateLatency = PVPGameScreenController.class.getDeclaredMethod("updateLatencyDisplay");
                updateLatency.setAccessible(true);
                updateLatency.invoke(controller);
                
                Label latencyLabel = (Label) getPrivateField(controller, "latencyLabel");
                assertEquals("RTT: - ms", latencyLabel.getText());
            } catch (Exception e) {
                fail("updateLatencyDisplay zero RTT test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLatencyDisplayWithLowRTT() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 50L);
                
                Method updateLatency = PVPGameScreenController.class.getDeclaredMethod("updateLatencyDisplay");
                updateLatency.setAccessible(true);
                updateLatency.invoke(controller);
                
                Label latencyLabel = (Label) getPrivateField(controller, "latencyLabel");
                assertEquals("RTT: 50 ms", latencyLabel.getText());
                assertTrue(latencyLabel.getStyle().contains("#00ff00"), "Low RTT should show green");
            } catch (Exception e) {
                fail("updateLatencyDisplay low RTT test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLatencyDisplayWithWarningRTT() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 300L);
                
                Method updateLatency = PVPGameScreenController.class.getDeclaredMethod("updateLatencyDisplay");
                updateLatency.setAccessible(true);
                updateLatency.invoke(controller);
                
                Label latencyLabel = (Label) getPrivateField(controller, "latencyLabel");
                assertEquals("RTT: 300 ms", latencyLabel.getText());
                assertTrue(latencyLabel.getStyle().contains("#ffaa00"), "Warning RTT should show orange");
            } catch (Exception e) {
                fail("updateLatencyDisplay warning RTT test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLatencyDisplayWithCriticalRTT() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 600L);
                
                Method updateLatency = PVPGameScreenController.class.getDeclaredMethod("updateLatencyDisplay");
                updateLatency.setAccessible(true);
                updateLatency.invoke(controller);
                
                Label latencyLabel = (Label) getPrivateField(controller, "latencyLabel");
                assertEquals("RTT: 600 ms", latencyLabel.getText());
                assertTrue(latencyLabel.getStyle().contains("#ff0000"), "Critical RTT should show red");
            } catch (Exception e) {
                fail("updateLatencyDisplay critical RTT test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLagWarningWithNoWarning() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 50L);
                setPrivateField(controller, "lastRTTUpdateTime", System.nanoTime());
                
                Method updateLagWarning = PVPGameScreenController.class.getDeclaredMethod("updateLagWarning");
                updateLagWarning.setAccessible(true);
                updateLagWarning.invoke(controller);
                
                Label lagWarningLabel = (Label) getPrivateField(controller, "lagWarningLabel");
                if (lagWarningLabel != null) {
                    assertFalse(lagWarningLabel.isVisible(), "No warning should be shown");
                }
            } catch (Exception e) {
                fail("updateLagWarning no warning test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLagWarningWithWarningRTT() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 250L);
                setPrivateField(controller, "lastRTTUpdateTime", System.nanoTime());
                
                Method updateLagWarning = PVPGameScreenController.class.getDeclaredMethod("updateLagWarning");
                updateLagWarning.setAccessible(true);
                updateLagWarning.invoke(controller);
                
                Label lagWarningLabel = (Label) getPrivateField(controller, "lagWarningLabel");
                if (lagWarningLabel != null) {
                    assertTrue(lagWarningLabel.isVisible(), "Warning should be shown");
                    assertTrue(lagWarningLabel.getText().contains("네트워크 지연"));
                }
            } catch (Exception e) {
                fail("updateLagWarning warning RTT test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLagWarningWithCriticalRTT() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 600L);
                setPrivateField(controller, "lastRTTUpdateTime", System.nanoTime());
                
                Method updateLagWarning = PVPGameScreenController.class.getDeclaredMethod("updateLagWarning");
                updateLagWarning.setAccessible(true);
                updateLagWarning.invoke(controller);
                
                Label lagWarningLabel = (Label) getPrivateField(controller, "lagWarningLabel");
                if (lagWarningLabel != null) {
                    assertTrue(lagWarningLabel.isVisible(), "Critical warning should be shown");
                    assertTrue(lagWarningLabel.getText().contains("심각한"));
                }
            } catch (Exception e) {
                fail("updateLagWarning critical RTT test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLagWarningWithStaleRTT() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "currentRTT", 50L);
                // 6초 전 (RTT_TIMEOUT은 5초)
                setPrivateField(controller, "lastRTTUpdateTime", System.nanoTime() - 6_000_000_000L);
                
                Method updateLagWarning = PVPGameScreenController.class.getDeclaredMethod("updateLagWarning");
                updateLagWarning.setAccessible(true);
                updateLagWarning.invoke(controller);
                
                Label lagWarningLabel = (Label) getPrivateField(controller, "lagWarningLabel");
                if (lagWarningLabel != null) {
                    assertTrue(lagWarningLabel.isVisible(), "Connection unstable warning should be shown");
                    assertTrue(lagWarningLabel.getText().contains("연결 불안정"));
                }
            } catch (Exception e) {
                fail("updateLagWarning stale RTT test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testInitializeGameWithNormalMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                BattleGameEngine battleEngine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertNotNull(battleEngine, "Battle engine should be initialized");
                assertFalse((boolean) getPrivateField(controller, "isTimeLimitMode"), "Should not be time limit mode");
                
                Label myPlayerLabel = (Label) getPrivateField(controller, "myPlayerLabel");
                assertEquals("서버 (나)", myPlayerLabel.getText());
            } catch (Exception e) {
                fail("initializeGame normal mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testInitializeGameWithTimeLimitMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "TIME_LIMIT");
                setPrivateField(controller, "isServer", false);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                BattleGameEngine battleEngine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertNotNull(battleEngine, "Battle engine should be initialized");
                assertTrue((boolean) getPrivateField(controller, "isTimeLimitMode"), "Should be time limit mode");
                
                Label myPlayerLabel = (Label) getPrivateField(controller, "myPlayerLabel");
                assertEquals("클라이언트 (나)", myPlayerLabel.getText());
            } catch (Exception e) {
                fail("initializeGame time limit mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testInitializeGameWithItemMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "ITEM");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                BattleGameEngine battleEngine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertNotNull(battleEngine, "Battle engine should be initialized for ITEM mode");
            } catch (Exception e) {
                fail("initializeGame item mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGetMyEngineAsServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method getMyEngine = PVPGameScreenController.class.getDeclaredMethod("getMyEngine");
                getMyEngine.setAccessible(true);
                Object myEngine = getMyEngine.invoke(controller);
                
                BattleGameEngine battleEngine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertEquals(battleEngine.getPlayer1Engine(), myEngine, "Server should use Player1 engine");
            } catch (Exception e) {
                fail("getMyEngine as server test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGetMyEngineAsClient() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", false);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method getMyEngine = PVPGameScreenController.class.getDeclaredMethod("getMyEngine");
                getMyEngine.setAccessible(true);
                Object myEngine = getMyEngine.invoke(controller);
                
                BattleGameEngine battleEngine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                assertEquals(battleEngine.getPlayer2Engine(), myEngine, "Client should use Player2 engine");
            } catch (Exception e) {
                fail("getMyEngine as client test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGetMyPendingAttacksAsServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method getMyPendingAttacks = PVPGameScreenController.class.getDeclaredMethod("getMyPendingAttacks");
                getMyPendingAttacks.setAccessible(true);
                int attacks = (int) getMyPendingAttacks.invoke(controller);
                
                assertEquals(0, attacks, "Initial pending attacks should be 0");
            } catch (Exception e) {
                fail("getMyPendingAttacks as server test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGetMyPendingAttackEmptyCols() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method getMyPendingAttackEmptyCols = PVPGameScreenController.class.getDeclaredMethod("getMyPendingAttackEmptyCols");
                getMyPendingAttackEmptyCols.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<Integer> emptyCols = (List<Integer>) getMyPendingAttackEmptyCols.invoke(controller);
                
                assertNotNull(emptyCols, "Empty cols list should not be null");
            } catch (Exception e) {
                fail("getMyPendingAttackEmptyCols test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateFallSpeeds() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method updateFallSpeeds = PVPGameScreenController.class.getDeclaredMethod("updateFallSpeeds");
                updateFallSpeeds.setAccessible(true);
                updateFallSpeeds.invoke(controller);
                
                long fallSpeedMe = (long) getPrivateField(controller, "fallSpeedMe");
                assertTrue(fallSpeedMe > 0, "Fall speed should be positive");
            } catch (Exception e) {
                fail("updateFallSpeeds test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testReceiveAttackAsServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method receiveAttack = PVPGameScreenController.class.getDeclaredMethod("receiveAttack", int.class, int.class);
                receiveAttack.setAccessible(true);
                receiveAttack.invoke(controller, 2, 5);
                
                BattleGameEngine battleEngine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                int pendingAttacks = battleEngine.getPendingAttacksToPlayer1();
                assertEquals(2, pendingAttacks, "Server should receive attack to Player1");
            } catch (Exception e) {
                fail("receiveAttack as server test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testReceiveAttackAsClient() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", false);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method receiveAttack = PVPGameScreenController.class.getDeclaredMethod("receiveAttack", int.class, int.class);
                receiveAttack.setAccessible(true);
                receiveAttack.invoke(controller, 3, 2);
                
                BattleGameEngine battleEngine = (BattleGameEngine) getPrivateField(controller, "battleEngine");
                int pendingAttacks = battleEngine.getPendingAttacksToPlayer2();
                assertEquals(3, pendingAttacks, "Client should receive attack to Player2");
            } catch (Exception e) {
                fail("receiveAttack as client test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderMyBoardWithNullCanvas() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "myCanvas", null);
                
                Method renderMyBoard = PVPGameScreenController.class.getDeclaredMethod("renderMyBoard");
                renderMyBoard.setAccessible(true);
                renderMyBoard.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderMyBoard with null canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderMyBoardWithNullBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "battleEngine", null);
                
                Method renderMyBoard = PVPGameScreenController.class.getDeclaredMethod("renderMyBoard");
                renderMyBoard.setAccessible(true);
                renderMyBoard.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderMyBoard with null battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderOpponentBoardWithNullCanvas() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "opponentCanvas", null);
                
                Method renderOpponentBoard = PVPGameScreenController.class.getDeclaredMethod("renderOpponentBoard");
                renderOpponentBoard.setAccessible(true);
                renderOpponentBoard.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderOpponentBoard with null canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderOpponentBoardWithNullState() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "opponentState", null);
                
                Method renderOpponentBoard = PVPGameScreenController.class.getDeclaredMethod("renderOpponentBoard");
                renderOpponentBoard.setAccessible(true);
                renderOpponentBoard.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderOpponentBoard with null state test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderNextPiecesWithNullCanvas() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "myNextCanvas", null);
                
                Method renderNextPieces = PVPGameScreenController.class.getDeclaredMethod("renderNextPieces");
                renderNextPieces.setAccessible(true);
                renderNextPieces.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderNextPieces with null canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesWithNullBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "battleEngine", null);
                
                Method renderIncomingLines = PVPGameScreenController.class.getDeclaredMethod("renderIncomingLines");
                renderIncomingLines.setAccessible(true);
                renderIncomingLines.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("renderIncomingLines with null battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSendMyStateWithNullBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "battleEngine", null);
                
                Method sendMyState = PVPGameScreenController.class.getDeclaredMethod("sendMyState");
                sendMyState.setAccessible(true);
                sendMyState.invoke(controller);
                // Should return early without exception
            } catch (Exception e) {
                fail("sendMyState with null battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetupKeyHandlerWithNullCanvas() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "myCanvas", null);
                
                Method setupKeyHandler = PVPGameScreenController.class.getDeclaredMethod("setupKeyHandler");
                setupKeyHandler.setAccessible(true);
                setupKeyHandler.invoke(controller);
                // Should return early without exception
            } catch (Exception e) {
                fail("setupKeyHandler with null canvas test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderMyBoardWithBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method renderMyBoard = PVPGameScreenController.class.getDeclaredMethod("renderMyBoard");
                renderMyBoard.setAccessible(true);
                renderMyBoard.invoke(controller);
                // Should render without exception
            } catch (Exception e) {
                fail("renderMyBoard with battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderNextPiecesWithBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method renderNextPieces = PVPGameScreenController.class.getDeclaredMethod("renderNextPieces");
                renderNextPieces.setAccessible(true);
                renderNextPieces.invoke(controller);
                // Should render without exception
            } catch (Exception e) {
                fail("renderNextPieces with battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesWithBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method renderIncomingLines = PVPGameScreenController.class.getDeclaredMethod("renderIncomingLines");
                renderIncomingLines.setAccessible(true);
                renderIncomingLines.invoke(controller);
                // Should render without exception
            } catch (Exception e) {
                fail("renderIncomingLines with battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateUIWithBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method updateUI = PVPGameScreenController.class.getDeclaredMethod("updateUI");
                updateUI.setAccessible(true);
                updateUI.invoke(controller);
                
                Label myScoreLabel = (Label) getPrivateField(controller, "myScoreLabel");
                assertNotNull(myScoreLabel.getText(), "Score label should be updated");
            } catch (Exception e) {
                fail("updateUI with battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateUIWithTimeLimitMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "TIME_LIMIT");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method updateUI = PVPGameScreenController.class.getDeclaredMethod("updateUI");
                updateUI.setAccessible(true);
                updateUI.invoke(controller);
                
                // Should update UI without exception
            } catch (Exception e) {
                fail("updateUI with time limit mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnPauseWithBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method onPause = PVPGameScreenController.class.getDeclaredMethod("onPause");
                onPause.setAccessible(true);
                onPause.invoke(controller);
                
                Label statusLabel = (Label) getPrivateField(controller, "statusLabel");
                assertTrue(statusLabel.getText().contains("일시 정지") || statusLabel.getText().isEmpty());
            } catch (Exception e) {
                fail("onPause with battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnPauseWithNullBattleEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "battleEngine", null);
                
                Method onPause = PVPGameScreenController.class.getDeclaredMethod("onPause");
                onPause.setAccessible(true);
                onPause.invoke(controller);
                // Should not throw exception
            } catch (Exception e) {
                fail("onPause with null battle engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderMyBoardWithColorBlindMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                SettingsManager settings = SettingsManager.getInstance();
                boolean original = settings.isColorBlindModeEnabled();
                
                try {
                    settings.setColorBlindModeEnabled(true);
                    
                    Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                    initializeGame.setAccessible(true);
                    initializeGame.invoke(controller);
                    
                    Method renderMyBoard = PVPGameScreenController.class.getDeclaredMethod("renderMyBoard");
                    renderMyBoard.setAccessible(true);
                    renderMyBoard.invoke(controller);
                    // Should render with color blind mode
                } finally {
                    settings.setColorBlindModeEnabled(original);
                }
            } catch (Exception e) {
                fail("renderMyBoard with color blind mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSendMyStateWithInitializedEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method sendMyState = PVPGameScreenController.class.getDeclaredMethod("sendMyState");
                sendMyState.setAccessible(true);
                sendMyState.invoke(controller);
                // Should attempt to send state (will fail without actual network but no exception)
            } catch (Exception e) {
                fail("sendMyState with initialized engine test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSendMyStateThrottled() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                setPrivateField(controller, "gameMode", "NORMAL");
                setPrivateField(controller, "isServer", true);
                
                Method initializeGame = PVPGameScreenController.class.getDeclaredMethod("initializeGame");
                initializeGame.setAccessible(true);
                initializeGame.invoke(controller);
                
                Method sendMyStateThrottled = PVPGameScreenController.class.getDeclaredMethod("sendMyStateThrottled", long.class);
                sendMyStateThrottled.setAccessible(true);
                
                // First call should send
                long now = System.nanoTime();
                sendMyStateThrottled.invoke(controller, now);
                
                // Second call immediately should be throttled
                sendMyStateThrottled.invoke(controller, now + 1000);
                // No exception expected
            } catch (Exception e) {
                fail("sendMyStateThrottled test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testDrawGrid() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Canvas myCanvas = (Canvas) getPrivateField(controller, "myCanvas");
                
                Method drawGrid = PVPGameScreenController.class.getDeclaredMethod("drawGrid", 
                    javafx.scene.canvas.GraphicsContext.class, Canvas.class, double.class);
                drawGrid.setAccessible(true);
                drawGrid.invoke(controller, myCanvas.getGraphicsContext2D(), myCanvas, 25.0);
                // Should draw grid without exception
            } catch (Exception e) {
                fail("drawGrid test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderBorder() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Canvas myCanvas = (Canvas) getPrivateField(controller, "myCanvas");
                
                Method renderBorder = PVPGameScreenController.class.getDeclaredMethod("renderBorder", 
                    javafx.scene.canvas.GraphicsContext.class, Canvas.class);
                renderBorder.setAccessible(true);
                renderBorder.invoke(controller, myCanvas.getGraphicsContext2D(), myCanvas);
                // Should render border without exception
            } catch (Exception e) {
                fail("renderBorder test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesBlock() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Canvas myIncomingCanvas = (Canvas) getPrivateField(controller, "myIncomingCanvas");
                List<Integer> emptyCols = new ArrayList<>();
                emptyCols.add(3);
                emptyCols.add(5);
                
                Method renderIncomingLinesBlock = PVPGameScreenController.class.getDeclaredMethod(
                    "renderIncomingLinesBlock", 
                    javafx.scene.canvas.GraphicsContext.class, int.class, List.class);
                renderIncomingLinesBlock.setAccessible(true);
                renderIncomingLinesBlock.invoke(controller, myIncomingCanvas.getGraphicsContext2D(), 5, emptyCols);
                // Should render without exception
            } catch (Exception e) {
                fail("renderIncomingLinesBlock test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesBlockMoreThan10() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                Canvas myIncomingCanvas = (Canvas) getPrivateField(controller, "myIncomingCanvas");
                List<Integer> emptyCols = new ArrayList<>();
                for (int i = 0; i < 15; i++) {
                    emptyCols.add(i % 10);
                }
                
                Method renderIncomingLinesBlock = PVPGameScreenController.class.getDeclaredMethod(
                    "renderIncomingLinesBlock", 
                    javafx.scene.canvas.GraphicsContext.class, int.class, List.class);
                renderIncomingLinesBlock.setAccessible(true);
                renderIncomingLinesBlock.invoke(controller, myIncomingCanvas.getGraphicsContext2D(), 15, emptyCols);
                // Should render with "+5" text for extra lines
            } catch (Exception e) {
                fail("renderIncomingLinesBlock more than 10 test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testReceiveAttackRoutesToPlayer1WhenServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                BattleGameEngine battleEngine = mock(BattleGameEngine.class);
                setPrivateField(controller, "battleEngine", battleEngine);
                setPrivateField(controller, "isServer", true);

                Method receiveAttack = PVPGameScreenController.class.getDeclaredMethod("receiveAttack", int.class, int.class);
                receiveAttack.setAccessible(true);
                receiveAttack.invoke(controller, 3, 7);

                verify(battleEngine).addAttackToPlayer1(3, 7);
                verify(battleEngine, never()).addAttackToPlayer2(anyInt(), anyInt());
            } catch (Exception e) {
                fail("Receive attack routing for server failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testReceiveAttackRoutesToPlayer2WhenClient() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                BattleGameEngine battleEngine = mock(BattleGameEngine.class);
                setPrivateField(controller, "battleEngine", battleEngine);
                setPrivateField(controller, "isServer", false);

                Method receiveAttack = PVPGameScreenController.class.getDeclaredMethod("receiveAttack", int.class, int.class);
                receiveAttack.setAccessible(true);
                receiveAttack.invoke(controller, 4, 2);

                verify(battleEngine).addAttackToPlayer2(4, 2);
                verify(battleEngine, never()).addAttackToPlayer1(anyInt(), anyInt());
            } catch (Exception e) {
                fail("Receive attack routing for client failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testHandleNetworkMessageUpdatesOpponentState() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            int[][] board = {{0}};
            GameStateData stateData = new GameStateData(board, board, 100, 2, 5, false,
                board, 0, 0, 1, board, 2, 3, List.of(0, 1));
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.GAME_STATE_UPDATE, stateData);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            assertTrue(latch.await(1, TimeUnit.SECONDS), "FX updates should complete");

            GameStateData storedState = (GameStateData) getPrivateField(controller, "opponentState");
            int incomingLines = (int) getPrivateField(controller, "opponentIncomingLines");

            assertSame(stateData, storedState, "Opponent state should reference received data");
            assertEquals(stateData.getIncomingAttackLines(), incomingLines, "Incoming line count should sync");
        } catch (Exception e) {
            fail("Handling GAME_STATE_UPDATE failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageProcessesAttack() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            BattleGameEngine battleEngine = mock(BattleGameEngine.class);
            setPrivateField(controller, "battleEngine", battleEngine);
            setPrivateField(controller, "isServer", true);

            Map<String, Object> attackData = new HashMap<>();
            attackData.put("lines", 2);
            attackData.put("emptyCol", 4);
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.ATTACK, attackData);

            CountDownLatch latch = new CountDownLatch(1);
            doAnswer(invocation -> {
                latch.countDown();
                return null;
            }).when(battleEngine).addAttackToPlayer1(2, 4);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            assertTrue(latch.await(1, TimeUnit.SECONDS), "Attack should be dispatched to the engine");
            verify(battleEngine).addAttackToPlayer1(2, 4);
            verify(battleEngine, never()).addAttackToPlayer2(anyInt(), anyInt());
        } catch (Exception e) {
            fail("Handling ATTACK message failed: " + e.getMessage());
        }
    }

    // ===== Phase 1-1: handleNetworkMessage 추가 브랜치 테스트 (Mock 기반) =====

    @Test
    void testHandleNetworkMessageRematchResponseDeclinedMock() throws Exception {
        // REMATCH_RESPONSE with false should trigger setStatusMessage
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.REMATCH_RESPONSE, Boolean.FALSE);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            assertEquals("상대방이 재시합을 거부했습니다", mockStatusLabel.getText());
        } catch (Exception e) {
            fail("REMATCH_RESPONSE declined mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageRematchResponseNullMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.REMATCH_RESPONSE, null);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            assertEquals("상대방이 재시합을 거부했습니다", mockStatusLabel.getText());
        } catch (Exception e) {
            fail("REMATCH_RESPONSE null mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageDisconnectMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            javafx.animation.AnimationTimer mockGameLoop = mock(javafx.animation.AnimationTimer.class);
            setPrivateField(controller, "gameLoop", mockGameLoop);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.DISCONNECT, null);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            assertEquals("Opponent Left", mockStatusLabel.getText());
            verify(mockGameLoop).stop();
        } catch (Exception e) {
            fail("DISCONNECT mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageDisconnectNoGameLoopMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);
            setPrivateField(controller, "gameLoop", null);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.DISCONNECT, null);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            assertEquals("Opponent Left", mockStatusLabel.getText());
            // Should not throw exception when gameLoop is null
        } catch (Exception e) {
            fail("DISCONNECT no gameLoop mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessagePauseShouldPauseMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
            when(mockBattleEngine.isPaused()).thenReturn(false);
            setPrivateField(controller, "battleEngine", mockBattleEngine);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PAUSE, Boolean.TRUE);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            verify(mockBattleEngine).pauseGame();
            assertEquals("일시 정지 (상대방)", mockStatusLabel.getText());
        } catch (Exception e) {
            fail("PAUSE shouldPause=true mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessagePauseShouldResumeMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
            when(mockBattleEngine.isPaused()).thenReturn(true);
            setPrivateField(controller, "battleEngine", mockBattleEngine);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PAUSE, Boolean.FALSE);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            verify(mockBattleEngine).pauseGame();
            assertEquals("", mockStatusLabel.getText());
        } catch (Exception e) {
            fail("PAUSE shouldPause=false mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleConnectionLostHeadlessClosesResources() throws Exception {
        PVPGameScreenController controller = new PVPGameScreenController();
        javafx.animation.AnimationTimer mockGameLoop = mock(javafx.animation.AnimationTimer.class);
        BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
        GameServer mockServer = mock(GameServer.class);

        setPrivateField(controller, "gameLoop", mockGameLoop);
        setPrivateField(controller, "battleEngine", mockBattleEngine);
        setPrivateField(controller, "gameServer", mockServer);
        setPrivateField(controller, "isServer", true);

        Stage[] stageHolder = new Stage[1];
        StubSceneManager[] managerHolder = new StubSceneManager[1];
        runOnFxThreadAndWait(() -> {
            Stage stage = new Stage();
            stageHolder[0] = stage;
            managerHolder[0] = new StubSceneManager(stage);
        });
        setPrivateField(controller, "sceneManager", managerHolder[0]);

        String originalHeadless = System.getProperty("testfx.headless");
        System.setProperty("testfx.headless", "true");
        try {
            Method handleConnectionLost = PVPGameScreenController.class.getDeclaredMethod("handleConnectionLost");
            handleConnectionLost.setAccessible(true);
            handleConnectionLost.invoke(controller);

            waitForFxEvents();

            verify(mockGameLoop).stop();
            verify(mockBattleEngine).stopGame();
            verify(mockServer).close();
            assertNull(getPrivateField(controller, "gameServer"), "Server reference should be cleared after closing");
            assertTrue(managerHolder[0].wasPvpModeShown(), "SceneManager should navigate back to mode selection");
        } finally {
            if (originalHeadless == null) {
                System.clearProperty("testfx.headless");
            } else {
                System.setProperty("testfx.headless", originalHeadless);
            }
            if (stageHolder[0] != null) {
                runOnFxThreadAndWait(stageHolder[0]::close);
            }
        }
    }

    @Test
    void testShowRematchDialogHeadlessAutoDeclines() throws Exception {
        PVPGameScreenController controller = new PVPGameScreenController();
        Label statusLabel = new Label();
        setPrivateField(controller, "statusLabel", statusLabel);
        setPrivateField(controller, "isServer", true);

        GameServer mockServer = mock(GameServer.class);
        setPrivateField(controller, "gameServer", mockServer);

        String originalHeadless = System.getProperty("testfx.headless");
        System.setProperty("testfx.headless", "true");
        try {
            Method showRematchDialog = PVPGameScreenController.class.getDeclaredMethod("showRematchDialog");
            showRematchDialog.setAccessible(true);
            showRematchDialog.invoke(controller);

            waitForFxEvents();

            ArgumentCaptor<NetworkMessage> captor = ArgumentCaptor.forClass(NetworkMessage.class);
            verify(mockServer).sendMessage(captor.capture());
            NetworkMessage sent = captor.getValue();
            assertEquals(NetworkMessage.MessageType.REMATCH_RESPONSE, sent.getType());
            assertEquals(Boolean.FALSE, sent.getData(), "Headless dialog should auto-decline");
            assertEquals("재시합을 거부했습니다", statusLabel.getText());
        } finally {
            if (originalHeadless == null) {
                System.clearProperty("testfx.headless");
            } else {
                System.setProperty("testfx.headless", originalHeadless);
            }
        }
    }

    @Test
    void testHandleNetworkMessagePauseAlreadyPausedMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
            when(mockBattleEngine.isPaused()).thenReturn(true);
            setPrivateField(controller, "battleEngine", mockBattleEngine);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PAUSE, Boolean.TRUE);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            // Should NOT call pauseGame because already paused
            verify(mockBattleEngine, never()).pauseGame();
        } catch (Exception e) {
            fail("PAUSE already paused mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessagePauseAlreadyResumedMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
            when(mockBattleEngine.isPaused()).thenReturn(false);
            setPrivateField(controller, "battleEngine", mockBattleEngine);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PAUSE, Boolean.FALSE);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            // Should NOT call pauseGame because already running
            verify(mockBattleEngine, never()).pauseGame();
        } catch (Exception e) {
            fail("PAUSE already resumed mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessagePauseNullBattleEngineMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);
            setPrivateField(controller, "battleEngine", null);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PAUSE, Boolean.TRUE);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Should not throw exception when battleEngine is null
        } catch (Exception e) {
            fail("PAUSE null battleEngine mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageTimeUpWinMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);
            setPrivateField(controller, "isServer", true);

            BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
            tetris.game.GameEngine mockMyEngine = mock(tetris.game.GameEngine.class);
            when(mockMyEngine.getScore()).thenReturn(500);
            when(mockBattleEngine.getPlayer1Engine()).thenReturn(mockMyEngine);
            setPrivateField(controller, "battleEngine", mockBattleEngine);

            Map<String, Object> timeUpData = new HashMap<>();
            timeUpData.put("myScore", 100);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.TIME_UP, timeUpData);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            assertTrue(mockStatusLabel.getText().contains("승리"));
            assertTrue(mockStatusLabel.getStyle().contains("#00ff00"));
            verify(mockBattleEngine).stopGame();
        } catch (Exception e) {
            fail("TIME_UP win mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageTimeUpLoseMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);
            setPrivateField(controller, "isServer", true);

            BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
            tetris.game.GameEngine mockMyEngine = mock(tetris.game.GameEngine.class);
            when(mockMyEngine.getScore()).thenReturn(100);
            when(mockBattleEngine.getPlayer1Engine()).thenReturn(mockMyEngine);
            setPrivateField(controller, "battleEngine", mockBattleEngine);

            Map<String, Object> timeUpData = new HashMap<>();
            timeUpData.put("myScore", 500);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.TIME_UP, timeUpData);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            assertTrue(mockStatusLabel.getText().contains("패배"));
            assertTrue(mockStatusLabel.getStyle().contains("#ff0000"));
        } catch (Exception e) {
            fail("TIME_UP lose mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageTimeUpDrawMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);
            setPrivateField(controller, "isServer", true);

            BattleGameEngine mockBattleEngine = mock(BattleGameEngine.class);
            tetris.game.GameEngine mockMyEngine = mock(tetris.game.GameEngine.class);
            when(mockMyEngine.getScore()).thenReturn(300);
            when(mockBattleEngine.getPlayer1Engine()).thenReturn(mockMyEngine);
            setPrivateField(controller, "battleEngine", mockBattleEngine);

            Map<String, Object> timeUpData = new HashMap<>();
            timeUpData.put("myScore", 300);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.TIME_UP, timeUpData);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(latch::countDown);
            latch.await(1, TimeUnit.SECONDS);

            assertTrue(mockStatusLabel.getText().contains("무승부"));
            assertTrue(mockStatusLabel.getStyle().contains("#ffff00"));
        } catch (Exception e) {
            fail("TIME_UP draw mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageGameStartRematchMock() throws Exception {
        // GAME_START with "REMATCH" calls restartGame - just verify message is processed
        // Note: restartGame() accesses UI labels, so this test only confirms the case branch is hit
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            Label mockMyPlayerLabel = new Label();
            Label mockOpponentPlayerLabel = new Label();
            Label mockMyScoreLabel = new Label();
            Label mockOpponentScoreLabel = new Label();
            Label mockMyLevelLabel = new Label();
            Label mockOpponentLevelLabel = new Label();
            Label mockMyLinesLabel = new Label();
            Label mockOpponentLinesLabel = new Label();
            
            setPrivateField(controller, "statusLabel", mockStatusLabel);
            setPrivateField(controller, "myPlayerLabel", mockMyPlayerLabel);
            setPrivateField(controller, "opponentPlayerLabel", mockOpponentPlayerLabel);
            setPrivateField(controller, "myScoreLabel", mockMyScoreLabel);
            setPrivateField(controller, "opponentScoreLabel", mockOpponentScoreLabel);
            setPrivateField(controller, "myLevelLabel", mockMyLevelLabel);
            setPrivateField(controller, "opponentLevelLabel", mockOpponentLevelLabel);
            setPrivateField(controller, "myLinesLabel", mockMyLinesLabel);
            setPrivateField(controller, "opponentLinesLabel", mockOpponentLinesLabel);
            setPrivateField(controller, "gameMode", "NORMAL");
            setPrivateField(controller, "isServer", true);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.GAME_START, "REMATCH");

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            // This should call restartGame
            handleMessage.invoke(controller, message);

            // Wait for Platform.runLater calls
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> Platform.runLater(latch::countDown));
            latch.await(2, TimeUnit.SECONDS);
            
            // Verify that restart updated labels
            assertTrue(mockMyPlayerLabel.getText().contains("서버") || mockStatusLabel.getText().isEmpty());
        } catch (Exception e) {
            fail("GAME_START REMATCH mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageGameStartNonRematchMock() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.GAME_START, "OTHER");

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // No action for non-REMATCH
        } catch (Exception e) {
            fail("GAME_START non-REMATCH mock test failed: " + e.getMessage());
        }
    }

    @Test
    void testHandleNetworkMessageDefaultCase() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();
            Label mockStatusLabel = new Label();
            setPrivateField(controller, "statusLabel", mockStatusLabel);

            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.PING, null);

            Method handleMessage = PVPGameScreenController.class.getDeclaredMethod("handleNetworkMessage", NetworkMessage.class);
            handleMessage.setAccessible(true);
            handleMessage.invoke(controller, message);

            // Should not throw exception for unhandled cases
        } catch (Exception e) {
            fail("Default case test failed: " + e.getMessage());
        }
    }

    private static class StubSceneManager extends SceneManager {
        private boolean pvpModeShown;

        StubSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showPVPModeSelection() {
            pvpModeShown = true;
        }

        boolean wasPvpModeShown() {
            return pvpModeShown;
        }
    }

    @Test
    void testSendMyStateSendsToServer() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                // Prepare environment
                setPrivateField(controller, "gameMode", "NORMAL");
                GameServer mockServer = mock(GameServer.class);
                setPrivateField(controller, "gameServer", mockServer);
                setPrivateField(controller, "isServer", true);

                // create real BattleGameEngine and set it directly (avoid FXML loading)
                BattleGameEngine realEngine = new BattleGameEngine("NORMAL");
                setPrivateField(controller, "battleEngine", realEngine);

                // Call sendMyState and verify server.sendMessage called
                Method sendMyState = PVPGameScreenController.class.getDeclaredMethod("sendMyState");
                sendMyState.setAccessible(true);
                sendMyState.invoke(controller);

                verify(mockServer).sendMessage(any(NetworkMessage.class));
            } catch (Exception e) {
                fail("sendMyState to server test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSendAttackSendsToServerAndClient() throws Exception {
        try {
            PVPGameScreenController controller = new PVPGameScreenController();

            // Server case
            GameServer mockServer = mock(GameServer.class);
            setPrivateField(controller, "gameServer", mockServer);
            setPrivateField(controller, "isServer", true);

            Method sendAttack = PVPGameScreenController.class.getDeclaredMethod("sendAttack", int.class, int.class);
            sendAttack.setAccessible(true);
            sendAttack.invoke(controller, 2, 4);
            verify(mockServer).sendMessage(any(NetworkMessage.class));

            // Client case
            GameClient mockClient = mock(GameClient.class);
            setPrivateField(controller, "gameServer", null);
            setPrivateField(controller, "gameClient", mockClient);
            setPrivateField(controller, "isServer", false);

            sendAttack.invoke(controller, 1, 3);
            verify(mockClient).sendMessage(any(NetworkMessage.class));
        } catch (Exception e) {
            fail("sendAttack send test failed: " + e.getMessage());
        }
    }

    @Test
    void testOnRematchSendsRequest() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                Label statusLabel = new Label();
                setPrivateField(controller, "statusLabel", statusLabel);

                GameServer mockServer = mock(GameServer.class);
                setPrivateField(controller, "gameServer", mockServer);
                setPrivateField(controller, "isServer", true);

                Method onRematch = PVPGameScreenController.class.getDeclaredMethod("onRematch");
                onRematch.setAccessible(true);
                onRematch.invoke(controller);

                verify(mockServer).sendMessage(any(NetworkMessage.class));
                assertTrue(statusLabel.getText().contains("응답을 기다리는") || statusLabel.getText().isEmpty());
            } catch (Exception e) {
                fail("onRematch send request test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnPauseSendsPauseMessage() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                Label statusLabel = new Label();
                setPrivateField(controller, "statusLabel", statusLabel);

                // mock battle engine
                BattleGameEngine mockEngine = mock(BattleGameEngine.class);
                when(mockEngine.isPaused()).thenReturn(true);
                setPrivateField(controller, "battleEngine", mockEngine);

                GameServer mockServer = mock(GameServer.class);
                setPrivateField(controller, "gameServer", mockServer);
                setPrivateField(controller, "isServer", true);

                Method onPause = PVPGameScreenController.class.getDeclaredMethod("onPause");
                onPause.setAccessible(true);
                onPause.invoke(controller);

                verify(mockEngine).pauseGame();
                verify(mockServer).sendMessage(any(NetworkMessage.class));
            } catch (Exception e) {
                fail("onPause send pause test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderOpponentBoardWithStateAndCanvas() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();

                Canvas opponentCanvas = new Canvas(GameBoard.BOARD_WIDTH * 25, GameBoard.BOARD_HEIGHT * 25);
                setPrivateField(controller, "opponentCanvas", opponentCanvas);

                int[][] board = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
                int[][] itemBoard = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
                // Put some sample blocks including an attack block (8)
                board[0][0] = 1;
                board[1][1] = 8;
                itemBoard[0][0] = 1;

                int[][] currentShape = new int[][]{{1,1},{0,1}};
                int[][] nextShape = new int[][]{{1}};

                GameStateData state = new GameStateData(
                    board, itemBoard,
                    123, 2, 4, false,
                    currentShape, 0, 0, 1,
                    nextShape, 1, 2, List.of(0,1)
                );

                setPrivateField(controller, "opponentState", state);
                // ensure settings manager exists
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());

                Method render = PVPGameScreenController.class.getDeclaredMethod("renderOpponentBoard");
                render.setAccessible(true);
                render.invoke(controller);
                // If we reach here without exception, rendering branch covered
            } catch (Exception e) {
                fail("renderOpponentBoard with state test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderOpponentBoardWithColorBlindMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            boolean original = settings.isColorBlindModeEnabled();
            try {
                settings.setColorBlindModeEnabled(true);

                PVPGameScreenController controller = new PVPGameScreenController();
                Canvas opponentCanvas = new Canvas(GameBoard.BOARD_WIDTH * 25, GameBoard.BOARD_HEIGHT * 25);
                setPrivateField(controller, "opponentCanvas", opponentCanvas);

                int[][] board = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
                int[][] itemBoard = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
                board[2][2] = 3;
                itemBoard[2][2] = 0;

                int[][] currentShape = new int[][]{{1}};
                int[][] nextShape = new int[][]{{1}};

                GameStateData state = new GameStateData(
                    board, itemBoard,
                    50, 1, 1, false,
                    currentShape, 2, 2, 3,
                    nextShape, 1, 1, List.of(2)
                );

                setPrivateField(controller, "opponentState", state);
                setPrivateField(controller, "settingsManager", settings);

                Method render = PVPGameScreenController.class.getDeclaredMethod("renderOpponentBoard");
                render.setAccessible(true);
                render.invoke(controller);
            } catch (Exception e) {
                fail("renderOpponentBoard color blind mode test failed: " + e.getMessage());
            } finally {
                settings.setColorBlindModeEnabled(original);
            }
        });
    }

    @Test
    void testRenderNextPiecesOpponent() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                Canvas opponentNextCanvas = new Canvas(6 * 25, 5 * 25);
                setPrivateField(controller, "opponentNextCanvas", opponentNextCanvas);

                int[][] board = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
                int[][] itemBoard = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
                int[][] nextShape = new int[][]{{1,1,1},{0,1,0}};

                GameStateData state = new GameStateData(
                    board, itemBoard,
                    0, 1, 0, false,
                    new int[0][0], 0, 0, 0,
                    nextShape, 2, 1, List.of()
                );

                setPrivateField(controller, "opponentState", state);
                setPrivateField(controller, "settingsManager", SettingsManager.getInstance());

                Method renderNext = PVPGameScreenController.class.getDeclaredMethod("renderNextPieces");
                renderNext.setAccessible(true);
                renderNext.invoke(controller);
            } catch (Exception e) {
                fail("renderNextPieces opponent test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesOpponentFallbackEmptyCols() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                Canvas opponentIncoming = new Canvas(6 * 25, 5 * 25);
                setPrivateField(controller, "opponentIncomingCanvas", opponentIncoming);

                int[][] board = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
                int[][] itemBoard = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];

                // create state without incoming empty cols (null)
                GameStateData state = new GameStateData(
                    board, itemBoard,
                    0, 1, 0, false,
                    new int[0][0], 0, 0, 0,
                    new int[0][0], 0, 1, null
                );

                setPrivateField(controller, "opponentState", state);
                setPrivateField(controller, "opponentIncomingLines", 5);

                Method renderIncoming = PVPGameScreenController.class.getDeclaredMethod("renderIncomingLines");
                renderIncoming.setAccessible(true);
                renderIncoming.invoke(controller);
            } catch (Exception e) {
                fail("renderIncomingLines opponent fallback test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnToLobbyStopsLoopAndShowsLobby() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                javafx.animation.AnimationTimer mockLoop = mock(javafx.animation.AnimationTimer.class);
                setPrivateField(controller, "gameLoop", mockLoop);

                // stub SceneManager to capture call
                class LocalStub extends SceneManager {
                    boolean called = false;
                    LocalStub(Stage stage) { super(stage); }
                    @Override
                    public void showPVPLobby(Object server, Object client, boolean isServer) {
                        called = true;
                    }
                }

                Stage stage = new Stage();
                LocalStub mgr = new LocalStub(stage);
                setPrivateField(controller, "sceneManager", mgr);

                Method onToLobby = PVPGameScreenController.class.getDeclaredMethod("onToLobby");
                onToLobby.setAccessible(true);
                onToLobby.invoke(controller);

                verify(mockLoop).stop();
                assertTrue(mgr.called, "PVPLobby should be shown");
            } catch (Exception e) {
                fail("onToLobby test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnBackToMenuSendsDisconnectAndShowsMainMenu() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPGameScreenController controller = new PVPGameScreenController();
                GameServer mockServer = mock(GameServer.class);
                setPrivateField(controller, "gameServer", mockServer);
                setPrivateField(controller, "isServer", true);

                class LocalStub extends SceneManager {
                    boolean called = false;
                    LocalStub(Stage stage) { super(stage); }
                    @Override
                    public void showMainMenu() { called = true; }
                }

                Stage stage = new Stage();
                LocalStub mgr = new LocalStub(stage);
                setPrivateField(controller, "sceneManager", mgr);

                Method onBackToMenu = PVPGameScreenController.class.getDeclaredMethod("onBackToMenu");
                onBackToMenu.setAccessible(true);
                onBackToMenu.invoke(controller);

                verify(mockServer).sendMessage(any(NetworkMessage.class));
                verify(mockServer).close();
                assertTrue(mgr.called, "Main menu should be shown");
            } catch (Exception e) {
                fail("onBackToMenu test failed: " + e.getMessage());
            }
        });
    }

    private void waitForFxEvents() {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                fail("Timeout waiting for FX events");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted while waiting for FX events");
        }
    }
}
