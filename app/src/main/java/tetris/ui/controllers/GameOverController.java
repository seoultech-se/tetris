package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tetris.data.ScoreManager;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.net.URL;
import java.util.ResourceBundle;

public class GameOverController implements Initializable {

    @FXML
    private Label finalScoreLabel;

    @FXML
    private Label scoreboardTitleLabel;

    @FXML
    private ListView<String> scoreListView;

    @FXML
    private TextField playerNameField;

    @FXML
    private Button saveScoreButton;

    private SceneManager sceneManager;
    private int finalScore;
    private String gameMode;
    private String difficulty;
    private String highlightedPlayerName = null;
    private int highlightedScore = -1;
    private boolean scoreSaved = false; // 점수 저장 여부 추적

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 현재 게임 설정 가져오기
        gameMode = SettingsManager.getInstance().getGameMode();
        difficulty = SettingsManager.getInstance().getDifficulty();
        
        // ListView 스타일 설정
        setupListViewCellFactory();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setFinalScore(int score) {
        this.finalScore = score;
        this.gameMode = SettingsManager.getInstance().getGameMode();
        this.difficulty = SettingsManager.getInstance().getDifficulty();
        
        if (finalScoreLabel != null) {
            finalScoreLabel.setText(String.format("최종 점수: %d점", score));
        }
        
        // 스코어보드 제목 업데이트
        if (scoreboardTitleLabel != null) {
            String modeText = gameMode.equals("ITEM") ? "아이템 모드" : "일반 모드";
            String diffText = gameMode.equals("NORMAL") ? " - " + difficulty : "";
            scoreboardTitleLabel.setText(modeText + diffText + " 스코어보드");
        }
        
        // 상위 10위 안에 드는지 확인
        boolean canSaveScore = checkIfCanSaveScore(score, gameMode, difficulty);
        
        // 상위 10위 안에 들지 못하면 입력 필드와 저장 버튼 숨기기
        if (playerNameField != null) {
            playerNameField.setVisible(canSaveScore);
            playerNameField.setManaged(canSaveScore);
        }
        if (saveScoreButton != null) {
            saveScoreButton.setVisible(canSaveScore);
            saveScoreButton.setManaged(canSaveScore);
        }
        
        // 현재 모드와 난이도에 맞는 스코어 로드
        loadScores();
    }
    
    private boolean checkIfCanSaveScore(int score, String gameMode, String difficulty) {
        java.util.List<String> scores;
        if (gameMode.equals("NORMAL")) {
            scores = ScoreManager.getInstance().getFormattedScoresByDifficulty(gameMode, difficulty);
        } else {
            scores = ScoreManager.getInstance().getFormattedScores(gameMode);
        }
        
        // 10개 미만이면 무조건 저장 가능
        if (scores.isEmpty() || scores.size() < 10) {
            return true;
        }
        
        // 10개 이상이면 마지막 점수와 비교
        String lastScoreEntry = scores.get(scores.size() - 1);
        // "순위. 이름 - 점수점 (난이도)" 형식에서 점수 추출
        try {
            int dashIndex = lastScoreEntry.indexOf(" - ");
            int pointIndex = lastScoreEntry.indexOf("점", dashIndex);
            if (dashIndex > 0 && pointIndex > dashIndex) {
                String scoreStr = lastScoreEntry.substring(dashIndex + 3, pointIndex).trim();
                int lastScore = Integer.parseInt(scoreStr);
                return score >= lastScore; // 마지막 점수 이상이면 저장 가능
            }
        } catch (Exception e) {
            System.err.println("점수 파싱 오류: " + e.getMessage());
        }
        
        return true; // 파싱 실패 시 일단 저장 가능하도록
    }
    
