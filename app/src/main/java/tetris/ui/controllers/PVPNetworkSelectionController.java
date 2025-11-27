package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import tetris.network.GameClient;
import tetris.network.GameServer;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PVPNetworkSelectionController implements Initializable {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Label gameModeLabel;

    @FXML
    private VBox modeSelectionBox;

    @FXML
    private Button serverButton;

    @FXML
    private Button clientButton;

    @FXML
    private VBox serverBox;

    @FXML
    private Label serverIpLabel;

    @FXML
    private Label connectionStatusLabel;

    @FXML
    private VBox clientBox;

    @FXML
    private TextField serverIpField;

    @FXML
    private Button connectButton;

    @FXML
    private Label clientStatusLabel;

    @FXML
    private Button backButton;

    private SceneManager sceneManager;
    private String gameMode;
    private GameServer gameServer;
    private GameClient gameClient;
    private boolean isServer = false;
    private static final int SERVER_PORT = 7777;

    // 게임 컨트롤러 참조 (메시지 전달용)
    private static PVPGameScreenController gameScreenController;

    public static void setGameScreenController(PVPGameScreenController controller) {
        System.out.println("[UI] Game screen controller registered");
        gameScreenController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 배경 이미지 크기 설정
        if (backgroundImage != null) {
            String screenSize = SettingsManager.getInstance().getScreenSize();
            switch (screenSize) {
                case "작게":
                    backgroundImage.setFitWidth(480);
                    backgroundImage.setFitHeight(720);
                    break;
                case "중간":
                    backgroundImage.setFitWidth(600);
                    backgroundImage.setFitHeight(900);
                    break;
                case "크게":
                    backgroundImage.setFitWidth(720);
                    backgroundImage.setFitHeight(1080);
                    break;
                default:
                    backgroundImage.setFitWidth(600);
                    backgroundImage.setFitHeight(900);
                    break;
            }
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
        if (gameModeLabel != null) {
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
            gameModeLabel.setText("PVP 대전 - " + modeText);
        }
    }

    @FXML
    private void onServerMode() {
        System.out.println("[UI] Server mode selected");

        // 이미 서버가 실행 중이면 정리
        if (gameServer != null) {
            System.out.println("[UI] Cleaning up existing server...");
            gameServer.close();
            gameServer = null;
            try {
                Thread.sleep(500); // 포트가 완전히 해제될 때까지 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        isServer = true;
        modeSelectionBox.setVisible(false);
        modeSelectionBox.setManaged(false);
        serverBox.setVisible(true);
        serverBox.setManaged(true);

        try {
            System.out.println("[UI] Creating server on port " + SERVER_PORT);
            gameServer = new GameServer(SERVER_PORT);
            String serverIP = gameServer.getServerIP();
            System.out.println("[UI] Server IP: " + serverIP);
            serverIpLabel.setText(serverIP);

            gameServer.setMessageHandler(new GameServer.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    System.out.println("[UI-SERVER] Message received");
                    // 게임 컨트롤러가 설정되어 있으면 그쪽으로 전달
                    if (gameScreenController != null && message instanceof NetworkMessage) {
                        System.out.println("[UI-SERVER] Forwarding to game controller");
                        gameScreenController.receiveNetworkMessage((NetworkMessage) message);
                    } else {
                        System.out.println("[UI-SERVER] Game controller not ready, handling locally");
                        Platform.runLater(() -> handleServerMessage(message));
                    }
                }

                @Override
                public void onClientConnected() {
                    System.out.println("[UI-SERVER] Client connected callback triggered");
                    Platform.runLater(() -> {
                        System.out.println("[UI-SERVER] Platform.runLater executed");
                        connectionStatusLabel.setText("Client Connected!");
                        connectionStatusLabel.setStyle("-fx-text-fill: #00ff00;");

                        // 연결 확인 메시지 전송
                        try {
                            System.out.println("[UI-SERVER] Sending CONNECTION_ACCEPTED message");
                            gameServer.sendMessage(new NetworkMessage(
                                NetworkMessage.MessageType.CONNECTION_ACCEPTED,
                                gameMode
                            ));
                            System.out.println("[UI-SERVER] CONNECTION_ACCEPTED sent successfully");
                        } catch (IOException e) {
                            System.err.println("[UI-SERVER] Failed to send CONNECTION_ACCEPTED: " + e.getMessage());
                            e.printStackTrace();
                        }

                        // 즉시 게임 시작 (별도 스레드 없이)
                        System.out.println("[UI-SERVER] Starting game immediately");
                        startGame();
                    });
                }

                @Override
                public void onClientDisconnected() {
                    System.out.println("[UI-SERVER] Client disconnected");
                    Platform.runLater(() -> {
                        connectionStatusLabel.setText("Client Disconnected");
                        connectionStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    });
                }

                @Override
                public void onError(Exception e) {
                    System.err.println("[UI-SERVER] Error: " + e.getMessage());
                    Platform.runLater(() -> {
                        connectionStatusLabel.setText("Error: " + e.getMessage());
                        connectionStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    });
                }
                
                @Override
                public void onRttUpdate(long rtt) {
                    // RTT 업데이트는 게임 화면에서 처리
                }
            });

            System.out.println("[UI] Starting server...");
            gameServer.start();

        } catch (IOException e) {
            System.err.println("[UI] Server creation failed: " + e.getMessage());
            connectionStatusLabel.setText("Server Start Failed: " + e.getMessage());
            connectionStatusLabel.setStyle("-fx-text-fill: #ff0000;");
            e.printStackTrace();
        }
    }

    @FXML
    private void onClientMode() {
        isServer = false;
        modeSelectionBox.setVisible(false);
        modeSelectionBox.setManaged(false);
        clientBox.setVisible(true);
        clientBox.setManaged(true);
    }

    @FXML
    private void onConnect() {
        String serverIP = serverIpField.getText().trim();
        System.out.println("[UI] Connect button clicked, IP: " + serverIP);
        if (serverIP.isEmpty()) {
            System.out.println("[UI] Empty IP address");
            clientStatusLabel.setText("Please enter IP address");
            clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
            return;
        }

        connectButton.setDisable(true);
        clientStatusLabel.setText("Connecting...");
        clientStatusLabel.setStyle("-fx-text-fill: #ffff00;");

        System.out.println("[UI] Creating GameClient...");
        gameClient = new GameClient();
        gameClient.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                System.out.println("[UI-CLIENT] Message received");
                // 게임 컨트롤러가 설정되어 있으면 그쪽으로 전달
                if (gameScreenController != null && message instanceof NetworkMessage) {
                    System.out.println("[UI-CLIENT] Forwarding to game controller");
                    gameScreenController.receiveNetworkMessage((NetworkMessage) message);
                } else {
                    System.out.println("[UI-CLIENT] Game controller not ready, handling locally");
                    Platform.runLater(() -> handleClientMessage(message));
                }
            }

            @Override
            public void onConnected() {
                System.out.println("[UI-CLIENT] Connected callback triggered");
                Platform.runLater(() -> {
                    clientStatusLabel.setText("Connected to Server!");
                    clientStatusLabel.setStyle("-fx-text-fill: #00ff00;");

                    // 연결 요청 메시지 전송
                    try {
                        System.out.println("[UI-CLIENT] Sending CONNECTION_REQUEST message");
                        gameClient.sendMessage(new NetworkMessage(
                            NetworkMessage.MessageType.CONNECTION_REQUEST,
                            "Player"
                        ));
                    } catch (IOException e) {
                        System.err.println("[UI-CLIENT] Failed to send CONNECTION_REQUEST: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onDisconnected() {
                System.out.println("[UI-CLIENT] Disconnected from server");
                Platform.runLater(() -> {
                    clientStatusLabel.setText("Server Disconnected");
                    clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }

            @Override
            public void onError(Exception e) {
                System.err.println("[UI-CLIENT] Connection error: " + e.getMessage());
                Platform.runLater(() -> {
                    clientStatusLabel.setText("Connection Failed: " + e.getMessage());
                    clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }

            @Override
            public void onRttUpdate(long rtt) {
                // Not used in this screen
            }
        });

        new Thread(() -> {
            try {
                System.out.println("[UI] Starting connection thread to " + serverIP + ":" + SERVER_PORT);
                gameClient.connect(serverIP, SERVER_PORT);
            } catch (IOException e) {
                System.err.println("[UI] Connection failed: " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> {
                    clientStatusLabel.setText("Connection Failed: " + e.getMessage());
                    clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }
        }).start();
    }

    private void handleServerMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            System.out.println("[UI-SERVER] Handling message: " + netMsg.getType());
            switch (netMsg.getType()) {
                case CONNECTION_REQUEST:
                    System.out.println("[UI-SERVER] CONNECTION_REQUEST received from client");
                    break;
                default:
                    // 다른 메시지는 게임 화면에서 처리
                    break;
            }
        }
    }

    private void handleClientMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            System.out.println("[UI-CLIENT] Handling message: " + netMsg.getType());
            switch (netMsg.getType()) {
                case CONNECTION_ACCEPTED:
                    // 서버가 보낸 게임 모드를 사용
                    String serverGameMode = (String) netMsg.getData();
                    System.out.println("[UI-CLIENT] CONNECTION_ACCEPTED received");
                    System.out.println("[UI-CLIENT] Server game mode: " + serverGameMode);
                    System.out.println("[UI-CLIENT] Updating local game mode from " + gameMode + " to " + serverGameMode);
                    this.gameMode = serverGameMode;
                    clientStatusLabel.setText("Starting Game...");
                    startGame();
                    break;
                default:
                    // 다른 메시지는 게임 화면에서 처리
                    break;
            }
        }
    }

    private void startGame() {
        System.out.println("[UI] Starting game - isServer: " + isServer + ", gameMode: " + gameMode);
        if (sceneManager != null) {
            // 네트워크 객체를 게임 화면으로 전달
            if (isServer) {
                System.out.println("[UI] Transitioning to PVP game screen (SERVER)");
                sceneManager.showPVPGameScreen(gameMode, gameServer, null, true);
            } else {
                System.out.println("[UI] Transitioning to PVP game screen (CLIENT)");
                sceneManager.showPVPGameScreen(gameMode, null, gameClient, false);
            }
        } else {
            System.err.println("[UI] ERROR: SceneManager is null!");
        }
    }

    @FXML
    private void onBack() {
        cleanup();
        if (sceneManager != null) {
            sceneManager.showPVPModeSelection();
        }
    }

    private void cleanup() {
        System.out.println("[UI] Cleaning up network objects...");
        if (gameServer != null) {
            System.out.println("[UI] Closing game server...");
            gameServer.close();
            gameServer = null;
        }
        if (gameClient != null) {
            System.out.println("[UI] Closing game client...");
            gameClient.close();
            gameClient = null;
        }
        // 게임 컨트롤러 참조 제거
        gameScreenController = null;
        System.out.println("[UI] Cleanup complete");
    }
}
