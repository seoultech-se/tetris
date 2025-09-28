package tetris.game;


public class GameBoard {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    private static final int SPAWN_X = BOARD_WIDTH / 2 - 1;
    private static final int SPAWN_Y = 0;

    private int[][] board;

    public GameBoard() {
        this.board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        clearBoard();
    }

    public void clearBoard() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = 0;
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

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;

                    if (newY >= 0 && newY < BOARD_HEIGHT &&
                        newX >= 0 && newX < BOARD_WIDTH) {
                        board[newY][newX] = pieceType;
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
        }
    }

    private void dropLinesAbove(int clearedRow) {
        for (int row = clearedRow; row > 0; row--) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = board[row - 1][col];
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[0][col] = 0;
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
}