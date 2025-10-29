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

    // 강조 상태 (문자열 비교 → 인덱스 비교로 변경)
    private Integer highlightedIndex = null;
    private boolean scoreSaved = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 현재 게임 설정 가져오기
        gameMode = SettingsManager.getInstance().getGameMode();
        difficulty = SettingsManager.getInstance().getDifficulty();

        // ListView 스타일 + CellFactory (초기 1회만 설정)
        setupListViewCellFactory();

        // 초기 스코어 로드
        loadScores();
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

        // 상위 10위 저장 가능 여부 확인
        boolean canSaveScore = checkIfCanSaveScore(score, gameMode, difficulty);

        // 저장 가능 여부에 따라 입력 UI 표시/숨김
        if (playerNameField != null) {
            playerNameField.setVisible(canSaveScore);
            playerNameField.setManaged(canSaveScore);
            playerNameField.setDisable(!canSaveScore);
            if (canSaveScore) playerNameField.clear();
        }
        if (saveScoreButton != null) {
            saveScoreButton.setVisible(canSaveScore);
            saveScoreButton.setManaged(canSaveScore);
            saveScoreButton.setDisable(!canSaveScore);
            if (canSaveScore) saveScoreButton.setText("점수 저장");
        }

        // 새로운 결과 진입 시 강조 리셋
        highlightedIndex = null;
        scoreSaved = false;

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

        // 10개 미만이면 저장 가능
        if (scores.isEmpty() || scores.size() < 10) {
            return true;
        }

        // 10개 이상이면 마지막 점수와 비교
        String lastScoreEntry = scores.get(scores.size() - 1);
        try {
            int dashIndex = lastScoreEntry.indexOf(" - ");
            int pointIndex = lastScoreEntry.indexOf("점", dashIndex);
            if (dashIndex > 0 && pointIndex > dashIndex) {
                String scoreStr = lastScoreEntry.substring(dashIndex + 3, pointIndex).trim();
                int lastScore = Integer.parseInt(scoreStr);
                return score >= lastScore;
            }
        } catch (Exception e) {
            System.err.println("점수 파싱 오류: " + e.getMessage());
        }

        return true; // 파싱 실패 시 일단 저장 가능하도록
    }

    private void setupListViewCellFactory() {
        if (scoreListView == null) return;

        scoreListView.setStyle("-fx-background-color: #34495e; -fx-border-color: #3498db; -fx-border-width: 2px;");

        scoreListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: #34495e;");
                    return;
                }

                setText(item);

                // 문자열 포함 비교 대신, 셀 인덱스 기반 강조
                boolean isHighlighted = highlightedIndex != null && getIndex() == highlightedIndex;

                if (isHighlighted) {
                    setStyle("-fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-font-weight: bold; " +
                            "-fx-background-color: #34495e; -fx-padding: 10px; " +
                            "-fx-border-color: #ffffff; -fx-border-width: 3px;");
                } else {
                    setStyle("-fx-font-size: 15px; -fx-text-fill: #ecf0f1; -fx-font-weight: bold; " +
                            "-fx-background-color: #2c3e50; -fx-padding: 8px;");
                }
            }
        });
    }

    private void loadScores() {
        if (scoreListView == null) return;

        scoreListView.getItems().clear();

        // gameMode / difficulty 미설정 방어
        if (gameMode == null) gameMode = SettingsManager.getInstance().getGameMode();
        if (difficulty == null) difficulty = SettingsManager.getInstance().getDifficulty();

        java.util.List<String> scores;
        if (gameMode.equals("NORMAL")) {
            scores = ScoreManager.getInstance().getFormattedScoresByDifficulty(gameMode, difficulty);
        } else {
            scores = ScoreManager.getInstance().getFormattedScores(gameMode);
        }

        if (scores.isEmpty()) {
            scoreListView.getItems().add("아직 등록된 점수가 없습니다.");
        } else {
            scoreListView.getItems().addAll(scores);
        }

        // 강조가 정해져 있으면 스크롤 위치도 맞춰줌
        if (highlightedIndex != null && highlightedIndex >= 0 && highlightedIndex < scoreListView.getItems().size()) {
            scoreListView.scrollTo(highlightedIndex);
        }

        System.out.println("로드된 스코어 개수: " + scores.size());
        System.out.println("게임 모드: " + gameMode + ", 난이도: " + difficulty);
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
        if (playerName == null || playerName.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("경고");
            alert.setHeaderText(null);
            alert.setContentText("플레이어 이름을 입력해주세요.");
            alert.showAndWait();
            return;
        }

        boolean isTopTen = saveScore(playerName.trim(), finalScore);

        // 저장 후 UI 잠금
        scoreSaved = true;
        if (saveScoreButton != null) {
            saveScoreButton.setDisable(true);
            saveScoreButton.setText("저장 완료");
        }
        playerNameField.setDisable(true);

        // ★ 강조 인덱스 계산 (1-base rank → 0-base index)
        try {
            String gm = SettingsManager.getInstance().getGameMode();
            String diff = SettingsManager.getInstance().getDifficulty();
            int rank = ScoreManager.getInstance().getRankByDifficulty(finalScore, gm, diff);
            highlightedIndex = Math.max(0, rank - 1);
        } catch (Exception e) {
            // 혹시 랭크 계산 실패 시 강조 해제
            highlightedIndex = null;
            System.err.println("랭크 계산 실패: " + e.getMessage());
        }

        // 리스트 새로고침 + 스크롤
        loadScores();
        if (scoreListView != null) {
            scoreListView.refresh();
            if (highlightedIndex != null) {
                scoreListView.scrollTo(highlightedIndex);
                javafx.application.Platform.runLater(() -> {
                    scoreListView.refresh();
                    scoreListView.scrollTo(highlightedIndex);
                });
            }
        }

        if (isTopTen && highlightedIndex != null) {
            String gm = SettingsManager.getInstance().getGameMode();
            String diff = SettingsManager.getInstance().getDifficulty();
            int rank = highlightedIndex + 1;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("축하합니다!");
            alert.setHeaderText(null);
            alert.setContentText(String.format("축하합니다! %d위에 랭크되었습니다!\n점수: %d점", rank, finalScore));
            alert.showAndWait();
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
