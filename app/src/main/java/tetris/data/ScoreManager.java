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
        
        // 난이도별로 점수 10개씩 제한
        if (gameMode.equals("NORMAL")) {
            // 일반 모드는 난이도별로 10개씩 유지
            limitScoresByDifficulty(targetScores);
        } else {
            // 아이템 모드는 전체 10개 유지
            if (targetScores.size() > MAX_SCORES) {
                List<ScoreEntry> limitedScores = new ArrayList<>(targetScores.subList(0, MAX_SCORES));
                targetScores.clear();
                targetScores.addAll(limitedScores);
            }
        }
        
        // 추가된 점수가 상위 10위 안에 드는지 확인 (난이도별로 확인)
        boolean isTopTen = false;
        if (gameMode.equals("NORMAL")) {
            // 같은 난이도 내에서 순위 확인
            int rankInDifficulty = 0;
            for (ScoreEntry entry : targetScores) {
                if (entry.getDifficulty().equals(difficulty)) {
                    rankInDifficulty++;
                    if (entry.equals(newEntry)) {
                        isTopTen = rankInDifficulty <= MAX_SCORES;
                        break;
                    }
                }
            }
        } else {
            isTopTen = targetScores.indexOf(newEntry) < MAX_SCORES;
        }
        
        saveScores(gameMode);
        return isTopTen;
    }
    
    private void limitScoresByDifficulty(List<ScoreEntry> scores) {
        // 각 난이도별로 점수 10개씩만 유지
        List<String> difficulties = List.of("Easy", "Normal", "Hard");
        List<ScoreEntry> limitedScores = new ArrayList<>();
        
        for (String difficulty : difficulties) {
            List<ScoreEntry> difficultyScores = new ArrayList<>();
            for (ScoreEntry entry : scores) {
                if (entry.getDifficulty().equals(difficulty)) {
                    difficultyScores.add(entry);
                }
            }
            
            // 해당 난이도에서 상위만 점수 10개만 추가
            int count = Math.min(difficultyScores.size(), MAX_SCORES);
            for (int i = 0; i < count; i++) {
                limitedScores.add(difficultyScores.get(i));
            }
        }
        
        scores.clear();
        scores.addAll(limitedScores);
        Collections.sort(scores); // 전체 정렬
    }
    
    public List<String> getFormattedScores(String gameMode) {
        List<ScoreEntry> targetScores = gameMode.equals("ITEM") ? itemScores : normalScores;
        List<String> formatted = new ArrayList<>();
        
        int currentRank = 1;
        for (int i = 0; i < targetScores.size(); i++) {
            // 이전 점수와 다르면 순위 업데이트
            if (i > 0 && targetScores.get(i).getScore() != targetScores.get(i - 1).getScore()) {
                currentRank = i + 1;
            }
            formatted.add(currentRank + ". " + targetScores.get(i).toString());
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
        
        // 최대 10개만 유지
        List<ScoreEntry> topScores = filteredScores.size() > MAX_SCORES 
            ? filteredScores.subList(0, MAX_SCORES) 
            : filteredScores;
        
        // 포맷팅 - 같은 점수는 같은 순위
        List<String> formatted = new ArrayList<>();
        int currentRank = 1;
        for (int i = 0; i < topScores.size(); i++) {
            // 이전 점수와 다르면 순위 업데이트
            if (i > 0 && topScores.get(i).getScore() != topScores.get(i - 1).getScore()) {
                currentRank = i + 1;
            }
            formatted.add(currentRank + ". " + topScores.get(i).toString());
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
    
    /**
     * 특정 모드와 난이도 내에서의 순위를 반환
     */
    public int getRankByDifficulty(int score, String gameMode, String difficulty) {
        if (gameMode.equals("ITEM")) {
            // 아이템 모드는 난이도가 없으므로 전체 순위 반환
            return getRank(score, gameMode);
        }
        
        // 일반 모드: 해당 난이도 내에서의 순위 계산
        List<ScoreEntry> targetScores = normalScores;
        List<ScoreEntry> difficultyScores = new ArrayList<>();
        
        // 해당 난이도의 점수만 필터링
        for (ScoreEntry entry : targetScores) {
            if (entry.getDifficulty().equals(difficulty)) {
                difficultyScores.add(entry);
            }
        }
        
        // 정렬
        Collections.sort(difficultyScores);
        
        // 순위 찾기
        int rank = 1;
        for (int i = 0; i < difficultyScores.size(); i++) {
            // 동점자 처리
            if (i > 0 && difficultyScores.get(i).getScore() != difficultyScores.get(i - 1).getScore()) {
                rank = i + 1;
            }
            
            if (difficultyScores.get(i).getScore() <= score) {
                return rank;
            }
        }
        
        return difficultyScores.size() < MAX_SCORES ? difficultyScores.size() + 1 : -1;
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