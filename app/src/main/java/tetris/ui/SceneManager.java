package tetris.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private final Stage primaryStage;
    private static final double WINDOW_WIDTH = 800;
    private static final double WINDOW_HEIGHT = 600;

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
        loadScene("/fxml/GameOverScreen.fxml");
    }

    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

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
                ((tetris.ui.controllers.GameOverController) controller).setSceneManager(this);
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