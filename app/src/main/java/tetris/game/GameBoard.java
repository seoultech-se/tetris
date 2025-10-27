package tetris.game;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    private static final int SPAWN_X = BOARD_WIDTH / 2 - 1;
    private static final int SPAWN_Y = 0;

    private int[][] board;
    private ItemType[][] itemBoard;  // 각 셀의 아이템 정보

    public GameBoard() {
        this.board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        this.itemBoard = new ItemType[BOARD_HEIGHT][BOARD_WIDTH];
        clearBoard();
    }

    public void clearBoard() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = 0;
                itemBoard[row][col] = ItemType.NONE;
            }
        }
    }

    public boolean isValidPosition(Piece piece) {
        if (piece == null) return false;

        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;

                    if (newX < 0 || newX >= BOARD_WIDTH ||
                        newY >= BOARD_HEIGHT ||
                        (newY >= 0 && board[newY][newX] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placePiece(Piece piece) {
        if (piece == null) return;

        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();
        int pieceType = piece.getType();

        // 블록을 보드에 배치하고 아이템 정보도 저장
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;

                    if (newY >= 0 && newY < BOARD_HEIGHT &&
                        newX >= 0 && newX < BOARD_WIDTH) {
                        board[newY][newX] = pieceType;
                        // 아이템 정보 저장
                        itemBoard[newY][newX] = piece.getItemAt(row, col);
                    }
                }
            }
        }
    }

    public int clearLines() {
        int linesCleared = 0;

        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                clearLine(row);
                dropLinesAbove(row);
                linesCleared++;
                row++;
            }
        }

        return linesCleared;
    }

    private boolean isLineFull(int row) {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearLine(int row) {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[row][col] = 0;
            itemBoard[row][col] = ItemType.NONE;
        }
    }

    private void dropLinesAbove(int clearedRow) {
        for (int row = clearedRow; row > 0; row--) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = board[row - 1][col];
                itemBoard[row][col] = itemBoard[row - 1][col];
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[0][col] = 0;
            itemBoard[0][col] = ItemType.NONE;
        }
    }

    public int getCell(int row, int col) {
        if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
            return board[row][col];
        }
        return 0;
    }

    public int getSpawnX() {
        return SPAWN_X;
    }

    public int getSpawnY() {
        return SPAWN_Y;
    }

    public int[][] getBoard() {
        return board;
    }

    /**
     * 특정 셀의 아이템 타입을 반환
     * @param row 행
     * @param col 열
     * @return 아이템 타입
     */
    public ItemType getItemAt(int row, int col) {
        if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
            return itemBoard[row][col];
        }
        return ItemType.NONE;
    }

    /**
     * LINE_CLEAR 아이템 효과를 처리
     * 블록이 배치될 때 호출되어, LINE_CLEAR 아이템이 있는 줄을 즉시 삭제
     * @param piece 배치된 블록
     * @return 삭제된 줄의 수
     */
    public int processItemEffects(Piece piece) {
        if (piece == null || !piece.hasItem()) {
            return 0;
        }

        List<Integer> rowsToClean = new ArrayList<>();

        // 블록이 배치된 위치에서 LINE_CLEAR 아이템이 있는 줄을 찾기
        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = piece.getItemAt(row, col);
                    if (itemType == ItemType.LINE_CLEAR) {
                        int boardRow = y + row;
                        if (boardRow >= 0 && boardRow < BOARD_HEIGHT) {
                            if (!rowsToClean.contains(boardRow)) {
                                rowsToClean.add(boardRow);
                            }
                        }
                    }
                }
            }
        }

        // 찾은 줄들을 삭제 (아래쪽부터)
        rowsToClean.sort((a, b) -> b - a);  // 내림차순 정렬
        for (int rowToClean : rowsToClean) {
            clearLine(rowToClean);
            dropLinesAbove(rowToClean);
        }

        return rowsToClean.size();
    }
}