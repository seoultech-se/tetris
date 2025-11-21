package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;
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
    private Canvas myIncomingCanvas;

    @FXML
    private Canvas opponentIncomingCanvas;

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

    @FXML
    private VBox gameOverBox;

    @FXML
    private Button rematchButton;

    @FXML
    private Button toLobbyButton;

    private SceneManager sceneManager;
    private SettingsManager settingsManager;
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
    
    // 카운트다운 관련
    private boolean isCountingDown = false;
    private int countdownNumber = 3;
    private long lastCountdownTime = 0;
    private static final long COUNTDOWN_INTERVAL = 1_000_000_000L; // 1초

    // 줄 삭제 애니메이션 관련
    private java.util.List<Integer> playerLinesToClear = null;
    private long clearAnimationStartTime = 0;
    private static final long CLEAR_ANIMATION_DURATION = 100_000_000;
    private boolean isAnimatingClear = false;

    // 상대방 상태 데이터
    private GameStateData opponentState;
    
    // 상대방 공격 큐
    private int opponentIncomingLines = 0;

    // 상태 전송 빈도 제한 관련
    private long lastStateSentTime = 0;
    private static final long STATE_SEND_INTERVAL = 50_000_000; // 50ms (나노초 단위)

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
        settingsManager = tetris.ui.SettingsManager.getInstance();
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
        if (myIncomingCanvas != null) {
            myIncomingCanvas.setWidth(6 * BLOCK_SIZE);
            myIncomingCanvas.setHeight(5 * BLOCK_SIZE);
        }
        if (opponentIncomingCanvas != null) {
            opponentIncomingCanvas.setWidth(6 * BLOCK_SIZE);
            opponentIncomingCanvas.setHeight(5 * BLOCK_SIZE);
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
        updateGameModeLabel();
    }

    // PVPNetworkSelectionController에서 메시지 전달용
    public void receiveNetworkMessage(NetworkMessage message) {
        try {
            System.out.println("[PVP-GAME] receiveNetworkMessage called with: " + message.getType());
            handleNetworkMessage(message);
            System.out.println("[PVP-GAME] Message handled successfully");
        } catch (Exception e) {
            System.err.println("[PVP-GAME] Error in receiveNetworkMessage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setNetworkObjects(Object server, Object client, boolean isServer) {
        System.out.println("[PVP-GAME] setNetworkObjects called");
        this.gameServer = (GameServer) server;
        this.gameClient = (GameClient) client;
        this.isServer = isServer;

        System.out.println("[PVP-GAME] Network objects configured");
        System.out.println("[PVP-GAME] Server mode: " + isServer);
        System.out.println("[PVP-GAME] Game mode: " + gameMode);

        // BattleGameEngine 초기화
        System.out.println("[PVP-GAME] Initializing game engine...");
        initializeGame();

        // 네트워크 메시지 핸들러 설정
        System.out.println("[PVP-GAME] Setting up network handlers...");
        setupNetworkHandlers();

        // 키 입력 핸들러 설정
        System.out.println("[PVP-GAME] Setting up key handler...");
        setupKeyHandler();

        // 카운트다운 시작
        System.out.println("[PVP-GAME] Starting countdown...");
        startCountdown();
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
        System.out.println("[PVP-GAME] Setting up network handlers for game screen");
        
        if (isServer && gameServer != null) {
            System.out.println("[PVP-GAME] Setting up server message handler");
            gameServer.setMessageHandler(new GameServer.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    if (message instanceof NetworkMessage) {
                        handleNetworkMessage((NetworkMessage) message);
                    }
                }

                @Override
                public void onClientConnected() {
                    System.out.println("[PVP-GAME] Client reconnected during game");
                }

                @Override
                public void onClientDisconnected() {
                    Platform.runLater(() -> {
                        System.out.println("[PVP-GAME] Client disconnected");
                        statusLabel.setText("상대방 연결 끊김");
                        if (gameLoop != null) {
                            gameLoop.stop();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("[PVP-GAME] Server error: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else if (!isServer && gameClient != null) {
            System.out.println("[PVP-GAME] Setting up client message handler");
            gameClient.setMessageHandler(new GameClient.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    if (message instanceof NetworkMessage) {
                        handleNetworkMessage((NetworkMessage) message);
                    }
                }

                @Override
                public void onConnected() {
                    System.out.println("[PVP-GAME] Connected to server");
                }

                @Override
                public void onDisconnected() {
                    Platform.runLater(() -> {
                        System.out.println("[PVP-GAME] Disconnected from server");
                        statusLabel.setText("서버 연결 끊김");
                        if (gameLoop != null) {
                            gameLoop.stop();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("[PVP-GAME] Client error: " + e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onRttUpdate(long rtt) {
                    // RTT 업데이트는 필요 시 사용
                }
            });
        }
        
        System.out.println("[PVP-GAME] Network handlers setup complete");
    }

    private void handleNetworkMessage(NetworkMessage message) {
        Platform.runLater(() -> {
            System.out.println("[PVP-GAME] Handling network message: " + message.getType());
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
                    System.out.println("[PVP-GAME] Received attack: " + lines + " lines, empty col: " + emptyCol);
                    receiveAttack(lines, emptyCol);
                    break;

                case GAME_OVER:
                    @SuppressWarnings("unchecked")
                    Map<String, Object> gameOverData = (Map<String, Object>) message.getData();
                    boolean opponentLost = (Boolean) gameOverData.get("isGameOver");
                    if (opponentLost) {
                        System.out.println("[PVP-GAME] Opponent lost - Victory!");
                        statusLabel.setText("승리!");
                        statusLabel.setStyle("-fx-text-fill: #00ff00;");
                        if (gameLoop != null) {
                            gameLoop.stop();
                        }
                        // 게임 오버 버튼 표시
                        if (gameOverBox != null) {
                            gameOverBox.setVisible(true);
                            gameOverBox.setManaged(true);
                        }
                    }
                    break;
                
                case REMATCH_REQUEST:
                    // 재시합 요청 받음 - 다이얼로그 표시
                    System.out.println("[PVP-GAME] Rematch request received");
                    showRematchDialog();
                    break;
                
                case REMATCH_RESPONSE:
                    // 재시합 응답 받음
                    Boolean accepted = (Boolean) message.getData();
                    if (accepted != null && accepted) {
                        System.out.println("[PVP-GAME] Rematch accepted");
                        restartGame();
                    } else {
                        System.out.println("[PVP-GAME] Rematch declined");
                        setStatusMessage("상대방이 재시합을 거부했습니다", "#ff0000");
                    }
                    break;

                case GAME_START:
                    // 이전 버전 호환성 유지
                    String data = (String) message.getData();
                    if ("REMATCH".equals(data)) {
                        System.out.println("[PVP-GAME] Rematch request received (legacy)");
                        restartGame();
                    }
                    break;

                case DISCONNECT:
                    System.out.println("[PVP-GAME] Opponent disconnected");
                    statusLabel.setText("Opponent Left");
                    if (gameLoop != null) {
                        gameLoop.stop();
                    }
                    break;
                
                case PAUSE:
                    // 상대방이 일시정지를 누른 경우
                    Boolean shouldPause = (Boolean) message.getData();
                    if (shouldPause != null && battleEngine != null) {
                        if (shouldPause && !battleEngine.isPaused()) {
                            battleEngine.pauseGame();
                            statusLabel.setText("일시 정지 (상대방)");
                        } else if (!shouldPause && battleEngine.isPaused()) {
                            battleEngine.pauseGame();
                            statusLabel.setText("");
                        }
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
        if (myCanvas == null) return;
        
        // 공통으로 쓸 핸들러 함수로 분리
        javafx.event.EventHandler<javafx.scene.input.KeyEvent> handler = event -> {
            javafx.scene.input.KeyCode code = event.getCode();

            if (code == javafx.scene.input.KeyCode.ESCAPE) {
                onPause();
                event.consume();
                return;
            }

            if (battleEngine != null & battleEngine.isGameRunning() && !battleEngine.isPaused()) {
                if (isServer) {
                    battleEngine.handlePlayer1KeyPress(code);
                } else {
                    battleEngine.handlePlayer2KeyPress(code);
                }
                event.consume();

                // 내 상태를 상대방에게 전송
                sendMyState();
            }
        };

        // 1) 나중에 Scene 이 붙는 경우 대비
        myCanvas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                System.out.println("[PVP] Scene change detected, registering key handler");
                newScene.setOnKeyPressed(handler);
                // 포커스 보장
                Platform.runLater(() -> newScene.getRoot().requestFocus());
            }
        });

        // 2) 이미 Scene 이 붙어 있는 상태라면 바로 등록
        if (myCanvas.getScene() != null) {
            System.out.println("[PVP] Scene already exists, registering key handler immediately");
            myCanvas.getScene().setOnKeyPressed(handler);
            Platform.runLater(() -> myCanvas.getScene().getRoot().requestFocus());
        }
    }

    private void startCountdown() {
        isCountingDown = true;
        countdownNumber = 3;
        lastCountdownTime = System.nanoTime();
        
        // 카운트다운 표시
        Platform.runLater(() -> {
            setStatusMessage(String.valueOf(countdownNumber), "#ffff00");
        });
        
        // 카운트다운 루프
        AnimationTimer countdownTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastCountdownTime >= COUNTDOWN_INTERVAL) {
                    countdownNumber--;
                    lastCountdownTime = now;
                    
                    if (countdownNumber > 0) {
                        // 숫자 표시
                        Platform.runLater(() -> {
                            setStatusMessage(String.valueOf(countdownNumber), "#ffff00");
                        });
                    } else if (countdownNumber == 0) {
                        // START! 표시
                        Platform.runLater(() -> {
                            setStatusMessage("START!", "#00ff00");
                        });
                    } else {
                        // 카운트다운 종료, 게임 시작
                        this.stop();
                        isCountingDown = false;
                        Platform.runLater(() -> {
                            statusLabel.setText("");
                            statusLabel.setStyle("");
                            startGame();
                        });
                    }
                }
            }
        };
        countdownTimer.start();
    }
    
    private void startGame() {
        battleEngine.startGame();
        
        // 게임 시작 직후 초기 상태 전송
        System.out.println("[PVP-GAME] Sending initial game state...");
        sendMyState();
        
        gameLoop = new AnimationTimer() {
            
            @Override
            public void handle(long now) {
                try {
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
                    if (!isAnimatingClear && now - lastUpdateTimeMe >= fallSpeedMe) {
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
                            java.util.List<Integer> currentLinesCleared = getMyEngine().getFullLines();
                            if (!currentLinesCleared.isEmpty()) {
                                playerLinesToClear = currentLinesCleared;
                                isAnimatingClear = true;
                                clearAnimationStartTime = now;
                            }

                            lastUpdateTimeMe = now;

                            // 상태 업데이트 전송 (빈도 제한 적용)
                            sendMyStateThrottled(now);
                        }
                    }

                    // 애니메이션 처리
                    if (isAnimatingClear) {
                        long elapsed = now - clearAnimationStartTime;
                        if (elapsed >= CLEAR_ANIMATION_DURATION) {
                            int beforeCleared = getMyEngine().getLinesCleared();
                            getMyEngine().clearLinesManually();
                            int afterCleared = getMyEngine().getLinesCleared();
                            int cleared = afterCleared - beforeCleared;

                            // 공격 메커니즘 처리 (줄 삭제 직후 바로 처리)
                            if (cleared >= 2) {
                                int lastBlockCol = getMyEngine().getLastPlacedBlockCol();
                                // 내가 서버면 Player1, 클라이언트면 Player2
                                if (isServer) {
                                    battleEngine.processPlayer1Attack(cleared, lastBlockCol);
                                } else {
                                    battleEngine.processPlayer2Attack(cleared, lastBlockCol);
                                }
                                // 상대방에게 공격 전송
                                sendAttack(cleared, lastBlockCol);
                            }

                            isAnimatingClear = false;
                            playerLinesToClear = null;
                            lastUpdateTimeMe = now;
                        }
                    }

                    // 렌더링
                    renderMyBoard();
                    renderOpponentBoard();
                    renderNextPieces();
                    renderIncomingLines();
                    updateUI();
                    updateFallSpeeds();
                } catch (Exception e) {
                    System.err.println("[PVP-GAME] Error in game loop: " + e.getMessage());
                    e.printStackTrace();
                    // 게임 루프 중지하여 크래시 방지
                    if (gameLoop != null) {
                        gameLoop.stop();
                    }
                }
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
        java.util.List<Integer> incomingEmptyCols = getMyPendingAttackEmptyCols();

        GameStateData stateData = new GameStateData(
            boardData, itemBoardData,
            myEngine.getScore(), myEngine.getLevel(), myEngine.getLinesCleared(),
            !myEngine.isGameRunning(),
            currentShape, currentX, currentY, currentType,
            nextShape, nextType, incomingLines, incomingEmptyCols
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

        // 색약모드에서는 회색 격자 표시
        if (settingsManager != null && settingsManager.isColorBlindModeEnabled()) {
            drawGrid(gc, myCanvas, blockSize);
        }

        GameBoard board = getMyEngine().getGameBoard();
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                int cellValue = board.getCell(row, col);
                if (cellValue > 0) {
                    ItemType itemType = board.getItemAt(row, col);
                    Color color;
                    
                    // 공격 블록은 회색으로 표시
                    if (board.isAttackBlock(row, col)) {
                        color = Color.web("#666666"); // 회색
                    } else if (isAnimatingClear && playerLinesToClear != null && playerLinesToClear.contains(row)) {
                        color = Color.WHITE;
                    } else {
                        color = PIECE_COLORS[cellValue];
                    }
                    
                    renderBlockScaled(gc, col * blockSize, row * blockSize, blockSize, color, cellValue, itemType);
                }
            }
        }

        if (!isAnimatingClear) {
            Piece currentPiece = getMyEngine().getCurrentPiece();
            if (currentPiece != null) {
                renderPieceScaled(gc, currentPiece, blockSize);
            }
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

        // 색약모드에서는 회색 격자 표시
        if (settingsManager != null && settingsManager.isColorBlindModeEnabled()) {
            drawGrid(gc, opponentCanvas, blockSize);
        }

        int[][] boardState = opponentState.getBoard();
        int[][] itemBoard = opponentState.getItemBoard();
        
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                int cellValue = boardState[row][col];
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

    private void renderIncomingLines() {
        if (battleEngine == null) return;

        // 내 공격받을 줄 렌더링
        if (myIncomingCanvas != null) {
            GraphicsContext gc = myIncomingCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, myIncomingCanvas.getWidth(), myIncomingCanvas.getHeight());

            int pendingLines = getMyPendingAttacks();
            if (pendingLines > 0) {
                java.util.List<Integer> emptyCols = getMyPendingAttackEmptyCols();
                renderIncomingLinesBlock(gc, pendingLines, emptyCols);
            }

            // 테두리
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, myIncomingCanvas.getWidth(), myIncomingCanvas.getHeight());
        }

        // 상대방 공격받을 줄 렌더링
        if (opponentIncomingCanvas != null && opponentState != null) {
            GraphicsContext gc = opponentIncomingCanvas.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, opponentIncomingCanvas.getWidth(), opponentIncomingCanvas.getHeight());

            if (opponentIncomingLines > 0) {
                // 상대방의 빈칸 정보를 GameStateData에서 가져오기
                java.util.List<Integer> emptyCols = opponentState.getIncomingAttackEmptyCols();
                if (emptyCols == null || emptyCols.isEmpty()) {
                    // 만약 데이터가 없으면 기본 패턴 사용
                    emptyCols = new java.util.ArrayList<>();
                    for (int i = 0; i < opponentIncomingLines; i++) {
                        emptyCols.add((i * 3) % GameBoard.BOARD_WIDTH);
                    }
                }
                renderIncomingLinesBlock(gc, opponentIncomingLines, emptyCols);
            }

            // 테두리
            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            gc.strokeRect(0, 0, opponentIncomingCanvas.getWidth(), opponentIncomingCanvas.getHeight());
        }
    }

    private void renderIncomingLinesBlock(GraphicsContext gc, int numLines, java.util.List<Integer> emptyCols) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        
        double blockSize = Math.min(canvasWidth / GameBoard.BOARD_WIDTH, canvasHeight / 12);
        
        int displayLines = Math.min(numLines, 10);
        Color attackColor = Color.web("#666666");
        Color gridColor = Color.web("#333333");

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

        // 각 줄을 렌더링
        double circleRadius = blockSize * 0.35;
        for (int line = 0; line < displayLines; line++) {
            double y = line * blockSize + blockSize / 2;
            int emptyCol = (line < emptyCols.size()) ? emptyCols.get(line) : (line * 3) % GameBoard.BOARD_WIDTH;
            
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                double x = col * blockSize + blockSize / 2;
                if (col != emptyCol) {
                    gc.setFill(attackColor);
                    gc.fillOval(x - circleRadius, y - circleRadius, circleRadius * 2, circleRadius * 2);
                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(0.5);
                    gc.strokeOval(x - circleRadius, y - circleRadius, circleRadius * 2, circleRadius * 2);
                }
            }
        }

        if (numLines > 10) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, (int)(blockSize * 0.8)));
            gc.fillText("+" + (numLines - 10), GameBoard.BOARD_WIDTH * blockSize - 20, 10 * blockSize - 2);
        }
    }

    private java.util.List<Integer> getMyPendingAttackEmptyCols() {
        return isServer ? battleEngine.getPendingAttackEmptyColsToPlayer1() : battleEngine.getPendingAttackEmptyColsToPlayer2();
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
            // 승패 결정
            boolean iWon = !getMyEngine().isGameRunning() ? false : true;
            
            if (iWon) {
                statusLabel.setText("승리!");
                statusLabel.setStyle("-fx-text-fill: #00ff00;");
            } else {
                statusLabel.setText("패배...");
                statusLabel.setStyle("-fx-text-fill: #ff0000;");
            }
            
            // 게임 오버 버튼 표시
            if (gameOverBox != null) {
                gameOverBox.setVisible(true);
                gameOverBox.setManaged(true);
            }
            
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
    private void onRematch() {
        System.out.println("[PVP-GAME] Rematch requested");
        // 재시합 요청 메시지 전송
        try {
            NetworkMessage message = new NetworkMessage(NetworkMessage.MessageType.REMATCH_REQUEST, "재시합 요청");
            if (isServer && gameServer != null) {
                gameServer.sendMessage(message);
                setStatusMessage("상대방의 응답을 기다리는 중...", "#ffff00");
            } else if (!isServer && gameClient != null) {
                gameClient.sendMessage(message);
                setStatusMessage("상대방의 응답을 기다리는 중...", "#ffff00");
            }
        } catch (IOException e) {
            System.err.println("재시합 요청 실패: " + e.getMessage());
        }
    }

    @FXML
    private void onToLobby() {
        System.out.println("[PVP-GAME] Returning to lobby");
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (sceneManager != null) {
            sceneManager.showPVPLobby(gameServer, gameClient, isServer);
        }
    }

    /**
     * 메시지 길이에 따라 폰트 크기를 자동 조정하여 표시
     */
    private void setStatusMessage(String message, String color) {
        statusLabel.setText(message);
        
        // 메시지 길이에 따라 폰트 크기 조정
        if (message.length() > 10) {
            // 긴 메시지는 작은 폰트
            statusLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + color + ";");
        } else {
            // 짧은 메시지는 큰 폰트
            statusLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: " + color + ";");
        }
    }

    private void showRematchDialog() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("재시합 요청");
            alert.setHeaderText("상대방이 재시합을 요청했습니다");
            alert.setContentText("다시 하시겠습니까?");
            
            ButtonType yesButton = new ButtonType("예");
            ButtonType noButton = new ButtonType("아니오");
            alert.getButtonTypes().setAll(yesButton, noButton);
            
            alert.showAndWait().ifPresent(response -> {
                boolean accepted = response == yesButton;
                
                // 응답 전송
                try {
                    NetworkMessage message = new NetworkMessage(
                        NetworkMessage.MessageType.REMATCH_RESPONSE, 
                        accepted
                    );
                    if (isServer && gameServer != null) {
                        gameServer.sendMessage(message);
                    } else if (!isServer && gameClient != null) {
                        gameClient.sendMessage(message);
                    }
                } catch (IOException e) {
                    System.err.println("재시합 응답 전송 실패: " + e.getMessage());
                }
                
                // 수락한 경우 게임 재시작
                if (accepted) {
                    restartGame();
                } else {
                    setStatusMessage("재시합을 거부했습니다", "#ff0000");
                }
            });
        });
    }

    private void restartGame() {
        Platform.runLater(() -> {
            // 게임 오버 UI 숨기기
            if (gameOverBox != null) {
                gameOverBox.setVisible(false);
                gameOverBox.setManaged(false);
            }
            statusLabel.setText("");
            statusLabel.setStyle("");
            
            // 게임 루프 정지
            if (gameLoop != null) {
                gameLoop.stop();
            }
            
            // 게임 엔진 재초기화
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
            
            // 카운트다운 시작
            startCountdown();
        });
    }

    @FXML
    private void onPause() {
        if (battleEngine != null) {
            battleEngine.pauseGame();
            boolean isPaused = battleEngine.isPaused();
            if (isPaused) {
                statusLabel.setText("일시 정지");
            } else {
                statusLabel.setText("");
            }
            
            // 일시정지 상태를 상대방에게 전송
            NetworkMessage pauseMsg = new NetworkMessage(NetworkMessage.MessageType.PAUSE, isPaused);
            try {
                if (isServer && gameServer != null) {
                    gameServer.sendMessage(pauseMsg);
                } else if (!isServer && gameClient != null) {
                    gameClient.sendMessage(pauseMsg);
                }
            } catch (Exception e) {
                System.err.println("일시정지 메시지 전송 실패: " + e.getMessage());
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
