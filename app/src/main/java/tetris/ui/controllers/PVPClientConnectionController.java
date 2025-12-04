package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tetris.data.RecentIPManager;
import tetris.network.GameClient;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PVPClientConnectionController implements Initializable {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private TextField serverIpField;

    @FXML
    private VBox recentIPsBox;

    @FXML
    private Button connectButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button backButton;

    private SceneManager sceneManager;
    private GameClient gameClient;
    private static final int SERVER_PORT = 7777;

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
        
        // 최근 접속 IP 표시
        loadRecentIPs();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    /**
     * 최근 접속 IP 목록 불러오기 및 표시
     */
    private void loadRecentIPs() {
        if (recentIPsBox == null) {
            return;
        }
        
        // 기존 버튼 제거 (제목 레이블은 유지)
        if (recentIPsBox.getChildren().size() > 1) {
            recentIPsBox.getChildren().remove(1, recentIPsBox.getChildren().size());
        }
        
        List<String> recentIPs = RecentIPManager.getInstance().getRecentIPs();
        
        if (recentIPs.isEmpty()) {
            // 최근 IP가 없으면 제목도 숨김
            if (!recentIPsBox.getChildren().isEmpty()) {
                recentIPsBox.getChildren().get(0).setVisible(false);
                recentIPsBox.getChildren().get(0).setManaged(false);
            }
            return;
        }
        
        // 제목 보이기
        if (!recentIPsBox.getChildren().isEmpty()) {
            recentIPsBox.getChildren().get(0).setVisible(true);
            recentIPsBox.getChildren().get(0).setManaged(true);
        }
        
        // 최근 IP 버튼 추가
        for (String ip : recentIPs) {
            HBox ipBox = new HBox(10);
            ipBox.setAlignment(Pos.CENTER);
            
            Button ipButton = new Button(ip);
            ipButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-padding: 5 15 5 15; -fx-background-radius: 5;");
            ipButton.setOnMouseEntered(e -> ipButton.setStyle("-fx-background-color: #666666; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-padding: 5 15 5 15; -fx-background-radius: 5;"));
            ipButton.setOnMouseExited(e -> ipButton.setStyle("-fx-background-color: #444444; -fx-text-fill: #ffffff; -fx-font-size: 12px; -fx-padding: 5 15 5 15; -fx-background-radius: 5;"));
            ipButton.setOnAction(e -> {
                serverIpField.setText(ip);
            });
            
            ipBox.getChildren().add(ipButton);
            recentIPsBox.getChildren().add(ipBox);
        }
    }

    @FXML
    private void onConnect() {
        String serverIP = serverIpField.getText().trim();
        if (serverIP.isEmpty()) {
            statusLabel.setText("IP 주소를 입력하세요");
            statusLabel.setStyle("-fx-text-fill: #ff0000;");
            return;
        }

        connectButton.setDisable(true);
        statusLabel.setText("연결 중...");
        statusLabel.setStyle("-fx-text-fill: #ffff00;");

        gameClient = new GameClient();
        gameClient.setMessageHandler(new GameClient.MessageHandler() {
            @Override
            public void onMessageReceived(Object message) {
                Platform.runLater(() -> handleMessage(message));
            }

            @Override
            public void onConnected() {
                Platform.runLater(() -> {
                    statusLabel.setText("서버에 연결됨!");
                    statusLabel.setStyle("-fx-text-fill: #00ff00;");
                    
                    try {
                        gameClient.sendMessage(new NetworkMessage(
                            NetworkMessage.MessageType.CONNECTION_REQUEST, 
                            "클라이언트"
                        ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void onDisconnected() {
                Platform.runLater(() -> {
                    statusLabel.setText("서버 연결 끊김");
                    statusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }

            @Override
            public void onError(Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("연결 실패: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #ff0000;");
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
                gameClient.connect(serverIP, SERVER_PORT);
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("연결 실패: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #ff0000;");
                    connectButton.setDisable(false);
                });
            }
        }).start();
    }

    private void handleMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            if (netMsg.getType() == NetworkMessage.MessageType.CONNECTION_ACCEPTED) {
                System.out.println("[PVP-CLIENT] CONNECTION_ACCEPTED received");
                statusLabel.setText("연결되었습니다! 로비로 이동 중...");
                statusLabel.setStyle("-fx-text-fill: #00ff00;");
                
                // 접속 성공한 IP 저장
                String connectedIP = serverIpField.getText().trim();
                RecentIPManager.getInstance().addRecentIP(connectedIP);
                System.out.println("[PVP-CLIENT] Saved recent IP: " + connectedIP);
                
                // 로비로 전환
                if (sceneManager != null) {
                    System.out.println("[PVP-CLIENT] Transitioning to lobby");
                    sceneManager.showPVPLobby(null, gameClient, false);
                } else {
                    System.err.println("[PVP-CLIENT] SceneManager is null, cannot transition to lobby");
                }
            }
        }
    }

    @FXML
    private void onBack() {
        if (gameClient != null) {
            gameClient.close();
        }
        if (sceneManager != null) {
            sceneManager.showPVPModeSelection();
        }
    }
}
