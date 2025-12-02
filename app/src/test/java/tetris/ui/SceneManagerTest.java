package tetris.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tetris.ui.controllers.JavaFXTestBase;
import tetris.ui.SettingsManager;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SceneManager.
 * Tests basic initialization and field setup.
 */
class SceneManagerTest extends JavaFXTestBase {
    private static Object getStaticField(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    @Test
    void testConstructor() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                SceneManager sceneManager = new SceneManager(stage);
                
                assertNotNull(sceneManager, "SceneManager should be created");
                assertEquals(stage, sceneManager.getPrimaryStage(), "PrimaryStage should be stored");
            } catch (Exception e) {
                fail("Constructor test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testGetPrimaryStage() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                SceneManager sceneManager = new SceneManager(stage);
                
                Stage result = sceneManager.getPrimaryStage();
                assertSame(stage, result, "getPrimaryStage should return the original stage");
            } catch (Exception e) {
                fail("getPrimaryStage test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetupStage() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                new SceneManager(stage);
                
                // Stage should be configured
                assertEquals("Tetris Game", stage.getTitle(), "Stage title should be set");
                assertFalse(stage.isResizable(), "Stage should not be resizable");
            } catch (Exception e) {
                fail("setupStage test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testScreenSizeConstantsSmall() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                double smallWidth = (double) getStaticField(SceneManager.class, "SMALL_WIDTH");
                double smallHeight = (double) getStaticField(SceneManager.class, "SMALL_HEIGHT");
                
                assertEquals(480.0, smallWidth, "SMALL_WIDTH should be 480");
                assertEquals(720.0, smallHeight, "SMALL_HEIGHT should be 720");
            } catch (Exception e) {
                fail("Screen size constants test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testScreenSizeConstantsMedium() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                double mediumWidth = (double) getStaticField(SceneManager.class, "MEDIUM_WIDTH");
                double mediumHeight = (double) getStaticField(SceneManager.class, "MEDIUM_HEIGHT");
                
                assertEquals(600.0, mediumWidth, "MEDIUM_WIDTH should be 600");
                assertEquals(900.0, mediumHeight, "MEDIUM_HEIGHT should be 900");
            } catch (Exception e) {
                fail("Screen size constants test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testScreenSizeConstantsLarge() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                double largeWidth = (double) getStaticField(SceneManager.class, "LARGE_WIDTH");
                double largeHeight = (double) getStaticField(SceneManager.class, "LARGE_HEIGHT");
                
                assertEquals(720.0, largeWidth, "LARGE_WIDTH should be 720");
                assertEquals(1080.0, largeHeight, "LARGE_HEIGHT should be 1080");
            } catch (Exception e) {
                fail("Screen size constants test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testMultipleSceneManagerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage1 = new Stage();
                Stage stage2 = new Stage();
                
                SceneManager sm1 = new SceneManager(stage1);
                SceneManager sm2 = new SceneManager(stage2);
                
                assertNotSame(sm1.getPrimaryStage(), sm2.getPrimaryStage(), 
                    "Different SceneManagers should have different stages");
            } catch (Exception e) {
                fail("Multiple SceneManager creation test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testStageNotNull() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                SceneManager sceneManager = new SceneManager(stage);
                
                assertNotNull(sceneManager.getPrimaryStage(), "Stage should not be null");
            } catch (Exception e) {
                fail("Stage not null test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testStageCloseRequestHandler() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                new SceneManager(stage);
                
                // Close request handler should be set (not null)
                assertNotNull(stage.getOnCloseRequest(), 
                    "Close request handler should be set");
            } catch (Exception e) {
                fail("Stage close request handler test failed: " + e.getMessage());
            }
        });
    }

    @ParameterizedTest
    @CsvSource({
        "작게,480,720",
        "중간,600,900",
        "크게,720,1080"
    })
    void testShowMainMenuAdjustsSceneSize(String screenSize, double expectedWidth, double expectedHeight) throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);
                SettingsManager settings = SettingsManager.getInstance();
                String previousSize = settings.getScreenSize();
                settings.setScreenSize(screenSize);
                try {
                    sceneManager.showMainMenu();

                    Scene scene = stage.getScene();
                    assertNotNull(scene, "Scene should be created for main menu");
                    assertEquals(expectedWidth, scene.getWidth(), 0.01, "Width should follow screen size setting");
                    assertEquals(expectedHeight, scene.getHeight(), 0.01, "Height should follow screen size setting");
                    assertTrue(sceneHasStylesheet(stage, "MainMenu.css"), "Main menu CSS should be applied");
                } finally {
                    settings.setScreenSize(previousSize);
                }
            } catch (Exception e) {
                fail("showMainMenu size test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowSettingsScreenAppliesCssAndLoadsControls() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showSettingsScreen();

                assertTrue(sceneHasStylesheet(stage, "SettingsScreen.css"), "Settings CSS should be applied");
                ComboBox<?> difficultyCombo = findNodeOfType(stage.getScene().getRoot(), ComboBox.class);
                assertNotNull(difficultyCombo, "Settings screen should render combo boxes");
            } catch (Exception e) {
                fail("showSettingsScreen test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowScoreBoardLoadsListViewWithCss() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showScoreBoard();

                assertTrue(sceneHasStylesheet(stage, "ScoreBoard.css"), "Score board CSS should be applied");
                ListView<?> scoreList = (ListView<?>) stage.getScene().lookup("#scoreListView");
                assertNotNull(scoreList, "Score list view should exist");
            } catch (Exception e) {
                fail("showScoreBoard test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowBattleModeSelectionUsesMenuStyles() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showBattleModeSelection();

                assertTrue(sceneHasStylesheet(stage, "MainMenu.css"), "Battle selection should reuse main menu CSS");
                Button normalButton = (Button) stage.getScene().lookup("#normalBattleButton");
                assertNotNull(normalButton, "Normal battle button should exist");
            } catch (Exception e) {
                fail("showBattleModeSelection test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowPVPModeSelectionDisplaysButtons() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showPVPModeSelection();

                assertTrue(sceneHasStylesheet(stage, "MainMenu.css"), "PVP mode selection should reuse main menu CSS");
                Button serverButton = (Button) stage.getScene().lookup("#serverButton");
                Button clientButton = (Button) stage.getScene().lookup("#clientButton");
                assertNotNull(serverButton, "Server button should exist");
                assertNotNull(clientButton, "Client button should exist");
            } catch (Exception e) {
                fail("showPVPModeSelection test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowPVPClientConnectionDisplaysFields() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showPVPClientConnection();

                assertTrue(sceneHasStylesheet(stage, "MainMenu.css"), "PVP client connection should reuse main menu CSS");
                TextField serverIpField = (TextField) stage.getScene().lookup("#serverIpField");
                assertNotNull(serverIpField, "Server IP input should exist");
            } catch (Exception e) {
                fail("showPVPClientConnection test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowGameScreenAppliesGameCssAndScoreLabel() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showGameScreen();

                assertTrue(sceneHasStylesheet(stage, "GameScreen.css"), "Game screen CSS should be applied");
                Label scoreLabel = (Label) stage.getScene().lookup("#scoreLabel");
                assertNotNull(scoreLabel, "Score label should exist on game screen");
            } catch (Exception e) {
                fail("showGameScreen test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowGameOverScreenDisplaysScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showGameOverScreen(4321);

                Label finalScoreLabel = (Label) stage.getScene().lookup("#finalScoreLabel");
                assertNotNull(finalScoreLabel, "Final score label should exist");
                assertEquals("최종 점수: 4321점", finalScoreLabel.getText(),
                    "Game over screen should display provided score");
            } catch (Exception e) {
                fail("showGameOverScreen test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowPVPNetworkSelectionUpdatesModeLabel() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showPVPNetworkSelection("TIME_LIMIT");

                Label modeLabel = (Label) stage.getScene().lookup("#gameModeLabel");
                assertNotNull(modeLabel, "Game mode label should exist");
                assertTrue(modeLabel.getText().contains("시간제한"),
                    "Network selection screen should describe selected game mode");
            } catch (Exception e) {
                fail("showPVPNetworkSelection test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowBattleGameScreenAgainstComputerAddsStylesheet() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showBattleGameScreenAgainstComputer("NORMAL");

                boolean hasBattleCss = stage.getScene().getStylesheets()
                    .stream()
                    .anyMatch(path -> path.contains("BattleGameScreen.css"));
                assertTrue(hasBattleCss, "Battle CSS should be applied for battle screen");
            } catch (Exception e) {
                fail("showBattleGameScreenAgainstComputer test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowPVPLobbySceneConfiguresServerLabels() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                sceneManager.showPVPLobby(null, null, true);

                Label serverStatus = (Label) stage.getScene().lookup("#serverStatusLabel");
                Label clientStatus = (Label) stage.getScene().lookup("#clientStatusLabel");
                assertNotNull(serverStatus, "Server status label should exist");
                assertNotNull(clientStatus, "Client status label should exist");
                assertTrue(serverStatus.getText().contains("서버"),
                    "Server label should reference server state");
                assertTrue(clientStatus.getText().contains("클라이언트"),
                    "Client label should reference client state");
            } catch (Exception e) {
                fail("showPVPLobby scene test failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testShowPVPServerWaitingSetsIpLabel() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                Stage stage = new Stage();
                this.stage = stage;
                SceneManager sceneManager = new SceneManager(stage);

                tetris.network.GameServer mockServer = org.mockito.Mockito.mock(tetris.network.GameServer.class);

                sceneManager.showPVPServerWaiting(mockServer, "10.0.0.1");

                Label ipLabel = (Label) stage.getScene().lookup("#serverIpLabel");
                assertNotNull(ipLabel, "Server IP label should exist");
                assertEquals("10.0.0.1", ipLabel.getText(),
                    "Waiting screen should display provided server IP");
            } catch (Exception e) {
                fail("showPVPServerWaiting test failed: " + e.getMessage());
            }
        });
    }

    private boolean sceneHasStylesheet(Stage stage, String cssFileName) {
        return stage.getScene() != null
            && stage.getScene().getStylesheets().stream()
                .anyMatch(path -> path.contains(cssFileName));
    }

    private <T> T findNodeOfType(Node root, Class<T> type) {
        if (type.isInstance(root)) {
            return type.cast(root);
        }
        if (root instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            T found = findNodeOfType(scrollPane.getContent(), type);
            if (found != null) {
                return found;
            }
        }
        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                T found = findNodeOfType(child, type);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
