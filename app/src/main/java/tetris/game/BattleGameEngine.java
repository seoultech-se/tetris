package tetris.game;

import tetris.ui.SettingsManager;
import java.util.Queue;
import java.util.LinkedList;

/**
 * 대전 모드 게임 엔진
 * 2명의 플레이어를 관리하고 공격 메커니즘을 처리합니다.
 */
public class BattleGameEngine {
    private GameEngine player1Engine;
    private GameEngine player2Engine;
    private boolean isGameRunning;
    private boolean isPaused;
    
    // 시간제한 모드 관련
    private long timeLimitSeconds; // 초 단위
    private long gameStartTime; // 나노초 단위
    private boolean timeLimitMode;
    
    // 승자 정보
    private String winner; // "PLAYER1", "PLAYER2", null
    
    // 대기 중인 공격 줄 큐 (각 플레이어에게 넘어갈 줄 수와 빈칸 위치)
    private Queue<AttackInfo> pendingAttacksToPlayer1;
    private Queue<AttackInfo> pendingAttacksToPlayer2;
    
    // 공격 정보를 담는 내부 클래스
    private static class AttackInfo {
        int lines;  // 공격 줄 수
        int emptyCol;  // 빈칸 위치 (마지막 블록이 채워진 열)
        
        AttackInfo(int lines, int emptyCol) {
            this.lines = lines;
            this.emptyCol = emptyCol;
        }
    }
    
    public BattleGameEngine(String battleMode) {
        this.player1Engine = new GameEngine();
        this.player2Engine = new GameEngine();
        this.isGameRunning = false;
        this.isPaused = false;
        this.timeLimitMode = "TIME_LIMIT".equals(battleMode);
        this.timeLimitSeconds = 180; // 기본 3분
        this.winner = null;
        this.pendingAttacksToPlayer1 = new LinkedList<>();
        this.pendingAttacksToPlayer2 = new LinkedList<>();
        
        // 아이템 모드 설정
        if ("ITEM".equals(battleMode)) {
            SettingsManager.getInstance().setGameMode("ITEM");
        } else {
            SettingsManager.getInstance().setGameMode("NORMAL");
        }
    }
    
    public void startGame() {
        isGameRunning = true;
        isPaused = false;
        gameStartTime = System.nanoTime();
        player1Engine.startGame();
        player2Engine.startGame();
    }
    
    public void pauseGame() {
        isPaused = !isPaused;
        if (isPaused) {
            player1Engine.pauseGame();
            player2Engine.pauseGame();
        } else {
            player1Engine.pauseGame();
            player2Engine.pauseGame();
        }
    }
    
    public void stopGame() {
        isGameRunning = false;
        isPaused = false;
        player1Engine.stopGame();
        player2Engine.stopGame();
    }
    
    // 각 플레이어의 이전 줄 삭제 수 추적
    private int player1PreviousCleared = 0;
    private int player2PreviousCleared = 0;
    
    /**
     * 플레이어 1의 키 입력 처리
     */
    public void handlePlayer1KeyPress(javafx.scene.input.KeyCode keyCode) {
        if (!isGameRunning || isPaused) {
            return;
        }
        player1Engine.handleKeyPress(keyCode);
        checkGameOver();
    }
    
    /**
     * 플레이어 2의 키 입력 처리
     * Player2 키 설정을 Player1 키 설정에 매핑
     */
    public void handlePlayer2KeyPress(javafx.scene.input.KeyCode keyCode) {
        if (!isGameRunning || isPaused) {
            return;
        }
        
        // Player2 키를 Player1 키 설정으로 변환
        javafx.scene.input.KeyCode mappedKey = mapPlayer2Key(keyCode);
        if (mappedKey != null) {
            player2Engine.handleKeyPress(mappedKey);
        }
        checkGameOver();
    }
    
    /**
     * 플레이어 2의 키를 플레이어 1 키 설정으로 매핑
     */
    private javafx.scene.input.KeyCode mapPlayer2Key(javafx.scene.input.KeyCode keyCode) {
        SettingsManager settings = SettingsManager.getInstance();
        
        // Player2 키와 Player1 키 설정 매핑
        String p2Left = settings.getKeyLeftP2();
        String p2Right = settings.getKeyRightP2();
        String p2Down = settings.getKeyDownP2();
        String p2Rotate = settings.getKeyRotateP2();
        String p2HardDrop = settings.getKeyHardDropP2();
        
        String keyName = keyCode.name();
        
        if (keyName.equals(p2Left)) {
            return javafx.scene.input.KeyCode.valueOf(settings.getKeyLeft());
        } else if (keyName.equals(p2Right)) {
            return javafx.scene.input.KeyCode.valueOf(settings.getKeyRight());
        } else if (keyName.equals(p2Down)) {
            return javafx.scene.input.KeyCode.valueOf(settings.getKeyDown());
        } else if (keyName.equals(p2Rotate)) {
            return javafx.scene.input.KeyCode.valueOf(settings.getKeyRotate());
        } else if (keyName.equals(p2HardDrop)) {
            return javafx.scene.input.KeyCode.valueOf(settings.getKeyHardDrop());
        }
        
        return null;
    }
    
