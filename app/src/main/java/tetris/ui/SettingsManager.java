package tetris.ui;

/**
 * 게임 설정을 관리하는 싱글톤 클래스
 */
public class SettingsManager {
    private static SettingsManager instance;

    private double volume = 50.0;
    private String difficulty = "Normal";
    private boolean soundEffectsEnabled = true;
    private boolean musicEnabled = true;
    private boolean accessibilityModeEnabled = false;
    
    private SettingsManager() {
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
    
    public boolean isAccessibilityModeEnabled() {
        return accessibilityModeEnabled;
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
    
    public void setAccessibilityModeEnabled(boolean enabled) {
        this.accessibilityModeEnabled = enabled;
    }

    public void resetToDefaults() {
        volume = 50.0;
        difficulty = "Normal";
        soundEffectsEnabled = true;
        musicEnabled = true;
        accessibilityModeEnabled = false;
    }
}
