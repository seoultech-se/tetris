package tetris.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class ScoreEntryTest {

    @Test
    void testScoreEntryCreation() {
        ScoreEntry entry = new ScoreEntry("Player1", 1000, "Normal", "NORMAL");
        
        assertEquals("Player1", entry.getPlayerName());
        assertEquals(1000, entry.getScore());
        assertEquals("Normal", entry.getDifficulty());
        assertEquals("NORMAL", entry.getGameMode());
        assertNotNull(entry.getDate());
    }

    @Test
    void testGetFormattedDate() {
        ScoreEntry entry = new ScoreEntry("Player1", 1000, "Normal", "NORMAL");
        String formatted = entry.getFormattedDate();
        
        assertNotNull(formatted);
        assertTrue(formatted.length() > 0);
    }

    @Test
    void testCompareTo() {
        ScoreEntry entry1 = new ScoreEntry("Player1", 1000, "Normal", "NORMAL");
        ScoreEntry entry2 = new ScoreEntry("Player2", 500, "Normal", "NORMAL");
        ScoreEntry entry3 = new ScoreEntry("Player3", 1000, "Normal", "NORMAL");
        
        assertTrue(entry2.compareTo(entry1) > 0); // entry2의 점수가 더 낮으므로 compareTo는 양수
        assertEquals(0, entry1.compareTo(entry3)); // 같은 점수
    }

    @Test
    void testToString() {
        ScoreEntry entry = new ScoreEntry("Player1", 1234, "Normal", "NORMAL");
        String str = entry.toString();
        
        assertTrue(str.contains("Player1"));
        assertTrue(str.contains("1234"));
    }
}