    /**
     * 게임 루프 업데이트 (매 프레임 호출)
     */
    public void update() {
        if (!isGameRunning || isPaused) {
            return;
        }
        
        // 게임 오버 체크 (매 프레임마다 확인)
        checkGameOver();
        
        // 시간제한 모드 체크
        if (timeLimitMode) {
            long elapsed = (System.nanoTime() - gameStartTime) / 1_000_000_000L;
            if (elapsed >= timeLimitSeconds) {
                // 시간 종료 - 점수 높은 사람 승리
                if (player1Engine.getScore() > player2Engine.getScore()) {
                    winner = "PLAYER1";
                } else if (player2Engine.getScore() > player1Engine.getScore()) {
                    winner = "PLAYER2";
                } else {
                    winner = "DRAW"; // 무승부
                }
                stopGame();
                return;
            }
        }
        
        // 각 플레이어의 점수 2배 아이템 상태 업데이트
        player1Engine.updateDoubleScoreStatus();
        player2Engine.updateDoubleScoreStatus();
        
        // 공격 메커니즘은 clearLinesManually() 호출 직후에 직접 처리됨
        // processAttacks()는 여기서 호출하지 않음
    }
    
    /**
     * 공격 메커니즘 처리 (사용되지 않음 - processPlayer1Attack/processPlayer2Attack 사용)
     * @deprecated processPlayer1Attack/processPlayer2Attack를 사용하세요
     */
    @Deprecated
    public void processAttacks() {
        // 더 이상 사용되지 않음
    }
    
    /**
     * 플레이어 1이 삭제한 줄 수를 직접 전달하여 공격 처리
     * 10줄이 차 있으면 공격이 넘어가지 않음
     * @param clearedLines 삭제된 줄 수
     * @param lastBlockCol 마지막 블록이 채워진 열 위치
     */
    public void processPlayer1Attack(int clearedLines, int lastBlockCol) {
        // 2줄 이상 삭제하면 공격
        if (clearedLines >= 2) {
            if (!player2Engine.getGameBoard().isAttackLinesFull()) {
                attackPlayer2(clearedLines, lastBlockCol);
            }
        }
        player1PreviousCleared = player1Engine.getLinesCleared();
    }
    
    /**
     * 플레이어 2가 삭제한 줄 수를 직접 전달하여 공격 처리
     * 10줄이 차 있으면 공격이 넘어가지 않음
     * @param clearedLines 삭제된 줄 수
     * @param lastBlockCol 마지막 블록이 채워진 열 위치
     */
    public void processPlayer2Attack(int clearedLines, int lastBlockCol) {
        // 2줄 이상 삭제하면 공격
        if (clearedLines >= 2) {
            if (!player1Engine.getGameBoard().isAttackLinesFull()) {
                attackPlayer1(clearedLines, lastBlockCol);
            }
        }
        player2PreviousCleared = player2Engine.getLinesCleared();
    }
    
    
    /**
     * 플레이어 2에게 공격
     * @param linesCleared 삭제된 줄 수
     * @param lastBlockCol 마지막 블록이 채워진 열 위치
     */
    private void attackPlayer2(int linesCleared, int lastBlockCol) {
        // 삭제된 줄 수만큼 모두 큐에 추가 (한 줄씩)
        if (linesCleared > 0) {
            for (int i = 0; i < linesCleared; i++) {
                pendingAttacksToPlayer2.offer(new AttackInfo(1, lastBlockCol));
            }
        }
    }
    
    /**
     * 플레이어 1에게 공격
     * @param linesCleared 삭제된 줄 수
     * @param lastBlockCol 마지막 블록이 채워진 열 위치
     */
    private void attackPlayer1(int linesCleared, int lastBlockCol) {
        // 삭제된 줄 수만큼 모두 큐에 추가 (한 줄씩)
        if (linesCleared > 0) {
            for (int i = 0; i < linesCleared; i++) {
                pendingAttacksToPlayer1.offer(new AttackInfo(1, lastBlockCol));
            }
        }
    }
    
    
    /**
     * 대기 중인 공격을 한 줄씩 보드에 적용 (블록이 떨어진 후 호출)
     * @param playerNumber 플레이어 번호 (1 또는 2)
     */
    public void applyPendingAttacks(int playerNumber) {
        if (playerNumber == 1) {
            // 플레이어 1에게 대기 중인 공격 한 줄씩 적용
            while (!pendingAttacksToPlayer1.isEmpty()) {
                AttackInfo attack = pendingAttacksToPlayer1.poll();
                player1Engine.getGameBoard().addAttackLines(1, attack.emptyCol);
                // 공격 블록 추가 후 게임 오버 체크
                if (!player1Engine.getGameBoard().isValidPosition(player1Engine.getCurrentPiece())) {
                    player1Engine.stopGame();
                }
            }
        } else if (playerNumber == 2) {
            // 플레이어 2에게 대기 중인 공격 한 줄씩 적용
            while (!pendingAttacksToPlayer2.isEmpty()) {
                AttackInfo attack = pendingAttacksToPlayer2.poll();
                player2Engine.getGameBoard().addAttackLines(1, attack.emptyCol);
                // 공격 블록 추가 후 게임 오버 체크
                if (!player2Engine.getGameBoard().isValidPosition(player2Engine.getCurrentPiece())) {
                    player2Engine.stopGame();
                }
            }
        }
    }
    
