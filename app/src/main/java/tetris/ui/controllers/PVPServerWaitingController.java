package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import tetris.network.GameServer;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

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
