package tetris.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

@DisplayName("GameStateData 테스트")
public class GameStateDataTest {

    private int[][] testBoard;
    private int[][] testItemBoard;
    private int[][] testCurrentPieceShape;
    private int[][] testNextPieceShape;

    @BeforeEach
    void setUp() {
        testBoard = new int[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0}
        };

        testItemBoard = new int[][] {
            {0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };

        testCurrentPieceShape = new int[][] {
            {1, 1},
            {1, 1}
        };

        testNextPieceShape = new int[][] {
            {1, 1, 1, 1}
        };
    }

    @Test
    @DisplayName("GameStateData 생성 및 모든 필드 확인 테스트")
    void testGameStateDataCreation() {
        // Given
        int score = 1000;
        int level = 5;
        int linesCleared = 10;
        boolean isGameOver = false;
        int currentPieceX = 4;
        int currentPieceY = 2;
        int currentPieceType = 1;
        int nextPieceType = 2;
        int incomingAttackLines = 3;

        // When
        GameStateData gameState = new GameStateData(
            testBoard,
            testItemBoard,
            score,
            level,
            linesCleared,
            isGameOver,
            testCurrentPieceShape,
            currentPieceX,
            currentPieceY,
            currentPieceType,
            testNextPieceShape,
            nextPieceType,
            incomingAttackLines
        );

        // Then
        assertNotNull(gameState);
        assertArrayEquals(testBoard, gameState.getBoard());
        assertArrayEquals(testItemBoard, gameState.getItemBoard());
        assertEquals(score, gameState.getScore());
        assertEquals(level, gameState.getLevel());
        assertEquals(linesCleared, gameState.getLinesCleared());
        assertEquals(isGameOver, gameState.isGameOver());
        assertArrayEquals(testCurrentPieceShape, gameState.getCurrentPieceShape());
        assertEquals(currentPieceX, gameState.getCurrentPieceX());
        assertEquals(currentPieceY, gameState.getCurrentPieceY());
        assertEquals(currentPieceType, gameState.getCurrentPieceType());
        assertArrayEquals(testNextPieceShape, gameState.getNextPieceShape());
        assertEquals(nextPieceType, gameState.getNextPieceType());
        assertEquals(incomingAttackLines, gameState.getIncomingAttackLines());
    }

    @Test
    @DisplayName("GameStateData 게임 오버 상태 테스트")
    void testGameStateDataGameOver() {
        // Given & When
        GameStateData gameOverState = new GameStateData(
            testBoard, testItemBoard, 500, 3, 5, true,
            testCurrentPieceShape, 0, 0, 1, testNextPieceShape, 2, 0
        );

        GameStateData activeState = new GameStateData(
            testBoard, testItemBoard, 500, 3, 5, false,
            testCurrentPieceShape, 0, 0, 1, testNextPieceShape, 2, 0
        );

        // Then
        assertTrue(gameOverState.isGameOver(), "게임 오버 상태여야 합니다");
        assertFalse(activeState.isGameOver(), "게임이 진행 중이어야 합니다");
    }

    @Test
    @DisplayName("GameStateData 공격 라인 수 테스트")
    void testGameStateDataIncomingAttackLines() {
        // Test different attack line counts
        int[] attackLineCounts = {0, 1, 5, 10};

        for (int attackLines : attackLineCounts) {
            GameStateData gameState = new GameStateData(
                testBoard, testItemBoard, 0, 1, 0, false,
                testCurrentPieceShape, 0, 0, 1, testNextPieceShape, 2, attackLines
            );

            assertEquals(attackLines, gameState.getIncomingAttackLines(),
                "공격 라인 수가 " + attackLines + "이어야 합니다");
        }
    }

