package tetris.game;

import tetris.ui.SettingsManager;

public class GameEngine {
    private GameBoard gameBoard;
    private Piece currentPiece;
    private Piece nextPiece;
    private int score;
    private int level;
    private int linesCleared;
    private int linesClearedSinceLastItem;  // 마지막 아이템 이후 삭제된 줄 수
    private boolean isGameRunning;
    private boolean isPaused;

    // 점수 2배 아이템 관련
    private boolean isDoubleScoreActive;
    private long doubleScoreEndTime;  // 나노초 단위
    private static final long DOUBLE_SCORE_DURATION = 30_000_000_000L;  // 30초

    public GameEngine() {
        this.gameBoard = new GameBoard();
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
        this.linesClearedSinceLastItem = 0;
        this.isGameRunning = false;
        this.isPaused = false;
        this.isDoubleScoreActive = false;
        this.doubleScoreEndTime = 0;
        generateNextPiece();
        spawnNewPiece();
    }

    public void startGame() {
        isGameRunning = true;
        isPaused = false;
    }

    public void pauseGame() {
        isPaused = !isPaused;
    }

    public void stopGame() {
        isGameRunning = false;
        isPaused = false;
    }

    public void handleKeyPress(javafx.scene.input.KeyCode keyCode) {
        if (!isGameRunning || isPaused || currentPiece == null) {
            return;
        }

        SettingsManager settings = SettingsManager.getInstance();
        String keyName = keyCode.getName().toUpperCase();

        if (keyName.equals(settings.getKeyLeft())) {
            movePieceLeft();
        } else if (keyName.equals(settings.getKeyRight())) {
            movePieceRight();
        } else if (keyName.equals(settings.getKeyDown())) {
            movePieceDown();
        } else if (keyName.equals(settings.getKeyRotate())) {
            rotatePiece();
        } else if (keyName.equals(settings.getKeyHardDrop()) || keyCode == javafx.scene.input.KeyCode.SPACE) {
            hardDrop();
        } else if (keyCode == javafx.scene.input.KeyCode.N && hasSkipItem()) {
            // N키를 누르고 nextPiece가 SKIP 아이템을 가지고 있으면 블록 넘기기
            skipCurrentPiece();
        }
    }
    private void movePieceLeft() {
        if (currentPiece != null) {
            // 무게추가 이미 착지했으면 좌우 이동 불가
            if (currentPiece.isWeightPiece() && currentPiece.hasLanded()) {
                return;
            }

            currentPiece.moveLeft();
            if (!gameBoard.isValidPosition(currentPiece)) {
                currentPiece.moveRight();
            }
        }
    }

    private void movePieceRight() {
        if (currentPiece != null) {
            // 무게추가 이미 착지했으면 좌우 이동 불가
            if (currentPiece.isWeightPiece() && currentPiece.hasLanded()) {
                return;
            }

            currentPiece.moveRight();
            if (!gameBoard.isValidPosition(currentPiece)) {
                currentPiece.moveLeft();
            }
        }
    }

    public void movePieceDown() {
        if (currentPiece != null) {
            currentPiece.moveDown();

            if (!gameBoard.isValidPosition(currentPiece)) {
                // 이동 불가능 - 블록이 착지함
                currentPiece.moveUp();

                // 무게추는 착지 표시
                if (currentPiece.isWeightPiece()) {
                    currentPiece.setLanded(true);
                }

                placePiece();
            } else {
                // 이동 성공 - 무게추면 밑의 블록 지우기
                if (currentPiece.isWeightPiece()) {
                    gameBoard.processWeightEffect(currentPiece);

                    // 블록을 지운 후 착지 여부 확인
                    currentPiece.moveDown();
                    if (!gameBoard.isValidPosition(currentPiece)) {
                        currentPiece.moveUp();
                        currentPiece.setLanded(true);
                    } else {
                        currentPiece.moveUp();
                    }
                }
            }
        }
    }

