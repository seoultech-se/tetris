package tetris.game;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tetris.ui.SettingsManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BattleGameEngine의 테스트 클래스
 * 배틀 모드와 제한시간 모드를 포함한 2인 플레이어 게임 엔진을 테스트합니다.
 */
class BattleGameEngineTest {

    private BattleGameEngine battleEngine;
    private BattleGameEngine timeLimitEngine;
    private BattleGameEngine itemBattleEngine;

    @BeforeEach
    void setUp() {
        SettingsManager.getInstance().setGameMode("NORMAL");
        SettingsManager.getInstance().setDifficulty("Normal");
        battleEngine = new BattleGameEngine("NORMAL"); // 일반 배틀 모드
        timeLimitEngine = new BattleGameEngine("TIME_LIMIT"); // 제한시간 모드
        itemBattleEngine = new BattleGameEngine("ITEM"); // 아이템 배틀 모드
    }

    // ==================== 생성자 및 초기화 테스트 ====================

    @Test
    void testBattleEngineCreation_NormalMode() {
        assertNotNull(battleEngine);
        assertNotNull(battleEngine.getPlayer1Engine());
        assertNotNull(battleEngine.getPlayer2Engine());
        assertFalse(battleEngine.isGameRunning());
        assertFalse(battleEngine.isPaused());
        assertNull(battleEngine.getWinner());
    }

    @Test
    void testBattleEngineCreation_TimeLimitMode() {
        assertNotNull(timeLimitEngine);
        assertTrue(timeLimitEngine.isTimeLimitMode());
        // 기본 3분 (180초), 게임 시작 전이므로 0 반환
        assertEquals(0, timeLimitEngine.getRemainingTime());
    }
    
    @Test
    void testBattleEngineCreation_ItemMode() {
        assertNotNull(itemBattleEngine);
        assertFalse(itemBattleEngine.isTimeLimitMode());
    }

    @Test
    void testGetPlayer1Engine() {
        GameEngine player1 = battleEngine.getPlayer1Engine();
        assertNotNull(player1);
        assertFalse(player1.isGameRunning());
    }

    @Test
    void testGetPlayer2Engine() {
        GameEngine player2 = battleEngine.getPlayer2Engine();
        assertNotNull(player2);
        assertFalse(player2.isGameRunning());
    }

    // ==================== 게임 시작/일시정지/중지 테스트 ====================

    @Test
    void testStartGame() {
        battleEngine.startGame();
        assertTrue(battleEngine.isGameRunning());
        assertTrue(battleEngine.getPlayer1Engine().isGameRunning());
        assertTrue(battleEngine.getPlayer2Engine().isGameRunning());
    }

    @Test
    void testPauseGame() {
        battleEngine.startGame();
        assertFalse(battleEngine.isPaused());
        
        battleEngine.pauseGame();
        assertTrue(battleEngine.isPaused());
        assertTrue(battleEngine.getPlayer1Engine().isPaused());
        assertTrue(battleEngine.getPlayer2Engine().isPaused());
    }

    @Test
    void testPauseGame_Toggle() {
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
        assertFalse(battleEngine.getPlayer1Engine().isGameRunning());
        assertFalse(battleEngine.getPlayer2Engine().isGameRunning());
    }

    // ==================== 플레이어 1 키 입력 테스트 ====================

    @Test
    void testHandlePlayer1KeyPress_MoveLeft() {
        battleEngine.startGame();
        Piece before = battleEngine.getPlayer1Engine().getCurrentPiece();
        int xBefore = before.getX();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.A);
        
        assertNotNull(battleEngine.getPlayer1Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer1KeyPress_MoveRight() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.D);
        
        assertNotNull(battleEngine.getPlayer1Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer1KeyPress_MoveDown() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.S);
        
        assertNotNull(battleEngine.getPlayer1Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer1KeyPress_Rotate() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.W);
        
