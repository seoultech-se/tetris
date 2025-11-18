package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import tetris.network.GameClient;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PVPClientConnectionController implements Initializable {

    @FXML
    private ImageView backgroundImage;

    @FXML
    private TextField serverIpField;

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
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
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
                statusLabel.setText("로비로 이동 중...");
                if (sceneManager != null) {
                    sceneManager.showPVPLobby(null, gameClient, false);
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
