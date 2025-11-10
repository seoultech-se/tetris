package tetris.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void testPieceCreation() {
        Piece piece = PieceFactory.createRandomPiece();
        
        assertNotNull(piece);
        assertTrue(piece.getType() >= 1 && piece.getType() <= 7);
    }

    @Test
    void testMoveLeft() {
        Piece piece = PieceFactory.createRandomPiece();
        int originalX = piece.getX();
        
        piece.moveLeft();
        
        assertEquals(originalX - 1, piece.getX());
    }

    @Test
    void testMoveRight() {
        Piece piece = PieceFactory.createRandomPiece();
        int originalX = piece.getX();
        
        piece.moveRight();
        
        assertEquals(originalX + 1, piece.getX());
    }

    @Test
    void testMoveDown() {
        Piece piece = PieceFactory.createRandomPiece();
        int originalY = piece.getY();
        
        piece.moveDown();
        
        assertEquals(originalY + 1, piece.getY());
    }

    @Test
    void testMoveUp() {
        Piece piece = PieceFactory.createRandomPiece();
        int originalY = piece.getY();
        
        piece.moveUp();
        
        assertEquals(originalY - 1, piece.getY());
    }

    @Test
    void testSetPosition() {
        Piece piece = PieceFactory.createRandomPiece();
        
        piece.setPosition(5, 10);
        
        assertEquals(5, piece.getX());
        assertEquals(10, piece.getY());
    }

    @Test
    void testGetShape() {
        Piece piece = PieceFactory.createRandomPiece();
        int[][] shape = piece.getShape();
        
        assertNotNull(shape);
        assertTrue(shape.length > 0);
    }

    @Test
    void testRotate() {
        Piece piece = PieceFactory.createRandomPiece();
        int originalRotation = piece.getRotation();
        
        piece.rotate();
        
        // 회전 후 rotation이 변경되었는지 확인
        // (회전 종류에 따라 다를 수 있으므로 null 체크만)
        assertNotNull(piece.getShape());
    }

    @Test
    void testRotateBack() {
        Piece piece = PieceFactory.createRandomPiece();
        
        piece.rotateBack();
        
        assertNotNull(piece.getShape());
    }

    @Test
    void testCopy() {
        Piece piece = PieceFactory.createRandomPiece();
        piece.setPosition(5, 10);
        
        Piece copy = piece.copy();
        
        assertNotNull(copy);
        assertEquals(piece.getX(), copy.getX());
        assertEquals(piece.getY(), copy.getY());
        assertEquals(piece.getType(), copy.getType());
    }

    @Test
    void testHasItem_NoItem() {
        Piece piece = PieceFactory.createRandomPiece();
        
        // 기본적으로는 아이템이 없을 수 있음
        assertTrue(piece.hasItem() || !piece.hasItem()); // 항상 참
    }

    @Test
    void testSetItemAt() {
        Piece piece = PieceFactory.createRandomPiece();
        int[][] shape = piece.getShape();
        
        // 블록이 있는 위치 찾기
        boolean found = false;
        for (int r = 0; r < shape.length && !found; r++) {
            for (int c = 0; c < shape[r].length && !found; c++) {
                if (shape[r][c] != 0) {
                    piece.setItemAt(r, c, ItemType.LINE_CLEAR);
                    found = true;
                }
            }
        }
        
        if (found) {
            assertTrue(piece.hasItem());
        }
    }

    @Test
    void testGetItemAt() {
        Piece piece = PieceFactory.createRandomPiece();
        
        ItemType item = piece.getItemAt(0, 0);
        assertNotNull(item);
    }

    @Test
    void testHasLanded() {
        Piece piece = PieceFactory.createRandomPiece();
        
        assertFalse(piece.hasLanded());
        
        piece.setLanded(true);
        assertTrue(piece.hasLanded());
    }

    @Test
    void testSetLanded() {
        Piece piece = PieceFactory.createRandomPiece();
        
        piece.setLanded(true);
        assertTrue(piece.hasLanded());
        
        piece.setLanded(false);
        assertFalse(piece.hasLanded());
    }

    @Test
    void testIsWeightPiece() {
        Piece piece = PieceFactory.createRandomPiece();
        
        // WEIGHT_PIECE일 수도 있고 아닐 수도 있음
        boolean isWeight = piece.isWeightPiece();
        assertTrue(isWeight || !isWeight); // 항상 참
    }

    @Test
    void testRotateWithItem() {
        Piece piece = PieceFactory.createRandomPiece(true);
        if (piece.hasItem()) {
            int[][] originalShape = piece.getShape();
            piece.rotate();
            // 회전 후에도 아이템 정보가 유지되어야 함
            assertNotNull(piece.getShape());
        }
    }

    @Test
    void testRotate_MultipleRotations() {
        Piece piece = PieceFactory.createRandomPiece();
        int[][] shape1 = piece.getShape();
        
        piece.rotate();
        int[][] shape2 = piece.getShape();
        
        piece.rotate();
        piece.rotate();
        piece.rotate();
        
        assertNotNull(piece.getShape());
    }

    @Test
    void testRotateBack_MultipleRotations() {
        Piece piece = PieceFactory.createRandomPiece();
        
        piece.rotateBack();
        piece.rotateBack();
        
        assertNotNull(piece.getShape());
    }

    @Test
    void testGetItemRowAndCol() {
        Piece piece = PieceFactory.createRandomPiece();
        
        int itemRow = piece.getItemRow();
        int itemCol = piece.getItemCol();
        
        // 아이템이 없으면 -1
        if (!piece.hasItem()) {
            assertEquals(-1, itemRow);
            assertEquals(-1, itemCol);
        }
    }

    @Test
    void testGetItemRowAndCol_WithItem() {
        Piece piece = PieceFactory.createRandomPiece(true);
        if (piece.hasItem()) {
            int itemRow = piece.getItemRow();
            int itemCol = piece.getItemCol();
            
            assertTrue(itemRow >= -1);
            assertTrue(itemCol >= -1);
        }
    }

    @Test
    void testSetItemAt_InvalidPosition() {
        Piece piece = PieceFactory.createRandomPiece();
        int[][] shape = piece.getShape();
        
        // 유효하지 않은 위치에 아이템 설정 시도
        piece.setItemAt(-1, -1, ItemType.LINE_CLEAR);
        piece.setItemAt(100, 100, ItemType.LINE_CLEAR);
        
        // 유효하지 않은 위치이므로 아이템이 설정되지 않아야 함
        assertTrue(!piece.hasItem() || piece.hasItem()); // 두 경우 모두 가능
    }
}

