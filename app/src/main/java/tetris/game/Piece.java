package tetris.game;

public class Piece {
    private int[][] shape;
    private int x;
    private int y;
    private int type;
    private int rotation;
    private int[][][] rotations;

    // 아이템 정보
    private ItemType itemType;  // 아이템 타입 (NONE, LINE_CLEAR, DOUBLE_SCORE, SKIP 등)
    private int itemBlockIndex;  // 아이템이 있는 블록의 논리적 인덱스 (0부터 시작, -1이면 없음)

    // 무게추 아이템 관련
    private boolean hasLanded;  // 블록이 한 번이라도 착지했는지 여부 (무게추 전용)

    public Piece(int[][][] rotations, int type) {
        this.rotations = rotations;
        this.type = type;
        this.rotation = 0;
        this.shape = rotations[0];
        this.x = 0;
        this.y = 0;
        this.hasLanded = false;

        // 아이템 정보 초기화
        this.itemType = ItemType.NONE;
        this.itemBlockIndex = -1;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void moveDown() {
        y++;
    }

    public void moveUp() {
        y--;
    }

    public void rotate() {
        // 회전 수행 (아이템 정보는 itemBlockIndex에 저장되어 있으므로 그대로 유지됨)
        int newRotation = (rotation + 1) % rotations.length;
        rotation = newRotation;
        shape = rotations[rotation];
        // itemType과 itemBlockIndex는 회전과 무관하게 유지됨
    }

    public void rotateBack() {
        // 역회전 수행 (아이템 정보는 itemBlockIndex에 저장되어 있으므로 그대로 유지됨)
        int newRotation = (rotation - 1 + rotations.length) % rotations.length;
        rotation = newRotation;
        shape = rotations[rotation];
        // itemType과 itemBlockIndex는 회전과 무관하게 유지됨
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public int getRotation() {
        return rotation;
    }

    public Piece copy() {
        Piece copy = new Piece(rotations, type);
        copy.x = this.x;
        copy.y = this.y;
        copy.rotation = this.rotation;
        copy.shape = this.shape;
        copy.hasLanded = this.hasLanded;

        // 아이템 정보 복사
        copy.itemType = this.itemType;
        copy.itemBlockIndex = this.itemBlockIndex;

        return copy;
    }

    /**
     * 특정 셀에 아이템을 설정
     * @param row 행 (shape 배열 기준)
     * @param col 열 (shape 배열 기준)
     * @param itemType 아이템 타입
     */
    public void setItemAt(int row, int col, ItemType itemType) {
        if (row >= 0 && row < shape.length && col >= 0 && col < shape[0].length) {
            if (shape[row][col] != 0) {  // 블록이 있는 위치에만 아이템 설정 가능
                // row, col을 논리적 인덱스로 변환
                int blockIndex = 0;
                boolean found = false;
                for (int r = 0; r < shape.length && !found; r++) {
                    for (int c = 0; c < shape[r].length && !found; c++) {
                        if (shape[r][c] != 0) {
                            if (r == row && c == col) {
                                this.itemType = itemType;
                                this.itemBlockIndex = itemType != ItemType.NONE ? blockIndex : -1;
                                found = true;
                            }
                            blockIndex++;
                        }
                    }
                }
            }
        }
    }

    /**
     * 특정 셀의 아이템 타입을 반환
     * @param row 행 (shape 배열 기준)
     * @param col 열 (shape 배열 기준)
     * @return 아이템 타입
     */
    public ItemType getItemAt(int row, int col) {
        if (itemBlockIndex == -1 || itemType == ItemType.NONE) {
            return ItemType.NONE;
        }

        if (row >= 0 && row < shape.length && col >= 0 && col < shape[0].length) {
            if (shape[row][col] != 0) {  // 블록이 있는 위치만 확인
                // row, col을 논리적 인덱스로 변환하여 확인
                int blockIndex = 0;
                for (int r = 0; r < shape.length; r++) {
                    for (int c = 0; c < shape[r].length; c++) {
                        if (shape[r][c] != 0) {
                            if (r == row && c == col) {
                                return blockIndex == itemBlockIndex ? itemType : ItemType.NONE;
                            }
                            blockIndex++;
                        }
                    }
                }
            }
        }
        return ItemType.NONE;
    }

    /**
     * 아이템이 있는 행을 반환 (shape 배열 기준)
     * @return 아이템 행, 아이템이 없으면 -1
     */
    public int getItemRow() {
        if (itemBlockIndex == -1) {
            return -1;
        }

        // 논리적 인덱스를 현재 shape의 row로 변환
        int blockIndex = 0;
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    if (blockIndex == itemBlockIndex) {
                        return r;
                    }
                    blockIndex++;
                }
            }
        }
        return -1;
    }

    /**
     * 아이템이 있는 열을 반환 (shape 배열 기준)
     * @return 아이템 열, 아이템이 없으면 -1
     */
    public int getItemCol() {
        if (itemBlockIndex == -1) {
            return -1;
        }

        // 논리적 인덱스를 현재 shape의 col로 변환
        int blockIndex = 0;
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    if (blockIndex == itemBlockIndex) {
                        return c;
                    }
                    blockIndex++;
                }
            }
        }
        return -1;
    }

    /**
     * 이 블록이 아이템을 가지고 있는지 확인
     * @return 아이템이 있으면 true, 없으면 false
     */
    public boolean hasItem() {
        return itemBlockIndex != -1 && itemType != ItemType.NONE;
    }

    /**
     * 블록이 착지했는지 여부를 반환
     * @return 착지했으면 true, 아니면 false
     */
    public boolean hasLanded() {
        return hasLanded;
    }

    /**
     * 블록의 착지 상태를 설정
     * @param hasLanded 착지 여부
     */
    public void setLanded(boolean hasLanded) {
        this.hasLanded = hasLanded;
    }

    /**
     * 이 블록이 WEIGHT 타입인지 확인
     * @return WEIGHT 타입이면 true, 아니면 false
     */
    public boolean isWeightPiece() {
        return this.type == PieceFactory.WEIGHT_PIECE;
    }
}