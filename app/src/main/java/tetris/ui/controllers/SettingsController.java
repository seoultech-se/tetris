package tetris.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import tetris.ui.SceneManager;

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

    private SceneManager sceneManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        // 설정 로드 로직
        if (volumeSlider != null) {
            volumeSlider.setValue(50);
        }
        if (soundEffectsCheckBox != null) {
            soundEffectsCheckBox.setSelected(true);
        }
        if (musicCheckBox != null) {
            musicCheckBox.setSelected(true);
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
        loadSettings();
    }

    @FXML
    private void onBackToMenu() {
        if (sceneManager != null) {
            sceneManager.showMainMenu();
        }
    }

    private void saveSettings() {
        // 실제 설정 저장 구현
        System.out.println("Settings saved");
    }
}