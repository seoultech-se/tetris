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
}

