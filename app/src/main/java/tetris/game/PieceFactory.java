package tetris.game;

import java.util.ArrayList;
import java.util.List;
import tetris.ui.SettingsManager;

public class PieceFactory {
    // 테트리스 피스 타입 정의
    public static final int I_PIECE = 1;
    public static final int O_PIECE = 2;
    public static final int T_PIECE = 3;
    public static final int S_PIECE = 4;
    public static final int Z_PIECE = 5;
    public static final int J_PIECE = 6;
    public static final int L_PIECE = 7;
    public static final int WEIGHT_PIECE = 8;  // 무게추 아이템 블록

    // I 피스 (일자형)
    private static final int[][][] I_ROTATIONS = {
        {{1, 1, 1, 1}},
        {{1}, {1}, {1}, {1}}
    };

    // O 피스 (정사각형)
    private static final int[][][] O_ROTATIONS = {
        {{2, 2}, {2, 2}}
    };

    // T 피스 (T자형)
    private static final int[][][] T_ROTATIONS = {
        {{0, 3, 0}, {3, 3, 3}},
        {{3, 0}, {3, 3}, {3, 0}},
        {{3, 3, 3}, {0, 3, 0}},
        {{0, 3}, {3, 3}, {0, 3}}
    };

    // S 피스
    private static final int[][][] S_ROTATIONS = {
        {{0, 4, 4}, {4, 4, 0}},
        {{4, 0}, {4, 4}, {0, 4}}
    };

    // Z 피스
    private static final int[][][] Z_ROTATIONS = {
        {{5, 5, 0}, {0, 5, 5}},
        {{0, 5}, {5, 5}, {5, 0}}
    };

    // J 피스
    private static final int[][][] J_ROTATIONS = {
        {{6, 0, 0}, {6, 6, 6}},
        {{6, 6}, {6, 0}, {6, 0}},
        {{6, 6, 6}, {0, 0, 6}},
        {{0, 6}, {0, 6}, {6, 6}}
    };

    // L 피스
    private static final int[][][] L_ROTATIONS = {
        {{0, 0, 7}, {7, 7, 7}},
        {{7, 0}, {7, 0}, {7, 7}},
        {{7, 7, 7}, {7, 0, 0}},
        {{7, 7}, {0, 7}, {0, 7}}
    };

    // 무게추 피스 (Weight) - 회전 불가
    // 모양:   WW
    //       WWWW
    private static final int[][][] WEIGHT_ROTATIONS = {
        {{0, 8, 8, 0}, {8, 8, 8, 8}}
    };

    private static List<Integer> pieceBag = new ArrayList<>();
    private static int bagIndex = 0;

    public static Piece createPiece(int type) {
        switch (type) {
            case I_PIECE:
                return new Piece(I_ROTATIONS, I_PIECE);
            case O_PIECE:
                return new Piece(O_ROTATIONS, O_PIECE);
            case T_PIECE:
                return new Piece(T_ROTATIONS, T_PIECE);
            case S_PIECE:
                return new Piece(S_ROTATIONS, S_PIECE);
            case Z_PIECE:
                return new Piece(Z_ROTATIONS, Z_PIECE);
            case J_PIECE:
                return new Piece(J_ROTATIONS, J_PIECE);
            case L_PIECE:
                return new Piece(L_ROTATIONS, L_PIECE);
            case WEIGHT_PIECE:
                return createWeightPiece();
            default:
                return createPiece(I_PIECE);
        }
    }

    /**
     * 무작위 블록 생성 (아이템 없음)
     * @return 생성된 블록
     */
    public static Piece createRandomPiece() {
        return createRandomPiece(false);
    }

    /**
     * 무작위 블록 생성 (아이템 포함 여부 지정 가능)
     * @param shouldHaveItem 아이템 포함 여부
     * @return 생성된 블록
     */
    public static Piece createRandomPiece(boolean shouldHaveItem) {
        SettingsManager settings = SettingsManager.getInstance();
        String difficulty = settings.getDifficulty();

        // 아이템을 생성해야 하는 경우 50% 확률로 LINE_CLEAR 또는 WEIGHT 선택
        if (shouldHaveItem) {
            double random = Math.random();
            if (random < 0.5) {
                // LINE_CLEAR 아이템 블록 생성
                if (bagIndex >= pieceBag.size()) {
                    refillBag(difficulty);
                }
                int type = pieceBag.get(bagIndex++);
                Piece piece = createPiece(type);
                addItemToPiece(piece, ItemType.LINE_CLEAR);
                return piece;
            } else {
                // WEIGHT 블록 생성 (무게추는 블록 전체가 아이템)
                return createWeightPiece();
            }
        }

        // 일반 블록 생성
        if (bagIndex >= pieceBag.size()) {
            refillBag(difficulty);
        }

        // 가방에서 다음 블럭을 순서대로 꺼내기
        int type = pieceBag.get(bagIndex++);
        Piece piece = createPiece(type);

        return piece;
    }

    private static void refillBag(String difficulty) {
        pieceBag.clear();
        
        for (int i = 2; i <= 7; i++) {
            for (int j = 0; j < 10; j++) {
                pieceBag.add(i);
            }
        }

        // 난이도에 따라 I 블록의 개수 조절
        int iPieceCount;
        if ("Easy".equals(difficulty)) {
            iPieceCount = 12;
        } else if ("Hard".equals(difficulty)) {
            iPieceCount = 8;
        } else {    // Normal
            iPieceCount = 10;
        }

        for (int i = 0; i < iPieceCount; i++) {
            pieceBag.add(I_PIECE);
        }

        // 가방을 무작위로 섞음
        java.util.Collections.shuffle(pieceBag);
        bagIndex = 0;
    }



    public static Piece createIPiece() {
        return createPiece(I_PIECE);
    }

    public static Piece createOPiece() {
        return createPiece(O_PIECE);
    }

    public static Piece createTPiece() {
        return createPiece(T_PIECE);
    }

    public static Piece createSPiece() {
        return createPiece(S_PIECE);
    }

    public static Piece createZPiece() {
        return createPiece(Z_PIECE);
    }

    public static Piece createJPiece() {
        return createPiece(J_PIECE);
    }

    public static Piece createLPiece() {
        return createPiece(L_PIECE);
    }

    /**
     * 무게추 블록 생성
     * @return 무게추 블록 (모든 셀에 WEIGHT 아이템 표시)
     */
    public static Piece createWeightPiece() {
        Piece piece = new Piece(WEIGHT_ROTATIONS, WEIGHT_PIECE);

        // 무게추 블록의 모든 셀에 WEIGHT 아이템 표시
        int[][] shape = piece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    piece.setItemAt(row, col, ItemType.WEIGHT);
                }
            }
        }

        return piece;
    }

    /**
     * 블록의 무작위 위치에 아이템을 추가
     * @param piece 아이템을 추가할 블록
     * @param itemType 추가할 아이템 타입
     */
    private static void addItemToPiece(Piece piece, ItemType itemType) {
        int[][] shape = piece.getShape();
        List<int[]> validPositions = new ArrayList<>();

        // 블록이 있는 모든 셀의 위치를 찾기
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {  // 블록이 있는 셀
                    validPositions.add(new int[]{i, j});
                }
            }
        }

        // 유효한 위치가 있으면 무작위로 하나 선택
        if (!validPositions.isEmpty()) {
            int randomIndex = (int) (Math.random() * validPositions.size());
            int[] position = validPositions.get(randomIndex);
            piece.setItemAt(position[0], position[1], itemType);
        }
    }
}