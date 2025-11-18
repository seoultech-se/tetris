package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import tetris.network.GameClient;
import tetris.network.GameServer;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PVPLobbyController implements Initializable {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private VBox gameModeBox;

    @FXML
    private RadioButton normalModeRadio;

    @FXML
    private RadioButton itemModeRadio;

    @FXML
    private RadioButton timeLimitModeRadio;

    @FXML
    private ToggleGroup gameModeGroup;

    @FXML
    private Label selectedModeLabel;

    @FXML
    private Label serverStatusLabel;

    @FXML
    private Label clientStatusLabel;

    @FXML
    private Button readyButton;

    @FXML
    private Button backButton;

    private SceneManager sceneManager;
    private GameServer gameServer;
    private GameClient gameClient;
    private boolean isServer;
    private boolean isReady = false;
    private boolean opponentReady = false;
    private String selectedGameMode = "NORMAL";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        // 게임 모드 변경 리스너
        if (gameModeGroup != null) {
            gameModeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == normalModeRadio) {
                    selectedGameMode = "NORMAL";
                } else if (newVal == itemModeRadio) {
                    selectedGameMode = "ITEM";
                } else if (newVal == timeLimitModeRadio) {
                    selectedGameMode = "TIME_LIMIT";
                }
                
                // 서버인 경우 게임 모드 변경을 클라이언트에게 전송
                if (isServer && gameServer != null) {
                    sendGameModeUpdate();
                }
            });
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setNetworkObjects(GameServer server, GameClient client, boolean isServer) {
        this.gameServer = server;
        this.gameClient = client;
        this.isServer = isServer;

        // UI 설정
        if (isServer) {
            // 서버: 게임 모드 선택 가능
            gameModeBox.setVisible(true);
            gameModeBox.setManaged(true);
            selectedModeLabel.setVisible(false);
            selectedModeLabel.setManaged(false);
            
            // 서버 메시지 핸들러 설정
            if (gameServer != null) {
                gameServer.setMessageHandler(new GameServer.MessageHandler() {
                    @Override
                    public void onMessageReceived(Object message) {
                        Platform.runLater(() -> handleServerMessage(message));
                    }

                    @Override
                    public void onClientConnected() {}

                    @Override
                    public void onClientDisconnected() {
                        Platform.runLater(() -> {
                            clientStatusLabel.setText("클라이언트: 연결 끊김");
                            clientStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } else {
            // 클라이언트: 게임 모드 선택 불가능, 서버가 선택한 모드 표시
            gameModeBox.setVisible(false);
            gameModeBox.setManaged(false);
            selectedModeLabel.setVisible(true);
            selectedModeLabel.setManaged(true);
            selectedModeLabel.setText("게임 모드: 일반 모드");
            
            // 클라이언트 메시지 핸들러 설정
            if (gameClient != null) {
                gameClient.setMessageHandler(new GameClient.MessageHandler() {
                    @Override
                    public void onMessageReceived(Object message) {
                        Platform.runLater(() -> handleClientMessage(message));
                    }

                    @Override
                    public void onConnected() {}

                    @Override
                    public void onDisconnected() {
                        Platform.runLater(() -> {
                            serverStatusLabel.setText("서버: 연결 끊김");
                            serverStatusLabel.setStyle("-fx-text-fill: #ff0000;");
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        
        updateStatusLabels();
    }

    @FXML
    private void onReady() {
        isReady = !isReady;
        
        if (isReady) {
            readyButton.setText("준비 취소");
        } else {
            readyButton.setText("게임 시작");
        }
        
        updateStatusLabels();
        sendReadyStatus();
        
        // 양쪽 모두 준비되면 게임 시작
        if (isReady && opponentReady) {
            startGame();
        }
    }

    private void sendReadyStatus() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("ready", isReady);
            
            NetworkMessage message = new NetworkMessage(
                NetworkMessage.MessageType.PLAYER_ACTION,
                data
            );
            
            if (isServer && gameServer != null) {
                gameServer.sendMessage(message);
            } else if (!isServer && gameClient != null) {
                gameClient.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendGameModeUpdate() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("gameMode", selectedGameMode);
            
            NetworkMessage message = new NetworkMessage(
                NetworkMessage.MessageType.GAME_START,
                data
            );
            
            if (gameServer != null) {
                gameServer.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleServerMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            
            if (netMsg.getType() == NetworkMessage.MessageType.PLAYER_ACTION) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) netMsg.getData();
                if (data.containsKey("ready")) {
                    opponentReady = (Boolean) data.get("ready");
                    updateStatusLabels();
                    
                    if (isReady && opponentReady) {
                        startGame();
                    }
                }
            }
        }
    }

    private void handleClientMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            
            if (netMsg.getType() == NetworkMessage.MessageType.PLAYER_ACTION) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) netMsg.getData();
                if (data.containsKey("ready")) {
                    opponentReady = (Boolean) data.get("ready");
                    updateStatusLabels();
                    
                    if (isReady && opponentReady) {
                        startGame();
                    }
                }
            } else if (netMsg.getType() == NetworkMessage.MessageType.GAME_START) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) netMsg.getData();
                if (data.containsKey("gameMode")) {
                    selectedGameMode = (String) data.get("gameMode");
                    updateGameModeLabel();
                }
            }
        }
    }

    private void updateStatusLabels() {
        if (isServer) {
            serverStatusLabel.setText("서버: " + (isReady ? "준비 완료" : "대기 중"));
            serverStatusLabel.setStyle(isReady ? "-fx-text-fill: #00ff00;" : "-fx-text-fill: #ffffff;");
            
            clientStatusLabel.setText("클라이언트: " + (opponentReady ? "준비 완료" : "대기 중"));
            clientStatusLabel.setStyle(opponentReady ? "-fx-text-fill: #00ff00;" : "-fx-text-fill: #ffffff;");
        } else {
            serverStatusLabel.setText("서버: " + (opponentReady ? "준비 완료" : "대기 중"));
            serverStatusLabel.setStyle(opponentReady ? "-fx-text-fill: #00ff00;" : "-fx-text-fill: #ffffff;");
            
            clientStatusLabel.setText("클라이언트: " + (isReady ? "준비 완료" : "대기 중"));
            clientStatusLabel.setStyle(isReady ? "-fx-text-fill: #00ff00;" : "-fx-text-fill: #ffffff;");
        }
    }

    private void updateGameModeLabel() {
        String modeText = "";
        switch (selectedGameMode) {
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
        selectedModeLabel.setText("게임 모드: " + modeText);
    }

    private void startGame() {
        if (sceneManager != null) {
            sceneManager.showPVPGameScreen(selectedGameMode, gameServer, gameClient, isServer);
        }
    }

    @FXML
    private void onBack() {
        if (gameServer != null) {
            gameServer.close();
        }
        if (gameClient != null) {
            gameClient.close();
        }
        if (sceneManager != null) {
            sceneManager.showPVPModeSelection();
        }
    }
}
