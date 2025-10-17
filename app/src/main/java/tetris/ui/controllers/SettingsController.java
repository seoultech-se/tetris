package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.net.URL;
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

    private SceneManager sceneManager;
    private SettingsManager settingsManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        settingsManager = SettingsManager.getInstance();
        setupDifficultyComboBox();
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
        System.out.println("Settings saved");
    }
}