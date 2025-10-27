package tetris.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private final Stage primaryStage;
    private static final double WINDOW_WIDTH = 600;
    private static final double WINDOW_HEIGHT = 900;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
            
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