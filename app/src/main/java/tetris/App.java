package tetris;

import javafx.application.Application;
import javafx.stage.Stage;
import tetris.ui.SceneManager;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = new SceneManager(primaryStage);
        sceneManager.showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
