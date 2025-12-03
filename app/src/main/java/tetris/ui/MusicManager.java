package tetris.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * 게임 음악과 효과음을 관리하는 싱글톤 클래스
 */
public class MusicManager {
    private static MusicManager instance;
    
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer gameMusicPlayer;
    private MediaPlayer removeBlockSoundPlayer;
    
    private SettingsManager settingsManager;
    
    private MusicManager() {
        settingsManager = SettingsManager.getInstance();
        initializeMusic();
    }
    
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    /**
     * 음악 파일들을 초기화
     */
    private void initializeMusic() {
        try {
            // 배경 음악 (메인 메뉴 등)
            URL backgroundMusicUrl = getClass().getResource("/assets/music/Background_Music.mp3");
            if (backgroundMusicUrl != null) {
                Media backgroundMedia = new Media(backgroundMusicUrl.toExternalForm());
                backgroundMusicPlayer = new MediaPlayer(backgroundMedia);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // 무한 반복
            }
            
            // 게임 브금
            URL gameMusicUrl = getClass().getResource("/assets/music/Game_Music.mp3");
            if (gameMusicUrl != null) {
                Media gameMedia = new Media(gameMusicUrl.toExternalForm());
                gameMusicPlayer = new MediaPlayer(gameMedia);
                gameMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // 무한 반복
            }
            
            // 블록 삭제 효과음
            URL removeBlockUrl = getClass().getResource("/assets/music/Remove_Block.mp3");
            if (removeBlockUrl != null) {
                Media removeBlockMedia = new Media(removeBlockUrl.toExternalForm());
                removeBlockSoundPlayer = new MediaPlayer(removeBlockMedia);
            }
            
            updateVolume();
        } catch (Exception e) {
            System.err.println("음악 파일 로드 실패: " + e.getMessage());
        }
    }
    
    /**
     * 볼륨 설정 업데이트
     */
    public void updateVolume() {
        double volume = settingsManager.getVolume() / 100.0;
        
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.setVolume(volume);
        }
        if (gameMusicPlayer != null) {
            gameMusicPlayer.setVolume(volume);
        }
        if (removeBlockSoundPlayer != null) {
            removeBlockSoundPlayer.setVolume(volume);
        }
    }
    
    /**
     * 배경 음악 재생 (메인 메뉴 등)
     */
    public void playBackgroundMusic() {
        if (!settingsManager.isMusicEnabled()) {
            return;
        }
        
        stopAllMusic();
        
        if (backgroundMusicPlayer != null) {
            updateVolume();
            backgroundMusicPlayer.play();
        }
    }
    
    /**
     * 게임 브금 재생
     */
    public void playGameMusic() {
        if (!settingsManager.isMusicEnabled()) {
            return;
        }
        
        stopAllMusic();
        
        if (gameMusicPlayer != null) {
            updateVolume();
            gameMusicPlayer.play();
        }
    }
    
    /**
     * 블록 삭제 효과음 재생
     */
    public void playRemoveBlockSound() {
        if (!settingsManager.isSoundEffectsEnabled()) {
            return;
        }
        
        if (removeBlockSoundPlayer != null) {
            updateVolume();
            // 효과음은 처음부터 재생
            removeBlockSoundPlayer.stop();
            removeBlockSoundPlayer.play();
        }
    }
    
    /**
     * 모든 음악 중지
     */
    public void stopAllMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
        }
        if (gameMusicPlayer != null) {
            gameMusicPlayer.stop();
        }
    }
    
    /**
     * 배경 음악 일시 정지
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.pause();
        }
    }
    
    /**
     * 게임 브금 일시 정지
     */
    public void pauseGameMusic() {
        if (gameMusicPlayer != null) {
            gameMusicPlayer.pause();
        }
    }
    
    /**
     * 배경 음악 재개
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusicPlayer != null && settingsManager.isMusicEnabled()) {
            updateVolume();
            backgroundMusicPlayer.play();
        }
    }
    
    /**
     * 게임 브금 재개
     */
    public void resumeGameMusic() {
        if (gameMusicPlayer != null && settingsManager.isMusicEnabled()) {
            updateVolume();
            gameMusicPlayer.play();
        }
    }
    
    /**
     * 리소스 정리
     */
    public void dispose() {
        stopAllMusic();
        
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.dispose();
        }
        if (gameMusicPlayer != null) {
            gameMusicPlayer.dispose();
        }
        if (removeBlockSoundPlayer != null) {
            removeBlockSoundPlayer.dispose();
        }
    }
}

