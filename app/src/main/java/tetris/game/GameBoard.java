package tetris.game;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    private static final int SPAWN_X = BOARD_WIDTH / 2 - 1;
    private static final int SPAWN_Y = 0;

    private int[][] board;
    private ItemType[][] itemBoard;  // 각 셀의 아이템 정보
    private boolean[][] attackBoard;  // 공격 블록 표시 (true면 공격 블록, 회색으로 표시)
    private int attackLinesCount;  // 현재 보드에 있는 공격 줄 수 (최대 10줄)

    public GameBoard() {
        this.board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        this.itemBoard = new ItemType[BOARD_HEIGHT][BOARD_WIDTH];
        this.attackBoard = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        this.attackLinesCount = 0;
        clearBoard();
    }

    public void clearBoard() {
        for (int row = 0; row < BOARD_HEIGHT; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = 0;
                itemBoard[row][col] = ItemType.NONE;
                attackBoard[row][col] = false;
            }
        }
        attackLinesCount = 0;
    }

    public boolean isValidPosition(Piece piece) {
        if (piece == null) return false;

        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;

                    if (newX < 0 || newX >= BOARD_WIDTH ||
                        newY >= BOARD_HEIGHT ||
                        (newY >= 0 && board[newY][newX] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placePiece(Piece piece) {
        if (piece == null) return;

        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();
        int pieceType = piece.getType();
        if (piece.isWeightPiece()) {
            piece.setLanded(true);
        }

        // 블록을 보드에 배치하고 아이템 정보도 저장
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int newX = x + col;
                    int newY = y + row;

                    if (newY >= 0 && newY < BOARD_HEIGHT &&
                        newX >= 0 && newX < BOARD_WIDTH) {
                        board[newY][newX] = pieceType;
                        // 아이템 정보 저장
                        itemBoard[newY][newX] = piece.getItemAt(row, col);
                    }
                }
            }
        }
    }

    public int clearLines() {
        // 먼저 모든 가득 찬 줄의 인덱스를 수집
        List<Integer> fullLineIndices = new ArrayList<>();
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                fullLineIndices.add(row);
            }
        }
        
        // 찾은 줄이 없으면 빠르게 반환
        if (fullLineIndices.isEmpty()) {
            return 0;
        }
        
        // 내림차순으로 정렬하여 위에서부터 아래로 처리
        fullLineIndices.sort((a, b) -> b - a);
        
        // 한 번의 배열 재구성으로 모든 줄 삭제 처리
        int[] newBoard[] = new int[BOARD_HEIGHT][BOARD_WIDTH];
        ItemType[][] newItemBoard = new ItemType[BOARD_HEIGHT][BOARD_WIDTH];
        boolean[][] newAttackBoard = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        
        // 초기화
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                newBoard[i][j] = 0;
                newItemBoard[i][j] = ItemType.NONE;
                newAttackBoard[i][j] = false;
            }
        }
        
        // 삭제되지 않은 줄들을 새 배열에 복사 (위에서 채우기)
        int targetRow = BOARD_HEIGHT - 1;
        for (int sourceRow = BOARD_HEIGHT - 1; sourceRow >= 0; sourceRow--) {
            if (!fullLineIndices.contains(sourceRow)) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    newBoard[targetRow][col] = board[sourceRow][col];
                    newItemBoard[targetRow][col] = itemBoard[sourceRow][col];
                    newAttackBoard[targetRow][col] = attackBoard[sourceRow][col];
                }
                targetRow--;
            }
        }
        
        // 새 보드로 교체
        this.board = newBoard;
        this.itemBoard = newItemBoard;
        this.attackBoard = newAttackBoard;
        
        // 공격 줄이 삭제되면 공격 줄 수 업데이트
        updateAttackLinesCount();

        return fullLineIndices.size();
    }
    
    /**
     * 공격 줄 수를 실제 보드 상태에 맞게 업데이트
     */
    private void updateAttackLinesCount() {
        attackLinesCount = 0;
        // 아래쪽부터 공격 줄 수 계산
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            boolean hasAttackBlock = false;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (attackBoard[row][col]) {
                    hasAttackBlock = true;
                    break;
                }
            }
            if (hasAttackBlock) {
                attackLinesCount++;
            } else {
                // 공격 블록이 없는 줄을 만나면 중단 (연속된 공격 줄만 카운트)
                break;
            }
        }
    }
    
    /**
     * 삭제될 줄들의 행 번호를 반환 (애니메이션용)
     */
    public List<Integer> getFullLines() {
        List<Integer> fullLines = new ArrayList<>();
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            if (isLineFull(row)) {
                fullLines.add(row);
            }
        }
        return fullLines;
    }

    private boolean isLineFull(int row) {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            if (board[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    private void clearLine(int row) {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[row][col] = 0;
            itemBoard[row][col] = ItemType.NONE;
            attackBoard[row][col] = false; // 공격 블록도 초기화
        }
    }

    private void dropLinesAbove(int clearedRow) {
        // 공격 블록도 함께 이동
        for (int row = clearedRow; row > 0; row--) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = board[row - 1][col];
                itemBoard[row][col] = itemBoard[row - 1][col];
                attackBoard[row][col] = attackBoard[row - 1][col];
            }
        }

        for (int col = 0; col < BOARD_WIDTH; col++) {
            board[0][col] = 0;
            itemBoard[0][col] = ItemType.NONE;
            attackBoard[0][col] = false;
        }
    }

    public int getCell(int row, int col) {
        if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
            return board[row][col];
        }
        return 0;
    }

    public int getSpawnX() {
        return SPAWN_X;
    }

    public int getSpawnY() {
        return SPAWN_Y;
    }

    public int[][] getBoard() {
        return board;
    }

    /**
     * 특정 셀의 아이템 타입을 반환
     * @param row 행
     * @param col 열
     * @return 아이템 타입
     */
    public ItemType getItemAt(int row, int col) {
        if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
            return itemBoard[row][col];
        }
        return ItemType.NONE;
    }

    /**
     * 아이템 효과를 처리 (LINE_CLEAR, BOMB)
     * 블록이 배치될 때 호출되어, 아이템 효과를 적용
     * @param piece 배치된 블록
     * @return 삭제된 줄의 수
     */
    public int processItemEffects(Piece piece) {
        if (piece == null || !piece.hasItem()) {
            return 0;
        }

        List<Integer> rowsToClean = new ArrayList<>();
        List<Integer> bombRows = new ArrayList<>();
        List<Integer> bombCols = new ArrayList<>();

        // 블록이 배치된 위치에서 아이템 찾기
        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = piece.getItemAt(row, col);
                    int boardRow = y + row;
                    int boardCol = x + col;

                    if (itemType == ItemType.LINE_CLEAR) {
                        // LINE_CLEAR: 해당 줄 삭제
                        if (boardRow >= 0 && boardRow < BOARD_HEIGHT) {
                            if (!rowsToClean.contains(boardRow)) {
                                rowsToClean.add(boardRow);
                            }
                        }
                    } else if (itemType == ItemType.BOMB) {
                        // BOMB: 2x2 폭탄이 차지하는 모든 행과 열 수집
                        if (boardRow >= 0 && boardRow < BOARD_HEIGHT && !bombRows.contains(boardRow)) {
                            bombRows.add(boardRow);
                        }
                        if (boardCol >= 0 && boardCol < BOARD_WIDTH && !bombCols.contains(boardCol)) {
                            bombCols.add(boardCol);
                        }
                    }
                }
            }
        }

        // 폭탄 효과 처리: 해당 행과 열의 모든 블록 제거
        if (!bombRows.isEmpty() || !bombCols.isEmpty()) {
            processBombEffect(bombRows, bombCols);
        }

        // LINE_CLEAR 효과: 찾은 줄들을 삭제 (아래쪽부터)
        rowsToClean.sort((a, b) -> b - a);  // 내림차순 정렬
        for (int rowToClean : rowsToClean) {
            clearLine(rowToClean);
            dropLinesAbove(rowToClean);
        }

        return rowsToClean.size();
    }

    /**
     * 폭탄 효과 처리: 1x1 폭탄이 위치한 행과 열을 십자가(+) 모양으로 제거
     * @param bombRows 폭탄이 위치한 행
     * @param bombCols 폭탄이 위치한 열
     */
    private void processBombEffect(List<Integer> bombRows, List<Integer> bombCols) {
        // 폭탄이 위치한 열 전체 제거 (세로선)
        for (int col : bombCols) {
            if (col >= 0 && col < BOARD_WIDTH) {
                for (int row = 0; row < BOARD_HEIGHT; row++) {
                    clearCell(row, col);
                }
            }
        }

        // 폭탄이 위치한 행 전체 제거 (가로선)
        for (int row : bombRows) {
            if (row >= 0 && row < BOARD_HEIGHT) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    clearCell(row, col);
                }
            }
        }
    }

    /**
     * 특정 셀을 지움 (범위 체크 포함)
     * @param row 행
     * @param col 열
     */
    private void clearCell(int row, int col) {
        if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
            board[row][col] = 0;
            itemBoard[row][col] = ItemType.NONE;
        }
    }

    /**
     * 무게추 블록이 이동할 때 밑에 있는 블록들을 지우는 처리
     * @param piece 무게추 블록
     */
    public void processWeightEffect(Piece piece) {
        if (piece == null || !piece.isWeightPiece()) {
            return;
        }

        int[][] shape = piece.getShape();
        int x = piece.getX();
        int y = piece.getY();

        // 무게추 블록의 가장 아래쪽 행
        int bottomRow = 1;

        // 무게추의 가장 아래 행의 각 블록 밑에 있는 보드의 블록들을 지우기
        for (int col = 0; col < shape[bottomRow].length; col++) {
            if (shape[bottomRow][col] != 0) {
                int boardCol = x + col;
                int boardRow = y + bottomRow + 1;  // 무게추 바로 밑
                
                // 해당 열의 밑에 있는 모든 블록 지우기
                if (boardCol >= 0 && boardCol < BOARD_WIDTH && boardRow >= 0 && boardRow < BOARD_HEIGHT) {
                    if (board[boardRow][boardCol] != 0) {
                        piece.setLanded(true);
                        board[boardRow][boardCol] = 0;
                        itemBoard[boardRow][boardCol] = ItemType.NONE;
                    }
                }
            }
        }
    }
    
    /**
     * 공격 블록을 보드 아래쪽에 추가 (오버로드 - 빈칸 위치 없음)
     * @param numLines 추가할 줄 수
     */
    public void addAttackLines(int numLines) {
        addAttackLines(numLines, 0); // 기본 빈칸 위치
    }
    
    /**
     * 공격 블록을 보드 아래쪽에 추가
     * 최대 10줄까지만 추가 가능, 여러 번의 공격은 아래쪽으로 누적
     * @param numLines 추가할 줄 수
     * @param emptyCol 빈칸 위치 (마지막 블록이 채워진 열)
     */
    public void addAttackLines(int numLines, int emptyCol) {
        if (numLines <= 0) {
            return;
        }
        
        // 최대 10줄 제한
        int newAttackLinesCount = attackLinesCount + numLines;
        if (newAttackLinesCount > 10) {
            // 10줄을 넘으면 제일 아래쪽 부분을 잘라냄
            int excessLines = newAttackLinesCount - 10;
            removeBottomAttackLines(excessLines);
            numLines = 10 - attackLinesCount; // 추가할 수 있는 줄 수만큼만 추가
        }
        
        if (numLines <= 0) {
            return; // 추가할 줄이 없음
        }
        
        // 기존 블록들을 위로 올림 (공격 블록을 아래에 추가하기 위해)
        for (int row = 0; row < BOARD_HEIGHT - numLines; row++) {
            for (int col = 0; col < BOARD_WIDTH; col++) {
                board[row][col] = board[row + numLines][col];
                itemBoard[row][col] = itemBoard[row + numLines][col];
                attackBoard[row][col] = attackBoard[row + numLines][col];
            }
        }
        
        // 맨 아래쪽에 공격 블록 추가 (아래서부터 쌓임)
        // 보드를 위로 올린 후, 맨 아래(BOARD_HEIGHT - numLines 위치부터)에 공격 블록 추가
        // 기존 공격 블록은 이미 위로 올라갔으므로, 새로운 공격 블록은 그 아래에 추가
        for (int i = 0; i < numLines; i++) {
            int row = BOARD_HEIGHT - numLines + i;
            // 마지막 블록이 채워진 위치에 빈칸 생성
            int emptyColPos = Math.max(0, Math.min(emptyCol, BOARD_WIDTH - 1));
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (col == emptyColPos) {
                    board[row][col] = 0;
                    attackBoard[row][col] = false;
                } else {
                    board[row][col] = 8; // 공격 블록 타입 (회색으로 표시)
                    attackBoard[row][col] = true;
                }
                itemBoard[row][col] = ItemType.NONE;
            }
        }
        
        // 공격 줄 수 업데이트
        attackLinesCount += numLines;
    }
    
    /**
     * 보드 아래쪽에서 공격 줄 제거 (10줄 제한을 위해)
     * @param numLines 제거할 줄 수
     */
    private void removeBottomAttackLines(int numLines) {
        if (numLines <= 0 || attackLinesCount == 0) {
            return;
        }
        
        // 아래쪽부터 공격 줄 제거
        int linesToRemove = Math.min(numLines, attackLinesCount);
        for (int i = 0; i < linesToRemove; i++) {
            int row = BOARD_HEIGHT - 1 - i;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (attackBoard[row][col]) {
                    board[row][col] = 0;
                    attackBoard[row][col] = false;
                    itemBoard[row][col] = ItemType.NONE;
                }
            }
        }
        
        attackLinesCount -= linesToRemove;
        
        // 빈 공간을 채우기 위해 블록들을 아래로 이동
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            boolean isEmpty = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] != 0) {
                    isEmpty = false;
                    break;
                }
            }
            
            if (isEmpty && row > 0) {
                // 빈 줄을 위의 줄로 채움
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    board[row][col] = board[row - 1][col];
                    itemBoard[row][col] = itemBoard[row - 1][col];
                    attackBoard[row][col] = attackBoard[row - 1][col];
                }
            }
        }
    }
    
    /**
     * 현재 보드에 있는 공격 줄 수 반환
     * @return 공격 줄 수 (최대 10줄)
     */
    public int getAttackLinesCount() {
        return attackLinesCount;
    }
    
    /**
     * 공격 줄이 10줄인지 확인
     * @return 10줄이면 true
     */
    public boolean isAttackLinesFull() {
        return attackLinesCount >= 10;
    }
    
    /**
     * 특정 셀이 공격 블록인지 확인
     * @param row 행
     * @param col 열
     * @return 공격 블록이면 true
     */
    public boolean isAttackBlock(int row, int col) {
        if (row >= 0 && row < BOARD_HEIGHT && col >= 0 && col < BOARD_WIDTH) {
            return attackBoard[row][col];
        }
        return false;
    }
}