package tetris.ui;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 게임 설정을 관리하는 싱글톤 클래스
 */
public class SettingsManager {
    private static final String APP_NAME = "Tetris";
    private static final String SETTINGS_FILENAME = "game_settings.properties";
    private static SettingsManager instance;

    private double volume = 50.0;
    private String difficulty = "Normal";
    private boolean soundEffectsEnabled = true;
    private boolean musicEnabled = true;
    private boolean colorBlindModeEnabled = false;
    private String screenSize = "중간";
    private String gameMode = "NORMAL"; // NORMAL 또는 ITEM

    // Player1 키 설정 (기본값: WASD + Space)
    private String keyLeft = "A";
    private String keyRight = "D";
    private String keyDown = "S";
    private String keyRotate = "W";
    private String keyHardDrop = "SPACE";
    
    // Player2 키 설정 (기본값: 화살표 키)
    private String keyLeftP2 = "LEFT";
    private String keyRightP2 = "RIGHT";
    private String keyDownP2 = "DOWN";
    private String keyRotateP2 = "UP";
    private String keyHardDropP2 = "ENTER";
    
    private SettingsManager() {
        loadFromFile();
    }
    
    public static SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }
    
    /**
     * 애플리케이션 데이터 디렉토리 경로를 반환
     * macOS: ~/Library/Application Support/Tetris
     * Windows: %APPDATA%/Tetris
     * Linux: ~/.local/share/Tetris
     */
    private static Path getDataDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        Path dataDir;
        
        if (os.contains("mac")) {
            dataDir = Paths.get(userHome, "Library", "Application Support", APP_NAME);
        } else if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData != null) {
                dataDir = Paths.get(appData, APP_NAME);
            } else {
                dataDir = Paths.get(userHome, "AppData", "Roaming", APP_NAME);
            }
        } else {
            // Linux and others
            dataDir = Paths.get(userHome, ".local", "share", APP_NAME);
        }
        
        // 디렉토리가 없으면 생성
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
        
        return dataDir;
    }
    
    /**
     * 설정 파일의 전체 경로를 반환
     */
    private static Path getSettingsFile() {
        return getDataDirectory().resolve(SETTINGS_FILENAME);
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

    public String getKeyLeftP2() {
        return keyLeftP2;
    }

    public String getKeyRightP2() {
        return keyRightP2;
    }

    public String getKeyDownP2() {
        return keyDownP2;
    }

    public String getKeyRotateP2() {
        return keyRotateP2;
    }

    public String getKeyHardDropP2() {
        return keyHardDropP2;
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

    public void setKeyLeftP2(String key) {
        this.keyLeftP2 = key.toUpperCase();
    }

    public void setKeyRightP2(String key) {
        this.keyRightP2 = key.toUpperCase();
    }

    public void setKeyDownP2(String key) {
        this.keyDownP2 = key.toUpperCase();
    }

    public void setKeyRotateP2(String key) {
        this.keyRotateP2 = key.toUpperCase();
    }

    public void setKeyHardDropP2(String key) {
        this.keyHardDropP2 = key.toUpperCase();
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
        keyLeftP2 = "LEFT";
        keyRightP2 = "RIGHT";
        keyDownP2 = "DOWN";
        keyRotateP2 = "UP";
        keyHardDropP2 = "ENTER";
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
        props.setProperty("keyLeftP2", keyLeftP2);
        props.setProperty("keyRightP2", keyRightP2);
        props.setProperty("keyDownP2", keyDownP2);
        props.setProperty("keyRotateP2", keyRotateP2);
        props.setProperty("keyHardDropP2", keyHardDropP2);

        Path settingsPath = getSettingsFile();
        try (FileOutputStream out = new FileOutputStream(settingsPath.toFile())) {
            props.store(out, "Tetris Game Settings");
            System.out.println("설정이 저장되었습니다: " + settingsPath);
        } catch (IOException e) {
            System.err.println("설정 저장 실패: " + e.getMessage());
        }
    }

    /**
     * 파일에서 설정을 불러오기
     */
    private void loadFromFile() {
        Properties props = new Properties();
        Path settingsPath = getSettingsFile();
        File settingsFile = settingsPath.toFile();
        
        try (FileInputStream in = new FileInputStream(settingsFile)) {
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
            keyLeftP2 = props.getProperty("keyLeftP2", "LEFT");
            keyRightP2 = props.getProperty("keyRightP2", "RIGHT");
            keyDownP2 = props.getProperty("keyDownP2", "DOWN");
            keyRotateP2 = props.getProperty("keyRotateP2", "UP");
            keyHardDropP2 = props.getProperty("keyHardDropP2", "ENTER");
            
            System.out.println("설정을 불러왔습니다: " + settingsPath);
        } catch (FileNotFoundException e) {
            System.out.println("설정 파일이 없습니다. 기본값을 사용합니다.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("설정 로드 실패: " + e.getMessage());
        }
    }
}
