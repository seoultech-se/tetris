package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
    private ToggleButton normalModeButton;

    @FXML
    private ToggleButton itemModeButton;

    @FXML
    private ToggleButton easyButton;

    @FXML
    private ToggleButton normalButton;

    @FXML
    private ToggleButton hardButton;

    @FXML
    private ImageView backgroundImageView;

    private SceneManager sceneManager;
    private String currentGameMode = "NORMAL";
    private String currentDifficulty = "Normal"; // 기본 난이도

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ToggleGroup 설정
        if (normalModeButton != null && itemModeButton != null) {
            ToggleGroup modeGroup = new ToggleGroup();
            normalModeButton.setToggleGroup(modeGroup);
            itemModeButton.setToggleGroup(modeGroup);
            normalModeButton.setSelected(true);
            
            // 모드 변경 리스너
            normalModeButton.setOnAction(e -> {
                currentGameMode = "NORMAL";
                showDifficultyButtons(true);
                loadScores();
            });
            
            itemModeButton.setOnAction(e -> {
                currentGameMode = "ITEM";
                showDifficultyButtons(false);
                loadScores();
            });
        }
        
        // 난이도 토글 버튼 설정
        if (easyButton != null && normalButton != null && hardButton != null) {
            ToggleGroup difficultyGroup = new ToggleGroup();
            easyButton.setToggleGroup(difficultyGroup);
            normalButton.setToggleGroup(difficultyGroup);
            hardButton.setToggleGroup(difficultyGroup);
            normalButton.setSelected(true);
            
            // 난이도 변경 리스너
            easyButton.setOnAction(e -> {
                currentDifficulty = "Easy";
                loadScores();
            });
            
            normalButton.setOnAction(e -> {
                currentDifficulty = "Normal";
                loadScores();
            });
            
            hardButton.setOnAction(e -> {
                currentDifficulty = "Hard";
                loadScores();
            });
        }
        
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
    
    private void showDifficultyButtons(boolean show) {
        if (easyButton != null && normalButton != null && hardButton != null) {
            easyButton.setVisible(show);
            normalButton.setVisible(show);
            hardButton.setVisible(show);
            easyButton.setManaged(show);
            normalButton.setManaged(show);
            hardButton.setManaged(show);
        }
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    private void loadScores() {
        if (scoreListView != null) {
            scoreListView.getItems().clear();
            if (currentGameMode.equals("NORMAL")) {
                // 일반 모드: 난이도별로 필터링
                scoreListView.getItems().addAll(
                    ScoreManager.getInstance().getFormattedScoresByDifficulty(currentGameMode, currentDifficulty)
                );
            } else {
                // 아이템 모드: 전체 표시
                scoreListView.getItems().addAll(
                    ScoreManager.getInstance().getFormattedScores(currentGameMode)
                );
            }
        }
    }

    @FXML
    private void onClearScores() {
        String modeText = currentGameMode.equals("ITEM") ? "아이템 모드" : "일반 모드";
        String difficultyText = currentGameMode.equals("NORMAL") 
            ? " [" + currentDifficulty + " 난이도]" 
            : "";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("스코어보드 초기화");
        alert.setHeaderText(modeText + difficultyText + " 점수 기록을 삭제하시겠습니까?");
        alert.setContentText("이 작업은 되돌릴 수 없습니다.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (currentGameMode.equals("NORMAL")) {
                // 일반 모드: 난이도별로 삭제
                ScoreManager.getInstance().clearScoresByDifficulty(currentGameMode, currentDifficulty);
            } else {
                // 아이템 모드: 전체 삭제
                ScoreManager.getInstance().clearScores(currentGameMode);
            }
            loadScores();
            
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("초기화 완료");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText(modeText + difficultyText + " 스코어보드가 초기화되었습니다.");
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