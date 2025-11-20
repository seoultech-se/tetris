package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import tetris.network.GameClient;
import tetris.network.GameServer;
import tetris.network.NetworkMessage;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PVPModeSelectionController implements Initializable {

    @FXML
    private Button normalPVPButton; // 서버 버튼으로 사용

    @FXML
    private Button itemPVPButton; // 클라이언트 버튼으로 사용

    @FXML
    private Button timeLimitPVPButton; // 사용 안함

    @FXML
    private Button backButton;

    @FXML
    private ImageView backgroundImage;

    private SceneManager sceneManager;
    private GameServer gameServer;
    private GameClient gameClient;
    private boolean isServer = false;
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
        menuButtons.add(normalPVPButton);
        menuButtons.add(itemPVPButton);
        menuButtons.add(timeLimitPVPButton);
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
        normalPVPButton.requestFocus();

        // 키 이벤트 처리 설정
        setupSceneKeyHandler();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        setupSceneKeyHandler();
    }

    private void setupSceneKeyHandler() {
        if (normalPVPButton != null) {
            normalPVPButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
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
        if (currentButton == normalPVPButton) {
            onNormalPVP();
        } else if (currentButton == itemPVPButton) {
            onItemPVP();
        } else if (currentButton == timeLimitPVPButton) {
            onTimeLimitPVP();
        } else if (currentButton == backButton) {
            onBack();
        }
    }

    @FXML
    private void onNormalPVP() {
        // 일반 모드로 네트워크 설정 화면으로 이동
        System.out.println("[PVP-MODE] Normal mode selected");
        if (sceneManager != null) {
            sceneManager.showPVPNetworkSelection("NORMAL");
        }
    }

    @FXML
    private void onItemPVP() {
        // 아이템 모드로 네트워크 설정 화면으로 이동
        System.out.println("[PVP-MODE] Item mode selected");
        if (sceneManager != null) {
            sceneManager.showPVPNetworkSelection("ITEM");
        }
    }

    @FXML
    private void onTimeLimitPVP() {
        // 시간제한 모드로 네트워크 설정 화면으로 이동
        System.out.println("[PVP-MODE] Time limit mode selected");
        if (sceneManager != null) {
            sceneManager.showPVPNetworkSelection("TIME_LIMIT");
        }
    }

    private void startServer() {
        try {
            gameServer = new GameServer(SERVER_PORT);
            String serverIP = gameServer.getServerIP();
            
            gameServer.setMessageHandler(new GameServer.MessageHandler() {
                @Override
                public void onMessageReceived(Object message) {
                    Platform.runLater(() -> handleServerMessage(message));
                }

                @Override
                public void onClientConnected() {
                    System.out.println("[PVP-MODE-SERVER] Client connected callback triggered");
                    Platform.runLater(() -> {
                        System.out.println("[PVP-MODE-SERVER] Platform.runLater executed");
                        System.out.println("[PVP-MODE-SERVER] Client connected!");

                        // 연결 확인 메시지 전송
                        try {
                            System.out.println("[PVP-MODE-SERVER] Sending CONNECTION_ACCEPTED");
                            gameServer.sendMessage(new NetworkMessage(
                                NetworkMessage.MessageType.CONNECTION_ACCEPTED,
                                "Server connection successful"
                            ));
                            System.out.println("[PVP-MODE-SERVER] CONNECTION_ACCEPTED sent");

                            // 로비 화면으로 이동
                            System.out.println("[PVP-MODE-SERVER] Transitioning to PVP Lobby");
                            sceneManager.showPVPLobby(gameServer, null, true);
                            System.out.println("[PVP-MODE-SERVER] PVP Lobby transition complete");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onClientDisconnected() {
                    Platform.runLater(() -> {
                        System.out.println("클라이언트 연결 끊김");
                    });
                }

                @Override
                public void onError(Exception e) {
                    Platform.runLater(() -> {
                        System.err.println("서버 오류: " + e.getMessage());
                        e.printStackTrace();
                    });
                }
            });
            
            gameServer.start();
            
            // 서버 대기 화면으로 이동
            sceneManager.showPVPServerWaiting(gameServer, serverIP);
            
        } catch (IOException e) {
            System.err.println("서버 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleServerMessage(Object message) {
        if (message instanceof NetworkMessage) {
            NetworkMessage netMsg = (NetworkMessage) message;
            switch (netMsg.getType()) {
                case CONNECTION_REQUEST:
                    System.out.println("클라이언트 연결 요청 수신");
                    break;
            }
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
