package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private Button normalModeButton;

    @FXML
    private Button itemModeButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button scoreBoardButton;

    @FXML
    private Button exitButton;

    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 초기화 로직
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void onStartNormalMode() {
        if (sceneManager != null) {
            SettingsManager.getInstance().setGameMode("NORMAL");
            SettingsManager.getInstance().saveToFile();
            sceneManager.showGameScreen();
        }
    }

    @FXML
    private void onStartItemMode() {
        if (sceneManager != null) {
            SettingsManager.getInstance().setGameMode("ITEM");
            SettingsManager.getInstance().saveToFile();
            sceneManager.showGameScreen();
        }
    }

    @FXML
    private void onSettings() {
        if (sceneManager != null) {
            sceneManager.showSettingsScreen();
        }
    }

    @FXML
    private void onScoreBoard() {
        if (sceneManager != null) {
            sceneManager.showScoreBoard();
        }
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }
}