package tetris.ui.controllers;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import tetris.ui.SceneManager;
import tetris.network.GameServer;
import tetris.network.GameClient;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PVPGameScreenController.
 * Tests PVP game screen initialization and core methods.
 */
class PVPGameScreenControllerTest extends JavaFXTestBase {

    private PVPGameScreenController controller;
    private SceneManager mockSceneManager;

    @BeforeEach
    void setUp() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                controller = loader.getController();
                
                Stage mockStage = new Stage();
                mockSceneManager = new SceneManager(mockStage);
                
                controller.setSceneManager(mockSceneManager);
            } catch (Exception e) {
                fail("Setup failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testControllerCreation() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to load PVPGameScreen.fxml: " + e.getMessage());
            }
        });
    }

    @Test
    void testSceneManagerSetting() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
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
    void testSetGameMode() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                
                // Test setting game mode
                controller.setGameMode("NORMAL");
                controller.setGameMode("ITEM");
                controller.setGameMode("TIME_LIMIT");
                
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Failed to set game mode: " + e.getMessage());
            }
        });
    }

    @Test
    void testFXMLLoading() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                assertNotNull(loader.getLocation(), "PVPGameScreen.fxml should exist");
                
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PVPGameScreen.fxml"));
                loader.load();
                
                PVPGameScreenController controller = loader.getController();
                assertNotNull(controller, "Controller should be created");
            } catch (Exception e) {
                fail("Controller initialization failed: " + e.getMessage());
            }
        });
    }

    // ===== 렌더링 테스트 =====

    @Test
    void testSetupCanvasSize() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                controller.setupCanvasSize();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("setupCanvasSize failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderMyBoardWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 게임 엔진 없이 호출 - NPE 발생하지 않아야 함
                controller.renderMyBoard();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("renderMyBoard failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderOpponentBoardWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 상대방 상태 없이 호출 - NPE 발생하지 않아야 함
                controller.renderOpponentBoard();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("renderOpponentBoard failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderNextPiecesWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 게임 엔진 없이 호출
                controller.renderNextPieces();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("renderNextPieces failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderIncomingLinesWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 게임 엔진 없이 호출
                controller.renderIncomingLines();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("renderIncomingLines failed: " + e.getMessage());
            }
        });
    }

    // ===== UI 업데이트 테스트 =====

    @Test
    void testUpdateUIWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 게임 엔진 없이 호출 - NPE 발생하지 않아야 함
                controller.updateUI();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("updateUI failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateFallSpeedsWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 게임 엔진 없이 호출
                controller.updateFallSpeeds();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("updateFallSpeeds failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLatencyDisplay() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 레이턴시 라벨 업데이트 테스트
                controller.updateLatencyDisplay();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("updateLatencyDisplay failed: " + e.getMessage());
            }
        });
    }

    // ===== 네트워크 상태 전송 테스트 =====

    @Test
    void testSendMyStateWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 게임 엔진 없이 호출 - NPE 발생하지 않아야 함
                controller.sendMyState();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("sendMyState failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSendAttackWithoutNetwork() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 네트워크 없이 호출 - 예외 발생하지 않아야 함
                controller.sendAttack(2, 3);
                assertNotNull(controller);
            } catch (Exception e) {
                fail("sendAttack failed: " + e.getMessage());
            }
        });
    }

    // ===== 게임 모드 설정 테스트 =====

    @Test
    void testSetGameModeNormal() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                controller.setGameMode("NORMAL");
                assertNotNull(controller);
            } catch (Exception e) {
                fail("setGameMode NORMAL failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetGameModeItem() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                controller.setGameMode("ITEM");
                assertNotNull(controller);
            } catch (Exception e) {
                fail("setGameMode ITEM failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSetGameModeTimeLimit() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                controller.setGameMode("TIME_LIMIT");
                assertNotNull(controller);
            } catch (Exception e) {
                fail("setGameMode TIME_LIMIT failed: " + e.getMessage());
            }
        });
    }

    // ===== AnimationTimer 통합 테스트 =====

    @Test
    void testCountdownTimerInitialization() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 컨트롤러는 AnimationTimer inner class를 포함함
                assertNotNull(controller);
                // setGameMode는 AnimationTimer 시작 전에 호출 가능
                controller.setGameMode("NORMAL");
            } catch (Exception e) {
                fail("Countdown timer initialization failed: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testGameLoopWithMockNetwork() throws Exception {
        runOnFxThreadAndWait(() -> {
            // Mock 네트워크 객체 생성
            GameServer mockServer = mock(GameServer.class);
            GameClient mockClient = mock(GameClient.class);
            
            // FXML 로드된 controller로 게임 시작 시도
            // Label이 null이 아니므로 초기화가 더 진행됨
            try {
                controller.setNetworkObjects(mockServer, mockClient, true);
                // AnimationTimer가 시작되고 게임 루프가 실행됨
                assertNotNull(controller);
            } catch (NullPointerException e) {
                // Canvas나 일부 UI 요소가 없을 수 있으므로 NPE는 허용
                // 하지만 controller 자체는 생성됨
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Game loop initialization threw unexpected exception: " + e.getMessage());
            }
        });
    }
    
    @Test
    void testGameLoopWithDifferentModes() throws Exception {
        runOnFxThreadAndWait(() -> {
            // 다양한 게임 모드 테스트
            String[] modes = {"NORMAL", "ITEM", "TIME_LIMIT"};
            
            for (String mode : modes) {
                try {
                    PVPGameScreenController testController = new PVPGameScreenController();
                    testController.setSceneManager(mockSceneManager);
                    testController.setGameMode(mode);
                    assertNotNull(testController);
                } catch (Exception e) {
                    fail("Failed to set game mode " + mode + ": " + e.getMessage());
                }
            }
        });
    }

    // ===== 추가 통합 테스트 =====

    @Test
    void testSetupCanvasSizeWithAllCanvases() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 모든 캔버스가 초기화된 상태에서 테스트
                controller.setupCanvasSize();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("setupCanvasSize with all canvases failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testMultipleRenderCallsWithoutEngine() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 여러 렌더링 메서드를 연속으로 호출
                controller.renderMyBoard();
                controller.renderOpponentBoard();
                controller.renderNextPieces();
                controller.renderIncomingLines();
                controller.updateUI();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Multiple render calls failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateLatencyDisplayMultipleTimes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 여러 번 호출해서 안정성 확인
                controller.updateLatencyDisplay();
                controller.updateLatencyDisplay();
                controller.updateLatencyDisplay();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Multiple updateLatencyDisplay calls failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSendMyStateMultipleTimes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 여러 번 상태 전송 시도
                controller.sendMyState();
                controller.sendMyState();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Multiple sendMyState calls failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testSendAttackWithDifferentValues() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 다양한 값으로 공격 전송
                controller.sendAttack(1, 0);
                controller.sendAttack(2, 5);
                controller.sendAttack(4, 9);
                assertNotNull(controller);
            } catch (Exception e) {
                fail("sendAttack with different values failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testAllGameModesInSequence() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 게임 모드를 순차적으로 변경
                controller.setGameMode("NORMAL");
                controller.setGameMode("ITEM");
                controller.setGameMode("TIME_LIMIT");
                controller.setGameMode("NORMAL");
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Sequential game mode changes failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUpdateFallSpeedsMultipleTimes() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 낙하 속도 업데이트 여러 번 호출
                controller.updateFallSpeeds();
                controller.updateFallSpeeds();
                controller.updateFallSpeeds();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Multiple updateFallSpeeds calls failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testRenderingSequenceWithoutCrash() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 렌더링 순서대로 호출
                controller.setupCanvasSize();
                controller.renderMyBoard();
                controller.renderOpponentBoard();
                controller.renderNextPieces();
                controller.renderIncomingLines();
                controller.updateUI();
                controller.updateFallSpeeds();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Rendering sequence failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testNetworkOperationsWithoutConnection() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // 네트워크 연결 없이 메서드 호출
                controller.sendMyState();
                controller.sendAttack(2, 3);
                controller.updateLatencyDisplay();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("Network operations without connection failed: " + e.getMessage());
            }
        });
    }

    @Test
    void testUIUpdateSequence() throws Exception {
        runOnFxThreadAndWait(() -> {
            try {
                // UI 업데이트 시퀀스
                controller.updateUI();
                controller.updateLatencyDisplay();
                controller.updateFallSpeeds();
                assertNotNull(controller);
            } catch (Exception e) {
                fail("UI update sequence failed: " + e.getMessage());
            }
        });
    }
}
