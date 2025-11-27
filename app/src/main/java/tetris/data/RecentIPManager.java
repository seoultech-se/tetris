package tetris.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 최근 접속한 IP 주소를 저장하고 관리하는 클래스
 * 사용자 홈 디렉토리에 설정 파일을 저장하여 배포 후에도 작동
 */
public class RecentIPManager {
    private static RecentIPManager instance;
    private static final String CONFIG_DIR = ".tetris";
    private static final String CONFIG_FILE = "recent_ips.txt";
    private static final int MAX_RECENT_IPS = 5;
    
    private final Path configPath;
    private final List<String> recentIPs;
    
    private RecentIPManager() {
        // 사용자 홈 디렉토리에 설정 파일 경로 설정
        String userHome = System.getProperty("user.home");
        Path configDir = Paths.get(userHome, CONFIG_DIR);
        configPath = configDir.resolve(CONFIG_FILE);
        recentIPs = new ArrayList<>();
        
        // 설정 디렉토리 생성
        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                System.out.println("[RecentIP] Created config directory: " + configDir);
            }
        } catch (IOException e) {
            System.err.println("[RecentIP] Failed to create config directory: " + e.getMessage());
        }
        
        // 저장된 IP 불러오기
        loadRecentIPs();
    }
    
    public static synchronized RecentIPManager getInstance() {
        if (instance == null) {
            instance = new RecentIPManager();
        }
        return instance;
    }
    
    /**
     * 저장된 최근 IP 목록 불러오기
     */
    private void loadRecentIPs() {
        recentIPs.clear();
        
        if (!Files.exists(configPath)) {
            System.out.println("[RecentIP] No saved IPs found");
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(configPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && isValidIP(line)) {
                    recentIPs.add(line);
                }
            }
            System.out.println("[RecentIP] Loaded " + recentIPs.size() + " recent IPs");
        } catch (IOException e) {
            System.err.println("[RecentIP] Failed to load recent IPs: " + e.getMessage());
        }
    }
    
    /**
     * 최근 IP 목록 저장
     */
    private void saveRecentIPs() {
        try (BufferedWriter writer = Files.newBufferedWriter(configPath, 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String ip : recentIPs) {
                writer.write(ip);
                writer.newLine();
            }
            System.out.println("[RecentIP] Saved " + recentIPs.size() + " recent IPs");
        } catch (IOException e) {
            System.err.println("[RecentIP] Failed to save recent IPs: " + e.getMessage());
        }
    }
    
    /**
     * 새로운 IP 추가 (중복 제거 및 최대 개수 제한)
     */
    public void addRecentIP(String ip) {
        if (ip == null || ip.trim().isEmpty() || !isValidIP(ip)) {
            return;
        }
        
        ip = ip.trim();
        
        // 중복 제거 (이미 있으면 제거 후 맨 앞에 추가)
        recentIPs.remove(ip);
        recentIPs.add(0, ip);
        
        // 최대 개수 제한
        while (recentIPs.size() > MAX_RECENT_IPS) {
            recentIPs.remove(recentIPs.size() - 1);
        }
        
        // 파일에 저장
        saveRecentIPs();
    }
    
    /**
     * 최근 IP 목록 반환
     */
    public List<String> getRecentIPs() {
        return new ArrayList<>(recentIPs);
    }
    
    /**
     * 가장 최근 IP 반환 (없으면 null)
     */
    public String getMostRecentIP() {
        return recentIPs.isEmpty() ? null : recentIPs.get(0);
    }
    
    /**
     * IP 주소 형식 검증
     */
    private boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        // localhost 허용
        if (ip.equalsIgnoreCase("localhost") || ip.equals("127.0.0.1")) {
            return true;
        }
        
        // IPv4 형식 검증 (간단한 검증)
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 모든 최근 IP 삭제
     */
    public void clearRecentIPs() {
        recentIPs.clear();
        saveRecentIPs();
    }
}
