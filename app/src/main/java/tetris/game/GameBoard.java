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
        if (piece.isWeightPiece()) {
            piece.setLanded(true);
        }

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
    
    /**
     * 삭제될 줄들의 행 번호를 반환 (애니메이션용)
     */
    public List<Integer> getFullLines() {
        List<Integer> fullLines = new ArrayList<>();
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                fullLines.add(row);
            }
        }
        return fullLines;
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
     * 아이템 효과를 처리 (LINE_CLEAR, BOMB)
     * 블록이 배치될 때 호출되어, 아이템 효과를 적용
     * @param piece 배치된 블록
     * @return 삭제된 줄의 수
     */
    public int processItemEffects(Piece piece) {
        if (piece == null || !piece.hasItem()) {
            return 0;
        }

        List<Integer> rowsToClean = new ArrayList<>();
        List<Integer> bombRows = new ArrayList<>();
        List<Integer> bombCols = new ArrayList<>();

        // 블록이 배치된 위치에서 아이템 찾기
        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = piece.getItemAt(row, col);
                    int boardRow = y + row;
                    int boardCol = x + col;

                    if (itemType == ItemType.LINE_CLEAR) {
                        // LINE_CLEAR: 해당 줄 삭제
                        if (boardRow >= 0 && boardRow < BOARD_HEIGHT) {
                            if (!rowsToClean.contains(boardRow)) {
                                rowsToClean.add(boardRow);
                            }
                        }
                    } else if (itemType == ItemType.BOMB) {
                        // BOMB: 2x2 폭탄이 차지하는 모든 행과 열 수집
                        if (boardRow >= 0 && boardRow < BOARD_HEIGHT && !bombRows.contains(boardRow)) {
                            bombRows.add(boardRow);
                        }
                        if (boardCol >= 0 && boardCol < BOARD_WIDTH && !bombCols.contains(boardCol)) {
                            bombCols.add(boardCol);
                        }
                    }
                }
            }
        }

        // 폭탄 효과 처리: 해당 행과 열의 모든 블록 제거
        if (!bombRows.isEmpty() || !bombCols.isEmpty()) {
            processBombEffect(bombRows, bombCols);
        }

        // LINE_CLEAR 효과: 찾은 줄들을 삭제 (아래쪽부터)
        rowsToClean.sort((a, b) -> b - a);  // 내림차순 정렬
        for (int rowToClean : rowsToClean) {
            clearLine(rowToClean);
            dropLinesAbove(rowToClean);
        }

        return rowsToClean.size();
    }

    /**
     * 폭탄 효과 처리: 1x1 폭탄이 위치한 행과 열을 십자가(+) 모양으로 제거
     * @param bombRows 폭탄이 위치한 행
     * @param bombCols 폭탄이 위치한 열
     */
    private void processBombEffect(List<Integer> bombRows, List<Integer> bombCols) {
        // 폭탄이 위치한 열 전체 제거 (세로선)
        for (int col : bombCols) {
            if (col >= 0 && col < BOARD_WIDTH) {
                for (int row = 0; row < BOARD_HEIGHT; row++) {
                    clearCell(row, col);
                }
            }
        }

        // 폭탄이 위치한 행 전체 제거 (가로선)
        for (int row : bombRows) {
            if (row >= 0 && row < BOARD_HEIGHT) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    clearCell(row, col);
                }
            }
        }
    }

    /**
     * 특정 셀을 지움 (범위 체크 포함)
     * @param row 행
     * @param col 열
     */
    private void clearCell(int row, int col) {
        if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
            board[row][col] = 0;
            itemBoard[row][col] = ItemType.NONE;
        }
    }

    /**
     * 무게추 블록이 이동할 때 밑에 있는 블록들을 지우는 처리
     * @param piece 무게추 블록
     */
    public void processWeightEffect(Piece piece) {
        if (piece == null || !piece.isWeightPiece()) {
            return;
        }

        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();

        // 무게추 블록의 가장 아래쪽 행
        int bottomRow = 1;

        // 무게추의 가장 아래 행의 각 블록 밑에 있는 보드의 블록들을 지우기
        for (int col = 0; col < shape[bottomRow].length; col++) {
            if (shape[bottomRow][col] != 0) {
                int boardCol = x + col;
                int boardRow = y + bottomRow + 1;  // 무게추 바로 밑
                
                // 해당 열의 밑에 있는 모든 블록 지우기
                if (boardCol >= 0 && boardCol < BOARD_WIDTH && boardRow >= 0 && boardRow < BOARD_HEIGHT) {
                    if (board[boardRow][boardCol] != 0) {
                        piece.setLanded(true);
                        board[boardRow][boardCol] = 0;
                        itemBoard[boardRow][boardCol] = ItemType.NONE;
                    }
                }
            }
        }
    }
}