package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tetris.ui.SceneManager;
import tetris.game.BattleGameEngine;
import tetris.game.GameBoard;
import tetris.game.Piece;
import tetris.game.ItemType;
import tetris.network.GameClient;
import tetris.network.GameServer;
import tetris.network.NetworkMessage;
import tetris.network.GameStateData;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PVPGameScreenController implements Initializable {

    @FXML
    private Canvas myCanvas;

    @FXML
    private Canvas opponentCanvas;

    @FXML
    private Canvas myNextCanvas;

    @FXML
    private Canvas opponentNextCanvas;

    @FXML
    private Label myPlayerLabel;

    @FXML
    private Label opponentPlayerLabel;

    @FXML
    private Label myScoreLabel;

    @FXML
    private Label opponentScoreLabel;

    @FXML
    private Label myLevelLabel;

    @FXML
    private Label opponentLevelLabel;

    @FXML
    private Label myLinesLabel;

    @FXML
    private Label opponentLinesLabel;

    @FXML
    private Label gameModeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label latencyLabel;

    private SceneManager sceneManager;
    private String gameMode;
    private GameServer gameServer;
    private GameClient gameClient;
    private boolean isServer;

    private BattleGameEngine battleEngine;
    private AnimationTimer gameLoop;
    private long lastUpdateTimeMe = 0;
    private long lastUpdateTimeOpponent = 0;
    private long fallSpeedMe = 1_000_000_000;
    private long fallSpeedOpponent = 1_000_000_000;

    // 네트워크 트래픽 최적화: 상태 전송 빈도 제한
    private long lastStateSentTime = 0;
    private static final long STATE_SEND_INTERVAL = 50_000_000; // 50ms (20 FPS)

    // 상대방 상태 데이터
    private GameStateData opponentState;
    
    // 상대방 공격 큐
    private int opponentIncomingLines = 0;

    private int BLOCK_SIZE = 25;

    // 블록 색상 설정
    private static final Color[] PIECE_COLORS = {
        Color.BLACK,
        Color.web("#56B4E9"),  // 1 - I
        Color.web("#F0E442"),  // 2 - O
        Color.web("#CC79A7"),  // 3 - T
        Color.web("#009E73"),  // 4 - S
        Color.web("#D55E00"),  // 5 - Z
        Color.web("#0072B2"),  // 6 - J
        Color.web("#E69F00"),  // 7 - L
        Color.web("#999999"),  // 8 - 공격 블록 (회색)
        Color.web("#FF0000")   // 9 - BOMB
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCanvasSize();
    }

    private void setupCanvasSize() {
        if (myCanvas != null) {
            myCanvas.setWidth(GameBoard.BOARD_WIDTH * BLOCK_SIZE);
            myCanvas.setHeight(GameBoard.BOARD_HEIGHT * BLOCK_SIZE);
            myCanvas.setFocusTraversable(false);
        }
        if (opponentCanvas != null) {
            opponentCanvas.setWidth(GameBoard.BOARD_WIDTH * BLOCK_SIZE);
            opponentCanvas.setHeight(GameBoard.BOARD_HEIGHT * BLOCK_SIZE);
            opponentCanvas.setFocusTraversable(false);
        }
        if (myNextCanvas != null) {
            myNextCanvas.setWidth(6 * BLOCK_SIZE);
            myNextCanvas.setHeight(5 * BLOCK_SIZE);
        }
        if (opponentNextCanvas != null) {
            opponentNextCanvas.setWidth(6 * BLOCK_SIZE);
            opponentNextCanvas.setHeight(5 * BLOCK_SIZE);
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
        updateGameModeLabel();
    }

    public void setNetworkObjects(Object server, Object client, boolean isServer) {
        this.gameServer = (GameServer) server;
        this.gameClient = (GameClient) client;
        this.isServer = isServer;
        
        System.out.println("네트워크 객체 설정 완료");
        System.out.println("서버 모드: " + isServer);
        System.out.println("게임 모드: " + gameMode);

        // BattleGameEngine 초기화
        initializeGame();
        
        // 네트워크 메시지 핸들러 설정
        setupNetworkHandlers();
        
        // 키 입력 핸들러 설정
        setupKeyHandler();
        
        // 게임 시작
        startGame();
    }

    private void initializeGame() {
        battleEngine = new BattleGameEngine(gameMode);
        
        if (isServer) {
            myPlayerLabel.setText("서버 (나)");
            opponentPlayerLabel.setText("클라이언트");
        } else {
            myPlayerLabel.setText("클라이언트 (나)");
            opponentPlayerLabel.setText("서버");
        }
        
        // 내 블록이 배치될 때마다 공격 적용
        getMyEngine().setOnPiecePlacedCallback(() -> {
            battleEngine.applyPendingAttacks(isServer ? 1 : 2);
        });
    }

    private void setupNetworkHandlers() {
        if (isServer && gameServer != null) {
            gameServer.setMessageHandler(new GameServer.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    if (message instanceof NetworkMessage) {
                        handleNetworkMessage((NetworkMessage) message);
                    }
                }

                @Override
                public void onClientConnected() {
                    System.out.println("클라이언트 연결됨");
                }

                @Override
                public void onClientDisconnected() {
                    Platform.runLater(() -> {
                        statusLabel.setText("상대방 연결 끊김");
                        if (gameLoop != null) {
                            gameLoop.stop();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("서버 에러: " + e.getMessage());
                }
            });
        } else if (!isServer && gameClient != null) {
            gameClient.setMessageHandler(new GameClient.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    if (message instanceof NetworkMessage) {
                        handleNetworkMessage((NetworkMessage) message);
                    }
                }

                @Override
                public void onConnected() {
                    System.out.println("서버 연결됨");
                }

                @Override
                public void onDisconnected() {
                    Platform.runLater(() -> {
                        statusLabel.setText("서버 연결 끊김");
                        if (gameLoop != null) {
                            gameLoop.stop();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("클라이언트 에러: " + e.getMessage());
                }

                @Override
                public void onRttUpdate(long rtt) {
                    Platform.runLater(() -> {
                        if (latencyLabel != null) {
                            latencyLabel.setText("RTT: " + rtt + " ms");
                        }
                    });
                }
            });
        }
    }

    private void handleNetworkMessage(NetworkMessage message) {
        Platform.runLater(() -> {
            switch (message.getType()) {
                case GAME_STATE_UPDATE:
                    GameStateData stateData = (GameStateData) message.getData();
                    opponentState = stateData;
                    opponentIncomingLines = stateData.getIncomingAttackLines();
                    break;
                    
                case ATTACK:
                    // 상대방이 공격을 보냄
                    @SuppressWarnings("unchecked")
                    Map<String, Object> attackData = (Map<String, Object>) message.getData();
                    int lines = (Integer) attackData.get("lines");
                    int emptyCol = (Integer) attackData.get("emptyCol");
                    receiveAttack(lines, emptyCol);
                    break;
                    
                case GAME_OVER:
                    @SuppressWarnings("unchecked")
                    Map<String, Object> gameOverData = (Map<String, Object>) message.getData();
                    boolean opponentLost = (Boolean) gameOverData.get("isGameOver");
                    if (opponentLost) {
                        statusLabel.setText("승리!");
                        if (gameLoop != null) {
                            gameLoop.stop();
                        }
                    }
                    break;
                    
                case DISCONNECT:
                    statusLabel.setText("상대방이 나갔습니다");
                    if (gameLoop != null) {
                        gameLoop.stop();
                    }
                    break;
                
                default:
                    break;
            }
        });
    }

    private void receiveAttack(int lines, int emptyCol) {
        // 상대방으로부터 공격을 받아 내 공격 큐에 직접 추가
        if (isServer) {
            // 서버는 Player1 - 상대방의 공격을 Player1에게 추가
            battleEngine.addAttackToPlayer1(lines, emptyCol);
        } else {
            // 클라이언트는 Player2 - 상대방의 공격을 Player2에게 추가
            battleEngine.addAttackToPlayer2(lines, emptyCol);
        }
    }

    private void setupKeyHandler() {
        if (myCanvas != null) {
            myCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setOnKeyPressed(event -> {
                        javafx.scene.input.KeyCode code = event.getCode();
                        
                        if (code == javafx.scene.input.KeyCode.ESCAPE) {
                            onPause();
                            event.consume();
                            return;
                        }
                        
                        if (battleEngine != null && battleEngine.isGameRunning() && !battleEngine.isPaused()) {
                            if (isServer) {
                                battleEngine.handlePlayer1KeyPress(code);
                            } else {
                                battleEngine.handlePlayer2KeyPress(code);
                            }
                            event.consume();

                            // 키 입력 시에는 상태 전송 생략 (게임 루프에서 전송)
                        }
                    });
                }
            });
        }
    }

    private void startGame() {
        battleEngine.startGame();
        
        gameLoop = new AnimationTimer() {
            private int myPreviousLinesCleared = 0;
            
            @Override
            public void handle(long now) {
                if (battleEngine == null) return;

                if (lastUpdateTimeMe == 0) {
                    lastUpdateTimeMe = now;
                }
                if (lastUpdateTimeOpponent == 0) {
                    lastUpdateTimeOpponent = now;
                }

                // 게임 오버 체크
                if (!battleEngine.isGameRunning()) {
                    gameLoop.stop();
                    showGameOver();
                    return;
                }

                // 업데이트
                battleEngine.update();

                // 내 블록 낙하
                if (now - lastUpdateTimeMe >= fallSpeedMe) {
                    if (battleEngine.isGameRunning() && !battleEngine.isPaused()) {
                        getMyEngine().movePieceDown();

                        // 블록이 배치되었는지 확인 후 줄 삭제 처리
                        if (getMyEngine().isPieceJustPlaced()) {
                            // 줄 삭제 수동 처리
                            java.util.List<Integer> fullLines = getMyEngine().getFullLines();
                            if (!fullLines.isEmpty()) {
                                getMyEngine().clearLinesManually();
                            }
                            getMyEngine().resetPieceJustPlaced();
                        }

                        // 줄 삭제 체크 및 공격 전송
                        int currentLinesCleared = getMyEngine().getLinesCleared();
                        if (currentLinesCleared > myPreviousLinesCleared) {
                            int cleared = currentLinesCleared - myPreviousLinesCleared;
                            myPreviousLinesCleared = currentLinesCleared;

                            // 2줄 이상 삭제시 공격
                            if (cleared >= 2) {
                                int lastBlockCol = getMyEngine().getLastPlacedBlockCol();
                                sendAttack(cleared, lastBlockCol);
                            }
                        }

                        lastUpdateTimeMe = now;

                        // 상태 업데이트 전송 (빈도 제한 적용)
                        sendMyStateThrottled(now);
                    }
                }

                // 렌더링
                renderMyBoard();
                renderOpponentBoard();
                renderNextPieces();
                updateUI();
                updateFallSpeeds();
            }
        };
        gameLoop.start();
    }

    private void updateFallSpeeds() {
        if (battleEngine != null) {
            fallSpeedMe = (long) (1_000_000_000 * Math.pow(0.9, getMyEngine().getLevel() - 1));
            getMyEngine().setFallSpeed(fallSpeedMe);
            
            if (opponentState != null) {
                fallSpeedOpponent = (long) (1_000_000_000 * Math.pow(0.9, opponentState.getLevel() - 1));
            }
        }
    }

    private tetris.game.GameEngine getMyEngine() {
        return isServer ? battleEngine.getPlayer1Engine() : battleEngine.getPlayer2Engine();
    }

    private int getMyPendingAttacks() {
        return isServer ? battleEngine.getPendingAttacksToPlayer1() : battleEngine.getPendingAttacksToPlayer2();
    }

    private void sendMyState() {
        if (battleEngine == null) return;

        tetris.game.GameEngine myEngine = getMyEngine();
        GameBoard board = myEngine.getGameBoard();
        Piece currentPiece = myEngine.getCurrentPiece();
        Piece nextPiece = myEngine.getNextPiece();

        int[][] boardData = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];
        int[][] itemBoardData = new int[GameBoard.BOARD_HEIGHT][GameBoard.BOARD_WIDTH];

        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                boardData[row][col] = board.getCell(row, col);
                ItemType itemType = board.getItemAt(row, col);
                itemBoardData[row][col] = (itemType != null) ? itemType.ordinal() : 0;
            }
        }

        int[][] currentShape = (currentPiece != null) ? currentPiece.getShape() : new int[0][0];
        int currentX = (currentPiece != null) ? currentPiece.getX() : 0;
        int currentY = (currentPiece != null) ? currentPiece.getY() : 0;
        int currentType = (currentPiece != null) ? currentPiece.getType() : 0;

        int[][] nextShape = (nextPiece != null) ? nextPiece.getShape() : new int[0][0];
        int nextType = (nextPiece != null) ? nextPiece.getType() : 0;

        int incomingLines = getMyPendingAttacks();

        GameStateData stateData = new GameStateData(
            boardData, itemBoardData,
            myEngine.getScore(),
            myEngine.getLevel(), myEngine.getLinesCleared(),
            !myEngine.isGameRunning(),
            currentShape, currentX, currentY, currentType,
            nextShape, nextType, incomingLines
        );

        NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.GAME_STATE_UPDATE, stateData);

        try {
            if (isServer && gameServer != null) {
                gameServer.sendMessage(message);
            } else if (!isServer && gameClient != null) {
                gameClient.sendMessage(message);
            }
        } catch (IOException e) {
            System.err.println("상태 전송 실패: " + e.getMessage());
        }
    }

    /**
     * 상태 전송 빈도 제한 (네트워크 트래픽 최적화)
     * @param now 현재 시간 (나노초)
     */
    private void sendMyStateThrottled(long now) {
        if (now - lastStateSentTime >= STATE_SEND_INTERVAL) {
            sendMyState();
            lastStateSentTime = now;
        }
    }

    private void sendAttack(int lines, int emptyCol) {
        Map<String, Object> attackData = new HashMap<>();
        attackData.put("lines", lines);
        attackData.put("emptyCol", emptyCol);
        
        NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.ATTACK, attackData);
        
        try {
            if (isServer && gameServer != null) {
                gameServer.sendMessage(message);
            } else if (!isServer && gameClient != null) {
                gameClient.sendMessage(message);
            }
        } catch (IOException e) {
            System.err.println("공격 전송 실패: " + e.getMessage());
        }
    }

    private void renderMyBoard() {
        if (myCanvas == null || battleEngine == null) return;

        GraphicsContext gc = myCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, myCanvas.getWidth(), myCanvas.getHeight());

        double blockSize = Math.min(
            myCanvas.getWidth() / GameBoard.BOARD_WIDTH,
            myCanvas.getHeight() / GameBoard.BOARD_HEIGHT
        );

        GameBoard board = getMyEngine().getGameBoard();
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                int cellValue = board.getCell(row, col);
                if (cellValue > 0) {
                    ItemType itemType = board.getItemAt(row, col);
                    Color color = board.isAttackBlock(row, col) ? Color.web("#666666") : PIECE_COLORS[cellValue];
                    renderBlockScaled(gc, col * blockSize, row * blockSize, blockSize, color, cellValue, itemType);
                }
            }
        }

        Piece currentPiece = getMyEngine().getCurrentPiece();
        if (currentPiece != null) {
            renderPieceScaled(gc, currentPiece, blockSize);
        }

        renderBorder(gc, myCanvas);
    }

    private void renderOpponentBoard() {
        if (opponentCanvas == null || opponentState == null) return;

        GraphicsContext gc = opponentCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, opponentCanvas.getWidth(), opponentCanvas.getHeight());

        double blockSize = Math.min(
            opponentCanvas.getWidth() / GameBoard.BOARD_WIDTH,
            opponentCanvas.getHeight() / GameBoard.BOARD_HEIGHT
        );

        int[][] board = opponentState.getBoard();
        int[][] itemBoard = opponentState.getItemBoard();
        
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                int cellValue = board[row][col];
                if (cellValue > 0) {
                    ItemType itemType = ItemType.values()[itemBoard[row][col]];
                    Color color = (cellValue == 8) ? Color.web("#666666") : PIECE_COLORS[cellValue];
                    renderBlockScaled(gc, col * blockSize, row * blockSize, blockSize, color, cellValue, itemType);
                }
            }
        }

        // 현재 블록 렌더링
        int[][] currentShape = opponentState.getCurrentPieceShape();
        if (currentShape != null && currentShape.length > 0) {
            int pieceX = opponentState.getCurrentPieceX();
            int pieceY = opponentState.getCurrentPieceY();
            int pieceType = opponentState.getCurrentPieceType();
            Color color = PIECE_COLORS[pieceType];

            for (int row = 0; row < currentShape.length; row++) {
                for (int col = 0; col < currentShape[row].length; col++) {
                    if (currentShape[row][col] != 0) {
                        double x = (pieceX + col) * blockSize;
                        double y = (pieceY + row) * blockSize;
                        renderBlockScaled(gc, x, y, blockSize, color, pieceType, null);
                    }
                }
            }
        }

        renderBorder(gc, opponentCanvas);
    }

    private void renderNextPieces() {
        // 내 다음 블록
        if (myNextCanvas != null && battleEngine != null) {
            GraphicsContext gc = myNextCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, myNextCanvas.getWidth(), myNextCanvas.getHeight());

            Piece nextPiece = getMyEngine().getNextPiece();
            if (nextPiece != null) {
                renderNextPiece(gc, nextPiece, myNextCanvas.getWidth(), myNextCanvas.getHeight());
            }

            renderBorder(gc, myNextCanvas);
        }

        // 상대방 다음 블록
        if (opponentNextCanvas != null && opponentState != null) {
            GraphicsContext gc = opponentNextCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, opponentNextCanvas.getWidth(), opponentNextCanvas.getHeight());

            int[][] nextShape = opponentState.getNextPieceShape();
            if (nextShape != null && nextShape.length > 0) {
                int nextType = opponentState.getNextPieceType();
                Color color = PIECE_COLORS[nextType];
                
                double blockSize = Math.min(
                    opponentNextCanvas.getWidth() / 6,
                    opponentNextCanvas.getHeight() / 5
                );
                
                double offsetX = (opponentNextCanvas.getWidth() - nextShape[0].length * blockSize) / 2;
                double offsetY = (opponentNextCanvas.getHeight() - nextShape.length * blockSize) / 2;

                for (int row = 0; row < nextShape.length; row++) {
                    for (int col = 0; col < nextShape[row].length; col++) {
                        if (nextShape[row][col] != 0) {
                            double x = offsetX + col * blockSize;
                            double y = offsetY + row * blockSize;
                            renderBlockScaled(gc, x, y, blockSize, color, nextType, null);
                        }
                    }
                }
            }

            renderBorder(gc, opponentNextCanvas);
        }
    }

    private void renderNextPiece(GraphicsContext gc, Piece piece, double canvasWidth, double canvasHeight) {
        int[][] shape = piece.getShape();
        Color color = PIECE_COLORS[piece.getType()];
        
        double blockSize = Math.min(canvasWidth / 6, canvasHeight / 5);
        double offsetX = (canvasWidth - shape[0].length * blockSize) / 2;
        double offsetY = (canvasHeight - shape.length * blockSize) / 2;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = piece.getItemAt(row, col);
                    double x = offsetX + col * blockSize;
                    double y = offsetY + row * blockSize;
                    renderBlockScaled(gc, x, y, blockSize, color, piece.getType(), itemType);
                }
            }
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

    private void renderBlockScaled(GraphicsContext gc, double x, double y, double size, Color color, int pieceType, ItemType itemType) {
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
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1);
                
                Text text = new Text(itemChar);
                text.setFont(font);
                double textWidth = text.getLayoutBounds().getWidth();
                double textHeight = text.getLayoutBounds().getHeight();
                double textX = x + (size - textWidth) / 2;
                double textY = y + (size + textHeight) / 2 - 2;
                
                gc.strokeText(itemChar, textX, textY);
                gc.fillText(itemChar, textX, textY);
            }
        }
    }

    private void renderBorder(GraphicsContext gc, Canvas canvas) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void updateUI() {
        if (battleEngine != null) {
            tetris.game.GameEngine myEngine = getMyEngine();
            myScoreLabel.setText(String.valueOf(myEngine.getScore()));
            myLevelLabel.setText("Lv: " + myEngine.getLevel());
            myLinesLabel.setText("Lines: " + myEngine.getLinesCleared());
        }

        if (opponentState != null) {
            opponentScoreLabel.setText(String.valueOf(opponentState.getScore()));
            opponentLevelLabel.setText("Lv: " + opponentState.getLevel());
            opponentLinesLabel.setText("Lines: " + opponentState.getLinesCleared());
        }
    }

    private void updateGameModeLabel() {
        if (gameModeLabel != null && gameMode != null) {
            String modeText = "";
            switch (gameMode) {
                case "NORMAL":
                    modeText = "일반 모드";
                    break;
                case "ITEM":
                    modeText = "아이템 모드";
                    break;
                case "TIME_LIMIT":
                    modeText = "시간제한 모드";
                    break;
            }
            gameModeLabel.setText(modeText);
        }
    }

    private void showGameOver() {
        Platform.runLater(() -> {
            statusLabel.setText("패배...");
            
            // 게임 오버 메시지 전송
            Map<String, Object> gameOverData = new HashMap<>();
            gameOverData.put("isGameOver", true);
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.GAME_OVER, gameOverData);
            
            try {
                if (isServer && gameServer != null) {
                    gameServer.sendMessage(message);
                } else if (!isServer && gameClient != null) {
                    gameClient.sendMessage(message);
                }
            } catch (IOException e) {
                System.err.println("게임 오버 메시지 전송 실패: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onPause() {
        if (battleEngine != null) {
            battleEngine.pauseGame();
            if (battleEngine.isPaused()) {
                statusLabel.setText("일시 정지");
            } else {
                statusLabel.setText("");
            }
        }
    }

    @FXML
    private void onBackToMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        // 연결 종료 메시지 전송
        NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.DISCONNECT, null);
        try {
            if (isServer && gameServer != null) {
                gameServer.sendMessage(message);
                gameServer.close();
            } else if (!isServer && gameClient != null) {
                gameClient.sendMessage(message);
                gameClient.close();
            }
        } catch (IOException e) {
            System.err.println("종료 메시지 전송 실패: " + e.getMessage());
        }
        
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }
}
