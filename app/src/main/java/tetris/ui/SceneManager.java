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

    public void showBattleModeSelection() {
        loadScene("/fxml/BattleModeSelection.fxml");
    }

    public void showPVPModeSelection() {
        loadScene("/fxml/PVPModeSelection.fxml");
    }

    public void showPVPServerWaiting(Object gameServer, String serverIP) {
        loadPVPServerWaitingScene("/fxml/PVPServerWaiting.fxml", gameServer, serverIP);
    }

    public void showPVPClientConnection() {
        loadScene("/fxml/PVPClientConnection.fxml");
    }

    public void showPVPLobby(Object gameServer, Object gameClient, boolean isServer) {
        loadPVPLobbyScene("/fxml/PVPLobby.fxml", gameServer, gameClient, isServer);
    }

    public void showPVPNetworkSelection(String gameMode) {
        loadPVPNetworkScene("/fxml/PVPNetworkSelection.fxml", gameMode);
    }

    public void showBattleGameScreen(String battleMode) {
        loadBattleScene("/fxml/BattleGameScreen.fxml", battleMode, false);
    }

    public void showBattleGameScreenAgainstComputer(String battleMode) {
        loadBattleScene("/fxml/BattleGameScreen.fxml", battleMode, true);
    }

    public void showPVPGameScreen(String gameMode, Object gameServer, Object gameClient, boolean isServer) {
        loadPVPScene("/fxml/PVPGameScreen.fxml", gameMode, gameServer, gameClient, isServer);
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
            } else if (fxmlPath.contains("BattleModeSelection")) {
                cssPath = "/css/MainMenu.css"; // 같은 스타일 사용
            } else if (fxmlPath.contains("PVPModeSelection")
                    || fxmlPath.contains("PVPNetworkSelection")
                    || fxmlPath.contains("PVPServerWaiting")
                    || fxmlPath.contains("PVPClientConnection")) {
                cssPath = "/css/MainMenu.css"; // 같은 스타일 사용
            } else if (fxmlPath.contains("BattleGameScreen")) {
                cssPath = "/css/GameScreen.css"; // 같은 스타일 사용
            } else if (fxmlPath.contains("PVPGameScreen")) {
                cssPath = "/css/GameScreen.css"; // 같은 스타일 사용
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
            } else if (controller instanceof tetris.ui.controllers.BattleModeSelectionController) {
                ((tetris.ui.controllers.BattleModeSelectionController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.PVPModeSelectionController) {
                ((tetris.ui.controllers.PVPModeSelectionController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.PVPServerWaitingController) {
                ((tetris.ui.controllers.PVPServerWaitingController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.PVPClientConnectionController) {
                ((tetris.ui.controllers.PVPClientConnectionController) controller).setSceneManager(this);
            } else if (controller instanceof tetris.ui.controllers.GameOverController) {
                tetris.ui.controllers.GameOverController gameOverController = 
                    (tetris.ui.controllers.GameOverController) controller;
                gameOverController.setSceneManager(this);
                if (finalScore > 0) {
                    gameOverController.setFinalScore(finalScore);
                }
            }

            applyScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadBattleScene(String fxmlPath, String battleMode, boolean vsComputer) {
        try {
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
                    width = MEDIUM_WIDTH;
                    height = MEDIUM_HEIGHT;
                    break;
            }

            // 대전 모드는 화면이 넓어야 하므로 가로로 확장
            width = width * 1.6; // 2개 보드를 표시하기 위해 넓게

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);

            // CSS 스타일 로드
            URL cssUrl = getClass().getResource("/css/BattleGameScreen.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // 컨트롤러에 SceneManager와 배틀 모드 설정
            Object controller = loader.getController();
            if (controller instanceof tetris.ui.controllers.BattleGameScreenController) {
                tetris.ui.controllers.BattleGameScreenController battleController =
                    (tetris.ui.controllers.BattleGameScreenController) controller;
                battleController.setSceneManager(this);
                battleController.setBattleMode(battleMode, vsComputer);
            }

            applyScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading battle scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadPVPScene(String fxmlPath, String gameMode, Object gameServer, Object gameClient, boolean isServer) {
        System.out.println("[SCENE] Loading PVP scene: " + fxmlPath);
        System.out.println("[SCENE] Game mode: " + gameMode + ", isServer: " + isServer);
        try {
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
                    width = MEDIUM_WIDTH;
                    height = MEDIUM_HEIGHT;
                    break;
            }

            // PVP 모드는 화면이 넓어야 하므로 가로로 확장 (2개 보드 표시)
            width = width * 1.6;

            System.out.println("[SCENE] Loading FXML...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);

            // CSS 스타일 로드
            URL cssUrl = getClass().getResource("/css/PVPGameScreen.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("[SCENE] CSS loaded");
            }

            // 컨트롤러에 SceneManager와 게임 모드, 네트워크 객체 설정
            System.out.println("[SCENE] Getting controller...");
            Object controller = loader.getController();
            if (controller instanceof tetris.ui.controllers.PVPGameScreenController) {
                System.out.println("[SCENE] PVPGameScreenController found, configuring...");
                tetris.ui.controllers.PVPGameScreenController pvpController =
                    (tetris.ui.controllers.PVPGameScreenController) controller;
                pvpController.setSceneManager(this);
                pvpController.setGameMode(gameMode);

                // PVPNetworkSelectionController에 게임 컨트롤러 참조 전달
                System.out.println("[SCENE] Registering game controller to network handler");
                tetris.ui.controllers.PVPNetworkSelectionController.setGameScreenController(pvpController);

                pvpController.setNetworkObjects(gameServer, gameClient, isServer);
            }

            System.out.println("[SCENE] Displaying scene...");
            applyScene(scene);
            System.out.println("[SCENE] PVP scene loaded successfully");
        } catch (IOException e) {
            System.err.println("[SCENE] Error loading PVP scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadPVPNetworkScene(String fxmlPath, String gameMode) {
        try {
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
                    width = MEDIUM_WIDTH;
                    height = MEDIUM_HEIGHT;
                    break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);

            // CSS 스타일 로드
            URL cssUrl = getClass().getResource("/css/MainMenu.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            // 컨트롤러에 SceneManager와 게임 모드 설정
            Object controller = loader.getController();
            if (controller instanceof tetris.ui.controllers.PVPNetworkSelectionController) {
                tetris.ui.controllers.PVPNetworkSelectionController networkController =
                    (tetris.ui.controllers.PVPNetworkSelectionController) controller;
                networkController.setSceneManager(this);
                networkController.setGameMode(gameMode);
            }

            applyScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading PVP network selection scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadPVPServerWaitingScene(String fxmlPath, Object gameServer, String serverIP) {
        try {
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
                    width = MEDIUM_WIDTH;
                    height = MEDIUM_HEIGHT;
                    break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);

            URL cssUrl = getClass().getResource("/css/MainMenu.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Object controller = loader.getController();
            if (controller instanceof tetris.ui.controllers.PVPServerWaitingController) {
                tetris.ui.controllers.PVPServerWaitingController waitingController =
                    (tetris.ui.controllers.PVPServerWaitingController) controller;
                waitingController.setSceneManager(this);
                waitingController.setServerInfo(
                    (tetris.network.GameServer) gameServer, 
                    serverIP
                );
            }

            applyScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading PVP server waiting scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void loadPVPLobbyScene(String fxmlPath, Object gameServer, Object gameClient, boolean isServer) {
        try {
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
                    width = MEDIUM_WIDTH;
                    height = MEDIUM_HEIGHT;
                    break;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), width, height);

            URL cssUrl = getClass().getResource("/css/MainMenu.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Object controller = loader.getController();
            if (controller instanceof tetris.ui.controllers.PVPLobbyController) {
                tetris.ui.controllers.PVPLobbyController lobbyController =
                    (tetris.ui.controllers.PVPLobbyController) controller;
                lobbyController.setSceneManager(this);
                lobbyController.setNetworkObjects(
                    (tetris.network.GameServer) gameServer,
                    (tetris.network.GameClient) gameClient,
                    isServer
                );
            }

            applyScene(scene);
        } catch (IOException e) {
            System.err.println("Error loading PVP lobby scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void applyScene(Scene scene) {
        primaryStage.setScene(scene);
        if (!isHeadlessEnvironment()) {
            primaryStage.show();
        }
    }

    private boolean isHeadlessEnvironment() {
        return Boolean.parseBoolean(System.getProperty("testfx.headless", "false"))
            || Boolean.parseBoolean(System.getProperty("java.awt.headless", "false"));
    }
}