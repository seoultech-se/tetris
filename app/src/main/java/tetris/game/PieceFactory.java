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
            default:
                return createPiece(I_PIECE);
        }
    }

    public static Piece createRandomPiece() {
        SettingsManager settings = SettingsManager.getInstance();
        String difficulty = settings.getDifficulty();
         
        if (bagIndex >= pieceBag.size()) {
            refillBag(difficulty);
        }

        // 가방에서 다음 블럭을 순서대로 꺼내기
        int type = pieceBag.get(bagIndex++);
        return createPiece(type);
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
}