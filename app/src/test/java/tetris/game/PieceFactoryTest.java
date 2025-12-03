package tetris.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tetris.ui.SettingsManager;
import static org.junit.jupiter.api.Assertions.*;

class PieceFactoryTest {

    @BeforeEach
    void setUp() {
        SettingsManager.getInstance().setDifficulty("Normal");
        SettingsManager.getInstance().setGameMode("NORMAL");
    }

    @Test
    void testCreatePiece() {
        Piece piece = PieceFactory.createPiece(PieceFactory.I_PIECE);
        assertNotNull(piece);
        assertEquals(PieceFactory.I_PIECE, piece.getType());
    }

    @Test
    void testCreateRandomPiece() {
        Piece piece = PieceFactory.createRandomPiece();
        assertNotNull(piece);
        assertTrue(piece.getType() >= 1 && piece.getType() <= 7);
    }

    @Test
    void testCreateRandomPieceWithItem() {
        SettingsManager.getInstance().setGameMode("ITEM");
        Piece piece = PieceFactory.createRandomPiece(true);
        assertNotNull(piece);
    }

    @Test
    void testCreateWeightPiece() {
        Piece weightPiece = PieceFactory.createWeightPiece();
        assertNotNull(weightPiece);
        assertEquals(PieceFactory.WEIGHT_PIECE, weightPiece.getType());
        assertTrue(weightPiece.hasItem());
    }

    @Test
    void testCreateBombPiece() {
        Piece bombPiece = PieceFactory.createBombPiece();
        assertNotNull(bombPiece);
        assertEquals(PieceFactory.BOMB_PIECE, bombPiece.getType());
        assertTrue(bombPiece.hasItem());
    }

    @Test
    void testCreateAllPieceTypes() {
        for (int type = 1; type <= 7; type++) {
            Piece piece = PieceFactory.createPiece(type);
            assertNotNull(piece);
            assertEquals(type, piece.getType());
        }
    }

    @Test
    void testCreateWeightPiece_HasItem() {
        Piece weightPiece = PieceFactory.createWeightPiece();
        int[][] shape = weightPiece.getShape();
        
        // 모든 블록 셀에 WEIGHT 아이템이 있어야 함
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    assertTrue(weightPiece.hasItem());
                }
            }
        }
    }

    @Test
    void testPieceGenerationProbability_Normal() {
        SettingsManager.getInstance().setDifficulty("Normal");
        final int ITERATIONS = 10000;
        final double[] EXPECTED_PROBABILITIES = {
            10.0 / 70.0, // I
            10.0 / 70.0, // O
            10.0 / 70.0, // T
            10.0 / 70.0, // S
            10.0 / 70.0, // Z
            10.0 / 70.0, // J
            10.0 / 70.0  // L
        };
        final double DELTA = 0.05; // 5%p 오차

        java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
        for (int i = 1; i <= 7; i++) {
            counts.put(i, 0);
        }

        for (int i = 0; i < ITERATIONS; i++) {
            Piece piece = PieceFactory.createRandomPiece(false);
            counts.put(piece.getType(), counts.get(piece.getType()) + 1);
        }

        for (int type = 1; type <= 7; type++) {
            double actualProbability = (double) counts.get(type) / ITERATIONS;
            assertEquals(EXPECTED_PROBABILITIES[type - 1], actualProbability, DELTA,
                "Type " + type + " probability is out of range for Normal difficulty.");
        }
    }

    @Test
    void testPieceGenerationProbability_Easy() {
        SettingsManager.getInstance().setDifficulty("Easy");
        final int ITERATIONS = 10000;
        final double[] EXPECTED_PROBABILITIES = {
            12.0 / 72.0, // I
            10.0 / 72.0, // O
            10.0 / 72.0, // T
            10.0 / 72.0, // S
            10.0 / 72.0, // Z
            10.0 / 72.0, // J
            10.0 / 72.0  // L
        };
        final double DELTA = 0.05; // 5%p 오차

        java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
        for (int i = 1; i <= 7; i++) {
            counts.put(i, 0);
        }

        for (int i = 0; i < ITERATIONS; i++) {
            Piece piece = PieceFactory.createRandomPiece(false);
            counts.put(piece.getType(), counts.get(piece.getType()) + 1);
        }

        for (int type = 1; type <= 7; type++) {
            double actualProbability = (double) counts.get(type) / ITERATIONS;
            assertEquals(EXPECTED_PROBABILITIES[type - 1], actualProbability, DELTA,
                "Type " + type + " probability is out of range for Easy difficulty.");
        }
    }

    @Test
    void testPieceGenerationProbability_Hard() {
        SettingsManager.getInstance().setDifficulty("Hard");
        final int ITERATIONS = 10000;
        final double[] EXPECTED_PROBABILITIES = {
            8.0 / 68.0,  // I
            10.0 / 68.0, // O
            10.0 / 68.0, // T
            10.0 / 68.0, // S
            10.0 / 68.0, // Z
            10.0 / 68.0, // J
            10.0 / 68.0  // L
        };
        final double DELTA = 0.05; // 5%p 오차

        java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
        for (int i = 1; i <= 7; i++) {
            counts.put(i, 0);
        }

        for (int i = 0; i < ITERATIONS; i++) {
            Piece piece = PieceFactory.createRandomPiece(false);
            counts.put(piece.getType(), counts.get(piece.getType()) + 1);
        }

        for (int type = 1; type <= 7; type++) {
            double actualProbability = (double) counts.get(type) / ITERATIONS;
            assertEquals(EXPECTED_PROBABILITIES[type - 1], actualProbability, DELTA,
                "Type " + type + " probability is out of range for Hard difficulty.");
        }
    }
}

