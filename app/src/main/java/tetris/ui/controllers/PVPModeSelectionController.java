package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import tetris.network.GameServer;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PVPModeSelectionController implements Initializable {

    @FXML
    private Button serverButton;

    @FXML
    private Button clientButton;

    @FXML
    private Button backButton;

    @FXML
    private ImageView backgroundImage;

    private SceneManager sceneManager;
    private GameServer gameServer;
    private static final int SERVER_PORT = 7777;

    private List<Button> menuButtons;
    private int currentIndex = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 배경 이미지 크기 설정
        if (backgroundImage != null) {
            String screenSize = SettingsManager.getInstance().getScreenSize();
            switch (screenSize) {
                case "작게":
                    backgroundImage.setFitWidth(480);
                    backgroundImage.setFitHeight(720);
                    break;
                case "중간":
                    backgroundImage.setFitWidth(600);
                    backgroundImage.setFitHeight(900);
                    break;
                case "크게":
                    backgroundImage.setFitWidth(720);
                    backgroundImage.setFitHeight(1080);
                    break;
                default:
                    backgroundImage.setFitWidth(600);
                    backgroundImage.setFitHeight(900);
                    break;
            }
        }

        // 메뉴 버튼 리스트 초기화
        menuButtons = new ArrayList<>();
        menuButtons.add(serverButton);
        menuButtons.add(clientButton);
        menuButtons.add(backButton);

        // 모든 버튼에 마우스 호버 이벤트 핸들러 추가
        for (Button button : menuButtons) {
            button.setOnMouseEntered(e -> {
                int index = menuButtons.indexOf(button);
                if (index != currentIndex) {
                    selectButton(index);
                }
            });
        }

        // 첫 번째 버튼 선택
        selectButton(0);
        serverButton.requestFocus();

        // 키 이벤트 처리 설정
        setupSceneKeyHandler();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        setupSceneKeyHandler();
    }

    private void setupSceneKeyHandler() {
        if (serverButton != null) {
            serverButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.setOnKeyPressed(event -> {
                        KeyCode code = event.getCode();

                        switch (code) {
                            case UP:
                                navigateToPreviousButton();
                                event.consume();
                                break;
                            case DOWN:
                                navigateToNextButton();
                                event.consume();
                                break;
                            case ENTER:
                                selectCurrentButton();
                                event.consume();
                                break;
                            case ESCAPE:
                                onBack();
                                event.consume();
                                break;
                            default:
                                break;
                        }
                    });
                }
            });
        }

        for (Button button : menuButtons) {
            if (button != null) {
                button.setOnKeyPressed(event -> {
                    KeyCode code = event.getCode();

                    switch (code) {
                        case UP:
                            navigateToPreviousButton();
                            event.consume();
                            break;
                        case DOWN:
                            navigateToNextButton();
                            event.consume();
                            break;
                        case ENTER:
                            selectCurrentButton();
                            event.consume();
                            break;
                        case ESCAPE:
                            onBack();
                            event.consume();
                            break;
                        default:
                            break;
                    }
                });
            }
        }
    }

    private void navigateToPreviousButton() {
        currentIndex = (currentIndex - 1 + menuButtons.size()) % menuButtons.size();
        selectButton(currentIndex);
        menuButtons.get(currentIndex).requestFocus();
    }

    private void navigateToNextButton() {
        currentIndex = (currentIndex + 1) % menuButtons.size();
        selectButton(currentIndex);
        menuButtons.get(currentIndex).requestFocus();
    }

    private void selectCurrentButton() {
        Button currentButton = menuButtons.get(currentIndex);
        if (currentButton == serverButton) {
            onServerMode();
        } else if (currentButton == clientButton) {
            onClientMode();
        } else if (currentButton == backButton) {
            onBack();
        }
    }

    @FXML
    private void onServerMode() {
        // 서버로 시작
        System.out.println("[PVP-MODE] Server mode selected");
        startServer();
    }

    @FXML
    private void onClientMode() {
        // 클라이언트로 접속
        System.out.println("[PVP-MODE] Client mode selected");
        if (sceneManager != null) {
            sceneManager.showPVPClientConnection();
        }
    }

    private void startServer() {
        try {
            gameServer = new GameServer(SERVER_PORT);
            String serverIP = gameServer.getServerIP();
            
            // 서버 시작 (MessageHandler는 PVPServerWaitingController에서 설정됨)
            gameServer.start();
            
            // 서버 대기 화면으로 이동
            System.out.println("[PVP-MODE] Starting server, transitioning to waiting screen");
            sceneManager.showPVPServerWaiting(gameServer, serverIP);
            
        } catch (IOException e) {
            System.err.println("서버 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onBack() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    private void selectButton(int index) {
        if (index < 0 || index >= menuButtons.size()) {
            return;
        }

        // 모든 버튼의 포커스 클래스 제거
        clearSelection();

        // 새로운 버튼 선택
        currentIndex = index;
        menuButtons.get(currentIndex).getStyleClass().add("focused");
        menuButtons.get(currentIndex).requestFocus();
    }

    private void clearSelection() {
        for (Button button : menuButtons) {
            button.getStyleClass().remove("focused");
        }
    }
}