    /**
     * 대기 중인 공격을 한 줄씩 보드에 적용 (양쪽 모두, 레거시 호환)
     */
    public void applyPendingAttacks() {
        applyPendingAttacks(1);
        applyPendingAttacks(2);
    }

    /**
     * 플레이어 1에게 직접 공격 추가 (네트워크로 받은 공격용)
     * @param lines 공격 줄 수
     * @param emptyCol 빈칸 위치
     */
    public void addAttackToPlayer1(int lines, int emptyCol) {
        for (int i = 0; i < lines; i++) {
            pendingAttacksToPlayer1.offer(new AttackInfo(1, emptyCol));
        }
    }

    /**
     * 플레이어 2에게 직접 공격 추가 (네트워크로 받은 공격용)
     * @param lines 공격 줄 수
     * @param emptyCol 빈칸 위치
     */
    public void addAttackToPlayer2(int lines, int emptyCol) {
        for (int i = 0; i < lines; i++) {
            pendingAttacksToPlayer2.offer(new AttackInfo(1, emptyCol));
        }
    }

    /**
     * 플레이어 1에게 대기 중인 공격 줄 수 반환
     */
    public int getPendingAttacksToPlayer1() {
        return pendingAttacksToPlayer1.size();
    }
    
    /**
     * 플레이어 2에게 대기 중인 공격 줄 수 반환
     */
    public int getPendingAttacksToPlayer2() {
        return pendingAttacksToPlayer2.size();
    }
    
    /**
     * 플레이어 1에게 대기 중인 공격 정보 반환 (공격 스크린 표시용)
     */
    public java.util.List<Integer> getPendingAttackEmptyColsToPlayer1() {
        java.util.List<Integer> cols = new java.util.ArrayList<>();
        for (AttackInfo attack : pendingAttacksToPlayer1) {
            cols.add(attack.emptyCol);
        }
        return cols;
    }
    
    /**
     * 플레이어 2에게 대기 중인 공격 정보 반환 (공격 스크린 표시용)
     */
    public java.util.List<Integer> getPendingAttackEmptyColsToPlayer2() {
        java.util.List<Integer> cols = new java.util.ArrayList<>();
        for (AttackInfo attack : pendingAttacksToPlayer2) {
            cols.add(attack.emptyCol);
        }
        return cols;
    }
    
    /**
     * 게임 오버 체크
     */
    private void checkGameOver() {
        boolean player1GameOver = !player1Engine.isGameRunning() || 
                                  !player1Engine.getGameBoard().isValidPosition(player1Engine.getCurrentPiece());
        boolean player2GameOver = !player2Engine.isGameRunning() || 
                                  !player2Engine.getGameBoard().isValidPosition(player2Engine.getCurrentPiece());
        
        if (player1GameOver && !player2GameOver) {
            winner = "PLAYER2";
            stopGame();
        } else if (player2GameOver && !player1GameOver) {
            winner = "PLAYER1";
            stopGame();
        } else if (player1GameOver && player2GameOver) {
            // 둘 다 게임 오버 - 점수 높은 사람 승리
            if (player1Engine.getScore() > player2Engine.getScore()) {
                winner = "PLAYER1";
            } else if (player2Engine.getScore() > player1Engine.getScore()) {
                winner = "PLAYER2";
            } else {
                winner = "DRAW";
            }
            stopGame();
        }
    }
    
    // Getters
    public GameEngine getPlayer1Engine() {
        return player1Engine;
    }
    
    public GameEngine getPlayer2Engine() {
        return player2Engine;
    }
    
    public boolean isGameRunning() {
        return isGameRunning;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public String getWinner() {
        return winner;
    }
    
    public boolean isTimeLimitMode() {
        return timeLimitMode;
    }
    
    public long getRemainingTime() {
        if (!timeLimitMode || !isGameRunning) {
            return 0;
        }
        long elapsed = (System.nanoTime() - gameStartTime) / 1_000_000_000L;
        return Math.max(0, timeLimitSeconds - elapsed);
    }
    
    public void setTimeLimit(long seconds) {
        this.timeLimitSeconds = seconds;
    }
}

