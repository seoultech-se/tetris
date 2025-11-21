package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import tetris.network.GameServer;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PVPServerWaitingController implements Initializable {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Label serverIpLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    private SceneManager sceneManager;
    private GameServer gameServer;

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
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setServerInfo(GameServer gameServer, String serverIP) {
        this.gameServer = gameServer;
        if (serverIpLabel != null) {
            serverIpLabel.setText(serverIP);
        }
        
        // 메시지 핸들러 설정
        if (gameServer != null) {
            gameServer.setMessageHandler(new GameServer.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    Platform.runLater(() -> handleMessage(message));
                }

                @Override
                public void onClientConnected() {
                    Platform.runLater(() -> {
                        if (statusLabel != null) {
                            statusLabel.setText("클라이언트가 연결되었습니다!");
                            statusLabel.setStyle("-fx-text-fill: #00ff00;");
                        }
                        
                        // CONNECTION_ACCEPTED 메시지 전송
                        try {
                            gameServer.sendMessage(new NetworkMessage(
                                NetworkMessage.MessageType.CONNECTION_ACCEPTED,
                                "연결 수락"
                            ));
                            System.out.println("[PVP-SERVER-WAIT] CONNECTION_ACCEPTED sent");
                            
                            // 로비 화면으로 이동
                            if (statusLabel != null) {
                                statusLabel.setText("로비로 이동 중...");
                            }
                            
                            // 짧은 대기 후 전환 (사용자가 메시지를 볼 수 있도록)
                            new Thread(() -> {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                                Platform.runLater(() -> {
                                    if (sceneManager != null) {
                                        System.out.println("[PVP-SERVER-WAIT] Transitioning to lobby");
                                        sceneManager.showPVPLobby(gameServer, null, true);
                                    }
                                });
                            }).start();
                            
                        } catch (IOException e) {
                            System.err.println("[PVP-SERVER-WAIT] Failed to send CONNECTION_ACCEPTED: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onClientDisconnected() {
                    Platform.runLater(() -> {
                        if (statusLabel != null) {
                            statusLabel.setText("클라이언트 연결 끊김");
                            statusLabel.setStyle("-fx-text-fill: #ff0000;");
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    Platform.runLater(() -> {
                        if (statusLabel != null) {
                            statusLabel.setText("오류: " + e.getMessage());
                            statusLabel.setStyle("-fx-text-fill: #ff0000;");
                        }
                    });
                }
            });
        }
    }
    
    private void handleMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            System.out.println("[PVP-SERVER-WAIT] Received message: " + netMsg.getType());
        }
    }

    @FXML
    private void onBack() {
        if (gameServer != null) {
            gameServer.close();
        }
        if (sceneManager != null) {
            sceneManager.showPVPModeSelection();
        }
    }
}
