package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.Test;
import tetris.ui.SettingsManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SettingsController.
 * Tests settings UI initialization and data binding.
 */
class SettingsControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
                loader.load();
                
                SettingsController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load SettingsScreen.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSettingsManagerIntegration() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                assertNotNull(settings);
                
                // Test that settings can be modified
                String originalSize = settings.getScreenSize();
                settings.setScreenSize("크게");
                assertEquals("크게", settings.getScreenSize());
                
                // Restore original
                settings.setScreenSize(originalSize);
            } catch (Exception e) {
                fail("Settings manager integration failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
                assertNotNull(loader.getLocation(), "SettingsScreen.fxml should exist");
                
                Object root = loader.load();
                assertNotNull(root, "FXML root should not be null");
            } catch (Exception e) {
                fail("Failed to load FXML: " + e.getMessage());
            }
        });
    }

    @Test
    void testControllerInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
                loader.load();
                
                SettingsController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 설정 로드 테스트 =====
    
    @Test
    void testLoadDefaultSettings() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
                loader.load();
                SettingsController controller = loader.getController();
                
                // 초기화 시 SettingsManager에서 설정 로드됨
                SettingsManager settings = SettingsManager.getInstance();
                assertNotNull(settings.getDifficulty());
                assertNotNull(settings.getScreenSize());
            } catch (Exception e) {
                fail("Load default settings failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testDifficultySettings() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                
                // 난이도 설정 테스트
                String[] difficulties = {"Easy", "Normal", "Hard"};
                for (String diff : difficulties) {
                    settings.setDifficulty(diff);
                    assertEquals(diff, settings.getDifficulty());
                }
            } catch (Exception e) {
                fail("Difficulty settings test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testScreenSizeSettings() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String original = settings.getScreenSize();
                
                // 화면 크기 설정 테스트
                String[] sizes = {"작게", "중간", "크게"};
                for (String size : sizes) {
                    settings.setScreenSize(size);
                    assertEquals(size, settings.getScreenSize());
                }
                
                // 원래 설정으로 복원
                settings.setScreenSize(original);
            } catch (Exception e) {
                fail("Screen size settings test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testColorBlindModeToggle() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                boolean original = settings.isColorBlindModeEnabled();
                
                // 색맹 모드 토글 테스트
                settings.setColorBlindModeEnabled(true);
                assertTrue(settings.isColorBlindModeEnabled());
                
                settings.setColorBlindModeEnabled(false);
                assertFalse(settings.isColorBlindModeEnabled());
                
                // 원래 설정으로 복원
                settings.setColorBlindModeEnabled(original);
            } catch (Exception e) {
                fail("Color blind mode toggle failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 리셋 테스트 =====
    
    @Test
    void testResetToDefaults() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                
                // 설정 변경
                settings.setDifficulty("Hard");
                settings.setScreenSize("크게");
                settings.setColorBlindModeEnabled(true);
                
                // 기본값으로 리셋
                settings.resetToDefaults();
                
                // 기본값 확인
                assertNotNull(settings.getDifficulty());
                assertNotNull(settings.getScreenSize());
            } catch (Exception e) {
                fail("Reset to defaults test failed: " + e.getMessage());
            }
        });
    }
    
    // ===== 통합 테스트 =====
    
    @Test
    void testFullSettingsWorkflow() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
                loader.load();
                SettingsController controller = loader.getController();
                
                SettingsManager settings = SettingsManager.getInstance();
                
                // 1. 설정 로드
                String difficulty = settings.getDifficulty();
                assertNotNull(difficulty);
                
                // 2. 설정 변경
                settings.setDifficulty("Easy");
                settings.setScreenSize("작게");
                
                // 3. 저장 (파일 저장은 실제로 수행됨)
                settings.saveToFile();
                
                // 4. 다시 로드하여 확인
                assertEquals("Easy", settings.getDifficulty());
                assertEquals("작게", settings.getScreenSize());
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Full settings workflow failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMultipleControllers() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 첫 번째 컨트롤러
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
                loader1.load();
                SettingsController controller1 = loader1.getController();
                assertNotNull(controller1);
                
                // 두 번째 컨트롤러 (동일한 SettingsManager 사용)
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/SettingsScreen.fxml"));
                loader2.load();
                SettingsController controller2 = loader2.getController();
                assertNotNull(controller2);
                
                // 싱글톤 SettingsManager 확인
                SettingsManager settings1 = SettingsManager.getInstance();
                SettingsManager settings2 = SettingsManager.getInstance();
                assertSame(settings1, settings2);
            } catch (Exception e) {
                fail("Multiple controllers test failed: " + e.getMessage());
            }
        });
    }
}
