package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;
import tetris.game.BattleGameEngine;
import tetris.game.GameBoard;
import tetris.game.Piece;
import tetris.game.ItemType;
import tetris.game.ai.ComputerOpponent;
import java.net.URL;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.ResourceBundle;

public class BattleGameScreenController implements Initializable {

    @FXML
    private Canvas player1Canvas;

    @FXML
    private Canvas player2Canvas;

    @FXML
    private Canvas player1NextCanvas;

    @FXML
    private Canvas player2NextCanvas;

    @FXML
    private Canvas player1IncomingCanvas;

    @FXML
    private Canvas player2IncomingCanvas;

    @FXML
    private Label player1ScoreLabel;

    @FXML
    private Label player2ScoreLabel;

    @FXML
    private Label player1LevelLabel;

    @FXML
    private Label player2LevelLabel;

    @FXML
    private Label player1LinesLabel;

    @FXML
    private Label player2LinesLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Label winnerLabel;

    @FXML
    private VBox gameOverBox;

    @FXML
    private Button rematchButton;

    @FXML
    private Button toMenuButton;

    private SceneManager sceneManager;
    private SettingsManager settingsManager;
    private BattleGameEngine battleEngine;
    private String battleMode;
    private AnimationTimer gameLoop;
    private long lastUpdateTime1 = 0;
    private long lastUpdateTime2 = 0;
    private long fallSpeed1 = 1_000_000_000;
    private long fallSpeed2 = 1_000_000_000;
    private boolean computerOpponentEnabled = false;
    private ComputerOpponent computerOpponent;

    // 줄 삭제 애니메이션 관련
    private java.util.List<Integer> player1LinesToClear = null;
    private java.util.List<Integer> player2LinesToClear = null;
    private long clearAnimationStartTime1 = 0;
    private long clearAnimationStartTime2 = 0;
    private static final long CLEAR_ANIMATION_DURATION = 100_000_000;
    private boolean isAnimatingClear1 = false;
    private boolean isAnimatingClear2 = false;
    

    // 블록 크기
    private int BLOCK_SIZE = 25;

    // 블록 색상 설정 (ColorBlind Safe 팔레트)
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
        "▼", // 8 - 공격 블록 (아래를 가리키는 화살표)
        "✸"  // 9 - BOMB (폭발 효과)
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsManager = SettingsManager.getInstance();

        // 화면 크기에 따라 블록 크기 설정
        String screenSize = settingsManager.getScreenSize();
        switch (screenSize) {
            case "작게":
                BLOCK_SIZE = 20;
                break;
            case "중간":
                BLOCK_SIZE = 25;
                break;
            case "크게":
                BLOCK_SIZE = 30;
                break;
            default:
                BLOCK_SIZE = 25;
                break;
        }

        setupCanvases();
        setupKeyHandler();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setBattleMode(String battleMode, boolean vsComputer) {
        this.battleMode = battleMode;
        this.computerOpponentEnabled = vsComputer;
        this.battleEngine = new BattleGameEngine(battleMode);
        configureBattleEngine();
        startGameLoop();
        battleEngine.startGame();
        startComputerOpponentIfNeeded();
    }

    private void configureBattleEngine() {
        if ("TIME_LIMIT".equals(battleMode)) {
            battleEngine.setTimeLimit(180); // 3분
        }

        battleEngine.getPlayer1Engine().setOnPiecePlacedCallback(() -> battleEngine.applyPendingAttacks(1));
        battleEngine.getPlayer2Engine().setOnPiecePlacedCallback(() -> battleEngine.applyPendingAttacks(2));
    }

    private void startComputerOpponentIfNeeded() {
        if (!computerOpponentEnabled || battleEngine == null) {
            return;
        }
        stopComputerOpponent();
        computerOpponent = new ComputerOpponent(battleEngine);
        computerOpponent.start();
    }

    private void stopComputerOpponent() {
        if (computerOpponent != null) {
            computerOpponent.stop();
            computerOpponent = null;
        }
    }

