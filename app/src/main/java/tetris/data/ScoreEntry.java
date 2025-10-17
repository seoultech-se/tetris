package tetris.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
    private static final long serialVersionUID = 1L;
    
    private final String playerName;
    private final int score;
    private final LocalDateTime date;
    
    public ScoreEntry(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
        this.date = LocalDateTime.now();
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public int getScore() {
        return score;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return date.format(formatter);
    }
    
    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %d점 (%s)", playerName, score, getFormattedDate());
    }
}