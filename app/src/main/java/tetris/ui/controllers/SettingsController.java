package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import tetris.data.ScoreManager;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    private Slider volumeSlider;

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private CheckBox soundEffectsCheckBox;

    @FXML
    private CheckBox musicCheckBox;

    @FXML
    private CheckBox accessibilityModeCheckBox;

    @FXML
    private ComboBox<String> screenSizeComboBox;

    @FXML
    private TextField keyLeftField;

    @FXML
    private TextField keyRightField;

    @FXML
    private TextField keyDownField;

    @FXML
    private TextField keyRotateField;

    @FXML
    private TextField keyHardDropField;

    private SceneManager sceneManager;
    private SettingsManager settingsManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsManager = SettingsManager.getInstance();
        setupDifficultyComboBox();
        setupKeyFields();
        loadSettings();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    private void setupDifficultyComboBox() {
        if (difficultyComboBox != null) {
            difficultyComboBox.getItems().addAll("Easy", "Normal", "Hard");
            difficultyComboBox.setValue("Normal");
        }
        if (screenSizeComboBox != null) {
            screenSizeComboBox.getItems().addAll("작게", "중간", "크게");
            screenSizeComboBox.setValue("중간");
        }
    }

    private void setupKeyFields() {
        // TextField에 한 글자만 입력되도록 제한
        setupSingleKeyField(keyLeftField);
        setupSingleKeyField(keyRightField);
        setupSingleKeyField(keyDownField);
        setupSingleKeyField(keyRotateField);
        setupSingleKeyField(keyHardDropField);
    }

    private void setupSingleKeyField(TextField field) {
        if (field == null) return;
        
        // 입력시 대문자로 변환하고 한 글자만 허용
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 0) {
                String upper = newValue.toUpperCase();
                // SPACE는 예외로 허용
                if (upper.equals("SPACE")) {
                    return;
                }
                if (upper.length() > 1 && !upper.equals("SPACE")) {
                    field.setText(upper.substring(0, 1));
                } else if (!upper.equals("SPACE")) {
                    field.setText(upper);
                }
            }
        });
    }

    private void loadSettings() {
        // SettingsManager에서 설정 로드
        if (volumeSlider != null) {
            volumeSlider.setValue(settingsManager.getVolume());
        }
        if (difficultyComboBox != null) {
            difficultyComboBox.setValue(settingsManager.getDifficulty());
        }
        if (soundEffectsCheckBox != null) {
            soundEffectsCheckBox.setSelected(settingsManager.isSoundEffectsEnabled());
        }
        if (musicCheckBox != null) {
            musicCheckBox.setSelected(settingsManager.isMusicEnabled());
        }
        if (accessibilityModeCheckBox != null) {
            accessibilityModeCheckBox.setSelected(settingsManager.isAccessibilityModeEnabled());
        }
        if (screenSizeComboBox != null) {
            screenSizeComboBox.setValue(settingsManager.getScreenSize());
        }
        // 키 설정 불러오기
        if (keyLeftField != null) {
            keyLeftField.setText(settingsManager.getKeyLeft());
        }
        if (keyRightField != null) {
            keyRightField.setText(settingsManager.getKeyRight());
        }
        if (keyDownField != null) {
            keyDownField.setText(settingsManager.getKeyDown());
        }
        if (keyRotateField != null) {
            keyRotateField.setText(settingsManager.getKeyRotate());
        }
        if (keyHardDropField != null) {
            keyHardDropField.setText(settingsManager.getKeyHardDrop());
        }
    }

    @FXML
    private void onSaveSettings() {
        // 설정 저장 로직
        saveSettings();
        onBackToMenu();
    }

    @FXML
    private void onResetSettings() {
        // 설정 초기화
        settingsManager.resetToDefaults();
        loadSettings();
    }

    @FXML
    private void onBackToMenu() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    private void saveSettings() {
        // UI에서 설정 값들을 SettingsManager에 저장
        if (volumeSlider != null) {
            settingsManager.setVolume(volumeSlider.getValue());
        }
        if (difficultyComboBox != null) {
            settingsManager.setDifficulty(difficultyComboBox.getValue());
        }
        if (soundEffectsCheckBox != null) {
            settingsManager.setSoundEffectsEnabled(soundEffectsCheckBox.isSelected());
        }
        if (musicCheckBox != null) {
            settingsManager.setMusicEnabled(musicCheckBox.isSelected());
        }
        if (accessibilityModeCheckBox != null) {
            settingsManager.setAccessibilityModeEnabled(accessibilityModeCheckBox.isSelected());
        }
        if (screenSizeComboBox != null) {
            String selectedSize = screenSizeComboBox.getValue();
            settingsManager.setScreenSize(selectedSize);
            applyScreenSize(selectedSize);
        }
        // 키 설정 저장
        if (keyLeftField != null && !keyLeftField.getText().isEmpty()) {
            settingsManager.setKeyLeft(keyLeftField.getText());
        }
        if (keyRightField != null && !keyRightField.getText().isEmpty()) {
            settingsManager.setKeyRight(keyRightField.getText());
        }
        if (keyDownField != null && !keyDownField.getText().isEmpty()) {
            settingsManager.setKeyDown(keyDownField.getText());
        }
        if (keyRotateField != null && !keyRotateField.getText().isEmpty()) {
            settingsManager.setKeyRotate(keyRotateField.getText());
        }
        if (keyHardDropField != null && !keyHardDropField.getText().isEmpty()) {
            settingsManager.setKeyHardDrop(keyHardDropField.getText());
        }
        // 파일에 저장
        settingsManager.saveToFile();
        System.out.println("Settings saved");
    }

    private void applyScreenSize(String size) {
        if (sceneManager == null) return;
        
        double width, height;
        switch (size) {
            case "작게":
                width = 500;
                height = 600;
                break;
            case "크게":
                width = 900;
                height = 1000;
                break;
            default: // "중간"
                width = 700;
                height = 800;
                break;
        }
        sceneManager.setWindowSize(width, height);
        System.out.println("화면 크기 변경: " + size + " (" + width + "x" + height + ")");
    }

    @FXML
    private void onClearScoreboard() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("스코어보드 초기화");
        alert.setHeaderText("모든 게임 모드의 점수 기록을 삭제하시겠습니까?");
        alert.setContentText("일반 모드와 아이템 모드의 모든 기록이 삭제됩니다. 이 작업은 되돌릴 수 없습니다.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ScoreManager.getInstance().clearScores("NORMAL");
            ScoreManager.getInstance().clearScores("ITEM");
            
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("초기화 완료");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("모든 스코어보드가 초기화되었습니다.");
            infoAlert.showAndWait();
        }
    }
}