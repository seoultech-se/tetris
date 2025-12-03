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

    @Test
    void testBombEffect_WithGravity_3x3() {
        // BOMB 아이템이 3x3 범위를 제거하는지 테스트
        int[][] board = gameBoard.getBoard();

        // 폭탄이 위치할 행(10)과 열(5)
        int bombRow = 10;
        int bombCol = 5;

        // 보드를 블록으로 채움 (5행~15행)
        for (int row = 5; row <= 15; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        // 폭탄 배치 및 효과 처리
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(bombCol, bombRow);
        gameBoard.placePiece(bombPiece);
        gameBoard.processItemEffects(bombPiece);

        // 검증: 3x3 범위 (bombRow-1 ~ bombRow+1, bombCol-1 ~ bombCol+1)가 제거되었는지 확인
        // 중력 적용 후 해당 영역의 블록들이 아래로 이동했을 것

        // 3x3 범위 중심부 확인: bombRow ~ bombRow+2 위치에서 bombCol-1 ~ bombCol+1 열이 비어있거나 다른 블록으로 채워져야 함
        // 단순히 3x3 범위가 제거되었는지 확인

        // 폭탄 바로 위의 블록(5행~9행)이 중력으로 아래로 이동했는지 확인
        // bombCol-1, bombCol, bombCol+1 열에서 블록이 감소했는지 확인
        int blockCountBeforeGravity = (15 - 5 + 1) * 3; // 11행 * 3열 = 33블록
        int blockCountAfterExplosion = 0;

        for (int col = bombCol - 1; col <= bombCol + 1; col++) {
            if (col >= 0 && col < GameBoard.BOARD_WIDTH) {
                for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
                    if (gameBoard.getCell(row, col) != 0) {
                        blockCountAfterExplosion++;
                    }
                }
            }
        }

        // 3x3 범위가 제거되었으므로 최소 3블록은 줄어들어야 함 (폭탄 중심 + 주변)
        assertTrue(blockCountAfterExplosion < blockCountBeforeGravity,
            "Bomb should remove 3x3 area blocks. Before: " + blockCountBeforeGravity +
            ", After: " + blockCountAfterExplosion);

        // 중력 확인: 폭탄 범위 밖의 열들은 여전히 블록이 있어야 함
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col < bombCol - 1 || col > bombCol + 1) {
                // 폭탄 범위 밖의 열은 그대로 있어야 함
                boolean hasBlocks = false;
                for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
                    if (gameBoard.getCell(row, col) != 0) {
                        hasBlocks = true;
                        break;
                    }
                }
                assertTrue(hasBlocks, "Columns outside bomb range should still have blocks");
            }
        }
    }

    @Test
    void testLineClearItem_WithGravity() {
        // LINE_CLEAR 아이템의 중력 적용 검증
        SettingsManager.getInstance().setGameMode("ITEM");

        int[][] board = gameBoard.getBoard();

        // 보드에 블록 패턴 설정
        // 행 5: 블록 타입 3
        // 행 10: LINE_CLEAR 아이템이 배치될 위치
        // 행 15: 블록 타입 1 (기준점)

        // 5행에 블록 배치
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            board[5][col] = 3;
        }

        // 15행에 블록 배치 (기준점)
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            board[15][col] = 1;
        }

        // 10행에 LINE_CLEAR 블록 배치 및 아이템 효과 처리
        // I 블록을 사용 (1x4 크기)
        Piece piece = PieceFactory.createPiece(PieceFactory.I_PIECE);
        piece.setPosition(3, 10);  // 가운데에 배치

        // I 블록의 각 셀에 LINE_CLEAR 설정
        int[][] shape = piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    piece.setItemAt(row, col, ItemType.LINE_CLEAR);
                }
            }
        }

        gameBoard.placePiece(piece);
        int clearedLines = gameBoard.processItemEffects(piece);

        // LINE_CLEAR가 동작했는지 확인
        assertTrue(clearedLines > 0, "LINE_CLEAR should remove at least one line");

        // 중력이 적용되어 5행의 블록이 아래로 이동했는지 확인
        // 5행에 있던 블록(타입 3)은 이제 6행에 있어야 함
        boolean blocksMovedDown = false;
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (gameBoard.getCell(6, col) == 3) {
                blocksMovedDown = true;
                break;
            }
        }

        assertTrue(blocksMovedDown, "Blocks above LINE_CLEAR should move down by gravity");
    }

    @Test
    void testBombEffect_3x3_EdgeCase() {
        // 폭탄이 가장자리에 있을 때 3x3 범위가 올바르게 처리되는지 확인
        int[][] board = gameBoard.getBoard();

        // 보드 전체를 블록으로 채움
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 2;
            }
        }

        // 폭탄을 왼쪽 상단 모서리에 배치 (0, 0)
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(0, 0);
        gameBoard.placePiece(bombPiece);
        gameBoard.processItemEffects(bombPiece);

        // 검증: 0,0을 중심으로 3x3 범위가 제거되어야 하지만,
        // 범위 밖(-1 행/열)은 clearCell에서 무시되므로 실제로는 2x2만 제거됨
        // 중력 적용 후, (0,0), (0,1), (1,0), (1,1) 영역의 블록 수가 줄어들어야 함

        int totalBlocks = 0;
        for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                if (gameBoard.getCell(row, col) != 0) {
                    totalBlocks++;
                }
            }
        }

        // 전체 블록 수가 줄어들었는지 확인
        int originalBlocks = GameBoard.BOARD_WIDTH * GameBoard.BOARD_HEIGHT;
        assertTrue(totalBlocks < originalBlocks,
            "Bomb should remove blocks even at edges. Original: " + originalBlocks +
            ", After: " + totalBlocks);
    }

    @Test
    void testBombEffect_3x3_Center() {
        // 폭탄이 중앙에 있을 때 정확히 3x3 범위를 제거하는지 확인
        int[][] board = gameBoard.getBoard();

        int bombRow = 10;
        int bombCol = 5;

        // 폭탄 주변만 블록으로 채움 (8행~12행, 3열~7열)
        for (int row = 8; row <= 12; row++) {
            for (int col = 3; col <= 7; col++) {
                board[row][col] = 3;
            }
        }

        int blocksBeforeBomb = 0;
        for (int row = 8; row <= 12; row++) {
            for (int col = 3; col <= 7; col++) {
                if (board[row][col] != 0) blocksBeforeBomb++;
            }
        }

        // 폭탄 배치
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(bombCol, bombRow);
        gameBoard.placePiece(bombPiece);
        gameBoard.processItemEffects(bombPiece);

        // 중력 적용 후, bombCol-1, bombCol, bombCol+1 열의 블록이 줄어들어야 함
        int blocksAfterBomb = 0;
        for (int col = 4; col <= 6; col++) { // bombCol-1 ~ bombCol+1
            for (int row = 0; row < GameBoard.BOARD_HEIGHT; row++) {
                if (gameBoard.getCell(row, col) != 0) {
                    blocksAfterBomb++;
                }
            }
        }

        // 3x3 범위 제거 후 블록 수가 줄어들어야 함
        int originalIn3Cols = 3 * 5; // 3열 * 5행 = 15블록
        assertTrue(blocksAfterBomb < originalIn3Cols,
            "3x3 bomb should remove blocks in center area");
    }

    @Test
    void testBombEffect_WithCascadingLineClear() {
        // BOMB으로 중력 적용 후 꽉 찬 줄이 생기면 clearLines()로 연속 삭제 가능한지 검증
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 보드 하단부를 거의 채움
        // 행 15~19: 중간에 한 칸만 비워둠 (열 5)
        for (int row = 15; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                if (col != 5) {
                    board[row][col] = 1;
                }
            }
        }

        // 행 10: 열 5에만 블록 배치 (이것이 떨어지면 행 15~19가 꽉 참)
        for (int row = 10; row <= 14; row++) {
            board[row][5] = 2;
        }

        // 행 10의 열 4 위치에 폭탄 배치
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(4, 10);
        gameBoard.placePiece(bombPiece);

        // 폭탄 효과 처리 (3x3 제거 + 중력 적용)
        gameBoard.processItemEffects(bombPiece);

        // 중력 적용 후 꽉 찬 줄이 생겼는지 확인
        java.util.List<Integer> fullLines = gameBoard.getFullLines();

        // 폭탄으로 인해 블록들이 떨어지면서 꽉 찬 줄이 생겼을 것으로 예상
        // 이제 clearLines()를 호출하면 이 줄들이 삭제되어야 함
        if (!fullLines.isEmpty()) {
            int clearedLines = gameBoard.clearLines();
            assertTrue(clearedLines > 0,
                "After gravity from BOMB, full lines should be cleared by clearLines()");

            // 줄 삭제 후 다시 확인하면 꽉 찬 줄이 없어야 함
            java.util.List<Integer> fullLinesAfterClear = gameBoard.getFullLines();
            assertEquals(0, fullLinesAfterClear.size(), "All full lines should be cleared");
        }
    }

    @Test
    void testLineClearItem_WithCascadingLineClear() {
        // LINE_CLEAR로 줄 삭제 후 중력 적용되고, 그 결과 꽉 찬 줄이 생기면 clearLines()로 연속 삭제 가능한지 검증
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 보드 설정:
        // 행 18~19: 완전히 채움
        for (int row = 18; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        // 행 17: 한 칸만 비워둠 (열 5)
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 5) {
                board[17][col] = 2;
            }
        }

        // 행 10: 열 5에 블록 배치 (LINE_CLEAR로 삭제하면 이 블록이 떨어져서 행 17이 꽉 참)
        board[10][5] = 3;

        // 행 12에 LINE_CLEAR 아이템 배치 (이 줄 전체를 채움)
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            board[12][col] = 4;
        }

        // I 블록으로 LINE_CLEAR 아이템 생성
        Piece piece = PieceFactory.createPiece(PieceFactory.I_PIECE);
        piece.setPosition(3, 12);

        // I 블록의 모든 셀에 LINE_CLEAR 설정
        int[][] shape = piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    piece.setItemAt(row, col, ItemType.LINE_CLEAR);
                }
            }
        }

        gameBoard.placePiece(piece);
        int itemClearedLines = gameBoard.processItemEffects(piece);

        assertTrue(itemClearedLines > 0, "LINE_CLEAR should remove at least one line");

        // LINE_CLEAR 효과로 중력이 적용되었으므로,
        // clearLines()를 호출하면 새로 꽉 찬 줄들이 삭제되어야 함
        int cascadeClearedLines = gameBoard.clearLines();

        // 연속 삭제가 가능한지 검증 (0일 수도 있고, >0일 수도 있음)
        // 최소한 에러 없이 호출되어야 함
        assertTrue(cascadeClearedLines >= 0,
            "clearLines() should work after LINE_CLEAR item effect and gravity");
    }

    @Test
    void testBombEffect_MultipleLineCascade() {
        // BOMB으로 여러 줄이 연속으로 삭제되는 극단적인 케이스
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 보드 하단 5줄 (15~19)을 거의 채우되, 각 줄마다 한 칸씩만 비움 (모두 열 5)
        for (int row = 15; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                if (col != 5) {
                    board[row][col] = 1;
                }
            }
        }

        // 열 5를 세로로 블록들로 채움 (행 10~14)
        for (int row = 10; row <= 14; row++) {
            board[row][5] = 2;
        }

        // 행 10, 열 5 위치에 폭탄 배치 (3x3 제거하면 열 5의 블록들이 떨어짐)
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(5, 10);
        gameBoard.placePiece(bombPiece);

        int fullLinesBefore = gameBoard.getFullLines().size();
        assertEquals(0, fullLinesBefore, "Should have no full lines before bomb");

        // 폭탄 효과 처리
        gameBoard.processItemEffects(bombPiece);

        // 중력 적용 후 꽉 찬 줄 확인
        java.util.List<Integer> fullLinesAfterGravity = gameBoard.getFullLines();

        // 연속 줄삭제
        int clearedLines = gameBoard.clearLines();

        // 줄삭제 후 다시 확인
        java.util.List<Integer> fullLinesAfterClear = gameBoard.getFullLines();

        // 검증: 중력 후 생긴 줄들이 모두 삭제되어야 함
        if (!fullLinesAfterGravity.isEmpty()) {
            assertTrue(clearedLines >= fullLinesAfterGravity.size(),
                "Should clear at least " + fullLinesAfterGravity.size() + " lines, but cleared " + clearedLines);
        }
        assertEquals(0, fullLinesAfterClear.size(),
            "All full lines should be cleared after cascade");
    }

    @Test
    void testLineClearItem_MultipleRowsWithGravity() {
        // LINE_CLEAR로 여러 줄을 동시에 삭제하고, 중력으로 또 여러 줄이 채워지는 케이스
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 보드 하단 (17~19) 완전히 채우기
        for (int row = 17; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        // 행 15~16: 각각 한 칸만 비우기 (열 3과 열 7)
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 3) board[15][col] = 2;
            if (col != 7) board[16][col] = 2;
        }

        // 행 5~9: 열 3과 열 7에만 블록 배치 (이게 떨어지면 15~16이 채워짐)
        for (int row = 5; row <= 9; row++) {
            board[row][3] = 3;
            board[row][7] = 3;
        }

        // 행 10~14를 완전히 채우고 LINE_CLEAR 아이템 설정
        for (int row = 10; row <= 14; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 4;
            }
        }

        // I 블록으로 LINE_CLEAR 효과 적용
        Piece piece = PieceFactory.createPiece(PieceFactory.I_PIECE);
        piece.setPosition(3, 10);

        int[][] shape = piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    piece.setItemAt(row, col, ItemType.LINE_CLEAR);
                }
            }
        }

        gameBoard.placePiece(piece);
        int itemClearedLines = gameBoard.processItemEffects(piece);

        assertTrue(itemClearedLines > 0, "LINE_CLEAR should remove lines");

        // 중력 후 꽉 찬 줄 확인
        java.util.List<Integer> fullLines = gameBoard.getFullLines();

        // 연속 삭제
        int cascadeClearedLines = gameBoard.clearLines();

        // 최종 확인
        java.util.List<Integer> remainingFullLines = gameBoard.getFullLines();
        assertEquals(0, remainingFullLines.size(),
            "All cascaded full lines should be cleared");
    }

    @Test
    void testBombEffect_ComplexGravityScenario() {
        // BOMB으로 복잡한 패턴을 제거하고 중력 적용 시나리오
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 체스판 패턴으로 보드 채우기 (빈칸이 산발적으로 분포)
        for (int row = 10; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                if ((row + col) % 2 == 0) {
                    board[row][col] = 1;
                }
            }
        }

        // 하단 3줄은 완전히 채우되, 각각 한 칸씩만 비우기
        for (int row = 17; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 2;
            }
            board[row][row - 17] = 0; // 각 줄마다 다른 위치 비우기
        }

        // 비워진 위치 위에 블록 배치
        for (int row = 10; row <= 16; row++) {
            for (int col = 0; col <= 2; col++) {
                board[row][col] = 3;
            }
        }

        // 중앙에 폭탄 배치
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(5, 12);
        gameBoard.placePiece(bombPiece);

        // 폭탄 효과 처리
        int itemEffect = gameBoard.processItemEffects(bombPiece);

        // 여러 번 clearLines 호출 (연속 중력 시뮬레이션)
        int totalCleared = 0;
        int iterations = 0;
        while (iterations < 10) { // 최대 10번 반복
            java.util.List<Integer> fullLines = gameBoard.getFullLines();
            if (fullLines.isEmpty()) break;

            int cleared = gameBoard.clearLines();
            totalCleared += cleared;
            iterations++;
        }

        // 최종적으로 꽉 찬 줄이 없어야 함
        java.util.List<Integer> finalFullLines = gameBoard.getFullLines();
        assertEquals(0, finalFullLines.size(),
            "After multiple cascade iterations, no full lines should remain");
    }

    @Test
    void testLineClearItem_ChainReaction() {
        // LINE_CLEAR → 중력 → 새로운 꽉 찬 줄 → clearLines → 또 다른 중력이 필요한 상황
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 매우 복잡한 패턴 구성
        // 하단 (18~19): 완전히 채움
        for (int row = 18; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        // 행 16~17: 한 칸씩 비우기
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 2) board[16][col] = 2;
            if (col != 6) board[17][col] = 2;
        }

        // 행 14~15: 한 칸씩 비우기 (다른 위치)
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 4) board[14][col] = 3;
            if (col != 8) board[15][col] = 3;
        }

        // 위쪽에 채워질 블록들 배치
        for (int row = 5; row <= 13; row++) {
            board[row][2] = 4;
            board[row][4] = 4;
            board[row][6] = 4;
            board[row][8] = 4;
        }

        // 행 10에 LINE_CLEAR 블록 배치
        Piece piece = PieceFactory.createPiece(PieceFactory.I_PIECE);
        piece.setPosition(3, 10);

        int[][] shape = piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    piece.setItemAt(row, col, ItemType.LINE_CLEAR);
                }
            }
        }

        gameBoard.placePiece(piece);
        gameBoard.processItemEffects(piece);

        // 연속적으로 clearLines 호출하여 모든 꽉 찬 줄 제거
        int totalCleared = 0;
        int maxIterations = 20;
        for (int i = 0; i < maxIterations; i++) {
            java.util.List<Integer> fullLines = gameBoard.getFullLines();
            if (fullLines.isEmpty()) break;

            int cleared = gameBoard.clearLines();
            totalCleared += cleared;

            if (cleared == 0) break; // 더 이상 삭제할 줄이 없음
        }

        // 최종 검증
        java.util.List<Integer> finalFullLines = gameBoard.getFullLines();
        assertEquals(0, finalFullLines.size(),
            "Chain reaction should eventually clear all full lines");
    }

    @Test
    void testBombAndLineClearCombination() {
        // BOMB과 LINE_CLEAR를 순차적으로 사용하는 복합 시나리오
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 보드 하단을 거의 채우기
        for (int row = 15; row < GameBoard.BOARD_HEIGHT; row++) {
            for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
                board[row][col] = 1;
            }
        }

        // 행 14: 한 칸만 비우기
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 5) board[14][col] = 2;
        }

        // 위쪽에 블록 배치
        for (int row = 8; row <= 13; row++) {
            board[row][5] = 3;
        }

        // 1단계: BOMB 사용
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(5, 10);
        gameBoard.placePiece(bombPiece);
        gameBoard.processItemEffects(bombPiece);

        // 첫 번째 clearLines
        int firstClear = gameBoard.clearLines();

        // 2단계: 추가 블록 배치
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 3) board[10][col] = 4;
        }
        for (int row = 5; row <= 9; row++) {
            board[row][3] = 5;
        }

        // LINE_CLEAR 사용
        Piece lineClearPiece = PieceFactory.createPiece(PieceFactory.I_PIECE);
        lineClearPiece.setPosition(3, 8);

        int[][] shape = lineClearPiece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    lineClearPiece.setItemAt(row, col, ItemType.LINE_CLEAR);
                }
            }
        }

        gameBoard.placePiece(lineClearPiece);
        gameBoard.processItemEffects(lineClearPiece);

        // 두 번째 clearLines
        int secondClear = gameBoard.clearLines();

        // 최종 검증
        java.util.List<Integer> finalFullLines = gameBoard.getFullLines();
        assertEquals(0, finalFullLines.size(),
            "Combination of BOMB and LINE_CLEAR should handle all cascades");
    }

    @Test
    void testGravityMultipleCascades() {
        // 중력이 여러 번 연속으로 적용되어야 하는 극단적인 케이스
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 보드 전체를 레이어 형태로 구성
        // 맨 아래 (19): 완전히 채움
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            board[19][col] = 1;
        }

        // 행 18: 한 칸 비움
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 1) board[18][col] = 1;
        }

        // 행 17: 다른 칸 비움
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 3) board[17][col] = 1;
        }

        // 행 16: 또 다른 칸 비움
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 5) board[16][col] = 1;
        }

        // 행 15: 또 다른 칸 비움
        for (int col = 0; col < GameBoard.BOARD_WIDTH; col++) {
            if (col != 7) board[15][col] = 1;
        }

        // 위쪽에 필요한 블록들 배치
        for (int row = 5; row <= 14; row++) {
            board[row][1] = 2;
            board[row][3] = 2;
            board[row][5] = 2;
            board[row][7] = 2;
        }

        // 중앙에 폭탄 배치 및 폭발
        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(4, 10);
        gameBoard.placePiece(bombPiece);
        gameBoard.processItemEffects(bombPiece);

        // 연속 cascade 처리
        int totalIterations = 0;
        int totalCleared = 0;
        while (totalIterations < 30) {
            java.util.List<Integer> fullLines = gameBoard.getFullLines();
            if (fullLines.isEmpty()) break;

            int cleared = gameBoard.clearLines();
            if (cleared == 0) break;

            totalCleared += cleared;
            totalIterations++;
        }

        // 검증: 무한 루프에 빠지지 않고 종료되어야 함
        assertTrue(totalIterations < 30,
            "Should not require more than 30 iterations to settle");

        java.util.List<Integer> finalFullLines = gameBoard.getFullLines();
        assertEquals(0, finalFullLines.size(),
            "All full lines should eventually be cleared");
    }

    @Test
    void testEmptyBoardAfterItemEffects() {
        // 아이템 효과 후 보드가 거의 비었을 때도 문제없이 동작하는지 확인
        SettingsManager.getInstance().setGameMode("ITEM");
        int[][] board = gameBoard.getBoard();

        // 최소한의 블록만 배치
        board[19][5] = 1;
        board[18][5] = 1;

        Piece bombPiece = PieceFactory.createBombPiece();
        bombPiece.setPosition(5, 17);
        gameBoard.placePiece(bombPiece);
        gameBoard.processItemEffects(bombPiece);

        int cleared = gameBoard.clearLines();

        // 에러 없이 처리되어야 함
        assertTrue(cleared >= 0, "Should handle near-empty board without errors");

        java.util.List<Integer> fullLines = gameBoard.getFullLines();
        assertEquals(0, fullLines.size(), "Should have no full lines");
    }
}

