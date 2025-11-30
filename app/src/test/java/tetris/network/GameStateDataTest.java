package tetris.network;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateDataTest {

    @Test
    void testGameStateDataCreation() {
        int[][] board = new int[20][10];
        int[][] itemBoard = new int[20][10];
        int[][] currentPieceShape = {{1, 1}, {1, 1}};
        int[][] nextPieceShape = {{0, 1, 0}, {1, 1, 1}};
        List<Integer> emptyCols = new ArrayList<>();
        emptyCols.add(5);

        GameStateData data = new GameStateData(
            board, itemBoard, 1000, 5, 20, false,
            currentPieceShape, 5, 10, 1,
            nextPieceShape, 2, 3, emptyCols
        );

        assertNotNull(data);
        assertEquals(1000, data.getScore());
        assertEquals(5, data.getLevel());
        assertEquals(20, data.getLinesCleared());
        assertFalse(data.isGameOver());
    }

    @Test
    void testGetBoard() {
        int[][] board = new int[20][10];
        board[0][0] = 5;
        
        GameStateData data = new GameStateData(
            board, new int[20][10], 0, 1, 0, false,
            new int[2][2], 0, 0, 1,
            new int[2][2], 1, 0, new ArrayList<>()
        );

        int[][] retrievedBoard = data.getBoard();
        assertNotNull(retrievedBoard);
        assertEquals(5, retrievedBoard[0][0]);
    }

    @Test
    void testGetItemBoard() {
        int[][] itemBoard = new int[20][10];
        itemBoard[5][5] = 3;
        
        GameStateData data = new GameStateData(
            new int[20][10], itemBoard, 0, 1, 0, false,
            new int[2][2], 0, 0, 1,
            new int[2][2], 1, 0, new ArrayList<>()
        );

        int[][] retrievedItemBoard = data.getItemBoard();
        assertNotNull(retrievedItemBoard);
        assertEquals(3, retrievedItemBoard[5][5]);
    }

    @Test
    void testScoreLevelAndLines() {
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 5000, 10, 50, false,
            new int[2][2], 5, 10, 1,
            new int[2][2], 2, 0, new ArrayList<>()
        );

        assertEquals(5000, data.getScore());
        assertEquals(10, data.getLevel());
        assertEquals(50, data.getLinesCleared());
    }

    @Test
    void testGameOverFlag() {
        GameStateData gameOverData = new GameStateData(
            new int[20][10], new int[20][10], 1000, 5, 20, true,
            new int[2][2], 5, 10, 1,
            new int[2][2], 2, 0, new ArrayList<>()
        );

        assertTrue(gameOverData.isGameOver());

        GameStateData runningData = new GameStateData(
            new int[20][10], new int[20][10], 1000, 5, 20, false,
            new int[2][2], 5, 10, 1,
            new int[2][2], 2, 0, new ArrayList<>()
        );

        assertFalse(runningData.isGameOver());
    }

    @Test
    void testCurrentPieceData() {
        int[][] currentPieceShape = {{1, 1, 1, 1}};
        
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 0, 1, 0, false,
            currentPieceShape, 3, 5, 1,
            new int[2][2], 2, 0, new ArrayList<>()
        );

        assertArrayEquals(currentPieceShape, data.getCurrentPieceShape());
        assertEquals(3, data.getCurrentPieceX());
        assertEquals(5, data.getCurrentPieceY());
        assertEquals(1, data.getCurrentPieceType());
    }

    @Test
    void testNextPieceData() {
        int[][] nextPieceShape = {{1, 1}, {1, 1}};
        
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 0, 1, 0, false,
            new int[2][2], 0, 0, 1,
            nextPieceShape, 2, 0, new ArrayList<>()
        );

        assertArrayEquals(nextPieceShape, data.getNextPieceShape());
        assertEquals(2, data.getNextPieceType());
    }

    @Test
    void testIncomingAttackData() {
        List<Integer> emptyCols = new ArrayList<>();
        emptyCols.add(2);
        emptyCols.add(5);
        emptyCols.add(8);
        
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 0, 1, 0, false,
            new int[2][2], 0, 0, 1,
            new int[2][2], 1, 3, emptyCols
        );

        assertEquals(3, data.getIncomingAttackLines());
        assertEquals(emptyCols, data.getIncomingAttackEmptyCols());
        assertEquals(3, data.getIncomingAttackEmptyCols().size());
    }

    @Test
    void testTimestamp() throws InterruptedException {
        long beforeTime = System.currentTimeMillis();
        Thread.sleep(10);
        
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 0, 1, 0, false,
            new int[2][2], 0, 0, 1,
            new int[2][2], 1, 0, new ArrayList<>()
        );
        
        Thread.sleep(10);
        long afterTime = System.currentTimeMillis();

        assertTrue(data.getTimestamp() >= beforeTime);
        assertTrue(data.getTimestamp() <= afterTime);
    }

    @Test
    void testEmptyIncomingAttack() {
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 0, 1, 0, false,
            new int[2][2], 0, 0, 1,
            new int[2][2], 1, 0, new ArrayList<>()
        );

        assertEquals(0, data.getIncomingAttackLines());
        assertTrue(data.getIncomingAttackEmptyCols().isEmpty());
    }

    @Test
    void testMultipleIncomingAttacks() {
        List<Integer> emptyCols = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            emptyCols.add(i);
        }
        
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 0, 1, 0, false,
            new int[2][2], 0, 0, 1,
            new int[2][2], 1, 10, emptyCols
        );

        assertEquals(10, data.getIncomingAttackLines());
        assertEquals(10, data.getIncomingAttackEmptyCols().size());
    }

    @Test
    void testHighScoreAndLevel() {
        GameStateData data = new GameStateData(
            new int[20][10], new int[20][10], 999999, 99, 1000, false,
            new int[2][2], 0, 0, 1,
            new int[2][2], 1, 0, new ArrayList<>()
        );

        assertEquals(999999, data.getScore());
        assertEquals(99, data.getLevel());
        assertEquals(1000, data.getLinesCleared());
    }

    @Test
    void testDifferentPieceTypes() {
        for (int pieceType = 1; pieceType <= 7; pieceType++) {
            GameStateData data = new GameStateData(
                new int[20][10], new int[20][10], 0, 1, 0, false,
                new int[2][2], 0, 0, pieceType,
                new int[2][2], pieceType, 0, new ArrayList<>()
            );

            assertEquals(pieceType, data.getCurrentPieceType());
            assertEquals(pieceType, data.getNextPieceType());
        }
    }
}
