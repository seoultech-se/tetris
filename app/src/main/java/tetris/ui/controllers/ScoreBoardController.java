package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tetris.data.ScoreManager;
import tetris.ui.SceneManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ScoreBoardController implements Initializable {

    @FXML
    private ListView<String> scoreListView;

    @FXML
    private ImageView backgroundImageView;

    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupListViewCellFactory();
        loadScores();
        loadBackgroundImage();
    }
    
    private void setupListViewCellFactory() {
        if (scoreListView != null) {
            scoreListView.setCellFactory(param -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(item);
                        setStyle("-fx-font-size: 18px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
                    }
                }
            });
        }
    }
    
    private void loadBackgroundImage() {
        if (backgroundImageView != null) {
            try {
                URL imageUrl = getClass().getResource("/assets/img/Scoreboard.png");
                if (imageUrl != null) {
                    Image image = new Image(imageUrl.toExternalForm());
                    backgroundImageView.setImage(image);
                }
            } catch (Exception e) {
                System.err.println("배경 이미지 로드 실패: " + e.getMessage());
            }
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    private void loadScores() {
        if (scoreListView != null) {
            scoreListView.getItems().clear();
            scoreListView.getItems().addAll(ScoreManager.getInstance().getFormattedScores());
        }
    }

    @FXML
    private void onClearScores() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("스코어보드 초기화");
        alert.setHeaderText("모든 점수 기록을 삭제하시겠습니까?");
        alert.setContentText("이 작업은 되돌릴 수 없습니다.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ScoreManager.getInstance().clearScores();
            loadScores();
            
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("초기화 완료");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("스코어보드가 초기화되었습니다.");
            infoAlert.showAndWait();
        }
    }

    @FXML
    private void onBackToMenu() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }
}