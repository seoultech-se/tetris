package tetris.data;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private static final String NORMAL_SCORE_FILE = "scores_normal.dat";
    private static final String ITEM_SCORE_FILE = "scores_item.dat";
    private static final int MAX_SCORES = 10;
    private static ScoreManager instance;
    
    private List<ScoreEntry> normalScores;
    private List<ScoreEntry> itemScores;
    
    private ScoreManager() {
        normalScores = new ArrayList<>();
        itemScores = new ArrayList<>();
        loadScores();
    }
    
    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }
    
    public boolean addScore(String playerName, int score, String difficulty, String gameMode) {
        ScoreEntry newEntry = new ScoreEntry(playerName, score, difficulty, gameMode);
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        
        targetScores.add(newEntry);
        Collections.sort(targetScores);
        
        boolean isTopTen = targetScores.indexOf(newEntry) < MAX_SCORES;
        
        if (targetScores.size() > MAX_SCORES) {
            List<ScoreEntry> limitedScores = targetScores.subList(0, MAX_SCORES);
            targetScores.clear();
            targetScores.addAll(limitedScores);
        }
        
        saveScores(gameMode);
        return isTopTen;
    }
    
    public List<String> getFormattedScores(String gameMode) {
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        List<String> formatted = new ArrayList<>();
        for (int i = 0; i < targetScores.size(); i++) {
            formatted.add((i + 1) + ". " + targetScores.get(i).toString());
        }
        return formatted;
    }
    
    public List<String> getFormattedScoresByDifficulty(String gameMode, String difficulty) {
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        List<ScoreEntry> filteredScores = new ArrayList<>();
        
        // 해당 난이도의 점수만 필터링
        for (ScoreEntry entry : targetScores) {
            if (entry.getDifficulty().equals(difficulty)) {
                filteredScores.add(entry);
            }
        }
        
        // 정렬
        Collections.sort(filteredScores);
        
        // 상위 10개만 선택
        List<ScoreEntry> topScores = filteredScores.size() > MAX_SCORES 
            ? filteredScores.subList(0, MAX_SCORES) 
            : filteredScores;
        
        // 포맷팅
        List<String> formatted = new ArrayList<>();
        for (int i = 0; i < topScores.size(); i++) {
            formatted.add((i + 1) + ". " + topScores.get(i).toString());
        }
        return formatted;
    }
    
    public void clearScores(String gameMode) {
        if (gameMode.equals("ITEM")) {
            itemScores.clear();
        } else {
            normalScores.clear();
        }
        saveScores(gameMode);
    }
    
    public void clearScoresByDifficulty(String gameMode, String difficulty) {
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        targetScores.removeIf(entry -> entry.getDifficulty().equals(difficulty));
        saveScores(gameMode);
    }
    
    public int getRank(int score, String gameMode) {
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        for (int i = 0; i < targetScores.size(); i++) {
            if (targetScores.get(i).getScore() <= score) {
                return i + 1;
            }
        }
        return targetScores.size() < MAX_SCORES ? targetScores.size() + 1 : -1;
    }
    
    public int getHighScore(String gameMode) {
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        return targetScores.isEmpty() ? 0 : targetScores.get(0).getScore();
    }
    
    private void saveScores(String gameMode) {
        String fileName = gameMode.equals("ITEM") ? ITEM_SCORE_FILE : NORMAL_SCORE_FILE;
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(targetScores);
        } catch (IOException e) {
            System.err.println("Failed to save scores: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadScores() {
        // Load normal scores
        File normalFile = new File(NORMAL_SCORE_FILE);
        if (normalFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(NORMAL_SCORE_FILE))) {
                normalScores = (List<ScoreEntry>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load normal scores: " + e.getMessage());
                normalScores = new ArrayList<>();
            }
        }
        
        // Load item scores
        File itemFile = new File(ITEM_SCORE_FILE);
        if (itemFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ITEM_SCORE_FILE))) {
                itemScores = (List<ScoreEntry>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load item scores: " + e.getMessage());
                itemScores = new ArrayList<>();
            }
        }
    }
}