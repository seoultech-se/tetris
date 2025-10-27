package tetris.game;

public class Piece {
    private int[][] shape;
    private int x;
    private int y;
    private int type;
    private int rotation;
    private int[][][] rotations;

    // 아이템 정보
    private ItemType[][] itemGrid;  // 각 블록 셀의 아이템 타입
    private int itemRow;  // 아이템이 있는 행 (shape 배열 기준)
    private int itemCol;  // 아이템이 있는 열 (shape 배열 기준)

    public Piece(int[][][] rotations, int type) {
        this.rotations = rotations;
        this.type = type;
        this.rotation = 0;
        this.shape = rotations[0];
        this.x = 0;
        this.y = 0;

        // 아이템 그리드 초기화 (모든 셀을 NONE으로)
        this.itemGrid = new ItemType[shape.length][shape[0].length];
        for (int i = 0; i < itemGrid.length; i++) {
            for (int j = 0; j < itemGrid[i].length; j++) {
                itemGrid[i][j] = ItemType.NONE;
            }
        }
        this.itemRow = -1;
        this.itemCol = -1;
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
        int newRotation = (rotation + 1) % rotations.length;
        rotation = newRotation;
        shape = rotations[rotation];
    }

    public void rotateBack() {
        int newRotation = (rotation - 1 + rotations.length) % rotations.length;
        rotation = newRotation;
        shape = rotations[rotation];
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

        // 아이템 정보 복사
        copy.itemGrid = new ItemType[this.itemGrid.length][this.itemGrid[0].length];
        for (int i = 0; i < this.itemGrid.length; i++) {
            for (int j = 0; j < this.itemGrid[i].length; j++) {
                copy.itemGrid[i][j] = this.itemGrid[i][j];
            }
        }
        copy.itemRow = this.itemRow;
        copy.itemCol = this.itemCol;

        return copy;
    }

    /**
     * 특정 셀에 아이템을 설정
     * @param row 행 (shape 배열 기준)
     * @param col 열 (shape 배열 기준)
     * @param itemType 아이템 타입
     */
    public void setItemAt(int row, int col, ItemType itemType) {
        if (row >= 0 && row < itemGrid.length && col >= 0 && col < itemGrid[0].length) {
            itemGrid[row][col] = itemType;
            if (itemType != ItemType.NONE) {
                this.itemRow = row;
                this.itemCol = col;
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
        if (row >= 0 && row < itemGrid.length && col >= 0 && col < itemGrid[0].length) {
            return itemGrid[row][col];
        }
        return ItemType.NONE;
    }

    /**
     * 아이템이 있는 행을 반환 (shape 배열 기준)
     * @return 아이템 행, 아이템이 없으면 -1
     */
    public int getItemRow() {
        return itemRow;
    }

    /**
     * 아이템이 있는 열을 반환 (shape 배열 기준)
     * @return 아이템 열, 아이템이 없으면 -1
     */
    public int getItemCol() {
        return itemCol;
    }

    /**
     * 이 블록이 아이템을 가지고 있는지 확인
     * @return 아이템이 있으면 true, 없으면 false
     */
    public boolean hasItem() {
        return itemRow != -1 && itemCol != -1;
    }
}