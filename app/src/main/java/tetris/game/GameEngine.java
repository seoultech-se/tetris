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

    public GameEngine() {
        this.gameBoard = new GameBoard();
        this.score = 0;
        this.level = 1;
        this.linesCleared = 0;
        this.linesClearedSinceLastItem = 0;
        this.isGameRunning = false;
        this.isPaused = false;
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
            while (gameBoard.isValidPosition(currentPiece)) {
                currentPiece.moveDown();
            }
            currentPiece.moveUp();
            placePiece();
        }
    }

    private void placePiece() {
        if (currentPiece != null) {
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

        switch (clearedLines) {
            case 1:
                score += 100 * level;
                break;
            case 2:
                score += 300 * level;
                break;
            case 3:
                score += 500 * level;
                break;
            case 4:
                score += 800 * level;
                break;
        }

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
}