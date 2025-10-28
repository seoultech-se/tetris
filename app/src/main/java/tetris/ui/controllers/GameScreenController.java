package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;
import tetris.game.GameEngine;
import tetris.game.GameBoard;
import tetris.game.Piece;
import tetris.game.ItemType;

import java.net.URL;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
    private Label nextItemLabel;

    @FXML
    private Label doubleScoreTimerLabel;

    @FXML
    private Label skipNotificationLabel;

    @FXML
    private Canvas nextPieceCanvas;

    private SceneManager sceneManager;
    private SettingsManager settingsManager;
    private GameEngine gameEngine;
    private AnimationTimer gameLoop;
    private long lastUpdateTime = 0;
    private long fallSpeed = 1_000_000_000; // 1 second in nanoseconds

    // 블록 크기와 색상 설정 (ColorBlind Safe 팔레트)
    private static final int BLOCK_SIZE = 25;
    private static final Color[] PIECE_COLORS = {
        Color.BLACK,
        Color.web("#56B4E9"),          // 1 - I 피스 (하늘색)
        Color.web("#F0E442"),          // 2 - O 피스 (노랑)
        Color.web("#CC79A7"),          // 3 - T 피스 (핑크/보라)
        Color.web("#009E73"),          // 4 - S 피스 (초록)
        Color.web("#D55E00"),          // 5 - Z 피스 (적갈색)
        Color.web("#0072B2"),          // 6 - J 피스 (파랑)
        Color.web("#E69F00"),          // 7 - L 피스 (주황)
        Color.web("#999999"),          // 8 - WEIGHT 피스 (회색 - 무게추)
        Color.web("#FF0000")           // 9 - BOMB 피스 (빨강 - 폭탄)
    };

    // 접근성 심볼 (0은 빈칸)
    private static final String[] PIECE_SYMBOLS = {
        " ", // 0
        "O", // 1 - I (직선 형태를 텍스트로 대체)
        "●", // 2 - O
        "★", // 3 - T
        "▲", // 4 - S
        "■", // 5 - Z
        "◆", // 6 - J (다이아몬드)
        "◇", // 7 - L (빈 다이아몬드)
        "▼", // 8 - WEIGHT (아래를 가리키는 화살표)
        "✸"  // 9 - BOMB (폭발 효과)
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 설정 매니저 초기화
        settingsManager = SettingsManager.getInstance();
        
        // 게임 엔진 초기화
        gameEngine = new GameEngine();
        setupGameCanvas();
        setupNextPieceCanvas();
        
        // Scene이 설정된 후 키 핸들러 등록
        if (gameCanvas != null) {
            gameCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    setupKeyHandler();
                }
            });
        }
        
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
            // 포커스 비활성화 - Scene 레벨에서 키 입력 처리
            gameCanvas.setFocusTraversable(false);
        }
    }
    
    public void setupKeyHandler() {
        // Scene에 키 이벤트 핸들러 등록
        if (gameCanvas != null && gameCanvas.getScene() != null) {
            gameCanvas.getScene().setOnKeyPressed(event -> {
                if (gameEngine != null && gameEngine.isGameRunning() && !gameEngine.isPaused()) {
                    // 게임 진행 중에만 키 입력을 게임 엔진으로 전달
                    gameEngine.handleKeyPress(event.getCode());
                    // 이벤트를 consume하여 버튼으로 전파되지 않도록 차단
                    event.consume();
                }
            });
        }
    }

    private void setupNextPieceCanvas() {
        if (nextPieceCanvas != null) {
            nextPieceCanvas.setWidth(6 * BLOCK_SIZE);
            nextPieceCanvas.setHeight(5 * BLOCK_SIZE);
            // Canvas 테두리 그리기
            drawNextPieceCanvasBorder();
        }
    }
    
    private void drawNextPieceCanvasBorder() {
        if (nextPieceCanvas != null) {
            GraphicsContext gc = nextPieceCanvas.getGraphicsContext2D();
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeRect(1, 1, nextPieceCanvas.getWidth() - 2, nextPieceCanvas.getHeight() - 2);
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0) {
                    lastUpdateTime = now;
                }

                // 게임 오버 체크
                if (!gameEngine.isGameRunning()) {
                    gameLoop.stop();
                    showGameOver();
                    return;
                }

                if (now - lastUpdateTime >= fallSpeed) {
                    if (gameEngine.isGameRunning() && !gameEngine.isPaused()) {
                        gameEngine.movePieceDown();
                    }
                    lastUpdateTime = now;
                }

                // 점수 2배 상태 업데이트
                gameEngine.updateDoubleScoreStatus();

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
                    ItemType itemType = board.getItemAt(row, col);
                    renderBlock(gc, col * BLOCK_SIZE, row * BLOCK_SIZE, PIECE_COLORS[cellValue], cellValue, itemType);
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
                        ItemType itemType = nextPiece.getItemAt(row, col);
                        renderBlock(gc, (col + 1) * BLOCK_SIZE, (row + 1) * BLOCK_SIZE, color, nextPiece.getType(), itemType);
                    }
                }
            }
        }

        // 테두리 다시 그리기
        drawNextPieceCanvasBorder();
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
                    ItemType itemType = piece.getItemAt(row, col);
                    renderBlock(gc, x, y, color, piece.getType(), itemType);
                }
            }
        }
    }

    private void renderBlock(GraphicsContext gc, int x, int y, Color color, int pieceType, ItemType itemType) {
        // 접근성 모드가 켜져 있으면 색 대신 심볼로 채운다
        if (settingsManager != null && settingsManager.isAccessibilityModeEnabled()) {
            // 배경을 검게 유지
            gc.setFill(Color.BLACK);
            gc.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);

            String symbol = "?";
            if (pieceType >= 0 && pieceType < PIECE_SYMBOLS.length) {
                symbol = PIECE_SYMBOLS[pieceType];
            }

            // 아이콘을 블록 크기에 맞게 최대한 크게 설정
            int fontSize = BLOCK_SIZE - 2;
            if (fontSize < 8) fontSize = 8;
            Font font = Font.font("Monospaced", fontSize);
            gc.setFont(font);
            gc.setFill(Color.WHITE);

            Text text = new Text(symbol);
            text.setFont(font);
            double textWidth = text.getLayoutBounds().getWidth();
            double textHeight = text.getLayoutBounds().getHeight();

            double tx = x + (BLOCK_SIZE - textWidth) / 2.0;
            double ty = y + (BLOCK_SIZE + textHeight) / 2.0 - 4;

            gc.fillText(symbol, tx, ty);

            // 아이템이 있으면 아이템 문자를 오른쪽 상단에 작게 표시
            if (itemType != null && itemType != ItemType.NONE) {
                String itemChar = itemType.getDisplayChar();
                if (!itemChar.isEmpty()) {
                    Font smallFont = Font.font("Monospaced", BLOCK_SIZE / 3);
                    gc.setFont(smallFont);
                    gc.setFill(Color.YELLOW);  // 눈에 잘 띄는 색상
                    gc.fillText(itemChar, x + BLOCK_SIZE - BLOCK_SIZE / 3, y + BLOCK_SIZE / 3);
                }
            }
            return;
        }

        // 기본 렌더링: 색으로 채우고 테두리 그림
        gc.setFill(color);
        gc.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);

        // 블록 테두리
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, BLOCK_SIZE, BLOCK_SIZE);

        // 아이템이 있으면 문자를 블록 중앙에 표시
        if (itemType != null && itemType != ItemType.NONE) {
            String itemChar = itemType.getDisplayChar();
            if (!itemChar.isEmpty()) {
                int fontSize = (int) (BLOCK_SIZE * 0.6);  // 블록 크기의 60%
                Font font = Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize);
                gc.setFont(font);
                gc.setFill(Color.WHITE);

                Text text = new Text(itemChar);
                text.setFont(font);
                double textWidth = text.getLayoutBounds().getWidth();
                double textHeight = text.getLayoutBounds().getHeight();

                double tx = x + (BLOCK_SIZE - textWidth) / 2.0;
                double ty = y + (BLOCK_SIZE + textHeight) / 2.0 - 2;

                gc.fillText(itemChar, tx, ty);
            }
        }
    }
    
    

    private void renderBorder(GraphicsContext gc) {
        // 접근성 모드에서도 게임 보드 외곽 테두리 표시
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    private void updateUI() {
        if (gameEngine != null) {
            updateScore(gameEngine.getScore());
            updateLevel(gameEngine.getLevel());
            updateLines(gameEngine.getLinesCleared());
            updateNextItemCounter();
            updateDoubleScoreTimer();
            updateSkipNotification();
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

    public void updateNextItemCounter() {
        if (nextItemLabel != null && gameEngine != null && settingsManager != null) {
            // 아이템 모드일 때만 표시
            if ("ITEM".equals(settingsManager.getGameMode())) {
                int linesUntilItem = gameEngine.getLinesUntilNextItem();
                if (linesUntilItem == 0) {
                    nextItemLabel.setText("Next Item: Ready!");
                    nextItemLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-weight: bold;");
                } else {
                    nextItemLabel.setText("Next Item: " + linesUntilItem + " lines");
                    nextItemLabel.setStyle("-fx-text-fill: #FFFFFF;");
                }
            } else {
                nextItemLabel.setText("");
            }
        }
    }

    public void updateDoubleScoreTimer() {
        if (doubleScoreTimerLabel != null && gameEngine != null) {
            if (gameEngine.isDoubleScoreActive()) {
                int remainingTime = gameEngine.getDoubleScoreRemainingTime();
                doubleScoreTimerLabel.setText("2X SCORE: " + remainingTime + "s");
                doubleScoreTimerLabel.setStyle("-fx-text-fill: #FFD700; -fx-font-weight: bold;");
            } else {
                doubleScoreTimerLabel.setText("");
            }
        }
    }

    public void updateSkipNotification() {
        if (skipNotificationLabel != null && gameEngine != null) {
            if (gameEngine.hasSkipItem()) {
                skipNotificationLabel.setText("N키를 눌러\n블록 넘기기!");
                skipNotificationLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-weight: bold;");
            } else {
                skipNotificationLabel.setText("");
            }
        }
    }

    public void showGameOver() {
        if (sceneManager != null) {
            int finalScore = gameEngine.getScore();
            sceneManager.showGameOverScreen(finalScore);
        }
    }
}