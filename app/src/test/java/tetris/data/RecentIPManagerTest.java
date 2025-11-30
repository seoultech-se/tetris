package tetris.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecentIPManagerTest {

    private String originalUserHome;
    private Path tempHomeDir;

    @BeforeEach
    void setUp() throws IOException {
        // 사용자 홈 디렉토리를 임시 디렉토리로 바꿔서 실제 환경에 영향 없게
        originalUserHome = System.getProperty("user.home");
        tempHomeDir = Files.createTempDirectory("recent-ip-test-");
        System.setProperty("user.home", tempHomeDir.toString());

        // 싱글톤을 새 환경으로 다시 만들기 위해 리플렉션으로 초기화
        try {
            var field = RecentIPManager.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to reset RecentIPManager singleton: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // 테스트 후 싱글톤 리셋 및 user.home 복구
        try {
            var field = RecentIPManager.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }

        System.setProperty("user.home", originalUserHome);

        if (tempHomeDir != null && Files.exists(tempHomeDir)) {
            Files.walk(tempHomeDir)
                    .sorted((a, b) -> b.compareTo(a)) // 파일부터 삭제
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    void initialGetRecentIPsShouldBeEmptyWhenNoFile() {
        RecentIPManager manager = RecentIPManager.getInstance();

        List<String> ips = manager.getRecentIPs();

        assertNotNull(ips, "Returned list should not be null");
        assertTrue(ips.isEmpty(), "Recent IP list should be empty initially when no file exists");
    }

    @Test
    void addRecentIPShouldAvoidDuplicatesAndKeepMostRecentFirst() {
        RecentIPManager manager = RecentIPManager.getInstance();

        manager.addRecentIP("192.168.0.1");
        manager.addRecentIP("192.168.0.2");
        manager.addRecentIP("192.168.0.1"); // 중복 추가

        List<String> ips = manager.getRecentIPs();

        assertEquals(2, ips.size(), "There should be no duplicate IPs");
        assertEquals("192.168.0.1", ips.get(0), "Most recently added IP should be first");
        assertEquals("192.168.0.2", ips.get(1), "Older IP should come after the most recent");
    }

    @Test
    void addRecentIPShouldLimitToMaxRecentIps() {
        RecentIPManager manager = RecentIPManager.getInstance();

        // MAX_RECENT_IPS = 5 기준으로 6개 넣기
        manager.addRecentIP("10.0.0.1");
        manager.addRecentIP("10.0.0.2");
        manager.addRecentIP("10.0.0.3");
        manager.addRecentIP("10.0.0.4");
        manager.addRecentIP("10.0.0.5");
        manager.addRecentIP("10.0.0.6");

        List<String> ips = manager.getRecentIPs();

        assertEquals(5, ips.size(), "Size should be limited to MAX_RECENT_IPS");
        assertEquals("10.0.0.6", ips.get(0), "Most recent IP should be first");
        assertFalse(ips.contains("10.0.0.1"), "Oldest IP should have been removed");
    }

    @Test
    void addRecentIPShouldIgnoreInvalidOrEmptyIps() {
        RecentIPManager manager = RecentIPManager.getInstance();

        manager.addRecentIP("");
        manager.addRecentIP("   ");
        manager.addRecentIP(null);
        manager.addRecentIP("not-an-ip");

        List<String> ips = manager.getRecentIPs();

        assertTrue(ips.isEmpty(), "Invalid IPs should not be added");
    }

    @Test
    void saveAndLoadShouldPersistIpsInOrder() throws IOException {
        // 첫 번째 인스턴스에서 IP 추가 (저장)
        RecentIPManager manager = RecentIPManager.getInstance();
        manager.addRecentIP("127.0.0.1");
        manager.addRecentIP("192.168.1.100");

        List<String> original = manager.getRecentIPs();
        assertEquals(2, original.size());

        // 최근 IP 파일이 실제로 생성되었는지 확인
        Path configFile = Paths.get(System.getProperty("user.home"), ".tetris", "recent_ips.txt");
        assertTrue(Files.exists(configFile), "Config file should be created after adding IPs");

        // 싱글톤 리셋 후 다시 로드 -> 파일에서 읽어와야 함
        try {
            var field = RecentIPManager.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to reset RecentIPManager singleton: " + e.getMessage());
        }

        RecentIPManager reloadedManager = RecentIPManager.getInstance();
        List<String> reloaded = reloadedManager.getRecentIPs();

        assertEquals(original, reloaded, "Reloaded IP list should match the original order and contents");
    }

    @Test
    void loadShouldHandleMissingFileGracefully() throws IOException {
        // .tetris 디렉토리는 만들되 파일은 명시적으로 삭제
        Path configDir = Paths.get(System.getProperty("user.home"), ".tetris");
        Files.createDirectories(configDir);
        Path configFile = configDir.resolve("recent_ips.txt");
        Files.deleteIfExists(configFile);

        // 새 인스턴스 생성 시 파일이 없어도 예외 없이 빈 리스트여야 함
        RecentIPManager manager = RecentIPManager.getInstance();

        List<String> ips = manager.getRecentIPs();

        assertNotNull(ips);
        assertTrue(ips.isEmpty(), "When config file is missing, list should be empty without exceptions");
    }
}
