package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tetris.data.ScoreManager;
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
            boolean isTopTen = saveScore(playerName, finalScore);
            
            if (isTopTen) {
                int rank = ScoreManager.getInstance().getRank(finalScore);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("축하합니다!");
                alert.setHeaderText(null);
                alert.setContentText(String.format("축하합니다! %d위에 랭크되었습니다!\n점수: %d점", rank, finalScore));
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("경고");
            alert.setHeaderText(null);
            alert.setContentText("플레이어 이름을 입력해주세요.");
            alert.showAndWait();
            return;
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

    private boolean saveScore(String playerName, int score) {
        return ScoreManager.getInstance().addScore(playerName, score);
    }
}