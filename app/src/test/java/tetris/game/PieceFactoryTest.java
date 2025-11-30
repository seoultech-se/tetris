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
    void testBombPieceShape() {
        Piece bombPiece = PieceFactory.createBombPiece();
        int[][] shape = bombPiece.getShape();
        
        // 폭탄은 1x1 블록
        assertEquals(1, shape.length);
        assertEquals(1, shape[0].length);
    }

    @Test
    void testIPieceRotations() {
        Piece iPiece = PieceFactory.createPiece(PieceFactory.I_PIECE);
        int[][] shape1 = iPiece.getShape();
        
        iPiece.rotate();
        int[][] shape2 = iPiece.getShape();
        
        // I 블록은 회전하면 가로/세로가 바뀜
        assertNotEquals(shape1.length, shape2.length);
    }

    @Test
    void testOPieceRotations() {
        Piece oPiece = PieceFactory.createPiece(PieceFactory.O_PIECE);
        int[][] shape1 = oPiece.getShape();
        
        oPiece.rotate();
        int[][] shape2 = oPiece.getShape();
        
        // O 블록은 회전해도 모양이 같음
        assertEquals(shape1.length, shape2.length);
    }

    @Test
    void testRandomPieceBagSystem() {
        SettingsManager.getInstance().setGameMode("NORMAL");
        
        // Generate many pieces to test bag system
        java.util.Set<Integer> types = new java.util.HashSet<>();
        for (int i = 0; i < 30; i++) {
            Piece piece = PieceFactory.createRandomPiece();
            types.add(piece.getType());
        }
        
        // Should have at least 5 different types
        assertTrue(types.size() >= 5);
    }

    @Test
    void testWeightPieceHasWeightItem() {
        Piece weightPiece = PieceFactory.createWeightPiece();
        assertTrue(weightPiece.hasItem());
        
        // 아이템이 있는 블록 찾기
        int[][] shape = weightPiece.getShape();
        boolean foundWeightItem = false;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = weightPiece.getItemAt(row, col);
                    if (itemType == ItemType.WEIGHT) {
                        foundWeightItem = true;
                    }
                }
            }
        }
        assertTrue(foundWeightItem);
    }

    @Test
    void testBombPieceHasBombItem() {
        Piece bombPiece = PieceFactory.createBombPiece();
        assertTrue(bombPiece.hasItem());
        
        // 아이템이 있는 블록 찾기
        int[][] shape = bombPiece.getShape();
        boolean foundBombItem = false;
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = bombPiece.getItemAt(row, col);
                    if (itemType == ItemType.BOMB) {
                        foundBombItem = true;
                    }
                }
            }
        }
        assertTrue(foundBombItem);
    }

    @Test
    void testCreateRandomPieceWithItemInItemMode() {
        SettingsManager.getInstance().setGameMode("ITEM");
        
        // 여러 번 생성하여 메서드가 정상 작동하는지 확인
        for (int i = 0; i < 20; i++) {
            Piece piece = PieceFactory.createRandomPiece(true);
            assertNotNull(piece);
            assertNotNull(piece.getShape());
        }
    }

    @Test
    void testAllPieceTypesAreValid() {
        for (int type = PieceFactory.I_PIECE; type <= PieceFactory.L_PIECE; type++) {
            Piece piece = PieceFactory.createPiece(type);
            assertNotNull(piece);
            assertNotNull(piece.getShape());
            assertTrue(piece.getShape().length > 0);
        }
    }

    @Test
    void testSpecialPiecesAreValid() {
        Piece weightPiece = PieceFactory.createWeightPiece();
        Piece bombPiece = PieceFactory.createBombPiece();
        
        assertNotNull(weightPiece);
        assertNotNull(bombPiece);
        assertNotEquals(weightPiece.getType(), bombPiece.getType());
    }
}

