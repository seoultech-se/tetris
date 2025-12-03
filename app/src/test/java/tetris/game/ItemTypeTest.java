package tetris.game;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemTypeTest {

    @Test
    void testGetDisplayChar() {
        assertEquals("L", ItemType.LINE_CLEAR.getDisplayChar());
        assertEquals("W", ItemType.WEIGHT.getDisplayChar());
        assertEquals("D", ItemType.DOUBLE_SCORE.getDisplayChar());
        assertEquals("B", ItemType.BOMB.getDisplayChar());
        assertEquals("S", ItemType.SKIP.getDisplayChar());
        assertEquals("", ItemType.NONE.getDisplayChar());
    }

    @Test
    void testItemTypeValues() {
        ItemType[] types = ItemType.values();
        assertTrue(types.length >= 6); // 최소 6개의 아이템 타입
    }
}