        assertNotNull(battleEngine.getPlayer1Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer1KeyPress_HardDrop() {
        battleEngine.startGame();
        int scoreBefore = battleEngine.getPlayer1Engine().getScore();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.SPACE);
        
        assertTrue(battleEngine.getPlayer1Engine().getScore() >= scoreBefore);
    }

    @Test
    void testHandlePlayer1KeyPress_GameNotRunning() {
        // 게임이 시작되지 않았을 때 키 입력 무시
        battleEngine.handlePlayer1KeyPress(KeyCode.A);
        assertFalse(battleEngine.isGameRunning());
    }

    @Test
    void testHandlePlayer1KeyPress_GamePaused() {
        battleEngine.startGame();
        battleEngine.pauseGame();
        
        Piece before = battleEngine.getPlayer1Engine().getCurrentPiece();
        int xBefore = before.getX();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.A);
        
        // 일시정지 중에는 이동하지 않아야 함
        assertEquals(xBefore, battleEngine.getPlayer1Engine().getCurrentPiece().getX());
    }

    // ==================== 플레이어 2 키 입력 테스트 ====================

    @Test
    void testHandlePlayer2KeyPress_MoveLeft() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.LEFT);
        
        assertNotNull(battleEngine.getPlayer2Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer2KeyPress_MoveRight() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.RIGHT);
        
        assertNotNull(battleEngine.getPlayer2Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer2KeyPress_MoveDown() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.DOWN);
        
        assertNotNull(battleEngine.getPlayer2Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer2KeyPress_Rotate() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.UP);
        
        assertNotNull(battleEngine.getPlayer2Engine().getCurrentPiece());
    }

    @Test
    void testHandlePlayer2KeyPress_HardDrop() {
        battleEngine.startGame();
        int scoreBefore = battleEngine.getPlayer2Engine().getScore();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.ENTER);
        
        assertTrue(battleEngine.getPlayer2Engine().getScore() >= scoreBefore);
    }

    @Test
    void testHandlePlayer2KeyPress_GameNotRunning() {
        battleEngine.handlePlayer2KeyPress(KeyCode.LEFT);
        assertFalse(battleEngine.isGameRunning());
    }

    @Test
    void testHandlePlayer2KeyPress_GamePaused() {
        battleEngine.startGame();
        battleEngine.pauseGame();
        
        Piece before = battleEngine.getPlayer2Engine().getCurrentPiece();
        int xBefore = before.getX();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.LEFT);
        
        assertEquals(xBefore, battleEngine.getPlayer2Engine().getCurrentPiece().getX());
    }

    // ==================== 공격 시스템 테스트 ====================

    @Test
    void testProcessPlayer1Attack() {
        battleEngine.startGame();
        int linesCleared = 2;
        int lastBlockCol = 5;
        
        battleEngine.processPlayer1Attack(linesCleared, lastBlockCol);
        
        // 공격이 처리되었는지 확인 (플레이어 2에게 대기 줄 추가)
        assertEquals(2, battleEngine.getPendingAttacksToPlayer2());
    }

    @Test
    void testProcessPlayer1Attack_SingleLine() {
        battleEngine.startGame();
        
        battleEngine.processPlayer1Attack(1, 3);
        
        // 1줄 삭제는 공격을 생성하지 않음
        assertEquals(0, battleEngine.getPendingAttacksToPlayer2());
    }

    @Test
    void testProcessPlayer1Attack_TwoLines() {
        battleEngine.startGame();
        
        battleEngine.processPlayer1Attack(2, 4);
        
        assertEquals(2, battleEngine.getPendingAttacksToPlayer2());
    }

    @Test
    void testProcessPlayer1Attack_ThreeLines() {
        battleEngine.startGame();
        
        battleEngine.processPlayer1Attack(3, 5);
        
        assertEquals(3, battleEngine.getPendingAttacksToPlayer2());
    }

    @Test
    void testProcessPlayer1Attack_FourLines() {
        battleEngine.startGame();
        
        battleEngine.processPlayer1Attack(4, 6);
        
        assertEquals(4, battleEngine.getPendingAttacksToPlayer2());
    }

    @Test
    void testProcessPlayer2Attack() {
        battleEngine.startGame();
        int linesCleared = 2;
        int lastBlockCol = 5;
        
        battleEngine.processPlayer2Attack(linesCleared, lastBlockCol);
        
        // 플레이어 1에게 대기 줄 추가
        assertEquals(2, battleEngine.getPendingAttacksToPlayer1());
    }

    @Test
    void testProcessPlayer2Attack_SingleLine() {
        battleEngine.startGame();
        
        battleEngine.processPlayer2Attack(1, 3);
        
        // 1줄 삭제는 공격을 생성하지 않음
        assertEquals(0, battleEngine.getPendingAttacksToPlayer1());
    }

    @Test
    void testProcessPlayer2Attack_TwoLines() {
        battleEngine.startGame();
        
        battleEngine.processPlayer2Attack(2, 4);
        
        assertEquals(2, battleEngine.getPendingAttacksToPlayer1());
    }

    @Test
    void testProcessPlayer2Attack_ThreeLines() {
        battleEngine.startGame();
        
        battleEngine.processPlayer2Attack(3, 5);
        
        assertEquals(3, battleEngine.getPendingAttacksToPlayer1());
    }

    @Test
    void testProcessPlayer2Attack_FourLines() {
        battleEngine.startGame();
        
        battleEngine.processPlayer2Attack(4, 6);
        
        assertEquals(4, battleEngine.getPendingAttacksToPlayer1());
    }

    @Test
    void testApplyPendingAttacks() {
        battleEngine.startGame();
        
        // 플레이어 1이 공격 (플레이어 2에게 방해 줄 추가)
        battleEngine.processPlayer1Attack(3, 5);
        assertEquals(3, battleEngine.getPendingAttacksToPlayer2());
        
        // 보류 중인 공격 적용
        battleEngine.applyPendingAttacks();
        
        // 적용 후 대기 줄 비어야 함
        assertEquals(0, battleEngine.getPendingAttacksToPlayer2());
    }

    @Test
    void testApplyPendingAttacks_NoAttacks() {
        battleEngine.startGame();
        
        // 공격 없이 적용 시도
        battleEngine.applyPendingAttacks();
        
        assertTrue(battleEngine.isGameRunning());
    }
    
    @Test
    void testApplyPendingAttacks_ByPlayerNumber() {
        battleEngine.startGame();
        
        // 플레이어 2에게 공격 추가
        battleEngine.processPlayer1Attack(2, 3);
        assertEquals(2, battleEngine.getPendingAttacksToPlayer2());
        
        // 플레이어 2에게만 적용
        battleEngine.applyPendingAttacks(2);
        assertEquals(0, battleEngine.getPendingAttacksToPlayer2());
    }
    
    @Test
    void testAddAttackToPlayer1() {
        battleEngine.startGame();
        
        battleEngine.addAttackToPlayer1(3, 4);
        
        assertEquals(3, battleEngine.getPendingAttacksToPlayer1());
    }
    
    @Test
    void testAddAttackToPlayer2() {
        battleEngine.startGame();
        
        battleEngine.addAttackToPlayer2(3, 4);
        
        assertEquals(3, battleEngine.getPendingAttacksToPlayer2());
    }
    
    @Test
    void testGetPendingAttackEmptyColsToPlayer1() {
        battleEngine.startGame();
        
        battleEngine.addAttackToPlayer1(2, 5);
        
        var cols = battleEngine.getPendingAttackEmptyColsToPlayer1();
        assertEquals(2, cols.size());
        assertEquals(5, cols.get(0));
        assertEquals(5, cols.get(1));
    }
    
    @Test
    void testGetPendingAttackEmptyColsToPlayer2() {
        battleEngine.startGame();
        
        battleEngine.addAttackToPlayer2(2, 3);
        
        var cols = battleEngine.getPendingAttackEmptyColsToPlayer2();
        assertEquals(2, cols.size());
        assertEquals(3, cols.get(0));
        assertEquals(3, cols.get(1));
    }

    // ==================== 게임 오버 및 승자 결정 테스트 ====================

    @Test
    void testGameOver_CheckedDuringKeyPress() {
        battleEngine.startGame();
        
        // 키 입력 중 게임오버 체크가 호출됨
        battleEngine.handlePlayer1KeyPress(KeyCode.S);
        
        assertTrue(battleEngine.isGameRunning());
        assertNull(battleEngine.getWinner());
    }

    @Test
    void testGetWinner_Initial() {
        assertNull(battleEngine.getWinner());
    }

    @Test
    void testIsGameOver_Initial() {
        assertNull(battleEngine.getWinner());
    }

    @Test
    void testIsGameOver_AfterStop() {
        battleEngine.startGame();
        battleEngine.stopGame();
        
        assertFalse(battleEngine.isGameRunning());
    }

    // ==================== 제한시간 모드 테스트 ====================

    @Test
    void testTimeLimitMode_IsTimeLimitMode() {
        assertTrue(timeLimitEngine.isTimeLimitMode());
        assertFalse(battleEngine.isTimeLimitMode());
    }

    @Test
    void testTimeLimitMode_GetRemainingTime_BeforeStart() {
        // 게임 시작 전에는 0 반환
        assertEquals(0, timeLimitEngine.getRemainingTime());
    }
    
    @Test
    void testTimeLimitMode_GetRemainingTime_AfterStart() {
        timeLimitEngine.startGame();
        // 게임 시작 직후에는 거의 180초
        long remaining = timeLimitEngine.getRemainingTime();
        assertTrue(remaining > 0 && remaining <= 180);
        timeLimitEngine.stopGame();
    }
    
    @Test
    void testTimeLimitMode_SetTimeLimit() {
        timeLimitEngine.setTimeLimit(60);
        timeLimitEngine.startGame();
        long remaining = timeLimitEngine.getRemainingTime();
        assertTrue(remaining > 0 && remaining <= 60);
        timeLimitEngine.stopGame();
    }

    @Test
    void testNormalMode_GetRemainingTime() {
        // 일반 모드에서는 시간 제한이 없으므로 0 반환
        long time = battleEngine.getRemainingTime();
        assertEquals(0, time);
    }

    // ==================== 게임보드 접근 테스트 ====================

    @Test
    void testGetPlayer1GameBoard() {
        GameBoard board = battleEngine.getPlayer1Engine().getGameBoard();
        assertNotNull(board);
    }

    @Test
    void testGetPlayer2GameBoard() {
        GameBoard board = battleEngine.getPlayer2Engine().getGameBoard();
        assertNotNull(board);
    }

    // ==================== 점수 관련 테스트 ====================

    @Test
    void testGetPlayer1Score() {
        battleEngine.startGame();
        assertEquals(0, battleEngine.getPlayer1Engine().getScore());
    }

    @Test
    void testGetPlayer2Score() {
        battleEngine.startGame();
        assertEquals(0, battleEngine.getPlayer2Engine().getScore());
    }

    @Test
    void testPlayer1Score_AfterHardDrop() {
        battleEngine.startGame();
        int scoreBefore = battleEngine.getPlayer1Engine().getScore();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.SPACE);
        
        assertTrue(battleEngine.getPlayer1Engine().getScore() >= scoreBefore);
    }

    @Test
    void testPlayer2Score_AfterHardDrop() {
        battleEngine.startGame();
        int scoreBefore = battleEngine.getPlayer2Engine().getScore();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.ENTER);
        
        assertTrue(battleEngine.getPlayer2Engine().getScore() >= scoreBefore);
    }

    // ==================== 레벨 및 줄 삭제 테스트 ====================

    @Test
    void testGetPlayer1Level() {
        battleEngine.startGame();
        assertEquals(1, battleEngine.getPlayer1Engine().getLevel());
    }

    @Test
    void testGetPlayer2Level() {
        battleEngine.startGame();
        assertEquals(1, battleEngine.getPlayer2Engine().getLevel());
    }

    @Test
    void testGetPlayer1LinesCleared() {
        battleEngine.startGame();
        assertEquals(0, battleEngine.getPlayer1Engine().getLinesCleared());
    }

    @Test
    void testGetPlayer2LinesCleared() {
        battleEngine.startGame();
        assertEquals(0, battleEngine.getPlayer2Engine().getLinesCleared());
    }

    // ==================== 현재 블록 및 다음 블록 테스트 ====================

    @Test
    void testGetPlayer1CurrentPiece() {
        battleEngine.startGame();
        assertNotNull(battleEngine.getPlayer1Engine().getCurrentPiece());
    }

    @Test
    void testGetPlayer2CurrentPiece() {
        battleEngine.startGame();
        assertNotNull(battleEngine.getPlayer2Engine().getCurrentPiece());
    }

    @Test
    void testGetPlayer1NextPiece() {
        battleEngine.startGame();
        assertNotNull(battleEngine.getPlayer1Engine().getNextPiece());
    }

    @Test
    void testGetPlayer2NextPiece() {
        battleEngine.startGame();
        assertNotNull(battleEngine.getPlayer2Engine().getNextPiece());
    }

    // ==================== 엔진 독립성 테스트 ====================

    @Test
    void testEnginesAreIndependent() {
        battleEngine.startGame();
        
        Piece piece1 = battleEngine.getPlayer1Engine().getCurrentPiece();
        Piece piece2 = battleEngine.getPlayer2Engine().getCurrentPiece();
        
        // 두 플레이어의 블록이 서로 다른 객체인지 확인
        assertNotSame(piece1, piece2);
    }

    @Test
    void testGameBoardsAreIndependent() {
        battleEngine.startGame();
        
        GameBoard board1 = battleEngine.getPlayer1Engine().getGameBoard();
        GameBoard board2 = battleEngine.getPlayer2Engine().getGameBoard();
        
        assertNotSame(board1, board2);
    }

    // ==================== 복합 시나리오 테스트 ====================

    @Test
    void testFullGameSession() {
        // 게임 시작
        battleEngine.startGame();
        assertTrue(battleEngine.isGameRunning());
        
        // 플레이어 1 이동
        battleEngine.handlePlayer1KeyPress(KeyCode.A);
        battleEngine.handlePlayer1KeyPress(KeyCode.D);
        battleEngine.handlePlayer1KeyPress(KeyCode.S);
        
        // 플레이어 2 이동
        battleEngine.handlePlayer2KeyPress(KeyCode.LEFT);
        battleEngine.handlePlayer2KeyPress(KeyCode.RIGHT);
        battleEngine.handlePlayer2KeyPress(KeyCode.DOWN);
        
        // 일시정지
        battleEngine.pauseGame();
        assertTrue(battleEngine.isPaused());
        
        // 재개
        battleEngine.pauseGame();
        assertFalse(battleEngine.isPaused());
        
        // 게임 중지
        battleEngine.stopGame();
        assertFalse(battleEngine.isGameRunning());
    }

    @Test
    void testTimeLimitMode_FullSession() {
        timeLimitEngine.startGame();
        assertTrue(timeLimitEngine.isGameRunning());
        
        long initialTime = timeLimitEngine.getRemainingTime();
        assertTrue(initialTime > 0);
        
        timeLimitEngine.stopGame();
        assertFalse(timeLimitEngine.isGameRunning());
    }

    @Test
    void testAttackSequence() {
        battleEngine.startGame();
        
        // 플레이어 1이 4줄 삭제 - 플레이어 2에게 공격
        battleEngine.processPlayer1Attack(4, 5);
        assertEquals(4, battleEngine.getPendingAttacksToPlayer2());
        battleEngine.applyPendingAttacks();
        assertEquals(0, battleEngine.getPendingAttacksToPlayer2());
        
        // 플레이어 2가 3줄 삭제 - 플레이어 1에게 공격
        battleEngine.processPlayer2Attack(3, 4);
        assertEquals(3, battleEngine.getPendingAttacksToPlayer1());
        battleEngine.applyPendingAttacks();
        assertEquals(0, battleEngine.getPendingAttacksToPlayer1());
    }

    // ==================== 키 입력 다양한 시나리오 테스트 ====================

    @Test
    void testPlayer1_AllKeyInputs() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer1KeyPress(KeyCode.A);  // 왼쪽
        battleEngine.handlePlayer1KeyPress(KeyCode.D);  // 오른쪽
        battleEngine.handlePlayer1KeyPress(KeyCode.S);  // 아래
        battleEngine.handlePlayer1KeyPress(KeyCode.W);  // 회전
        battleEngine.handlePlayer1KeyPress(KeyCode.SPACE);  // 하드드롭
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testPlayer2_AllKeyInputs() {
        battleEngine.startGame();
        
        battleEngine.handlePlayer2KeyPress(KeyCode.LEFT);   // 왼쪽
        battleEngine.handlePlayer2KeyPress(KeyCode.RIGHT);  // 오른쪽
        battleEngine.handlePlayer2KeyPress(KeyCode.DOWN);   // 아래
        battleEngine.handlePlayer2KeyPress(KeyCode.UP);     // 회전
        battleEngine.handlePlayer2KeyPress(KeyCode.ENTER);  // 하드드롭
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testUnknownKeyCode_Player1() {
        battleEngine.startGame();
        
        // 알 수 없는 키 입력 (무시되어야 함)
        battleEngine.handlePlayer1KeyPress(KeyCode.F1);
        battleEngine.handlePlayer1KeyPress(KeyCode.ESCAPE);
        
        assertTrue(battleEngine.isGameRunning());
    }

    @Test
    void testUnknownKeyCode_Player2() {
        battleEngine.startGame();
        
        // 알 수 없는 키 입력 (무시되어야 함)
        battleEngine.handlePlayer2KeyPress(KeyCode.F1);
        battleEngine.handlePlayer2KeyPress(KeyCode.ESCAPE);
        
        assertTrue(battleEngine.isGameRunning());
    }
    
    // ==================== Update 메서드 테스트 ====================
    
    @Test
    void testUpdate_GameRunning() {
        battleEngine.startGame();
        
        battleEngine.update();
        
        assertTrue(battleEngine.isGameRunning());
    }
    
    @Test
    void testUpdate_GameNotRunning() {
        battleEngine.update();
        
        assertFalse(battleEngine.isGameRunning());
    }
    
    @Test
    void testUpdate_GamePaused() {
        battleEngine.startGame();
        battleEngine.pauseGame();
        
        battleEngine.update();
        
        assertTrue(battleEngine.isPaused());
    }
    
    @Test
    @SuppressWarnings("deprecation")
    void testProcessAttacks_Deprecated() {
        battleEngine.startGame();
        
        // Deprecated 메서드이지만 호출은 가능해야 함
        battleEngine.processAttacks();
        
        assertTrue(battleEngine.isGameRunning());
    }
    
    // ==================== 추가 시나리오 테스트 ====================
    
    @Test
    void testMultipleAttacks() {
        battleEngine.startGame();
        
        // 여러 번의 공격
        battleEngine.processPlayer1Attack(2, 1);
        battleEngine.processPlayer1Attack(3, 2);
        battleEngine.processPlayer1Attack(4, 3);
        
        // 총 9줄이 대기 중이어야 함
        assertEquals(9, battleEngine.getPendingAttacksToPlayer2());
    }
    
    @Test
    void testAlternatingAttacks() {
        battleEngine.startGame();
        
        // 번갈아가며 공격
        battleEngine.processPlayer1Attack(2, 1);
        battleEngine.processPlayer2Attack(2, 2);
        
        assertEquals(2, battleEngine.getPendingAttacksToPlayer2());
        assertEquals(2, battleEngine.getPendingAttacksToPlayer1());
    }
}
