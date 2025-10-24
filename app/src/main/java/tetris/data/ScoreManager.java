package tetris.data;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private static final String SCORE_FILE = "scores.dat";
    private static final int MAX_SCORES = 10;
    private static ScoreManager instance;
    
    private List<ScoreEntry> scores;
    
    private ScoreManager() {
        scores = new ArrayList<>();
        loadScores();
    }
    
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }
    
    public boolean addScore(String playerName, int score) {
        ScoreEntry newEntry = new ScoreEntry(playerName, score);
        scores.add(newEntry);
        Collections.sort(scores);
        
        boolean isTopTen = scores.indexOf(newEntry) < MAX_SCORES;
        
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }
        
        saveScores();
        return isTopTen;
    }
    
    public List<String> getFormattedScores() {
        List<String> formatted = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            formatted.add((i + 1) + ". " + scores.get(i).toString());
        }
        return formatted;
    }
    
    public void clearScores() {
        scores.clear();
        saveScores();
    }
    
    public int getRank(int score) {
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).getScore() <= score) {
                return i + 1;
            }
        }
        return scores.size() < MAX_SCORES ? scores.size() + 1 : -1;
    }
    
    public int getHighScore() {
        return scores.isEmpty() ? 0 : scores.get(0).getScore();
    }
    
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            System.err.println("Failed to save scores: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(SCORE_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCORE_FILE))) {
            scores = (List<ScoreEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load scores: " + e.getMessage());
            scores = new ArrayList<>();
        }
    }
}