    private void setupCanvases() {
        if (player1Canvas != null) {
            player1Canvas.setWidth(GameBoard.BOARD_WIDTH * BLOCK_SIZE);
            player1Canvas.setHeight(GameBoard.BOARD_HEIGHT * BLOCK_SIZE);
            player1Canvas.setFocusTraversable(false);
        }
        if (player2Canvas != null) {
            player2Canvas.setWidth(GameBoard.BOARD_WIDTH * BLOCK_SIZE);
            player2Canvas.setHeight(GameBoard.BOARD_HEIGHT * BLOCK_SIZE);
            player2Canvas.setFocusTraversable(false);
        }
        if (player1NextCanvas != null) {
            player1NextCanvas.setWidth(6 * BLOCK_SIZE);
            player1NextCanvas.setHeight(5 * BLOCK_SIZE);
        }
        if (player2NextCanvas != null) {
            player2NextCanvas.setWidth(6 * BLOCK_SIZE);
            player2NextCanvas.setHeight(5 * BLOCK_SIZE);
        }
        if (player1IncomingCanvas != null) {
            player1IncomingCanvas.setWidth(6 * BLOCK_SIZE);
            player1IncomingCanvas.setHeight(5 * BLOCK_SIZE);
        }
        if (player2IncomingCanvas != null) {
            player2IncomingCanvas.setWidth(6 * BLOCK_SIZE);
            player2IncomingCanvas.setHeight(5 * BLOCK_SIZE);
        }
    }

    private void setupKeyHandler() {
        if (player1Canvas != null) {
            player1Canvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setOnKeyPressed(event -> {
                        javafx.scene.input.KeyCode code = event.getCode();
                        
                        if (code == javafx.scene.input.KeyCode.ESCAPE) {
                            onPause();
                            event.consume();
                            return;
                        }
                        
                        if (battleEngine != null && battleEngine.isGameRunning() && !battleEngine.isPaused()) {
                            // Player1 키 설정
                            javafx.scene.input.KeyCode p1Left = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyLeft());
                            javafx.scene.input.KeyCode p1Right = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyRight());
                            javafx.scene.input.KeyCode p1Down = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyDown());
                            javafx.scene.input.KeyCode p1Rotate = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyRotate());
                            javafx.scene.input.KeyCode p1HardDrop = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyHardDrop());
                            