    private void rotatePiece() {
        if (currentPiece != null) {
            currentPiece.rotate();
            if (!gameBoard.isValidPosition(currentPiece)) {
                currentPiece.rotateBack();
            }
        }
    }

    private void hardDrop() {
        if (currentPiece != null) {
            // 무게추 블록인 경우, 한 칸씩 내려가면서 무게추 효과 적용
            if (currentPiece.isWeightPiece()) {
                while (gameBoard.isValidPosition(currentPiece)) {
                    currentPiece.moveDown();

                    if (gameBoard.isValidPosition(currentPiece)) {
                        // 유효한 위치면 무게추 효과 처리 (아래 블록 삭제)
                        gameBoard.processWeightEffect(currentPiece);

                        // 무게추 효과 후 다시 위치 확인
                        currentPiece.moveDown();
                        if (!gameBoard.isValidPosition(currentPiece)) {
                            currentPiece.moveUp();
                            break;
                        }
                        currentPiece.moveUp();
                    } else {
                        // 더 이상 내려갈 수 없으면 한 칸 올리고 종료
                        currentPiece.moveUp();
                        break;
                    }
                }
                currentPiece.setLanded(true);  // 무게추는 착지 후 좌우 이동 불가
                placePiece();
            } else {
                // 일반 블록은 기존처럼 빠르게 하드드롭
                while (gameBoard.isValidPosition(currentPiece)) {
                    currentPiece.moveDown();
                }
                currentPiece.moveUp();
                placePiece();
            }
        }
    }

    private void placePiece() {
        if (currentPiece != null) {
            // DOUBLE_SCORE 아이템 확인 및 활성화 (블록 배치 전에 확인)
            if (currentPiece.hasItem()) {
                int[][] shape = currentPiece.getShape();
                for (int row = 0; row < shape.length; row++) {
                    for (int col = 0; col < shape[row].length; col++) {
                        if (shape[row][col] != 0) {
                            ItemType itemType = currentPiece.getItemAt(row, col);
                            if (itemType == ItemType.DOUBLE_SCORE) {
                                activateDoubleScore();
                                break;
                            }
                        }
                    }
                }
            }

            // 블록을 보드에 배치
            gameBoard.placePiece(currentPiece);

            // 아이템 효과 처리 (LINE_CLEAR 아이템이 있으면 즉시 줄 삭제)
            int itemClearedLines = gameBoard.processItemEffects(currentPiece);

            // 일반 줄 삭제 (꽉 찬 줄)
            int normalClearedLines = gameBoard.clearLines();

            // 총 삭제된 줄 수 (아이템 + 일반)
            int totalClearedLines = itemClearedLines + normalClearedLines;
            updateScore(totalClearedLines);

            spawnNewPiece();

            if (!gameBoard.isValidPosition(currentPiece)) {
                stopGame();
            }
        }
    }

    private void spawnNewPiece() {
        currentPiece = nextPiece;
        generateNextPiece();
        if (currentPiece != null) {
            currentPiece.setPosition(gameBoard.getSpawnX(), gameBoard.getSpawnY());
        }
    }

    private void generateNextPiece() {
        SettingsManager settings = SettingsManager.getInstance();
        String gameMode = settings.getGameMode();

        // ITEM 모드이고 10줄마다 아이템 블록 생성
        boolean shouldHaveItem = "ITEM".equals(gameMode) && linesClearedSinceLastItem >= 10;

        nextPiece = PieceFactory.createRandomPiece(shouldHaveItem);

        // 아이템이 생성되었으면 카운터 리셋
        if (shouldHaveItem && nextPiece.hasItem()) {
            linesClearedSinceLastItem = 0;
        }
    }

