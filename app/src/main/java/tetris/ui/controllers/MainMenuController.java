package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML
    private Button normalModeButton;

    @FXML
    private Button itemModeButton;

    @FXML
    private Button battleModeButton;

    @FXML
    private Button pvpModeButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button scoreBoardButton;

    @FXML
    private Button exitButton;
    
    @FXML
    private ImageView backgroundImage;

    @FXML
    private VBox buttonContainer;

    @FXML
    private StackPane rootContainer;

    private SceneManager sceneManager;
    
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
        
        // 메뉴 버튼 리스트 초기화 (왼쪽에서 오른쪽, 위에서 아래 순서)
        menuButtons = new ArrayList<>();
        menuButtons.add(normalModeButton);
        menuButtons.add(itemModeButton);
        menuButtons.add(battleModeButton);
        menuButtons.add(pvpModeButton);
        menuButtons.add(scoreBoardButton);
        menuButtons.add(settingsButton);
        menuButtons.add(exitButton);
        
        applyLayoutForScreenSize();

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
        normalModeButton.requestFocus();
        
        // Scene 레벨에서 키 이벤트 처리 설정
        setupSceneKeyHandler();
        
        // 추가: 잠시 후 키 핸들러 다시 설정 (Scene이 완전히 로드된 후)
        javafx.application.Platform.runLater(() -> {
            setupSceneKeyHandler();
        });
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        // SceneManager 설정 후 키 핸들러 다시 설정
        setupSceneKeyHandler();
    }
    
    private void setupSceneKeyHandler() {
        // Scene이 설정된 후 키 핸들러 등록
        if (normalModeButton != null) {
            normalModeButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
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
                            default:
                                break;
                        }
                    });
                }
            });
        }
        
        // 추가: 모든 버튼에 키 이벤트 핸들러 추가
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
    
    private void applyLayoutForScreenSize() {
        String screenSize = SettingsManager.getInstance().getScreenSize();
        if ("작게".equals(screenSize)) {
            applyCompactLayout();
        } else {
            resetToVerticalLayout();
            if ("중간".equals(screenSize)) {
                applyMediumLayout();
            } else {
                applyDefaultLayout();
            }
        }
    }

    private void applyCompactLayout() {
        if (buttonContainer == null) {
            return;
        }

        clearLayoutClasses();
        buttonContainer.getChildren().clear();

        HBox row1 = createMenuRow(normalModeButton, itemModeButton);
        HBox row2 = createMenuRow(battleModeButton, pvpModeButton);
        HBox row3 = createMenuRow(scoreBoardButton, settingsButton, exitButton);

        buttonContainer.getChildren().addAll(row1, row2, row3);
        buttonContainer.getStyleClass().add("menu-compact");
    }

    private void applyMediumLayout() {
        if (buttonContainer == null) {
            return;
        }
        clearLayoutClasses();
        if (!buttonContainer.getStyleClass().contains("menu-medium")) {
            buttonContainer.getStyleClass().add("menu-medium");
        }
    }

    private void applyDefaultLayout() {
        clearLayoutClasses();
    }

    private void clearLayoutClasses() {
        if (buttonContainer == null) {
            return;
        }
        buttonContainer.getStyleClass().removeAll("menu-compact", "menu-medium");
    }

    private void resetToVerticalLayout() {
        if (buttonContainer == null) {
            return;
        }
        detachButton(normalModeButton);
        detachButton(itemModeButton);
        detachButton(battleModeButton);
        detachButton(pvpModeButton);
        detachButton(scoreBoardButton);
        detachButton(settingsButton);
        detachButton(exitButton);

        buttonContainer.getChildren().setAll(
                normalModeButton,
                itemModeButton,
                battleModeButton,
                pvpModeButton,
                scoreBoardButton,
                settingsButton,
                exitButton
        );
    }

    private void detachButton(Button button) {
        if (button == null) {
            return;
        }
        if (button.getParent() instanceof Pane pane) {
            pane.getChildren().remove(button);
        }
    }

    private HBox createMenuRow(Button... buttons) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER);
        row.getStyleClass().add("menu-row");
        row.getChildren().addAll(buttons);
        return row;
    }

    private void navigateToNextButton() {
        currentIndex = (currentIndex + 1) % menuButtons.size();
        selectButton(currentIndex);
        menuButtons.get(currentIndex).requestFocus();
    }
    
    private void selectCurrentButton() {
        Button currentButton = menuButtons.get(currentIndex);
        if (currentButton == normalModeButton) {
            onStartNormalMode();
        } else if (currentButton == itemModeButton) {
            onStartItemMode();
        } else if (currentButton == battleModeButton) {
            onStartBattleMode();
        } else if (currentButton == pvpModeButton) {
            onStartPVPMode();
        } else if (currentButton == scoreBoardButton) {
            onScoreBoard();
        } else if (currentButton == settingsButton) {
            onSettings();
        } else if (currentButton == exitButton) {
            onExit();
        }
    }

    @FXML
    private void onStartNormalMode() {
        if (sceneManager != null) {
            SettingsManager.getInstance().setGameMode("NORMAL");
            SettingsManager.getInstance().saveToFile();
            sceneManager.showGameScreen();
        }
    }

    @FXML
    private void onStartItemMode() {
        if (sceneManager != null) {
            SettingsManager.getInstance().setGameMode("ITEM");
            SettingsManager.getInstance().saveToFile();
            sceneManager.showGameScreen();
        }
    }

    @FXML
    private void onStartBattleMode() {
        if (sceneManager != null) {
            sceneManager.showBattleModeSelection();
        }
    }

    @FXML
    private void onStartPVPMode() {
        if (sceneManager != null) {
            sceneManager.showPVPModeSelection();
        }
    }

    @FXML
    private void onSettings() {
        if (sceneManager != null) {
            sceneManager.showSettingsScreen();
        }
    }

    @FXML
    private void onScoreBoard() {
        if (sceneManager != null) {
            sceneManager.showScoreBoard();
        }
    }

    @FXML
    private void onExit() {
        System.exit(0);
    }
    
    
    /**
     * 버튼 선택 및 스타일 적용
     */
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
    
    /**
     * 모든 버튼 선택 해제 (마우스 호버 시 사용)
     */
    private void clearSelection() {
        for (Button button : menuButtons) {
            button.getStyleClass().remove("focused");
        }
    }
}