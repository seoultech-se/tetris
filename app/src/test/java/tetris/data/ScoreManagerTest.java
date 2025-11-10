package tetris.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class ScoreManagerTest {

    @BeforeEach
    void setUp() {
        // 테스트 전에 ScoreManager 인스턴스 초기화를 위해
        try {
            // 테스트용 임시 디렉토리 사용
            Path testDataDir = Paths.get(System.getProperty("java.io.tmpdir"), "TetrisTest");
            if (Files.exists(testDataDir)) {
                Files.list(testDataDir)
                    .filter(p -> p.toString().endsWith(".dat"))
                    .forEach(p -> {
                        try { Files.delete(p); } catch (Exception e) {}
                    });
            }
        } catch (Exception e) {
            // 무시
        }
    }

    @Test
    void testGetInstance() {
        ScoreManager instance1 = ScoreManager.getInstance();
        ScoreManager instance2 = ScoreManager.getInstance();
        
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    void testAddScore_Normal() {
        ScoreManager manager = ScoreManager.getInstance();
        
        boolean isTopTen = manager.addScore("Player1", 1000, "Normal", "NORMAL");
        assertTrue(isTopTen); // 첫 점수이므로 상위 10위 안
        
        assertEquals(1000, manager.getHighScore("NORMAL"));
    }

    @Test
    void testAddScore_Item() {
        ScoreManager manager = ScoreManager.getInstance();
        
        boolean isTopTen = manager.addScore("Player1", 500, "Normal", "ITEM");
        
        assertEquals(500, manager.getHighScore("ITEM"));
    }

    @Test
    void testGetFormattedScores() {
        ScoreManager manager = ScoreManager.getInstance();
        
        manager.addScore("Player1", 1000, "Normal", "NORMAL");
        manager.addScore("Player2", 500, "Easy", "NORMAL");
        
        var scores = manager.getFormattedScores("NORMAL");
        assertNotNull(scores);
        assertTrue(scores.size() > 0);
    }

    @Test
    void testGetFormattedScoresByDifficulty() {
        ScoreManager manager = ScoreManager.getInstance();
        
        manager.addScore("Player1", 1000, "Normal", "NORMAL");
        manager.addScore("Player2", 500, "Easy", "NORMAL");
        
        var scores = manager.getFormattedScoresByDifficulty("NORMAL", "Normal");
        assertNotNull(scores);
    }

    @Test
    void testClearScores() {
        ScoreManager manager = ScoreManager.getInstance();
        
        manager.addScore("Player1", 1000, "Normal", "NORMAL");
        manager.clearScores("NORMAL");
        
        assertEquals(0, manager.getHighScore("NORMAL"));
    }

    @Test
    void testClearScoresByDifficulty() {
        ScoreManager manager = ScoreManager.getInstance();
        
        manager.addScore("Player1", 1000, "Normal", "NORMAL");
        manager.addScore("Player2", 500, "Easy", "NORMAL");
        
        manager.clearScoresByDifficulty("NORMAL", "Easy");
        
        assertEquals(1000, manager.getHighScore("NORMAL"));
    }

    @Test
    void testGetRank() {
        ScoreManager manager = ScoreManager.getInstance();
        
        manager.addScore("Player1", 1000, "Normal", "NORMAL");
        
        int rank = manager.getRank(500, "NORMAL");
        assertTrue(rank >= 1);
    }

    @Test
    void testGetRankByDifficulty() {
        ScoreManager manager = ScoreManager.getInstance();
        
        manager.addScore("Player1", 1000, "Normal", "NORMAL");
        
        int rank = manager.getRankByDifficulty(500, "NORMAL", "Normal");
        assertTrue(rank >= 1);
    }
}

