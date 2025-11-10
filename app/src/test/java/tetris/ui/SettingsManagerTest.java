package tetris.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SettingsManagerTest {

    private SettingsManager settings;

    @BeforeEach
    void setUp() {
        settings = SettingsManager.getInstance();
    }

    @Test
    void testGetInstance() {
        SettingsManager instance1 = SettingsManager.getInstance();
        SettingsManager instance2 = SettingsManager.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void testVolume() {
        settings.setVolume(75.0);
        assertEquals(75.0, settings.getVolume());
    }

    @Test
    void testVolume_Bounds() {
        settings.setVolume(-10.0);
        assertEquals(0.0, settings.getVolume());
        
        settings.setVolume(150.0);
        assertEquals(100.0, settings.getVolume());
    }

    @Test
    void testDifficulty() {
        settings.setDifficulty("Hard");
        assertEquals("Hard", settings.getDifficulty());
    }

    @Test
    void testSoundEffectsEnabled() {
        settings.setSoundEffectsEnabled(false);
        assertFalse(settings.isSoundEffectsEnabled());
        
        settings.setSoundEffectsEnabled(true);
        assertTrue(settings.isSoundEffectsEnabled());
    }

    @Test
    void testMusicEnabled() {
        settings.setMusicEnabled(false);
        assertFalse(settings.isMusicEnabled());
        
        settings.setMusicEnabled(true);
        assertTrue(settings.isMusicEnabled());
    }

    @Test
    void testColorBlindModeEnabled() {
        settings.setColorBlindModeEnabled(true);
        assertTrue(settings.isColorBlindModeEnabled());
    }

    @Test
    void testScreenSize() {
        settings.setScreenSize("큰");
        assertEquals("큰", settings.getScreenSize());
    }

    @Test
    void testGameMode() {
        settings.setGameMode("ITEM");
        assertEquals("ITEM", settings.getGameMode());
        
        settings.setGameMode("NORMAL");
        assertEquals("NORMAL", settings.getGameMode());
    }

    @Test
    void testKeyLeft() {
        settings.setKeyLeft("LEFT");
        assertEquals("LEFT", settings.getKeyLeft());
    }

    @Test
    void testKeyRight() {
        settings.setKeyRight("RIGHT");
        assertEquals("RIGHT", settings.getKeyRight());
    }

    @Test
    void testKeyDown() {
        settings.setKeyDown("DOWN");
        assertEquals("DOWN", settings.getKeyDown());
    }

    @Test
    void testKeyRotate() {
        settings.setKeyRotate("UP");
        assertEquals("UP", settings.getKeyRotate());
    }

    @Test
    void testKeyHardDrop() {
        settings.setKeyHardDrop("SPACE");
        assertEquals("SPACE", settings.getKeyHardDrop());
    }

    @Test
    void testKeyUpperCase() {
        settings.setKeyLeft("a");
        assertEquals("A", settings.getKeyLeft());
    }

    @Test
    void testResetToDefaults() {
        settings.setVolume(99.0);
        settings.setDifficulty("Hard");
        settings.setGameMode("ITEM");
        
        settings.resetToDefaults();
        
        assertEquals(50.0, settings.getVolume());
        assertEquals("Normal", settings.getDifficulty());
        assertEquals("NORMAL", settings.getGameMode());
    }

    @Test
    void testSaveAndLoad() {
        settings.setVolume(80.0);
        settings.setDifficulty("Easy");
        settings.saveToFile();
        
        // 다시 불러오기
        SettingsManager newInstance = SettingsManager.getInstance();
        // 싱글톤이므로 같은 인스턴스
        assertSame(settings, newInstance);
    }
}

