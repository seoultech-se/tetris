package tetris.game.ai;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import tetris.game.BattleGameEngine;
import tetris.game.GameBoard;
import tetris.game.GameEngine;
import tetris.game.Piece;
import tetris.ui.SettingsManager;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 간단한 휴리스틱을 사용해 플레이어 2를 대신 조작하는 컴퓨터 상대.
 * 현재 보드 상태를 평가해 가장 좋은 열과 회전을 찾고,
 * 0.5~1초 간격으로 입력을 BattleGameEngine에 전달한다.
 */
public class ComputerOpponent {

    private final BattleGameEngine battleEngine;
    private final SettingsManager settingsManager = SettingsManager.getInstance();
    private final ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    private static final long MIN_DELAY_MS = 500;
    private static final long MAX_DELAY_MS = 1000;

    public ComputerOpponent(BattleGameEngine battleEngine) {
        this.battleEngine = battleEngine;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new ComputerThreadFactory());
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        scheduleNextStep(600);
    }

    public void stop() {
        running = false;
        scheduler.shutdownNow();
    }

    private void scheduleNextStep(long delayMillis) {
        if (!running) {
            return;
        }
        scheduler.schedule(() -> {
            try {
                performStep();
            } finally {
                long nextDelay = ThreadLocalRandom.current().nextLong(MIN_DELAY_MS, MAX_DELAY_MS + 1);
                scheduleNextStep(nextDelay);
            }
        }, delayMillis, TimeUnit.MILLISECONDS);
    }

    private void performStep() {
        if (!running || battleEngine == null || !battleEngine.isGameRunning() || battleEngine.isPaused()) {
            return;
        }

        GameEngine engine = battleEngine.getPlayer2Engine();
        if (engine == null) {
            return;
        }

        KeyCode keyToPress = decideNextKey(engine);
        if (keyToPress != null) {
            Platform.runLater(() -> battleEngine.handlePlayer2KeyPress(keyToPress));
        }
    }

    private KeyCode decideNextKey(GameEngine engine) {
        Piece currentPiece = engine.getCurrentPiece();
        GameBoard board = engine.getGameBoard();
        if (currentPiece == null || board == null) {
            return null;
        }

        MoveDecision decision = findBestPlacement(currentPiece, board);
        if (decision == null) {
            return KeyCode.valueOf(settingsManager.getKeyDownP2());
        }

        int rotationDifference = ((decision.targetRotation - currentPiece.getRotation()) % 4 + 4) % 4;
        if (rotationDifference != 0) {
            return KeyCode.valueOf(settingsManager.getKeyRotateP2());
        }

        if (currentPiece.getX() < decision.targetX) {
            return KeyCode.valueOf(settingsManager.getKeyRightP2());
        } else if (currentPiece.getX() > decision.targetX) {
            return KeyCode.valueOf(settingsManager.getKeyLeftP2());
        }

        return KeyCode.valueOf(settingsManager.getKeyHardDropP2());
    }

    private MoveDecision findBestPlacement(Piece piece, GameBoard board) {
        int[][] boardState = deepCopy(board.getBoard());
        MoveDecision bestDecision = null;

        for (int rotation = 0; rotation < 4; rotation++) {
            Piece rotatedPiece = piece.copy();
            alignRotation(rotatedPiece, rotation);

            for (int x = -4; x < GameBoard.BOARD_WIDTH + 4; x++) {
                Piece candidate = rotatedPiece.copy();
                candidate.setPosition(x, rotatedPiece.getY());

                if (!isValidPosition(candidate, boardState)) {
                    continue;
                }

                Piece dropped = candidate.copy();
                while (isValidPosition(dropped, boardState)) {
                    dropped.moveDown();
                }
                dropped.moveUp();

                if (!isValidPosition(dropped, boardState)) {
                    continue;
                }

                int[][] simulationBoard = deepCopy(boardState);
                placePiece(simulationBoard, dropped);
                int clearedLines = clearLines(simulationBoard);
                double score = evaluateBoard(simulationBoard, clearedLines);

                if (bestDecision == null || score > bestDecision.score) {
                    int targetX = dropped.getX();
                    bestDecision = new MoveDecision(targetX, rotation, score);
                }
            }
        }

        return bestDecision;
    }

    private void alignRotation(Piece piece, int targetRotation) {
        int rotationDifference = ((targetRotation - piece.getRotation()) % 4 + 4) % 4;
        for (int i = 0; i < rotationDifference; i++) {
            piece.rotate();
        }
    }

    private boolean isValidPosition(Piece piece, int[][] board) {
        int[][] shape = piece.getShape();
        int baseX = piece.getX();
        int baseY = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 0) continue;

                int x = baseX + col;
                int y = baseY + row;

                if (x < 0 || x >= GameBoard.BOARD_WIDTH) {
                    return false;
                }
                if (y >= GameBoard.BOARD_HEIGHT) {
                    return false;
                }
                if (y >= 0 && board[y][x] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void placePiece(int[][] board, Piece piece) {
        int[][] shape = piece.getShape();
        int baseX = piece.getX();
        int baseY = piece.getY();
        int type = piece.getType();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 0) continue;
                int x = baseX + col;
                int y = baseY + row;
                if (y >= 0 && y < GameBoard.BOARD_HEIGHT &&
                    x >= 0 && x < GameBoard.BOARD_WIDTH) {
                    board[y][x] = type;
                }
            }
        }
    }

    private int clearLines(int[][] board) {
        int cleared = 0;
        for (int row = GameBoard.BOARD_HEIGHT - 1; row >= 0; row--) {
            if (isFullLine(board[row])) {
                removeLine(board, row);
                cleared++;
                row++;
            }
        }
        return cleared;
    }

    private boolean isFullLine(int[] row) {
        for (int cell : row) {
            if (cell == 0) {
                return false;
            }
        }
        return true;
    }

    private void removeLine(int[][] board, int line) {
        for (int row = line; row > 0; row--) {
            board[row] = Arrays.copyOf(board[row - 1], GameBoard.BOARD_WIDTH);
        }
        board[0] = new int[GameBoard.BOARD_WIDTH];
    }

    private double evaluateBoard(int[][] board, int clearedLines) {
        int[] heights = computeHeights(board);
        int aggregateHeight = Arrays.stream(heights).sum();
        int holes = countHoles(board, heights);
        int bumpiness = computeBumpiness(heights);

        double score = 0;
        score += clearedLines * 1.5;
        score -= aggregateHeight * 0.18;
        score -= holes * 0.75;
        score -= bumpiness * 0.3;
        return score;
    }

    private int[] computeHeights(int[][] board) {
        int[] heights = new int[GameBoard.BOARD_WIDTH];
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            int height = 0;
            for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
                if (board[row][col] != 0) {
                    height = GameBoard.BOARD_HEIGHT - row;
                    break;
                }
            }
            heights[col] = height;
        }
        return heights;
    }

    private int countHoles(int[][] board, int[] heights) {
        int holes = 0;
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            boolean blockFound = false;
            for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
                if (board[row][col] != 0) {
                    blockFound = true;
                } else if (blockFound) {
                    holes++;
                }
            }
        }
        return holes;
    }

    private int computeBumpiness(int[] heights) {
        int bumpiness = 0;
        for (int col = 0; col < GameBoard.BOARD_WIDTH - 1; col++) {
            bumpiness += Math.abs(heights[col] - heights[col + 1]);
        }
        return bumpiness;
    }

    private int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return copy;
    }

    private static class MoveDecision {
        final int targetX;
        final int targetRotation;
        final double score;

        MoveDecision(int targetX, int targetRotation, double score) {
            this.targetX = targetX;
            this.targetRotation = targetRotation;
            this.score = score;
        }
    }

    private static class ComputerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "computer-opponent");
            thread.setDaemon(true);
            return thread;
        }
    }
}

