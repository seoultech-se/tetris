package tetris.ui;

import java.io.*;
import java.util.Properties;

/**
 * 게임 설정을 관리하는 싱글톤 클래스
 */
public class SettingsManager {
    private static final String SETTINGS_FILE = "game_settings.properties";
    private static SettingsManager instance;

    private double volume = 50.0;
    private String difficulty = "Normal";
    private boolean soundEffectsEnabled = true;
    private boolean musicEnabled = true;
    private boolean colorBlindModeEnabled = false;
    private String screenSize = "중간";
    private String gameMode = "NORMAL"; // NORMAL 또는 ITEM

    // 키 설정 (기본값: WASD + Space)
    private String keyLeft = "A";
    private String keyRight = "D";
    private String keyDown = "S";
    private String keyRotate = "W";
    private String keyHardDrop = "SPACE";
    
    private SettingsManager() {
        loadFromFile();
    }
    
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public double getVolume() {
        return volume;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public boolean isColorBlindModeEnabled() {
        return colorBlindModeEnabled;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getKeyLeft() {
        return keyLeft;
    }

    public String getKeyRight() {
        return keyRight;
    }

    public String getKeyDown() {
        return keyDown;
    }

    public String getKeyRotate() {
        return keyRotate;
    }

    public String getKeyHardDrop() {
        return keyHardDrop;
    }

    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(100.0, volume));
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public void setSoundEffectsEnabled(boolean enabled) {
        this.soundEffectsEnabled = enabled;
    }
    
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
    }
    
    public void setColorBlindModeEnabled(boolean enabled) {
        this.colorBlindModeEnabled = enabled;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public void setKeyLeft(String key) {
        this.keyLeft = key.toUpperCase();
    }

    public void setKeyRight(String key) {
        this.keyRight = key.toUpperCase();
    }

    public void setKeyDown(String key) {
        this.keyDown = key.toUpperCase();
    }

    public void setKeyRotate(String key) {
        this.keyRotate = key.toUpperCase();
    }

    public void setKeyHardDrop(String key) {
        this.keyHardDrop = key.toUpperCase();
    }

    public void resetToDefaults() {
        volume = 50.0;
        difficulty = "Normal";
        soundEffectsEnabled = true;
        musicEnabled = true;
        colorBlindModeEnabled = false;
        screenSize = "중간";
        gameMode = "NORMAL";
        keyLeft = "A";
        keyRight = "D";
        keyDown = "S";
        keyRotate = "W";
        keyHardDrop = "SPACE";
        saveToFile();
    }

    /**
     * 설정을 파일에 저장
     */
    public void saveToFile() {
        Properties props = new Properties();
        props.setProperty("volume", String.valueOf(volume));
        props.setProperty("difficulty", difficulty);
        props.setProperty("soundEffectsEnabled", String.valueOf(soundEffectsEnabled));
        props.setProperty("musicEnabled", String.valueOf(musicEnabled));
        props.setProperty("colorBlindModeEnabled", String.valueOf(colorBlindModeEnabled));
        props.setProperty("screenSize", screenSize);
        props.setProperty("gameMode", gameMode);
        props.setProperty("keyLeft", keyLeft);
        props.setProperty("keyRight", keyRight);
        props.setProperty("keyDown", keyDown);
        props.setProperty("keyRotate", keyRotate);
        props.setProperty("keyHardDrop", keyHardDrop);

        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Tetris Game Settings");
            System.out.println("설정이 저장되었습니다: " + SETTINGS_FILE);
        } catch (IOException e) {
            System.err.println("설정 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 파일에서 설정을 불러오기
     */
    private void loadFromFile() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(SETTINGS_FILE)) {
            props.load(in);
            
            volume = Double.parseDouble(props.getProperty("volume", "50.0"));
            difficulty = props.getProperty("difficulty", "Normal");
            soundEffectsEnabled = Boolean.parseBoolean(props.getProperty("soundEffectsEnabled", "true"));
            musicEnabled = Boolean.parseBoolean(props.getProperty("musicEnabled", "true"));
            colorBlindModeEnabled = Boolean.parseBoolean(props.getProperty("colorBlindModeEnabled", "false"));
            screenSize = props.getProperty("screenSize", "중간");
            gameMode = props.getProperty("gameMode", "NORMAL");
            keyLeft = props.getProperty("keyLeft", "A");
            keyRight = props.getProperty("keyRight", "D");
            keyDown = props.getProperty("keyDown", "S");
            keyRotate = props.getProperty("keyRotate", "W");
            keyHardDrop = props.getProperty("keyHardDrop", "SPACE");
            
            System.out.println("설정을 불러왔습니다: " + SETTINGS_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("설정 파일이 없습니다. 기본값을 사용합니다.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("설정 로드 실패: " + e.getMessage());
        }
    }
}
