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
        isServer = true;
        modeSelectionBox.setVisible(false);
        modeSelectionBox.setManaged(false);
        serverBox.setVisible(true);
        serverBox.setManaged(true);

        try {
            gameServer = new GameServer(SERVER_PORT);
            String serverIP = gameServer.getServerIP();
            serverIpLabel.setText(serverIP);
            
            gameServer.setMessageHandler(new GameServer.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    Platform.runLater(() -> handleServerMessage(message));
                }

                @Override
                public void onClientConnected() {
                    Platform.runLater(() -> {
                        connectionStatusLabel.setText("클라이언트 연결됨!");
                        connectionStatusLabel.setStyle("-fx-text-fill: #00ff00;");
                        
                        // 연결 확인 메시지 전송
                        try {
                            gameServer.sendMessage(new NetworkMessage(
                                NetworkMessage.MessageType.CONNECTION_ACCEPTED, 
                                gameMode
                            ));
                            
                            // 잠시 후 게임 시작
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                    Platform.runLater(() -> startGame());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onClientDisconnected() {
                    Platform.runLater(() -> {
                        connectionStatusLabel.setText("클라이언트 연결 끊김");
                        connectionStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    });
                }

                @Override
                public void onError(Exception e) {
                    Platform.runLater(() -> {
                        connectionStatusLabel.setText("오류: " + e.getMessage());
                        connectionStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    });
                }
            });
            
            gameServer.start();
            
        } catch (IOException e) {
            connectionStatusLabel.setText("서버 시작 실패: " + e.getMessage());
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
        if (serverIP.isEmpty()) {
            clientStatusLabel.setText("IP 주소를 입력하세요");
            clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
            return;
        }

        connectButton.setDisable(true);
        clientStatusLabel.setText("연결 중...");
        clientStatusLabel.setStyle("-fx-text-fill: #ffff00;");

        gameClient = new GameClient();
        gameClient.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                Platform.runLater(() -> handleClientMessage(message));
            }

            @Override
            public void onConnected() {
                Platform.runLater(() -> {
                    clientStatusLabel.setText("서버에 연결됨!");
                    clientStatusLabel.setStyle("-fx-text-fill: #00ff00;");
                    
                    // 연결 요청 메시지 전송
                    try {
                        gameClient.sendMessage(new NetworkMessage(
                            NetworkMessage.MessageType.CONNECTION_REQUEST, 
                            "Player"
                        ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onDisconnected() {
                Platform.runLater(() -> {
                    clientStatusLabel.setText("서버 연결 끊김");
                    clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }

            @Override
            public void onError(Exception e) {
                Platform.runLater(() -> {
                    clientStatusLabel.setText("연결 실패: " + e.getMessage());
                    clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }
        });

        new Thread(() -> {
            try {
                gameClient.connect(serverIP, SERVER_PORT);
            } catch (IOException e) {
                Platform.runLater(() -> {
                    clientStatusLabel.setText("연결 실패: " + e.getMessage());
                    clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }
        }).start();
    }

    private void handleServerMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            switch (netMsg.getType()) {
                case CONNECTION_REQUEST:
                    System.out.println("클라이언트 연결 요청 수신");
                    break;
                // 추가 메시지 처리
            }
        }
    }

    private void handleClientMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            switch (netMsg.getType()) {
                case CONNECTION_ACCEPTED:
                    System.out.println("서버 연결 승인 수신");
                    clientStatusLabel.setText("게임 시작 준비 중...");
                    startGame();
                    break;
                // 추가 메시지 처리
            }
        }
    }

    private void startGame() {
        if (sceneManager != null) {
            // 네트워크 객체를 게임 화면으로 전달
            if (isServer) {
                sceneManager.showPVPGameScreen(gameMode, gameServer, null, true);
            } else {
                sceneManager.showPVPGameScreen(gameMode, null, gameClient, false);
            }
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
        if (gameServer != null) {
            gameServer.close();
            gameServer = null;
        }
        if (gameClient != null) {
            gameClient.close();
            gameClient = null;
        }
    }
}
