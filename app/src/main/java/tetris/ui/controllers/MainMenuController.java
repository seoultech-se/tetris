package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private Button settingsButton;

    @FXML
    private Button scoreBoardButton;

    @FXML
    private Button exitButton;
    
    @FXML
    private ImageView backgroundImage;

    private SceneManager sceneManager;
    
    private List<Button> menuButtons;
    private int currentIndex = 0;
    
    // 선택된 스타일 (버튼 pressed 상태와 동일)
    private static final String SELECTED_STYLE = "-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #ffffff;";

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
        menuButtons.add(scoreBoardButton);
        menuButtons.add(settingsButton);
        menuButtons.add(exitButton);
        
        // 모든 버튼에 이벤트 핸들러 추가
        for (Button button : menuButtons) {
            // 마우스 호버 시 키보드 선택 해제
            button.setOnMouseEntered(e -> {
                int index = menuButtons.indexOf(button);
                if (index != currentIndex) {
                    clearSelection();
                }
            });
            
            // 키보드 이벤트 핸들러
            button.setOnKeyPressed(this::handleKeyPress);
        }
        
        // 첫 번째 버튼 선택
        selectButton(0);
        normalModeButton.requestFocus();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
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
     * 키보드 입력 처리
     */
    private void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();
        
        switch (code) {
            case UP:
                // 위로 이동 (위 줄로)
                if (currentIndex >= 2) {
                    selectButton(currentIndex - 2);
                }
                event.consume();
                break;
                
            case DOWN:
                // 아래로 이동 (아래 줄로)
                if (currentIndex < 2) {
                    selectButton(currentIndex + 2);
                }
                event.consume();
                break;
                
            case LEFT:
                // 왼쪽으로 이동
                if (currentIndex % 2 == 1) { // 오른쪽 버튼에 있으면
                    selectButton(currentIndex - 1);
                }
                event.consume();
                break;
                
            case RIGHT:
                // 오른쪽으로 이동
                if (currentIndex % 2 == 0 && currentIndex < menuButtons.size() - 1) { // 왼쪽 버튼에 있으면
                    selectButton(currentIndex + 1);
                }
                event.consume();
                break;
                
            case ENTER:
                // 현재 선택된 버튼 실행
                menuButtons.get(currentIndex).fire();
                event.consume();
                break;
                
            default:
                break;
        }
    }
    
    /**
     * 버튼 선택 및 스타일 적용
     */
    private void selectButton(int index) {
        if (index < 0 || index >= menuButtons.size()) {
            return;
        }
        
        // 이전 선택 버튼 스타일 초기화 (CSS 기본 스타일로 복원)
        if (currentIndex >= 0 && currentIndex < menuButtons.size()) {
            menuButtons.get(currentIndex).setStyle("");
        }
        
        // 새로운 버튼 선택
        currentIndex = index;
        menuButtons.get(currentIndex).setStyle(SELECTED_STYLE);
        menuButtons.get(currentIndex).requestFocus();
    }
    
    /**
     * 모든 버튼 선택 해제 (마우스 호버 시 사용)
     */
    private void clearSelection() {
        if (currentIndex >= 0 && currentIndex < menuButtons.size()) {
            menuButtons.get(currentIndex).setStyle("");
            currentIndex = -1;
        }
    }
}