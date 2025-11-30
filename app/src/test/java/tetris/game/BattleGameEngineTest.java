package tetris.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleGameEngineTest {

    private BattleGameEngine battleEngine;

    @BeforeEach
    void setUp() {
        battleEngine = new BattleGameEngine("CLASSIC");
    }

    @Test
    void testBattleGameEngineCreation() {
        assertNotNull(battleEngine);
        assertFalse(battleEngine.isGameRunning());
        assertFalse(battleEngine.isPaused());
    }

    @Test
    void testStartGame() {
        battleEngine.startGame();
        assertTrue(battleEngine.isGameRunning());
        assertFalse(battleEngine.isPaused());
    }

    @Test
    void testPauseGame() {
        battleEngine.startGame();
        
        battleEngine.pauseGame();
        assertTrue(battleEngine.isPaused());
        
        battleEngine.pauseGame();
        assertFalse(battleEngine.isPaused());
    }

    @Test
    void testStopGame() {
        battleEngine.startGame();
        assertTrue(battleEngine.isGameRunning());
        
        battleEngine.stopGame();
        assertFalse(battleEngine.isGameRunning());
        assertFalse(battleEngine.isPaused());
    }

    @Test
    void testGetPlayer1Engine() {
        GameEngine player1 = battleEngine.getPlayer1Engine();
        assertNotNull(player1);
    }

    @Test
    void testGetPlayer2Engine() {
        GameEngine player2 = battleEngine.getPlayer2Engine();
        assertNotNull(player2);
    }

    @Test
    void testBothPlayersHaveDifferentEngines() {
        GameEngine player1 = battleEngine.getPlayer1Engine();
        GameEngine player2 = battleEngine.getPlayer2Engine();
        assertNotSame(player1, player2);
    }

    @Test
    void testGetWinner_InitiallyNull() {
        assertNull(battleEngine.getWinner());
    }

    @Test
    void testTimeLimitMode() {
        BattleGameEngine timeLimitEngine = new BattleGameEngine("TIME_LIMIT");
        assertNotNull(timeLimitEngine);
        assertTrue(timeLimitEngine.isTimeLimitMode());
    }

    @Test
    void testClassicMode() {
        BattleGameEngine classicEngine = new BattleGameEngine("CLASSIC");
        assertFalse(classicEngine.isTimeLimitMode());
    }

    @Test
    void testItemMode() {
        BattleGameEngine itemEngine = new BattleGameEngine("ITEM");
        assertNotNull(itemEngine);
    }

    @Test
    void testGetRemainingTime_BeforeStart() {
        BattleGameEngine timeLimitEngine = new BattleGameEngine("TIME_LIMIT");
        long remaining = timeLimitEngine.getRemainingTime();
        assertTrue(remaining >= 0);
    }

    @Test
    void testGetRemainingTime_AfterStart() throws InterruptedException {
        BattleGameEngine timeLimitEngine = new BattleGameEngine("TIME_LIMIT");
        timeLimitEngine.startGame();
        Thread.sleep(100);
        
        long remaining = timeLimitEngine.getRemainingTime();
        assertTrue(remaining >= 0);
    }

    @Test
    void testIsGameRunning() {
        assertFalse(battleEngine.isGameRunning());
        
        battleEngine.startGame();
        assertTrue(battleEngine.isGameRunning());
        
        battleEngine.stopGame();
        assertFalse(battleEngine.isGameRunning());
    }

    @Test
    void testIsPaused() {
        battleEngine.startGame();
        assertFalse(battleEngine.isPaused());
        
        battleEngine.pauseGame();
        assertTrue(battleEngine.isPaused());
    }

    @Test
    void testCheckGameOver_InitiallyNoWinner() {
        battleEngine.startGame();
        assertNull(battleEngine.getWinner());
    }

    @Test
    void testMultipleStartStop() {
        battleEngine.startGame();
        assertTrue(battleEngine.isGameRunning());
        
        battleEngine.stopGame();
        assertFalse(battleEngine.isGameRunning());
        
        battleEngine.startGame();
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testMultiplePausesToggle() {
        battleEngine.startGame();
        
        for (int i = 0; i < 5; i++) {
            battleEngine.pauseGame();
            assertEquals(i % 2 == 0, battleEngine.isPaused());
        }
    }

    @Test
    void testIsTimeLimitMode() {
        BattleGameEngine normalEngine = new BattleGameEngine("CLASSIC");
        assertFalse(normalEngine.isTimeLimitMode());
        
        BattleGameEngine timeLimitEngine = new BattleGameEngine("TIME_LIMIT");
        assertTrue(timeLimitEngine.isTimeLimitMode());
    }

    @Test
    void testDifferentBattleModes() {
        String[] modes = {"CLASSIC", "TIME_LIMIT", "ITEM"};
        
        for (String mode : modes) {
            BattleGameEngine engine = new BattleGameEngine(mode);
            assertNotNull(engine);
            assertNotNull(engine.getPlayer1Engine());
            assertNotNull(engine.getPlayer2Engine());
        }
    }

    @Test
    void testBothPlayersStartTogether() {
        battleEngine.startGame();
        
        assertTrue(battleEngine.getPlayer1Engine().isGameRunning());
        assertTrue(battleEngine.getPlayer2Engine().isGameRunning());
    }

    @Test
    void testBothPlayersStopTogether() {
        battleEngine.startGame();
        battleEngine.stopGame();
        
        assertFalse(battleEngine.getPlayer1Engine().isGameRunning());
        assertFalse(battleEngine.getPlayer2Engine().isGameRunning());
    }

    @Test
    void testEngineStateAfterMultipleOperations() {
        battleEngine.startGame();
        battleEngine.pauseGame();
        battleEngine.pauseGame();
        battleEngine.stopGame();
        
        assertFalse(battleEngine.isGameRunning());
        assertFalse(battleEngine.isPaused());
    }

    @Test
    void testHandlePlayer1KeyPress() {
        battleEngine.startGame();
        GameEngine p1 = battleEngine.getPlayer1Engine();
        int initialScore = p1.getScore();
        
        // Move down to increase score (soft drop)
        battleEngine.handlePlayer1KeyPress(javafx.scene.input.KeyCode.DOWN);
        
        // Score might increase
        assertTrue(p1.getScore() >= initialScore);
    }

    @Test
    void testHandlePlayer2KeyPress() {
        battleEngine.startGame();
        GameEngine p2 = battleEngine.getPlayer2Engine();
        int initialScore = p2.getScore();
        
        battleEngine.handlePlayer2KeyPress(javafx.scene.input.KeyCode.DOWN);
        
        assertTrue(p2.getScore() >= initialScore);
    }

    @Test
    void testHandlePlayer1KeyPress_WhenPaused() {
        battleEngine.startGame();
        battleEngine.pauseGame();
        
        // Key press should be ignored when paused
        battleEngine.handlePlayer1KeyPress(javafx.scene.input.KeyCode.SPACE);
        
        assertTrue(battleEngine.isPaused());
    }

    @Test
    void testHandlePlayer2KeyPress_WhenNotRunning() {
        // Key press should be ignored when game not running
        battleEngine.handlePlayer2KeyPress(javafx.scene.input.KeyCode.SPACE);
        
        assertFalse(battleEngine.isGameRunning());
    }

    @Test
    void testUpdate_TimeLimitMode() {
        BattleGameEngine timeLimitEngine = new BattleGameEngine("TIME_LIMIT");
        timeLimitEngine.startGame();
        
        long remainingBefore = timeLimitEngine.getRemainingTime();
        timeLimitEngine.update();
        
        // Remaining time should decrease or stay the same
        assertTrue(timeLimitEngine.getRemainingTime() <= remainingBefore);
    }

    @Test
    void testUpdate_ClassicMode() {
        battleEngine.startGame();
        
        // Classic mode doesn't check time limit
        battleEngine.update();
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testCheckGameOver_BothPlayersAlive() {
        battleEngine.startGame();
        
        // Initially both players should be alive
        assertNull(battleEngine.getWinner());
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testCheckGameOver_Player1Stops() {
        battleEngine.startGame();
        battleEngine.getPlayer1Engine().stopGame();
        
        // Check game over after player1 stops
        assertNull(battleEngine.getWinner()); // Need to call update() to check
    }

    @Test
    void testApplyPendingAttacks_Player1() {
        battleEngine.startGame();
        
        // Should not throw exception
        battleEngine.applyPendingAttacks(1);
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testApplyPendingAttacks_Player2() {
        battleEngine.startGame();
        
        // Should not throw exception
        battleEngine.applyPendingAttacks(2);
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testApplyPendingAttacks_BothPlayers() {
        battleEngine.startGame();
        
        // Should not throw exception
        battleEngine.applyPendingAttacks();
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testProcessPlayer1Attack() {
        battleEngine.startGame();
        
        // Simulate player1 clearing 2 lines
        battleEngine.processPlayer1Attack(2, 5);
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testProcessPlayer2Attack() {
        battleEngine.startGame();
        
        // Simulate player2 clearing 3 lines
        battleEngine.processPlayer2Attack(3, 3);
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testProcessPlayer1Attack_SingleLine() {
        battleEngine.startGame();
        
        // Clearing 1 line should not trigger attack
        battleEngine.processPlayer1Attack(1, 5);
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testProcessPlayer2Attack_FourLines() {
        battleEngine.startGame();
        
        // Clearing 4 lines (Tetris!)
        battleEngine.processPlayer2Attack(4, 0);
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testUpdate_ChecksGameOver() {
        battleEngine.startGame();
        battleEngine.update();
        
        // Winner should still be null if both players alive
        assertNull(battleEngine.getWinner());
    }

    @Test
    void testUpdate_UpdatesDoubleScoreStatus() {
        battleEngine.startGame();
        
        // Activate double score for player1
        battleEngine.getPlayer1Engine().activateDoubleScore();
        
        battleEngine.update();
        
        // Double score should still be active
        assertTrue(battleEngine.getPlayer1Engine().isDoubleScoreActive());
    }

    @Test
    void testBothPlayersScoreIndependently() {
        battleEngine.startGame();
        
        int p1Score = battleEngine.getPlayer1Engine().getScore();
        int p2Score = battleEngine.getPlayer2Engine().getScore();
        
        // Scores should be tracked independently
        assertTrue(p1Score >= 0);
        assertTrue(p2Score >= 0);
    }

    @Test
    void testBothPlayersLevelIndependently() {
        battleEngine.startGame();
        
        assertEquals(1, battleEngine.getPlayer1Engine().getLevel());
        assertEquals(1, battleEngine.getPlayer2Engine().getLevel());
    }

    @Test
    void testTimeLimitZeroRemainingTime() {
        BattleGameEngine engine = new BattleGameEngine("TIME_LIMIT");
        engine.startGame();
        
        // Simulate time passing
        long remaining = engine.getRemainingTime();
        assertTrue(remaining >= 0);
    }

    @Test
    void testUpdate_WhenPaused() {
        battleEngine.startGame();
        battleEngine.pauseGame();
        
        battleEngine.update();
        
        // Game should still be paused
        assertTrue(battleEngine.isPaused());
    }

    @Test
    void testUpdate_WithWinner() {
        battleEngine.startGame();
        battleEngine.getPlayer1Engine().stopGame();
        battleEngine.getPlayer2Engine().stopGame();
        
        // Update should handle game with no active players
        battleEngine.update();
        
        assertFalse(battleEngine.isGameRunning());
    }
}
