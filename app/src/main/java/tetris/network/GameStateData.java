package tetris.network;

import java.io.Serializable;

public class GameStateData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int[][] board;
    private int[][] itemBoard;  // 아이템 정보
    private int score;
    private int level;
    private int linesCleared;
    private boolean isGameOver;
    
    // 현재 블록 정보
    private int[][] currentPieceShape;
    private int currentPieceX;
    private int currentPieceY;
    private int currentPieceType;
    
    // 다음 블록 정보
    private int[][] nextPieceShape;
    private int nextPieceType;
    
    // 공격 정보
    private int incomingAttackLines;  // 대기 중인 공격 줄 수
    private java.util.List<Integer> incomingAttackEmptyCols;  // 각 공격 줄의 빈칸 위치
    
    // 타임스탬프 (RTT 추정용)
    private long timestamp;
    
    public GameStateData(int[][] board, int[][] itemBoard, int score, int level, int linesCleared, boolean isGameOver,
                         int[][] currentPieceShape, int currentPieceX, int currentPieceY, int currentPieceType,
                         int[][] nextPieceShape, int nextPieceType, int incomingAttackLines, java.util.List<Integer> incomingAttackEmptyCols) {
        this.board = board;
        this.itemBoard = itemBoard;
        this.score = score;
        this.level = level;
        this.linesCleared = linesCleared;
        this.isGameOver = isGameOver;
        this.currentPieceShape = currentPieceShape;
        this.currentPieceX = currentPieceX;
        this.currentPieceY = currentPieceY;
        this.currentPieceType = currentPieceType;
        this.nextPieceShape = nextPieceShape;
        this.nextPieceType = nextPieceType;
        this.incomingAttackLines = incomingAttackLines;
        this.incomingAttackEmptyCols = incomingAttackEmptyCols;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int[][] getBoard() {
        return board;
    }
    
    public int[][] getItemBoard() {
        return itemBoard;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getLinesCleared() {
        return linesCleared;
    }
    
    public boolean isGameOver() {
        return isGameOver;
    }
    
    public int[][] getCurrentPieceShape() {
        return currentPieceShape;
    }
    
    public int getCurrentPieceX() {
        return currentPieceX;
    }
    
    public int getCurrentPieceY() {
        return currentPieceY;
    }
    
    public int getCurrentPieceType() {
        return currentPieceType;
    }
    
    public int[][] getNextPieceShape() {
        return nextPieceShape;
    }
    
    public int getNextPieceType() {
        return nextPieceType;
    }
    
    public int getIncomingAttackLines() {
        return incomingAttackLines;
    }
    
    public java.util.List<Integer> getIncomingAttackEmptyCols() {
        return incomingAttackEmptyCols;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}
