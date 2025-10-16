package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import tetris.ui.SceneManager;
import tetris.game.GameEngine;
import javafx.scene.input.KeyCode;
import tetris.game.GameBoard;
import tetris.game.Piece;

import java.net.URL;
import java.util.ResourceBundle;

public class GameScreenController implements Initializable {

    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label linesLabel;

    @FXML
    private Canvas nextPieceCanvas;

    private SceneManager sceneManager;
    private GameEngine gameEngine;
    private AnimationTimer gameLoop;
    private long lastUpdateTime = 0;
    private long fallSpeed = 1_000_000_000; // 1 second in nanoseconds

    // 블록 크기와 색상 설정
    private static final int BLOCK_SIZE = 25;
    private static final Color[] PIECE_COLORS = {
        Color.BLACK,        // 0 - 빈 공간
        Color.CYAN,         // 1 - I 피스
        Color.YELLOW,       // 2 - O 피스
        Color.PURPLE,       // 3 - T 피스
        Color.GREEN,        // 4 - S 피스
        Color.RED,          // 5 - Z 피스
        Color.BLUE,         // 6 - J 피스
        Color.ORANGE        // 7 - L 피스
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 게임 엔진 초기화
        gameEngine = new GameEngine();
        setupGameCanvas();
        setupNextPieceCanvas();
        startGameLoop();
        gameEngine.startGame();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    private void setupGameCanvas() {
        if (gameCanvas != null) {
            gameCanvas.setWidth(GameBoard.BOARD_WIDTH * BLOCK_SIZE);
            gameCanvas.setHeight(GameBoard.BOARD_HEIGHT * BLOCK_SIZE);
            gameCanvas.setFocusTraversable(true);

            // 키보드 이벤트 처리
            gameCanvas.setOnKeyPressed(event -> {
                if (gameEngine != null) {
                    KeyCode keyCode = event.getCode();
                    if (keyCode != null) {
                        gameEngine.handleKeyPress(keyCode);
                    }
                }
            });
        }
    }

    // mapKeyCode 메서드 제거 (KeyCode 직접 사용)

    private void setupNextPieceCanvas() {
        if (nextPieceCanvas != null) {
            nextPieceCanvas.setWidth(6 * BLOCK_SIZE);
            nextPieceCanvas.setHeight(5 * BLOCK_SIZE);
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0) {
                    lastUpdateTime = now;
                }

                if (now - lastUpdateTime >= fallSpeed) {
                    if (gameEngine.isGameRunning() && !gameEngine.isPaused()) {
                        gameEngine.movePieceDown();
                    }
                    lastUpdateTime = now;
                }

                renderGame();
                renderNextPiece();
                updateUI();
                updateFallSpeed();
            }
        };
        gameLoop.start();
    }

    // 블록 낙하 속도 조절
    private void updateFallSpeed() {
        fallSpeed = (long) (1_000_000_000 / (1 + 0.1 * gameEngine.getLevel()));
    }

    private void renderGame() {
        if (gameCanvas == null || gameEngine == null) return;

        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        // 배경을 검은색으로 설정
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // 게임 보드 렌더링
        GameBoard board = gameEngine.getGameBoard();
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                int cellValue = board.getCell(row, col);
                if (cellValue > 0) {
                    renderBlock(gc, col * BLOCK_SIZE, row * BLOCK_SIZE, PIECE_COLORS[cellValue]);
                }
            }
        }

        // 현재 피스 렌더링
        Piece currentPiece = gameEngine.getCurrentPiece();
        if (currentPiece != null) {
            renderPiece(gc, currentPiece);
        }

        // 테두리 렌더링
        renderBorder(gc);
    }

    private void renderNextPiece() {
        if (nextPieceCanvas == null || gameEngine == null) return;

        GraphicsContext gc = nextPieceCanvas.getGraphicsContext2D();

        // 배경을 검은색으로 설정
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, nextPieceCanvas.getWidth(), nextPieceCanvas.getHeight());

        Piece nextPiece = gameEngine.getNextPiece();
        if (nextPiece != null) {
            int[][] shape = nextPiece.getShape();
            Color color = PIECE_COLORS[nextPiece.getType()];

            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] != 0) {
                        renderBlock(gc, (col + 1)* BLOCK_SIZE, (row + 1) * BLOCK_SIZE, color);
                    }
                }
            }
        }
    }

    private void renderPiece(GraphicsContext gc, Piece piece) {
        int[][] shape = piece.getShape();
        Color color = PIECE_COLORS[piece.getType()];
        int pieceX = piece.getX();
        int pieceY = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int x = (pieceX + col) * BLOCK_SIZE;
                    int y = (pieceY + row) * BLOCK_SIZE;
                    renderBlock(gc, x, y, color);
                }
            }
        }
    }

    private void renderBlock(GraphicsContext gc, int x, int y, Color color) {
        gc.setFill(color);
        gc.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);

        // 블록 테두리
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
    }

    private void renderBorder(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    private void updateUI() {
        if (gameEngine != null) {
            updateScore(gameEngine.getScore());
            updateLevel(gameEngine.getLevel());
            updateLines(gameEngine.getLinesCleared());
        }
    }

    @FXML
    private void onPause() {
        if (gameEngine != null) {
            gameEngine.pauseGame();
        }
    }

    @FXML
    private void onBackToMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (gameEngine != null) {
            gameEngine.stopGame();
        }
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    public void updateScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Score: " + score);
        }
    }

    public void updateLevel(int level) {
        if (levelLabel != null) {
            levelLabel.setText("Level: " + level);
        }
    }

    public void updateLines(int lines) {
        if (linesLabel != null) {
            linesLabel.setText("Lines: " + lines);
        }
    }

    public void showGameOver() {
        if (sceneManager != null) {
            sceneManager.showGameOverScreen();
        }
    }
}