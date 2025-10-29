package tetris.game;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 조각을 시계 방향으로 90도 회전시킵니다.
     * 아이템이 있는 경우, 아이템의 위치를 조각의 중심을 기준으로 회전시켜
     * 회전 후에도 아이템이 기하학적으로 올바른 위치의 블록에 있도록 보장합니다.
     */
    public void rotate() {
        if (itemBlockIndex != -1) {
            // 1. 현재 아이템의 shape 배열 기준 좌표 (row, col)를 가져옵니다.
            int itemRow = getItemRow();
            int itemCol = getItemCol();
            // 2. 현재 조각의 기하학적 중심점을 계산합니다.
            double[] center = getCenter();
            // 3. 중심점 기준 아이템의 상대 좌표를 계산합니다.
            double relativeX = itemCol - center[1];
            double relativeY = itemRow - center[0];

            // 4. 상대 좌표를 시계 방향으로 90도 회전시킵니다. (x, y) -> (y, -x)
            double newRelativeX = -relativeY;
            double newRelativeY = relativeX;

            // 5. 조각의 회전 상태를 업데이트하고 새로운 shape 배열을 가져옵니다.
            rotation = (rotation + 1) % rotations.length;
            shape = rotations[rotation];
            // 6. 새로운 shape의 중심점을 다시 계산합니다.
            center = getCenter();

            // 7. 새로운 중심점과 회전된 상대 좌표를 이용해 아이템의 새로운 절대 좌표를 계산합니다.
            int newRow = (int) Math.round(center[0] + newRelativeY);
            int newCol = (int) Math.round(center[1] + newRelativeX);

            // 8. 계산된 새 좌표에서 가장 가까운 블록을 찾아 itemBlockIndex를 업데이트합니다.
            updateItemBlockIndex(newRow, newCol);
        } else {
            // 아이템이 없으면 단순히 회전만 수행합니다.
            rotation = (rotation + 1) % rotations.length;
            shape = rotations[rotation];
        }
    }

    /**
     * 조각을 반시계 방향으로 90도 회전시킵니다.
     * 아이템이 있는 경우, 아이템의 위치를 조각의 중심을 기준으로 회전시켜
     * 회전 후에도 아이템이 기하학적으로 올바른 위치의 블록에 있도록 보장합니다.
     */
    public void rotateBack() {
        if (itemBlockIndex != -1) {
            // 1. 현재 아이템의 shape 배열 기준 좌표 (row, col)를 가져옵니다.
            int itemRow = getItemRow();
            int itemCol = getItemCol();
            // 2. 현재 조각의 기하학적 중심점을 계산합니다.
            double[] center = getCenter();
            // 3. 중심점 기준 아이템의 상대 좌표를 계산합니다.
            double relativeX = itemCol - center[1];
            double relativeY = itemRow - center[0];

            // 4. 상대 좌표를 반시계 방향으로 90도 회전시킵니다. (x, y) -> (-y, x)
            double newRelativeX = relativeY;
            double newRelativeY = -relativeX;

            // 5. 조각의 회전 상태를 업데이트하고 새로운 shape 배열을 가져옵니다.
            rotation = (rotation - 1 + rotations.length) % rotations.length;
            shape = rotations[rotation];
            // 6. 새로운 shape의 중심점을 다시 계산합니다.
            center = getCenter();

            // 7. 새로운 중심점과 회전된 상대 좌표를 이용해 아이템의 새로운 절대 좌표를 계산합니다.
            int newRow = (int) Math.round(center[0] + newRelativeY);
            int newCol = (int) Math.round(center[1] + newRelativeX);

            // 8. 계산된 새 좌표에서 가장 가까운 블록을 찾아 itemBlockIndex를 업데이트합니다.
            updateItemBlockIndex(newRow, newCol);
        } else {
            // 아이템이 없으면 단순히 회전만 수행합니다.
            rotation = (rotation - 1 + rotations.length) % rotations.length;
            shape = rotations[rotation];
        }
    }

    /**
     * 현재 조각(shape)을 구성하는 모든 블록들의 기하학적 중심점을 계산합니다.
     * @return double 배열, [중심 y좌표, 중심 x좌표]
     */
    private double[] getCenter() {
        List<int[]> blockCoords = new ArrayList<>();
        // shape 배열을 순회하며 블록(0이 아닌 값)의 좌표를 리스트에 추가합니다.
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    blockCoords.add(new int[]{r, c});
                }
            }
        }
        double sumX = 0;
        double sumY = 0;
        // 모든 블록 좌표의 합을 구합니다.
        for (int[] coord : blockCoords) {
            sumY += coord[0];
            sumX += coord[1];
        }
        // 좌표 합을 블록의 개수로 나누어 평균, 즉 중심점을 계산합니다.
        return new double[]{sumY / blockCoords.size(), sumX / blockCoords.size()};
    }

    /**
     * 회전 후 계산된 목표 좌표(targetRow, targetCol)와 가장 가까운 블록을 찾아
     * 그 블록의 논리적 인덱스로 itemBlockIndex를 업데이트합니다.
     * @param targetRow 목표 행 좌표
     * @param targetCol 목표 열 좌표
     */
    private void updateItemBlockIndex(int targetRow, int targetCol) {
        int blockIndex = 0;
        int closestBlockIndex = -1;
        double minDistance = Double.MAX_VALUE;

        // 현재 shape의 모든 블록을 순회합니다.
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] != 0) {
                    // 각 블록과 목표 좌표 사이의 유클리드 거리를 계산합니다.
                    double distance = Math.sqrt(Math.pow(r - targetRow, 2) + Math.pow(c - targetCol, 2));
                    // 현재까지의 최소 거리보다 더 가까운 블록을 찾으면,
                    if (distance < minDistance) {
                        // 최소 거리를 업데이트하고 해당 블록의 논리적 인덱스를 저장합니다.
                        minDistance = distance;
                        closestBlockIndex = blockIndex;
                    }
                    blockIndex++;
                }
            }
        }
        // 가장 가까웠던 블록의 인덱스로 itemBlockIndex를 업데이트합니다.
        this.itemBlockIndex = closestBlockIndex;
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
        if (row >= 0 && row < shape.length && col >= 0 && col < shape[row].length) {
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