                            // Player2 키 설정
                            javafx.scene.input.KeyCode p2Left = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyLeftP2());
                            javafx.scene.input.KeyCode p2Right = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyRightP2());
                            javafx.scene.input.KeyCode p2Down = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyDownP2());
                            javafx.scene.input.KeyCode p2Rotate = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyRotateP2());
                            javafx.scene.input.KeyCode p2HardDrop = javafx.scene.input.KeyCode.valueOf(settingsManager.getKeyHardDropP2());
                            
                            // 플레이어 1 키 처리
                            if (code == p1Left || code == p1Right || code == p1Down || 
                                code == p1Rotate || code == p1HardDrop || 
                                code == javafx.scene.input.KeyCode.N) {
                                battleEngine.handlePlayer1KeyPress(code);
                                event.consume();
                            }
                            // 플레이어 2 키 처리
                            else if (!computerOpponentEnabled && (code == p2Left || code == p2Right || code == p2Down ||
                                     code == p2Rotate || code == p2HardDrop)) {
                                battleEngine.handlePlayer2KeyPress(code);
                                event.consume();
                            }
                        }
                    });
                }
            });
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (battleEngine == null) return;

                if (lastUpdateTime1 == 0) {
                    lastUpdateTime1 = now;
                }
                if (lastUpdateTime2 == 0) {
                    lastUpdateTime2 = now;
                }

                // 게임 오버 체크
                if (!battleEngine.isGameRunning()) {
                    gameLoop.stop();
                    showGameOver();
                    return;
                }

                // 업데이트
                battleEngine.update();

                // 플레이어 1 블록 낙하
                if (!isAnimatingClear1 && now - lastUpdateTime1 >= fallSpeed1) {
                    if (battleEngine.isGameRunning() && !battleEngine.isPaused()) {
                        battleEngine.getPlayer1Engine().movePieceDown();
                        // 공격은 placePiece() 내부의 콜백에서 자동으로 적용됨
                        java.util.List<Integer> fullLines = battleEngine.getPlayer1Engine().getFullLines();
                        if (!fullLines.isEmpty()) {
                            player1LinesToClear = fullLines;
                            isAnimatingClear1 = true;
                            clearAnimationStartTime1 = now;
                        }
                    }
                    lastUpdateTime1 = now;
                }

                // 플레이어 2 블록 낙하
                if (!isAnimatingClear2 && now - lastUpdateTime2 >= fallSpeed2) {
                    if (battleEngine.isGameRunning() && !battleEngine.isPaused()) {
                        battleEngine.getPlayer2Engine().movePieceDown();
                        // 공격은 placePiece() 내부의 콜백에서 자동으로 적용됨
                        java.util.List<Integer> fullLines = battleEngine.getPlayer2Engine().getFullLines();
                        if (!fullLines.isEmpty()) {
                            player2LinesToClear = fullLines;
                            isAnimatingClear2 = true;
                            clearAnimationStartTime2 = now;
                        }
                    }
                    lastUpdateTime2 = now;
                }
                

                // 애니메이션 처리
                if (isAnimatingClear1) {
                    long elapsed = now - clearAnimationStartTime1;
                    if (elapsed >= CLEAR_ANIMATION_DURATION) {
                        int beforeCleared = battleEngine.getPlayer1Engine().getLinesCleared();
                        battleEngine.getPlayer1Engine().clearLinesManually();
                        int afterCleared = battleEngine.getPlayer1Engine().getLinesCleared();
                        int cleared = afterCleared - beforeCleared;
                        
                        // 공격 메커니즘 처리 (줄 삭제 직후 바로 처리)
                        if (cleared >= 2) {
                            int lastBlockCol = battleEngine.getPlayer1Engine().getLastPlacedBlockCol();
                            battleEngine.processPlayer1Attack(cleared, lastBlockCol);
                        }
                        
                        isAnimatingClear1 = false;
                        player1LinesToClear = null;
                        lastUpdateTime1 = now;
                    }
                }

                if (isAnimatingClear2) {
                    long elapsed = now - clearAnimationStartTime2;
                    if (elapsed >= CLEAR_ANIMATION_DURATION) {
                        int beforeCleared = battleEngine.getPlayer2Engine().getLinesCleared();
                        battleEngine.getPlayer2Engine().clearLinesManually();
                        int afterCleared = battleEngine.getPlayer2Engine().getLinesCleared();
                        int cleared = afterCleared - beforeCleared;
                        
                        // 공격 메커니즘 처리 (줄 삭제 직후 바로 처리)
                        if (cleared >= 2) {
                            int lastBlockCol = battleEngine.getPlayer2Engine().getLastPlacedBlockCol();
                            battleEngine.processPlayer2Attack(cleared, lastBlockCol);
                        }
                        
                        isAnimatingClear2 = false;
                        player2LinesToClear = null;
                        lastUpdateTime2 = now;
                    }
                }

                // 렌더링
                renderPlayer1();
                renderPlayer2();
                renderNextPieces();
                renderIncomingLines();
                updateUI();
                updateFallSpeeds();
            }
        };
        gameLoop.start();
    }

    private void updateFallSpeeds() {
        if (battleEngine != null) {
            fallSpeed1 = (long) (1_000_000_000 * Math.pow(0.9, battleEngine.getPlayer1Engine().getLevel() - 1));
            fallSpeed2 = (long) (1_000_000_000 * Math.pow(0.9, battleEngine.getPlayer2Engine().getLevel() - 1));
            battleEngine.getPlayer1Engine().setFallSpeed(fallSpeed1);
            battleEngine.getPlayer2Engine().setFallSpeed(fallSpeed2);
        }
    }

    private void renderPlayer1() {
        if (player1Canvas == null || battleEngine == null) return;

        GraphicsContext gc = player1Canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, player1Canvas.getWidth(), player1Canvas.getHeight());

        // Canvas 크기에 맞게 블록 크기 계산
        double blockSize = Math.min(
            player1Canvas.getWidth() / GameBoard.BOARD_WIDTH,
            player1Canvas.getHeight() / GameBoard.BOARD_HEIGHT
        );

        // 색약모드에서는 회색 격자 표시
        if (settingsManager != null && settingsManager.isColorBlindModeEnabled()) {
            drawGrid(gc, player1Canvas, blockSize);
        }

        GameBoard board = battleEngine.getPlayer1Engine().getGameBoard();
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                int cellValue = board.getCell(row, col);
                if (cellValue > 0) {
                    ItemType itemType = board.getItemAt(row, col);
                    Color color;
                    
                    // 공격 블록은 회색으로 표시
                    if (board.isAttackBlock(row, col)) {
                        color = Color.web("#666666"); // 회색
                    } else if (isAnimatingClear1 && player1LinesToClear != null && player1LinesToClear.contains(row)) {
                        color = Color.WHITE;
                    } else {
                        color = PIECE_COLORS[cellValue];
                    }
                    
                    renderBlockScaled(gc, col * blockSize, row * blockSize, blockSize, color, cellValue, itemType);
                }
            }
        }

        if (!isAnimatingClear1) {
            Piece currentPiece = battleEngine.getPlayer1Engine().getCurrentPiece();
            if (currentPiece != null) {
                renderPieceScaled(gc, currentPiece, blockSize);
            }
        }

        renderBorder(gc, player1Canvas);
    }

    private void renderPlayer2() {
        if (player2Canvas == null || battleEngine == null) return;

        GraphicsContext gc = player2Canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, player2Canvas.getWidth(), player2Canvas.getHeight());

        // Canvas 크기에 맞게 블록 크기 계산
        double blockSize = Math.min(
            player2Canvas.getWidth() / GameBoard.BOARD_WIDTH,
            player2Canvas.getHeight() / GameBoard.BOARD_HEIGHT
        );

        // 색약모드에서는 회색 격자 표시
        if (settingsManager != null && settingsManager.isColorBlindModeEnabled()) {
            drawGrid(gc, player2Canvas, blockSize);
        }

        GameBoard board = battleEngine.getPlayer2Engine().getGameBoard();
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                int cellValue = board.getCell(row, col);
                if (cellValue > 0) {
                    ItemType itemType = board.getItemAt(row, col);
                    Color color;
                    
                    // 공격 블록은 회색으로 표시
                    if (board.isAttackBlock(row, col)) {
                        color = Color.web("#666666"); // 회색
                    } else if (isAnimatingClear2 && player2LinesToClear != null && player2LinesToClear.contains(row)) {
                        color = Color.WHITE;
                    } else {
                        color = PIECE_COLORS[cellValue];
                    }
                    
                    renderBlockScaled(gc, col * blockSize, row * blockSize, blockSize, color, cellValue, itemType);
                }
            }
        }

        if (!isAnimatingClear2) {
            Piece currentPiece = battleEngine.getPlayer2Engine().getCurrentPiece();
            if (currentPiece != null) {
                renderPieceScaled(gc, currentPiece, blockSize);
            }
        }

        renderBorder(gc, player2Canvas);
    }

    private void renderNextPieces() {
        if (battleEngine == null) return;

        // 플레이어 1 다음 블록
        if (player1NextCanvas != null) {
            GraphicsContext gc = player1NextCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, player1NextCanvas.getWidth(), player1NextCanvas.getHeight());

            Piece nextPiece = battleEngine.getPlayer1Engine().getNextPiece();
            if (nextPiece != null) {
                renderNextPiece(gc, nextPiece);
            }
        }

        // 플레이어 2 다음 블록
        if (player2NextCanvas != null) {
            GraphicsContext gc = player2NextCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, player2NextCanvas.getWidth(), player2NextCanvas.getHeight());

            Piece nextPiece = battleEngine.getPlayer2Engine().getNextPiece();
            if (nextPiece != null) {
                renderNextPiece(gc, nextPiece);
            }
        }
    }

    private void renderNextPiece(GraphicsContext gc, Piece piece) {
        int[][] shape = piece.getShape();
        Color color = PIECE_COLORS[piece.getType()];

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = piece.getItemAt(row, col);
                    renderBlock(gc, (col + 1) * BLOCK_SIZE, (row + 1) * BLOCK_SIZE, color, piece.getType(), itemType);
                }
            }
        }
    }

    private void renderIncomingLines() {
        if (battleEngine == null) return;

        // 플레이어 1에게 넘어가는 줄 렌더링
        if (player1IncomingCanvas != null) {
            GraphicsContext gc = player1IncomingCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, player1IncomingCanvas.getWidth(), player1IncomingCanvas.getHeight());

            int pendingLines = battleEngine.getPendingAttacksToPlayer1();
            if (pendingLines > 0) {
                java.util.List<Integer> emptyCols = battleEngine.getPendingAttackEmptyColsToPlayer1();
                renderIncomingLinesBlock(gc, pendingLines, emptyCols);
            }

            // 테두리
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, player1IncomingCanvas.getWidth(), player1IncomingCanvas.getHeight());
        }

        // 플레이어 2에게 넘어가는 줄 렌더링
        if (player2IncomingCanvas != null) {
            GraphicsContext gc = player2IncomingCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, player2IncomingCanvas.getWidth(), player2IncomingCanvas.getHeight());

            int pendingLines = battleEngine.getPendingAttacksToPlayer2();
            if (pendingLines > 0) {
                java.util.List<Integer> emptyCols = battleEngine.getPendingAttackEmptyColsToPlayer2();
                renderIncomingLinesBlock(gc, pendingLines, emptyCols);
            }

            // 테두리
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, player2IncomingCanvas.getWidth(), player2IncomingCanvas.getHeight());
        }
    }

    private void renderIncomingLinesBlock(GraphicsContext gc, int numLines, java.util.List<Integer> emptyCols) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        
        // Canvas 크기에 맞게 블록 크기 계산 (최대 10줄까지 표시, 오른쪽 여백 없이 딱 맞게)
        // BOARD_WIDTH만큼만 사용하여 오른쪽 여백 제거
        double blockSize = Math.min(canvasWidth / GameBoard.BOARD_WIDTH, canvasHeight / 12);
        
        // 최대 10줄까지 표시
        int displayLines = Math.min(numLines, 10);
        Color attackColor = Color.web("#666666"); // 회색
        Color gridColor = Color.web("#333333"); // 격자 색상

        // 격자 그리기
        gc.setStroke(gridColor);
        gc.setLineWidth(0.5);
        for (int row = 0; row <= 10; row++) {
            double y = row * blockSize;
            gc.strokeLine(0, y, GameBoard.BOARD_WIDTH * blockSize, y);
        }
        for (int col = 0; col <= GameBoard.BOARD_WIDTH; col++) {
            double x = col * blockSize;
            gc.strokeLine(x, 0, x, Math.min(10 * blockSize, canvasHeight));
        }

        // 각 줄을 렌더링 (마지막 블록 위치에 빈칸, 작은 원으로 표시)
        double circleRadius = blockSize * 0.35; // 원의 반지름 (블록 크기의 35%)
        for (int line = 0; line < displayLines; line++) {
            double y = line * blockSize + blockSize / 2; // 줄의 중앙
            // 큐에서 빈칸 위치 가져오기 (없으면 기본값)
            int emptyCol = (line < emptyCols.size()) ? emptyCols.get(line) : (line * 3) % GameBoard.BOARD_WIDTH;
            // emptyCol 값이 유효한 범위인지 확인
            if (emptyCol < 0 || emptyCol >= GameBoard.BOARD_WIDTH) {
                emptyCol = (line * 3) % GameBoard.BOARD_WIDTH;
            }
            
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                double x = col * blockSize + blockSize / 2; // 열의 중앙
                if (col == emptyCol) {
                    // 빈 칸은 그대로 둠 (검은 배경)
                } else {
                    // 작은 원으로 표시
                    gc.setFill(attackColor);
                    gc.fillOval(x - circleRadius, y - circleRadius, circleRadius * 2, circleRadius * 2);
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(0.5);
                    gc.strokeOval(x - circleRadius, y - circleRadius, circleRadius * 2, circleRadius * 2);
                }
            }
        }

        // 더 많은 줄이 있으면 숫자로 표시
        if (numLines > 10) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, (int)(blockSize * 0.8)));
            gc.fillText("+" + (numLines - 10), GameBoard.BOARD_WIDTH * blockSize - 20, 10 * blockSize - 2);
        }
    }

    private void renderPieceScaled(GraphicsContext gc, Piece piece, double blockSize) {
        int[][] shape = piece.getShape();
        Color color = PIECE_COLORS[piece.getType()];
        int pieceX = piece.getX();
        int pieceY = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = piece.getItemAt(row, col);
                    double x = (pieceX + col) * blockSize;
                    double y = (pieceY + row) * blockSize;
                    renderBlockScaled(gc, x, y, blockSize, color, piece.getType(), itemType);
                }
            }
        }
    }

    private void renderBlock(GraphicsContext gc, int x, int y, Color color, int pieceType, ItemType itemType) {
        renderBlockScaled(gc, x, y, BLOCK_SIZE, color, pieceType, itemType);
    }
    
    private void renderBlockScaled(GraphicsContext gc, double x, double y, double size, Color color, int pieceType, ItemType itemType) {
        // 색약모드가 켜져 있으면 색 대신 심볼로 채운다
        if (settingsManager != null && settingsManager.isColorBlindModeEnabled()) {
            String symbol = "?";
            if (pieceType >= 0 && pieceType < PIECE_SYMBOLS.length) {
                symbol = PIECE_SYMBOLS[pieceType];
            }

            // 아이콘을 블록 크기에 맞게 최대한 크게 설정
            double fontSize = size - 2;
            if (fontSize < 8) fontSize = 8;
            Font font = Font.font("Monospaced", fontSize);
            gc.setFont(font);
            gc.setFill(Color.WHITE);

            Text text = new Text(symbol);
            text.setFont(font);
            double textWidth = text.getLayoutBounds().getWidth();
            double textHeight = text.getLayoutBounds().getHeight();

            // 사각형 배경 (검정색)
            gc.setFill(Color.BLACK);
            gc.fillRect(x, y, size, size);

            // 심볼 그리기 (정중앙 정렬)
            gc.setFill(Color.WHITE);
            double tx = x + (size - textWidth) / 2.0;
            double ty = y + (size + textHeight) / 2.0 - 4;
            gc.fillText(symbol, tx, ty);

            // 아이템 표시는 별도로
            if (itemType != null && itemType != ItemType.NONE) {
                String itemChar = itemType.getDisplayChar();
                if (!itemChar.isEmpty()) {
                    double itemFontSize = size * 0.4;
                    Font itemFont = Font.font("Arial", javafx.scene.text.FontWeight.BOLD, itemFontSize);
                    gc.setFont(itemFont);
                    gc.setFill(Color.YELLOW);

                    Text itemText = new Text(itemChar);
                    itemText.setFont(itemFont);
                    double itemTextWidth = itemText.getLayoutBounds().getWidth();

                    double itemTx = x + size - itemTextWidth - 2;
                    double itemTy = y + itemFontSize + 2;
                    gc.fillText(itemChar, itemTx, itemTy);
                }
            }
        } else {
            // 일반 모드
            gc.setFill(color);
            gc.fillRect(x, y, size, size);

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeRect(x, y, size, size);

            if (itemType != null && itemType != ItemType.NONE) {
                String itemChar = itemType.getDisplayChar();
                if (!itemChar.isEmpty()) {
                    double fontSize = size * 0.6;
                    Font font = Font.font("Arial", javafx.scene.text.FontWeight.BOLD, fontSize);
                    gc.setFont(font);
                    gc.setFill(Color.WHITE);

                    Text text = new Text(itemChar);
                    text.setFont(font);
                    double textWidth = text.getLayoutBounds().getWidth();
                    double textHeight = text.getLayoutBounds().getHeight();

                    double tx = x + (size - textWidth) / 2.0;
                    double ty = y + (size + textHeight) / 2.0 - 2;

                    gc.fillText(itemChar, tx, ty);
                }
            }
        }
    }

    // 색약모드용 보드 격자선 렌더링
    private void drawGrid(GraphicsContext gc, Canvas canvas, double blockSize) {
        gc.setStroke(Color.web("#444444"));
        gc.setLineWidth(1);
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // 세로선
        for (int x = 0; x <= GameBoard.BOARD_WIDTH; x++) {
            double px = x * blockSize;
            gc.strokeLine(px, 0, px, height);
        }
        // 가로선
        for (int y = 0; y <= GameBoard.BOARD_HEIGHT; y++) {
            double py = y * blockSize;
            gc.strokeLine(0, py, width, py);
        }
    }

    private void renderBorder(GraphicsContext gc, Canvas canvas) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void updateUI() {
        if (battleEngine == null) return;

        // 플레이어 1 정보
        if (player1ScoreLabel != null) {
            player1ScoreLabel.setText("" + battleEngine.getPlayer1Engine().getScore());
        }
        if (player1LevelLabel != null) {
            player1LevelLabel.setText("LEVEL: " + battleEngine.getPlayer1Engine().getLevel());
        }
        if (player1LinesLabel != null) {
            player1LinesLabel.setText("LINES: " + battleEngine.getPlayer1Engine().getLinesCleared());
        }

        // 플레이어 2 정보
        if (player2ScoreLabel != null) {
            player2ScoreLabel.setText("" + battleEngine.getPlayer2Engine().getScore());
        }
        if (player2LevelLabel != null) {
            player2LevelLabel.setText("LEVEL: " + battleEngine.getPlayer2Engine().getLevel());
        }
        if (player2LinesLabel != null) {
            player2LinesLabel.setText("LINES: " + battleEngine.getPlayer2Engine().getLinesCleared());
        }

        // 시간제한 모드 타이머
        if (timerLabel != null && battleEngine.isTimeLimitMode()) {
            long remaining = battleEngine.getRemainingTime();
            long minutes = remaining / 60;
            long seconds = remaining % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        } else if (timerLabel != null) {
            timerLabel.setText("");
        }

        // 승자 표시
        if (winnerLabel != null) {
            String winner = battleEngine.getWinner();
            if (winner != null) {
                switch (winner) {
                    case "PLAYER1":
                        winnerLabel.setText("플레이어 1 승리!");
                        winnerLabel.setTextFill(Color.GREEN);
                        break;
                    case "PLAYER2":
                        winnerLabel.setText("플레이어 2 승리!");
                        winnerLabel.setTextFill(Color.GREEN);
                        break;
                    case "DRAW":
                        winnerLabel.setText("무승부!");
                        winnerLabel.setTextFill(Color.YELLOW);
                        break;
                }
            } else {
                winnerLabel.setText("");
            }
        }
    }

    @FXML
    private void onPause() {
        if (battleEngine != null) {
            battleEngine.pauseGame();
        }
    }

    @FXML
    private void onBackToMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        stopComputerOpponent();
        if (battleEngine != null) {
            battleEngine.stopGame();
        }
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    private void showGameOver() {
        stopComputerOpponent();
        if (sceneManager != null) {
            updateUI();
            
            Platform.runLater(() -> {
                // 승패 판정
                boolean player1Lost = !battleEngine.getPlayer1Engine().isGameRunning();
                boolean player2Lost = !battleEngine.getPlayer2Engine().isGameRunning();
                
                if (player1Lost && player2Lost) {
                    winnerLabel.setText("무승부!");
                } else if (player1Lost) {
                    winnerLabel.setText("플레이어 2 승리!");
                } else if (player2Lost) {
                    winnerLabel.setText("플레이어 1 승리!");
                }
                
                // 게임 오버 버튼 표시
                if (gameOverBox != null) {
                    gameOverBox.setVisible(true);
                    gameOverBox.setManaged(true);
                }
            });
        }
    }

    @FXML
    private void onRematch() {
        System.out.println("[BATTLE] Rematch");
        restartGame();
    }

    private void restartGame() {
        Platform.runLater(() -> {
            // 게임 오버 UI 숨기기
            if (gameOverBox != null) {
                gameOverBox.setVisible(false);
                gameOverBox.setManaged(false);
            }
            winnerLabel.setText("");
            
            // 게임 루프 정지
            if (gameLoop != null) {
                gameLoop.stop();
            }
            
            // 게임 엔진 재초기화
            stopComputerOpponent();
            battleEngine = new BattleGameEngine(battleMode);
            configureBattleEngine();
            
            // 게임 시작
            startGameLoop();
            battleEngine.startGame();
            startComputerOpponentIfNeeded();
        });
    }
}

