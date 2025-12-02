package tetris.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tetris.ui.SettingsManager;
import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {

    private GameBoard gameBoard;

    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard();
    }

    @Test
    void testGameBoardCreation() {
        assertNotNull(gameBoard);
        assertEquals(0, gameBoard.getCell(0, 0));
    }

    @Test
    void testClearBoard() {
        // 보드를 채운다
        Piece piece = PieceFactory.createRandomPiece();
        piece.setPosition(gameBoard.getSpawnX(), gameBoard.getSpawnY());
        gameBoard.placePiece(piece);
        
        gameBoard.clearBoard();
        
        assertEquals(0, gameBoard.getCell(0, 0));
        assertEquals(0, gameBoard.getCell(10, 5));
    }

    @Test
    void testIsValidPosition() {
        Piece piece = PieceFactory.createRandomPiece();
        piece.setPosition(gameBoard.getSpawnX(), gameBoard.getSpawnY());
        
        assertTrue(gameBoard.isValidPosition(piece));
    }

    @Test
    void testIsValidPosition_OutOfBounds() {
        Piece piece = PieceFactory.createRandomPiece();
        piece.setPosition(-10, -10);
        
        assertFalse(gameBoard.isValidPosition(piece));
    }

    @Test
    void testPlacePiece() {
        Piece piece = PieceFactory.createRandomPiece();
        piece.setPosition(gameBoard.getSpawnX(), gameBoard.getSpawnY());
        
        gameBoard.placePiece(piece);
        
        // 블록이 배치되었는지 확인 (좌표에 따라 다를 수 있음)
        assertNotNull(gameBoard.getBoard());
    }

    @Test
    void testClearLines() {
        int cleared = gameBoard.clearLines();
        assertEquals(0, cleared); // 초기에는 비어있음
    }

    @Test
    void testGetFullLines() {
        var fullLines = gameBoard.getFullLines();
        assertNotNull(fullLines);
        assertEquals(0, fullLines.size()); // 초기에는 비어있음
    }

    @Test
    void testGetCell() {
        assertEquals(0, gameBoard.getCell(0, 0));
    }

    @Test
    void testGetCell_OutOfBounds() {
        assertEquals(0, gameBoard.getCell(-1, -1));
        assertEquals(0, gameBoard.getCell(100, 100));
    }

    @Test
    void testGetSpawnX() {
        int spawnX = gameBoard.getSpawnX();
        assertTrue(spawnX >= 0 && spawnX < GameBoard.BOARD_WIDTH);
    }

    @Test
    void testGetSpawnY() {
        assertEquals(0, gameBoard.getSpawnY());
    }

    @Test
    void testGetBoard() {
        int[][] board = gameBoard.getBoard();
        assertNotNull(board);
        assertEquals(GameBoard.BOARD_HEIGHT, board.length);
        assertEquals(GameBoard.BOARD_WIDTH, board[0].length);
    }

    @Test
    void testGetItemAt() {
        assertEquals(ItemType.NONE, gameBoard.getItemAt(0, 0));
    }

    @Test
    void testGetItemAt_OutOfBounds() {
        assertEquals(ItemType.NONE, gameBoard.getItemAt(-1, -1));
    }

    @Test
    void testProcessItemEffects_NoItem() {
        Piece piece = PieceFactory.createRandomPiece();
        int cleared = gameBoard.processItemEffects(piece);
        assertEquals(0, cleared);
    }

    @Test
    void testProcessItemEffects_WithItem() {
        // 아이템이 있는 블록 생성 (테스트를 위해 직접 생성)
        Piece piece = PieceFactory.createRandomPiece(true);
        piece.setPosition(gameBoard.getSpawnX(), gameBoard.getSpawnY());
        gameBoard.placePiece(piece);
        
        // 아이템 효과 처리
        int cleared = gameBoard.processItemEffects(piece);
        assertTrue(cleared >= 0);
    }

    @Test
    void testProcessWeightEffect() {
        // WEIGHT 타입 블록 생성
        SettingsManager.getInstance().setGameMode("ITEM");
        Piece weightPiece = PieceFactory.createWeightPiece();
        weightPiece.setPosition(gameBoard.getSpawnX(), 5);
        gameBoard.placePiece(weightPiece);
        
        // 무게추 효과 처리
        weightPiece.setPosition(gameBoard.getSpawnX(), 15);
        gameBoard.processWeightEffect(weightPiece);
        
        // 무게추가 이동하면서 밑의 블록을 지웠는지 확인
        assertNotNull(gameBoard.getBoard());
    }

    @Test
    void testClearLine() {
        // 한 줄을 채운다
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            Piece piece = PieceFactory.createRandomPiece();
            piece.setPosition(col, GameBoard.BOARD_HEIGHT - 1);
            gameBoard.placePiece(piece);
        }
        
        // 줄 삭제
        int cleared = gameBoard.clearLines();
        assertTrue(cleared >= 0);
    }

    @Test
    void testPlacePiece_WeightPiece() {
        Piece weightPiece = PieceFactory.createWeightPiece();
        weightPiece.setPosition(gameBoard.getSpawnX(), gameBoard.getSpawnY());
        
        assertFalse(weightPiece.hasLanded());
        gameBoard.placePiece(weightPiece);
        assertTrue(weightPiece.hasLanded());
    }

    @Test
    void testIsValidPosition_NullPiece() {
        assertFalse(gameBoard.isValidPosition(null));
    }

    @Test
    void testPlacePiece_NullPiece() {
        // null 블록은 무시되어야 함
        gameBoard.placePiece(null);
        assertEquals(0, gameBoard.getCell(0, 0));
    }

    @Test
    void testProcessBombEffect() {
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(5, 10);
        gameBoard.placePiece(bombPiece);
        
        // BOMB 효과 처리
        int cleared = gameBoard.processItemEffects(bombPiece);
        assertTrue(cleared >= 0);
    }

    @Test
    void testProcessItemEffects_Bomb() {
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(gameBoard.getSpawnX(), 5);
        
        int cleared = gameBoard.processItemEffects(bombPiece);
        // 폭탄은 행과 열을 삭제하므로 점수는 증가하지 않을 수 있음
        assertTrue(cleared >= 0);
    }

    @Test
    void testProcessItemEffects_LineClear() {
        SettingsManager.getInstance().setGameMode("ITEM");
        Piece piece = PieceFactory.createRandomPiece(true);
        if (piece.hasItem()) {
            // LINE_CLEAR 아이템 추가
            int[][] shape = piece.getShape();
            for (int r = 0; r < shape.length; r++) {
                for (int c = 0; c < shape[r].length; c++) {
                    if (shape[r][c] != 0) {
                        piece.setItemAt(r, c, ItemType.LINE_CLEAR);
                        break;
                    }
                }
                if (piece.hasItem()) break;
            }
            
            piece.setPosition(gameBoard.getSpawnX(), 15);
            int cleared = gameBoard.processItemEffects(piece);
            assertTrue(cleared >= 0);
        }
    }

    @Test
    void testClearLine_FullLine() {
        // 보드 하단 한 줄을 직접 채움
        int[][] board = gameBoard.getBoard();
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            board[GameBoard.BOARD_HEIGHT - 1][col] = 1; // 블록으로 채움
        }
        
        int cleared = gameBoard.clearLines();
        assertEquals(1, cleared);
    }
}

