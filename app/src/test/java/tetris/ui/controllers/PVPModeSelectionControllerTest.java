package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PVPModeSelectionController.
 */
class PVPModeSelectionControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load PVPModeSelection.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                Stage mockStage = new Stage();
                SceneManager sceneManager = new SceneManager(mockStage);
                
                controller.setSceneManager(sceneManager);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set scene manager: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                assertNotNull(loader.getLocation(), "PVPModeSelection.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMultipleInstances() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                    loader.load();
                    assertNotNull(loader.getController());
                }
            } catch (Exception e) {
                fail("Multiple instances test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testMenuButtonsInitialized() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                Field menuButtonsField = PVPModeSelectionController.class.getDeclaredField("menuButtons");
                menuButtonsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<Button> menuButtons = (List<Button>) menuButtonsField.get(controller);
                
                assertNotNull(menuButtons, "Menu buttons should be initialized");
                assertEquals(3, menuButtons.size(), "Should have 3 menu buttons");
            } catch (Exception e) {
                fail("Menu buttons test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testNavigateToPreviousButton() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                PVPModeSelectionController controller = new PVPModeSelectionController();

                List<Button> buttons = List.of(new HeadlessSafeButton(), new HeadlessSafeButton(), new HeadlessSafeButton());

                Field serverButtonField = PVPModeSelectionController.class.getDeclaredField("serverButton");
                serverButtonField.setAccessible(true);
                serverButtonField.set(controller, buttons.get(0));

                Field clientButtonField = PVPModeSelectionController.class.getDeclaredField("clientButton");
                clientButtonField.setAccessible(true);
                clientButtonField.set(controller, buttons.get(1));

                Field backButtonField = PVPModeSelectionController.class.getDeclaredField("backButton");
                backButtonField.setAccessible(true);
                backButtonField.set(controller, buttons.get(2));

                Field menuButtonsField = PVPModeSelectionController.class.getDeclaredField("menuButtons");
                menuButtonsField.setAccessible(true);
                menuButtonsField.set(controller, new java.util.ArrayList<>(buttons));

                Field currentIndexField = PVPModeSelectionController.class.getDeclaredField("currentIndex");
                currentIndexField.setAccessible(true);
                currentIndexField.set(controller, 0);

                Method selectButton = PVPModeSelectionController.class.getDeclaredMethod("selectButton", int.class);
                selectButton.setAccessible(true);
                selectButton.invoke(controller, 0);

                Method navigatePrev = PVPModeSelectionController.class.getDeclaredMethod("navigateToPreviousButton");
                navigatePrev.setAccessible(true);
                navigatePrev.invoke(controller);

                int newIndex = (int) currentIndexField.get(controller);
                assertEquals(2, newIndex, "Index should wrap to the last button when navigating up from 0");
            } catch (Exception e) {
                fail("Navigate previous test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testNavigateToNextButton() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                Field currentIndexField = PVPModeSelectionController.class.getDeclaredField("currentIndex");
                currentIndexField.setAccessible(true);
                
                // Navigate to next
                Method navigateNext = PVPModeSelectionController.class.getDeclaredMethod("navigateToNextButton");
                navigateNext.setAccessible(true);
                navigateNext.invoke(controller);
                
                int newIndex = (int) currentIndexField.get(controller);
                assertEquals(1, newIndex, "Index should be 1 after navigating next");
                
                // Navigate again
                navigateNext.invoke(controller);
                newIndex = (int) currentIndexField.get(controller);
                assertEquals(2, newIndex, "Index should be 2");
                
                // Navigate again (should wrap to 0)
                navigateNext.invoke(controller);
                newIndex = (int) currentIndexField.get(controller);
                assertEquals(0, newIndex, "Index should wrap to 0");
            } catch (Exception e) {
                fail("Navigate next test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSelectButton() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                Method selectButton = PVPModeSelectionController.class.getDeclaredMethod("selectButton", int.class);
                selectButton.setAccessible(true);
                
                // Select each button
                selectButton.invoke(controller, 0);
                selectButton.invoke(controller, 1);
                selectButton.invoke(controller, 2);
                
                // Invalid indices should be ignored
                selectButton.invoke(controller, -1);
                selectButton.invoke(controller, 10);
            } catch (Exception e) {
                fail("Select button test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testClearSelection() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                Method clearSelection = PVPModeSelectionController.class.getDeclaredMethod("clearSelection");
                clearSelection.setAccessible(true);
                clearSelection.invoke(controller);
                
                // No exception should be thrown
            } catch (Exception e) {
                fail("Clear selection test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBackAction() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);
                
                Method onBack = PVPModeSelectionController.class.getDeclaredMethod("onBack");
                onBack.setAccessible(true);
                onBack.invoke(controller);
                
                assertTrue(sceneManager.mainMenuShown, "Main menu should be shown on back");
            } catch (Exception e) {
                fail("Back action test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testOnClientMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);
                
                Method onClientMode = PVPModeSelectionController.class.getDeclaredMethod("onClientMode");
                onClientMode.setAccessible(true);
                onClientMode.invoke(controller);
                
                assertTrue(sceneManager.pvpClientConnectionShown, "PVP client connection should be shown");
            } catch (Exception e) {
                fail("Client mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSelectCurrentButton() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);
                
                // Set current index to back button (index 2)
                Field currentIndexField = PVPModeSelectionController.class.getDeclaredField("currentIndex");
                currentIndexField.setAccessible(true);
                currentIndexField.set(controller, 2);
                
                // Select current button (back button)
                Method selectCurrentButton = PVPModeSelectionController.class.getDeclaredMethod("selectCurrentButton");
                selectCurrentButton.setAccessible(true);
                selectCurrentButton.invoke(controller);
                
                assertTrue(sceneManager.mainMenuShown, "Main menu should be shown when back button is selected");
            } catch (Exception e) {
                fail("Select current button test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSelectCurrentButtonClientMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader.load();
                
                PVPModeSelectionController controller = loader.getController();
                
                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);
                
                // Set current index to client button (index 1)
                Field currentIndexField = PVPModeSelectionController.class.getDeclaredField("currentIndex");
                currentIndexField.setAccessible(true);
                currentIndexField.set(controller, 1);
                
                // Select current button (client button)
                Method selectCurrentButton = PVPModeSelectionController.class.getDeclaredMethod("selectCurrentButton");
                selectCurrentButton.setAccessible(true);
                selectCurrentButton.invoke(controller);
                
                assertTrue(sceneManager.pvpClientConnectionShown, "PVP client connection should be shown");
            } catch (Exception e) {
                fail("Select current button client mode test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testBackgroundImageSizes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                SettingsManager settings = SettingsManager.getInstance();
                String original = settings.getScreenSize();
                
                // Test small size
                settings.setScreenSize("작게");
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader1.load();
                assertNotNull(loader1.getController());
                
                // Test medium size
                settings.setScreenSize("중간");
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader2.load();
                assertNotNull(loader2.getController());
                
                // Test large size
                settings.setScreenSize("크게");
                FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/PVPModeSelection.fxml"));
                loader3.load();
                assertNotNull(loader3.getController());
                
                // Restore original
                settings.setScreenSize(original);
            } catch (Exception e) {
                fail("Background image size test failed: " + e.getMessage());
            }
        });
    }

    private static class TestSceneManager extends SceneManager {
        boolean mainMenuShown = false;
        boolean pvpClientConnectionShown = false;

        TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showMainMenu() {
            mainMenuShown = true;
        }

        @Override
        public void showPVPClientConnection() {
            pvpClientConnectionShown = true;
        }
    }

    private static class HeadlessSafeButton extends Button {
        @Override
        public void requestFocus() {
            // Skip focus requests in headless tests to avoid Window._updateViewSize errors
        }
    }
}
