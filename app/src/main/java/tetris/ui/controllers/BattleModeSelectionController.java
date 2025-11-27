package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BattleModeSelectionController implements Initializable {

    @FXML
    private Button normalBattleButton;

    @FXML
    private Button itemBattleButton;

    @FXML
    private Button timeLimitBattleButton;

    @FXML
    private Button computerBattleButton;

    @FXML
    private Button backButton;

    @FXML
    private ImageView backgroundImage;

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

        // 메뉴 버튼 리스트 초기화
        menuButtons = new ArrayList<>();
        menuButtons.add(normalBattleButton);
        menuButtons.add(itemBattleButton);
        menuButtons.add(timeLimitBattleButton);
        menuButtons.add(computerBattleButton);
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
        normalBattleButton.requestFocus();

        // 키 이벤트 처리 설정
        setupSceneKeyHandler();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        setupSceneKeyHandler();
    }

    private void setupSceneKeyHandler() {
        if (normalBattleButton != null) {
            normalBattleButton.sceneProperty().addListener((obs, oldScene, newScene) -> {
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
        if (currentButton == normalBattleButton) {
            onNormalBattle();
        } else if (currentButton == itemBattleButton) {
            onItemBattle();
        } else if (currentButton == timeLimitBattleButton) {
            onTimeLimitBattle();
        } else if (currentButton == computerBattleButton) {
            onComputerBattle();
        } else if (currentButton == backButton) {
            onBack();
        }
    }

    @FXML
    private void onNormalBattle() {
        if (sceneManager != null) {
            sceneManager.showBattleGameScreen("NORMAL");
        }
    }

    @FXML
    private void onItemBattle() {
        if (sceneManager != null) {
            sceneManager.showBattleGameScreen("ITEM");
        }
    }

    @FXML
    private void onTimeLimitBattle() {
        if (sceneManager != null) {
            sceneManager.showBattleGameScreen("TIME_LIMIT");
        }
    }

    @FXML
    private void onComputerBattle() {
        if (sceneManager != null) {
            sceneManager.showBattleGameScreenAgainstComputer("NORMAL");
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

        clearSelection();
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

