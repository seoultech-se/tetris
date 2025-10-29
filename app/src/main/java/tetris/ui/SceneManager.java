package tetris.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private final Stage primaryStage;
    
    // 화면 크기 상수 (작게, 중간, 크게)
    private static final double SMALL_WIDTH = 480;
    private static final double SMALL_HEIGHT = 720;
    private static final double MEDIUM_WIDTH = 600;
    private static final double MEDIUM_HEIGHT = 900;
    private static final double LARGE_WIDTH = 720;
    private static final double LARGE_HEIGHT = 1080;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupStage();
    }

    private void setupStage() {
        primaryStage.setTitle("Tetris Game");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }

    public void showMainMenu() {
        loadScene("/fxml/MainMenu.fxml");
    }

    public void showGameScreen() {
        loadScene("/fxml/GameScreen.fxml");
    }

    public void showSettingsScreen() {
        loadScene("/fxml/SettingsScreen.fxml");
    }

    public void showScoreBoard() {
        loadScene("/fxml/ScoreBoard.fxml");
    }

    public void showGameOverScreen() {
        loadScene("/fxml/GameOverScreen.fxml", 0);
    }

    public void showGameOverScreen(int finalScore) {
        loadScene("/fxml/GameOverScreen.fxml", finalScore);
    }

    private void loadScene(String fxmlPath) {
        loadScene(fxmlPath, 0);
    }

    private void loadScene(String fxmlPath, int finalScore) {
        try {
            // 현재 설정된 화면 크기 가져오기
            String screenSize = SettingsManager.getInstance().getScreenSize();
            double width = MEDIUM_WIDTH;
            double height = MEDIUM_HEIGHT;
            
            switch (screenSize) {
                case "작게":
                    width = SMALL_WIDTH;
                    height = SMALL_HEIGHT;
                    break;
                case "중간":
                    width = MEDIUM_WIDTH;
                    height = MEDIUM_HEIGHT;
                    break;
                case "크게":
                    width = LARGE_WIDTH;
                    height = LARGE_HEIGHT;
                    break;
                default:
                    // 기본값은 중간
                    width = MEDIUM_WIDTH;
                    height = MEDIUM_HEIGHT;
                    break;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);
            
            // CSS 스타일 로드
            String cssPath = null;
            if (fxmlPath.contains("GameScreen")) {
                cssPath = "/css/GameScreen.css";
            } else if (fxmlPath.contains("MainMenu")) {
                cssPath = "/css/MainMenu.css";
            } else if (fxmlPath.contains("SettingsScreen")) {
                cssPath = "/css/SettingsScreen.css";
            } else if (fxmlPath.contains("ScoreBoard")) {
                cssPath = "/css/ScoreBoard.css";
            } else if (fxmlPath.contains("GameOverScreen")) {
                cssPath = "/css/GameOverScreen.css";
            }
            
            if (cssPath != null) {
                URL cssUrl = getClass().getResource(cssPath);
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS 로드 성공: " + cssPath);
                } else {
                    System.err.println("CSS 파일을 찾을 수 없습니다: " + cssPath);
                }
            }

            // 컨트롤러에 SceneManager 설정
            Object controller = loader.getController();
            if (controller instanceof tetris.ui.controllers.MainMenuController) {
                ((tetris.ui.controllers.MainMenuController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.GameScreenController) {
                ((tetris.ui.controllers.GameScreenController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.SettingsController) {
                ((tetris.ui.controllers.SettingsController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.ScoreBoardController) {
                ((tetris.ui.controllers.ScoreBoardController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.GameOverController) {
                tetris.ui.controllers.GameOverController gameOverController = 
                    (tetris.ui.controllers.GameOverController) controller;
                gameOverController.setSceneManager(this);
                if (finalScore > 0) {
                    gameOverController.setFinalScore(finalScore);
                }
            }

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}