    private void setupListViewCellFactory() {
        if (scoreListView != null) {
            // ListView 배경 스타일 설정
            scoreListView.setStyle("-fx-background-color: #34495e; -fx-border-color: #3498db; -fx-border-width: 2px;");
            
            scoreListView.setCellFactory(param -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle("-fx-background-color: #34495e;");
                    } else {
                        setText(item);
                        
                        // 방금 저장한 점수인지 확인하여 강조 표시
                        boolean isHighlighted = false;
                        if (highlightedPlayerName != null && highlightedScore > 0) {
                            if (item.contains(highlightedPlayerName) && item.contains(String.valueOf(highlightedScore))) {
                                isHighlighted = true;
                            }
                        }
                        
                        if (isHighlighted) {
                            // 강조 표시: 금색 배경과 텍스트
                            setStyle("-fx-font-size: 16px; -fx-text-fill: #f39c12; -fx-font-weight: bold; " +
                                   "-fx-background-color: #1c2833; -fx-padding: 10px; " +
                                   "-fx-border-color: #f39c12; -fx-border-width: 2px;");
                        } else {
                            // 일반 표시
                            setStyle("-fx-font-size: 15px; -fx-text-fill: #ecf0f1; -fx-font-weight: bold; " +
                                   "-fx-background-color: #2c3e50; -fx-padding: 8px;");
                        }
                    }
                }
            });
        }
    }
    
    private void loadScores() {
        if (scoreListView != null) {
            scoreListView.getItems().clear();
            
            java.util.List<String> scores;
            if (gameMode.equals("NORMAL")) {
                // 일반 모드: 난이도별로 필터링
                scores = ScoreManager.getInstance().getFormattedScoresByDifficulty(gameMode, difficulty);
            } else {
                // 아이템 모드: 전체 표시
                scores = ScoreManager.getInstance().getFormattedScores(gameMode);
            }
            
            if (scores.isEmpty()) {
                // 스코어가 없을 때 안내 메시지 표시
                scoreListView.getItems().add("아직 등록된 점수가 없습니다.");
            } else {
                scoreListView.getItems().addAll(scores);
            }
            
            System.out.println("로드된 스코어 개수: " + scores.size());
            System.out.println("게임 모드: " + gameMode + ", 난이도: " + difficulty);
        }
    }

    @FXML
    private void onSaveScore() {
        // 이미 점수를 저장했는지 확인
        if (scoreSaved) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("중복 저장 불가");
            alert.setHeaderText(null);
            alert.setContentText("이미 점수를 저장했습니다.\n중복하여 저장할 수 없습니다.");
            alert.showAndWait();
            return;
        }
        
        String playerName = playerNameField.getText();
        if (playerName != null && !playerName.trim().isEmpty()) {
            boolean isTopTen = saveScore(playerName, finalScore);
            
            // 점수 저장 완료 - 버튼 비활성화
            scoreSaved = true;
            if (saveScoreButton != null) {
                saveScoreButton.setDisable(true);
                saveScoreButton.setText("저장 완료");
            }
            playerNameField.setDisable(true);
            
            if (isTopTen) {
                String gameMode = SettingsManager.getInstance().getGameMode();
                String difficulty = SettingsManager.getInstance().getDifficulty();
                // 난이도별 순위 사용 (NORMAL 모드) 또는 전체 순위 사용 (ITEM 모드)
                int rank = ScoreManager.getInstance().getRankByDifficulty(finalScore, gameMode, difficulty);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("축하합니다!");
                alert.setHeaderText(null);
                alert.setContentText(String.format("축하합니다! %d위에 랭크되었습니다!\n점수: %d점", rank, finalScore));
                alert.showAndWait();
                
                // 점수 저장 후 스코어보드 업데이트하여 강조 표시
                highlightedPlayerName = playerName.trim();
                highlightedScore = finalScore;
                setupListViewCellFactory(); // CellFactory 다시 설정
                loadScores(); // 스코어 다시 로드
            } else {
                // 상위 10위 밖이면 스코어보드 업데이트
                loadScores();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("경고");
            alert.setHeaderText(null);
            alert.setContentText("플레이어 이름을 입력해주세요.");
            alert.showAndWait();
            return;
        }
    }

    @FXML
    private void onBackToMenu() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    @FXML
    private void onExitGame() {
        System.exit(0);
    }

    private boolean saveScore(String playerName, int score) {
        String difficulty = SettingsManager.getInstance().getDifficulty();
        String gameMode = SettingsManager.getInstance().getGameMode();
        return ScoreManager.getInstance().addScore(playerName, score, difficulty, gameMode);
    }
}