package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import tetris.ui.SceneManager;

import java.net.URL;
import java.util.ResourceBundle;

public class ScoreBoardController implements Initializable {

    @FXML
    private ListView<String> scoreListView;

    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadScores();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    private void loadScores() {
        if (scoreListView != null) {
            // 임시 스코어 데이터
            scoreListView.getItems().addAll(
                "1. Player1 - 15000",
                "2. Player2 - 12000",
                "3. Player3 - 10000",
                "4. Player4 - 8000",
                "5. Player5 - 6000"
            );
        }
    }

    @FXML
    private void onClearScores() {
        if (scoreListView != null) {
            scoreListView.getItems().clear();
        }
    }

    @FXML
    private void onBackToMenu() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }
}