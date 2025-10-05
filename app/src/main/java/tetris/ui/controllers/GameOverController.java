package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tetris.ui.SceneManager;

import java.net.URL;
import java.util.ResourceBundle;

public class GameOverController implements Initializable {

    @FXML
    private Label finalScoreLabel;

    @FXML
    private TextField playerNameField;

    private SceneManager sceneManager;
    private int finalScore;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 초기화 로직
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setFinalScore(int score) {
        this.finalScore = score;
        if (finalScoreLabel != null) {
            finalScoreLabel.setText("Final Score: " + score);
        }
    }

    @FXML
    private void onSaveScore() {
        String playerName = playerNameField.getText();
        if (playerName != null && !playerName.trim().isEmpty()) {
            // 스코어 저장 로직
            saveScore(playerName, finalScore);
        }
        onBackToMenu();
    }

    @FXML
    private void onPlayAgain() {
        if (sceneManager != null) {
            sceneManager.showGameScreen();
        }
    }

    @FXML
    private void onBackToMenu() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    private void saveScore(String playerName, int score) {
        // 실제 스코어 저장 구현
        System.out.println("Score saved: " + playerName + " - " + score);
    }
}