    @Test
    @DisplayName("GameStateData 직렬화 테스트")
    void testGameStateDataSerialization() throws IOException, ClassNotFoundException {
        // Given
        GameStateData originalState = new GameStateData(
            testBoard,
            testItemBoard,
            1234,
            7,
            15,
            false,
            testCurrentPieceShape,
            5,
            10,
            3,
            testNextPieceShape,
            4,
            2
        );

        // When - Serialize
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(originalState);
        out.flush();

        // When - Deserialize
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        GameStateData deserializedState = (GameStateData) in.readObject();

        // Then
        assertNotNull(deserializedState);
        assertArrayEquals(originalState.getBoard(), deserializedState.getBoard());
        assertArrayEquals(originalState.getItemBoard(), deserializedState.getItemBoard());
        assertEquals(originalState.getScore(), deserializedState.getScore());
        assertEquals(originalState.getLevel(), deserializedState.getLevel());
        assertEquals(originalState.getLinesCleared(), deserializedState.getLinesCleared());
        assertEquals(originalState.isGameOver(), deserializedState.isGameOver());
        assertArrayEquals(originalState.getCurrentPieceShape(), deserializedState.getCurrentPieceShape());
        assertEquals(originalState.getCurrentPieceX(), deserializedState.getCurrentPieceX());
        assertEquals(originalState.getCurrentPieceY(), deserializedState.getCurrentPieceY());
        assertEquals(originalState.getCurrentPieceType(), deserializedState.getCurrentPieceType());
        assertArrayEquals(originalState.getNextPieceShape(), deserializedState.getNextPieceShape());
        assertEquals(originalState.getNextPieceType(), deserializedState.getNextPieceType());
        assertEquals(originalState.getIncomingAttackLines(), deserializedState.getIncomingAttackLines());

        // Cleanup
        out.close();
        in.close();
    }

    @Test
    @DisplayName("GameStateData 보드 데이터 접근 테스트")
    void testGameStateDataBoardAccess() {
        // Given
        int[][] localBoard = new int[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {1, 1, 0, 0, 0}
        };

        GameStateData gameState = new GameStateData(
            localBoard, testItemBoard, 100, 1, 0, false,
            testCurrentPieceShape, 0, 0, 1, testNextPieceShape, 2, 0
        );

        // Then - GameStateData should return the board
        assertNotNull(gameState.getBoard(), "보드 데이터가 null이 아니어야 합니다");
        assertEquals(3, gameState.getBoard().length, "보드 행 수가 일치해야 합니다");
        assertEquals(5, gameState.getBoard()[0].length, "보드 열 수가 일치해야 합니다");
        assertEquals(1, gameState.getBoard()[2][0], "보드 데이터가 올바르게 저장되어야 합니다");
    }

    @Test
    @DisplayName("GameStateData 레벨과 점수 증가 테스트")
    void testGameStateDataLevelAndScoreProgression() {
        // Test score and level progression
        GameStateData state1 = new GameStateData(
            testBoard, testItemBoard, 0, 1, 0, false,
            testCurrentPieceShape, 0, 0, 1, testNextPieceShape, 2, 0
        );

        GameStateData state2 = new GameStateData(
            testBoard, testItemBoard, 1000, 2, 10, false,
            testCurrentPieceShape, 0, 0, 1, testNextPieceShape, 2, 0
        );

        GameStateData state3 = new GameStateData(
            testBoard, testItemBoard, 5000, 5, 50, false,
            testCurrentPieceShape, 0, 0, 1, testNextPieceShape, 2, 0
        );

        assertTrue(state2.getScore() > state1.getScore());
        assertTrue(state3.getScore() > state2.getScore());
        assertTrue(state2.getLevel() > state1.getLevel());
        assertTrue(state3.getLevel() > state2.getLevel());
        assertTrue(state2.getLinesCleared() > state1.getLinesCleared());
        assertTrue(state3.getLinesCleared() > state2.getLinesCleared());
    }

    @Test
    @DisplayName("GameStateData 현재 블록 위치 테스트")
    void testGameStateDataCurrentPiecePosition() {
        // Test different piece positions
        int[][] positions = {
            {0, 0},
            {5, 10},
            {-1, -1},
            {100, 200}
        };

        for (int[] pos : positions) {
            GameStateData gameState = new GameStateData(
                testBoard, testItemBoard, 0, 1, 0, false,
                testCurrentPieceShape, pos[0], pos[1], 1,
                testNextPieceShape, 2, 0
            );

            assertEquals(pos[0], gameState.getCurrentPieceX());
            assertEquals(pos[1], gameState.getCurrentPieceY());
        }
    }

    @Test
    @DisplayName("GameStateData 블록 타입 테스트")
    void testGameStateDataPieceTypes() {
        // Test different piece types (typically 0-6 for Tetris pieces)
        for (int pieceType = 0; pieceType < 7; pieceType++) {
            GameStateData gameState = new GameStateData(
                testBoard, testItemBoard, 0, 1, 0, false,
                testCurrentPieceShape, 0, 0, pieceType,
                testNextPieceShape, (pieceType + 1) % 7, 0
            );

            assertEquals(pieceType, gameState.getCurrentPieceType());
            assertEquals((pieceType + 1) % 7, gameState.getNextPieceType());
        }
    }
}
