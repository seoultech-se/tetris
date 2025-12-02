package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import tetris.data.ScoreManager;
import tetris.ui.SceneManager;
import tetris.ui.SettingsManager;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameOverController.
 * Tests game over screen initialization and score display.
 */
class GameOverControllerTest extends JavaFXTestBase {

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load GameOverScreen.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
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
    void testSetFinalScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
                
                // Test setting final score
                int testScore = 5000;
                controller.setFinalScore(testScore);
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set final score: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                assertNotNull(loader.getLocation(), "GameOverScreen.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                
                GameOverController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testMultipleScoreValues() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                new Scene(loader.load(), 600, 900);
                
                GameOverController controller = loader.getController();
                
                // Test various score values
                int[] testScores = {0, 100, 1000, 10000, 99999};
                for (int score : testScores) {
                    controller.setFinalScore(score);
                    // Just verify no exception is thrown
                }
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to handle multiple scores: " + e.getMessage());
            }
        });
    }
    
    // ===== 추가 점수 테스트 =====
    
    @Test
    void testZeroScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();
                
                controller.setFinalScore(0);
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Zero score test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testMaxScore() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();
                
                controller.setFinalScore(Integer.MAX_VALUE);
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Max score test failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testRepeatedScoreSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();
                
                // 여러 번 점수 설정
                for (int i = 0; i < 10; i++) {
                    controller.setFinalScore(i * 1000);
                }
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Repeated score setting failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testDifferentScreenSizes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 다양한 화면 크기에서 로드
                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                new Scene(loader1.load(), 480, 720);
                assertNotNull(loader1.getController());
                
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                new Scene(loader2.load(), 600, 900);
                assertNotNull(loader2.getController());
                
                FXMLLoader loader3 = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                new Scene(loader3.load(), 720, 1080);
                assertNotNull(loader3.getController());
            } catch (Exception e) {
                fail("Screen size tests failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testFinalScoreUpdatesLabelsAndInputs() throws Exception {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            String originalMode = settings.getGameMode();
            String originalDifficulty = settings.getDifficulty();
            ScoreManager scoreManager = ScoreManager.getInstance();
            try {
                settings.setGameMode("NORMAL");
                settings.setDifficulty("Normal");
                scoreManager.clearScores("NORMAL");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();

                controller.setFinalScore(1234);

                Label finalScoreLabel = getNode(loader, "finalScoreLabel");
                Label scoreboardTitleLabel = getNode(loader, "scoreboardTitleLabel");
                TextField playerNameField = getNode(loader, "playerNameField");
                Button saveButton = getNode(loader, "saveScoreButton");

                assertEquals("최종 점수: 1234점", finalScoreLabel.getText());
                assertTrue(scoreboardTitleLabel.getText().contains("일반 모드"));
                assertTrue(playerNameField.isVisible());
                assertFalse(saveButton.isDisable());
            } catch (Exception e) {
                fail("Final score UI update failed: " + e.getMessage());
            } finally {
                settings.setGameMode(originalMode);
                settings.setDifficulty(originalDifficulty);
                scoreManager.clearScores("NORMAL");
            }
        });
    }

    @Test
    void testSaveInputsHiddenWhenNotTopTen() throws Exception {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            String originalMode = settings.getGameMode();
            String originalDifficulty = settings.getDifficulty();
            ScoreManager scoreManager = ScoreManager.getInstance();
            try {
                settings.setGameMode("NORMAL");
                settings.setDifficulty("Normal");
                scoreManager.clearScores("NORMAL");
                for (int i = 0; i < 10; i++) {
                    scoreManager.addScore("P" + i, 10000 - i * 100, "Normal", "NORMAL");
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();

                controller.setFinalScore(100);

                TextField playerNameField = getNode(loader, "playerNameField");
                Button saveButton = getNode(loader, "saveScoreButton");

                assertFalse(playerNameField.isVisible(), "Name field hidden when not qualified");
                assertTrue(saveButton.isDisabled());
            } catch (Exception e) {
                fail("Save inputs visibility test failed: " + e.getMessage());
            } finally {
                settings.setGameMode(originalMode);
                settings.setDifficulty(originalDifficulty);
                scoreManager.clearScores("NORMAL");
            }
        });
    }

    @Test
    void testBackToMenuCallsSceneManager() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOverScreen.fxml"));
                loader.load();
                GameOverController controller = loader.getController();

                TestSceneManager sceneManager = new TestSceneManager(new Stage());
                controller.setSceneManager(sceneManager);

                var backMethod = GameOverController.class.getDeclaredMethod("onBackToMenu");
                backMethod.setAccessible(true);
                backMethod.invoke(controller);

                assertTrue(sceneManager.mainMenuShown);
            } catch (Exception e) {
                fail("Back to menu action failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testCheckIfCanSaveScoreHonorsTopTenThreshold() {
        SettingsManager settings = SettingsManager.getInstance();
        ScoreManager scoreManager = ScoreManager.getInstance();
        String originalMode = settings.getGameMode();
        String originalDifficulty = settings.getDifficulty();
        try {
            settings.setGameMode("NORMAL");
            settings.setDifficulty("Normal");
            scoreManager.clearScores("NORMAL");
            for (int i = 0; i < 10; i++) {
                scoreManager.addScore("P" + i, 1000 - i * 50, "Normal", "NORMAL");
            }

            GameOverController controller = new GameOverController();
            var checkMethod = GameOverController.class.getDeclaredMethod("checkIfCanSaveScore", int.class, String.class, String.class);
            checkMethod.setAccessible(true);

            boolean canSaveLow = (boolean) checkMethod.invoke(controller, 100, "NORMAL", "Normal");
            boolean canSaveHigh = (boolean) checkMethod.invoke(controller, 1200, "NORMAL", "Normal");

            assertFalse(canSaveLow, "Score below top ten threshold should not be saved");
            assertTrue(canSaveHigh, "Higher score should be eligible for saving");
        } catch (Exception e) {
            fail("Top ten threshold test failed: " + e.getMessage());
        } finally {
            settings.setGameMode(originalMode);
            settings.setDifficulty(originalDifficulty);
            scoreManager.clearScores("NORMAL");
        }
    }

    @Test
    void testLoadScoresShowsPlaceholderWhenEmpty() {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            ScoreManager scoreManager = ScoreManager.getInstance();
            String originalMode = settings.getGameMode();
            String originalDifficulty = settings.getDifficulty();
            try {
                settings.setGameMode("NORMAL");
                settings.setDifficulty("Normal");
                scoreManager.clearScores("NORMAL");

                GameOverController controller = new GameOverController();
                setPrivateField(controller, "scoreListView", new ListView<>());

                controller.setFinalScore(500);

                ListView<String> listView = getPrivateListView(controller);
                assertEquals(1, listView.getItems().size());
                assertEquals("아직 등록된 점수가 없습니다.", listView.getItems().get(0));
            } catch (Exception e) {
                fail("Empty scores placeholder test failed: " + e.getMessage());
            } finally {
                settings.setGameMode(originalMode);
                settings.setDifficulty(originalDifficulty);
                scoreManager.clearScores("NORMAL");
            }
        });
    }

    @Test
    void testLoadScoresDisplaysExistingEntries() {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            ScoreManager scoreManager = ScoreManager.getInstance();
            String originalMode = settings.getGameMode();
            String originalDifficulty = settings.getDifficulty();
            try {
                settings.setGameMode("NORMAL");
                settings.setDifficulty("Normal");
                scoreManager.clearScores("NORMAL");
                scoreManager.addScore("AAA", 1500, "Normal", "NORMAL");
                scoreManager.addScore("BBB", 900, "Normal", "NORMAL");

                GameOverController controller = new GameOverController();
                setPrivateField(controller, "scoreListView", new ListView<>());

                controller.setFinalScore(400);

                ListView<String> listView = getPrivateListView(controller);
                assertEquals(2, listView.getItems().size());
                assertTrue(listView.getItems().get(0).contains("AAA"));
                assertTrue(listView.getItems().get(1).contains("BBB"));
            } catch (Exception e) {
                fail("Existing scores display test failed: " + e.getMessage());
            } finally {
                settings.setGameMode(originalMode);
                settings.setDifficulty(originalDifficulty);
                scoreManager.clearScores("NORMAL");
            }
        });
    }

    @Test
    void testSetFinalScoreResetsStateAndUpdatesNormalModeLabels() {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            ScoreManager scoreManager = ScoreManager.getInstance();
            String originalMode = settings.getGameMode();
            String originalDifficulty = settings.getDifficulty();
            try {
                settings.setGameMode("NORMAL");
                settings.setDifficulty("Hard");
                scoreManager.clearScores("NORMAL");

                GameOverController controller = new GameOverController();
                Label finalScoreLabel = new Label();
                Label scoreboardLabel = new Label();
                TextField playerNameField = new TextField("TEMP");
                Button saveButton = new Button();
                saveButton.setDisable(true);
                ListView<String> listView = new ListView<>();

                setPrivateField(controller, "finalScoreLabel", finalScoreLabel);
                setPrivateField(controller, "scoreboardTitleLabel", scoreboardLabel);
                setPrivateField(controller, "playerNameField", playerNameField);
                setPrivateField(controller, "saveScoreButton", saveButton);
                setPrivateField(controller, "scoreListView", listView);
                setPrivateField(controller, "highlightedIndex", 5);
                setPrivateField(controller, "scoreSaved", true);

                controller.setFinalScore(777);

                assertEquals("최종 점수: 777점", finalScoreLabel.getText());
                assertEquals("일반 모드 - Hard 스코어보드", scoreboardLabel.getText());
                assertNull(getPrivateFieldValue(controller, "highlightedIndex", Integer.class));
                assertFalse(getPrivateFieldValue(controller, "scoreSaved", Boolean.class));
                assertTrue(playerNameField.isVisible());
                assertFalse(playerNameField.isDisable());
                assertTrue(playerNameField.getText().isEmpty());
                assertTrue(saveButton.isVisible());
                assertFalse(saveButton.isDisable());
                assertEquals("점수 저장", saveButton.getText());
                assertEquals(1, listView.getItems().size());
                assertEquals("아직 등록된 점수가 없습니다.", listView.getItems().get(0));
            } catch (Exception e) {
                fail("Normal mode state reset test failed: " + e.getMessage());
            } finally {
                settings.setGameMode(originalMode);
                settings.setDifficulty(originalDifficulty);
                scoreManager.clearScores("NORMAL");
            }
        });
    }

    @Test
    void testSetFinalScoreUsesItemModeTitleAndClearsInput() {
        runOnFxThreadAndWait(() -> {
            SettingsManager settings = SettingsManager.getInstance();
            ScoreManager scoreManager = ScoreManager.getInstance();
            String originalMode = settings.getGameMode();
            String originalDifficulty = settings.getDifficulty();
            try {
                settings.setGameMode("ITEM");
                settings.setDifficulty("Normal");
                scoreManager.clearScores("ITEM");

                GameOverController controller = new GameOverController();
                Label finalScoreLabel = new Label();
                Label scoreboardLabel = new Label();
                TextField playerNameField = new TextField("Player");
                Button saveButton = new Button("Before");
                saveButton.setDisable(true);
                ListView<String> listView = new ListView<>();

                setPrivateField(controller, "finalScoreLabel", finalScoreLabel);
                setPrivateField(controller, "scoreboardTitleLabel", scoreboardLabel);
                setPrivateField(controller, "playerNameField", playerNameField);
                setPrivateField(controller, "saveScoreButton", saveButton);
                setPrivateField(controller, "scoreListView", listView);

                controller.setFinalScore(321);

                assertEquals("최종 점수: 321점", finalScoreLabel.getText());
                assertEquals("아이템 모드 스코어보드", scoreboardLabel.getText());
                assertTrue(playerNameField.isVisible());
                assertTrue(playerNameField.isManaged());
                assertFalse(playerNameField.isDisable());
                assertTrue(playerNameField.getText().isEmpty());
                assertTrue(saveButton.isVisible());
                assertTrue(saveButton.isManaged());
                assertFalse(saveButton.isDisable());
                assertEquals("점수 저장", saveButton.getText());
                assertEquals(1, listView.getItems().size());
                assertEquals("아직 등록된 점수가 없습니다.", listView.getItems().get(0));
            } catch (Exception e) {
                fail("Item mode title test failed: " + e.getMessage());
            } finally {
                settings.setGameMode(originalMode);
                settings.setDifficulty(originalDifficulty);
                scoreManager.clearScores("ITEM");
            }
        });
    }

    @Test
    void testCheckIfCanSaveScoreHandlesItemModeThreshold() {
        SettingsManager settings = SettingsManager.getInstance();
        ScoreManager scoreManager = ScoreManager.getInstance();
        String originalMode = settings.getGameMode();
        String originalDifficulty = settings.getDifficulty();
        try {
            settings.setGameMode("ITEM");
            settings.setDifficulty("Normal");
            scoreManager.clearScores("ITEM");
            for (int i = 0; i < 10; i++) {
                scoreManager.addScore("I" + i, 1000 - i * 10, "Normal", "ITEM");
            }

            GameOverController controller = new GameOverController();
            var checkMethod = GameOverController.class.getDeclaredMethod("checkIfCanSaveScore", int.class, String.class, String.class);
            checkMethod.setAccessible(true);

            boolean canSaveLow = (boolean) checkMethod.invoke(controller, 100, "ITEM", "Normal");
            boolean canSaveHigh = (boolean) checkMethod.invoke(controller, 995, "ITEM", "Normal");

            assertFalse(canSaveLow, "Low score should not qualify in full item mode list");
            assertTrue(canSaveHigh, "High score should qualify in item mode list");
        } catch (Exception e) {
            fail("Item mode threshold test failed: " + e.getMessage());
        } finally {
            settings.setGameMode(originalMode);
            settings.setDifficulty(originalDifficulty);
            scoreManager.clearScores("ITEM");
        }
    }

    private <T> T getNode(FXMLLoader loader, String fxId) {
        Object node = loader.getNamespace().get(fxId);
        assertNotNull(node, fxId + " should exist");
        @SuppressWarnings("unchecked")
        T casted = (T) node;
        return casted;
    }

    private void setPrivateField(GameOverController controller, String fieldName, Object value) {
        try {
            var field = GameOverController.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(controller, value);
        } catch (ReflectiveOperationException e) {
            fail("Failed to set field " + fieldName + ": " + e.getMessage());
        }
    }

    private ListView<String> getPrivateListView(GameOverController controller) {
        try {
            var field = GameOverController.class.getDeclaredField("scoreListView");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ListView<String> listView = (ListView<String>) field.get(controller);
            return listView;
        } catch (ReflectiveOperationException e) {
            fail("Failed to access scoreListView: " + e.getMessage());
            return null;
        }
    }

    private <T> T getPrivateFieldValue(GameOverController controller, String fieldName, Class<T> type) {
        try {
            var field = GameOverController.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(controller);
            return type.cast(value);
        } catch (ReflectiveOperationException e) {
            fail("Failed to access field " + fieldName + ": " + e.getMessage());
            return null;
        }
    }

    private static class TestSceneManager extends SceneManager {
        private boolean mainMenuShown;

        TestSceneManager(Stage stage) {
            super(stage);
        }

        @Override
        public void showMainMenu() {
            mainMenuShown = true;
        }
    }
}