    private void updateScore(int clearedLines) {
        this.linesCleared += clearedLines;
        this.linesClearedSinceLastItem += clearedLines;  // 아이템 카운터도 업데이트

        int baseScore = 0;
        switch (clearedLines) {
            case 1:
                baseScore = 100 * level;
                break;
            case 2:
                baseScore = 300 * level;
                break;
            case 3:
                baseScore = 500 * level;
                break;
            case 4:
                baseScore = 800 * level;
                break;
        }

        // 점수 2배 아이템이 활성화되어 있으면 2배 적용
        if (isDoubleScoreActive) {
            baseScore *= 2;
        }

        score += baseScore;

        SettingsManager settings = SettingsManager.getInstance();
        String difficulty = settings.getDifficulty();
        int linesPerLevel;

        switch (difficulty) {
            case "Easy":
                linesPerLevel = 12;
                break;
            case "Hard":
                linesPerLevel = 8;
                break;
            default:    // Normal
                linesPerLevel = 10;
                break;
        }

        level = (linesCleared / linesPerLevel) + 1;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public Piece getNextPiece() {
        return nextPiece;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 점수 2배 아이템을 활성화
     */
    public void activateDoubleScore() {
        isDoubleScoreActive = true;
        doubleScoreEndTime = System.nanoTime() + DOUBLE_SCORE_DURATION;
    }

    /**
     * 점수 2배 아이템의 상태를 업데이트 (매 프레임마다 호출)
     */
    public void updateDoubleScoreStatus() {
        if (isDoubleScoreActive && System.nanoTime() >= doubleScoreEndTime) {
            isDoubleScoreActive = false;
        }
    }

    /**
     * 점수 2배 아이템이 활성화되어 있는지 확인
     * @return 활성화되어 있으면 true
     */
    public boolean isDoubleScoreActive() {
        return isDoubleScoreActive;
    }

    /**
     * 점수 2배 아이템의 남은 시간을 초 단위로 반환
     * @return 남은 시간 (초), 비활성화 상태면 0
     */
    public int getDoubleScoreRemainingTime() {
        if (!isDoubleScoreActive) {
            return 0;
        }
        long remaining = doubleScoreEndTime - System.nanoTime();
        return (int) Math.max(0, remaining / 1_000_000_000L);
    }

    /**
     * nextPiece가 SKIP 아이템을 가지고 있는지 확인
     * @return SKIP 아이템이 있으면 true
     */
    public boolean hasSkipItem() {
        if (nextPiece == null || !nextPiece.hasItem()) {
            return false;
        }

        int[][] shape = nextPiece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    ItemType itemType = nextPiece.getItemAt(row, col);
                    if (itemType == ItemType.SKIP) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 블록 넘기기 (SKIP 아이템 사용)
     * 현재 블록을 버리고 다음 블록을 가져옴
     * 다음 블록은 현재 블록과 반드시 달라야 함
     */
    public void skipCurrentPiece() {
        if (nextPiece == null) {
            return;
        }

        // 현재 블록의 타입 저장
        int oldType = currentPiece.getType();

        // 다음 블록을 현재 블록으로 설정
        currentPiece = nextPiece;
        currentPiece.setPosition(gameBoard.getSpawnX(), gameBoard.getSpawnY());

        // 새로운 다음 블록 생성 (이전 블록과 달라야 함)
        generateNextPieceDifferentFrom(oldType);

        // 게임 오버 체크
        if (!gameBoard.isValidPosition(currentPiece)) {
            stopGame();
        }
    }

    /**
     * 특정 타입과 다른 블록을 생성
     * @param excludeType 제외할 블록 타입
     */
    private void generateNextPieceDifferentFrom(int excludeType) {
        SettingsManager settings = SettingsManager.getInstance();
        String gameMode = settings.getGameMode();

        // ITEM 모드이고 10줄마다 아이템 블록 생성
        boolean shouldHaveItem = "ITEM".equals(gameMode) && linesClearedSinceLastItem >= 10;

        int maxAttempts = 10;
        int attempts = 0;

        do {
            nextPiece = PieceFactory.createRandomPiece(shouldHaveItem);
            attempts++;
        } while (nextPiece.getType() == excludeType && attempts < maxAttempts);

        // 아이템이 생성되었으면 카운터 리셋
        if (shouldHaveItem && nextPiece.hasItem()) {
            linesClearedSinceLastItem = 0;
        }
    }
}