package tetris.game;

/**
 * 테트리스 게임의 아이템 타입을 정의하는 enum
 */
public enum ItemType {
    /**
     * 아이템이 없는 일반 블록
     */
    NONE,

    /**
     * 줄 삭제 아이템 (Line Clear)
     * 블록이 착지할 때 'L'이 위치한 가로 줄을 즉시 삭제
     */
    LINE_CLEAR;

    /**
     * 아이템 타입의 표시 문자를 반환
     * @return LINE_CLEAR의 경우 'L', 그 외는 빈 문자열
     */
    public String getDisplayChar() {
        return this == LINE_CLEAR ? "L" : "";
    }
}
