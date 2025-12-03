package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for JavaFX controller tests using TestFX.
 * Ensures the JavaFX toolkit is initialized and provides helper methods.
 */
public abstract class JavaFXTestBase {
    private static final AtomicBoolean TOOLKIT_INITIALIZED = new AtomicBoolean(false);
    protected Stage stage;

    @BeforeAll
    static void initToolkit() throws Exception {
        if (TOOLKIT_INITIALIZED.compareAndSet(false, true)) {
            // Remove headless mode properties - use platform native rendering
            System.clearProperty("testfx.headless");
            System.clearProperty("prism.order");
            System.clearProperty("prism.text");
            
            // Initialize JavaFX toolkit
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> {
                Platform.setImplicitExit(false);
                latch.countDown();
            });
            latch.await(5, TimeUnit.SECONDS);
        }
    }

    @AfterEach
    void cleanupStage() throws Exception {
        if (stage != null) {
            runOnFxThreadAndWait(() -> {
                stage.close();
                stage = null;
            });
        }
    }

    /**
     * Creates a simple stage for testing.
     */
    protected Stage createTestStage() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Stage[] stageHolder = new Stage[1];
        
        Platform.runLater(() -> {
            try {
                Stage testStage = new Stage();
                testStage.setScene(new Scene(new StackPane(), 800, 600));
                stageHolder[0] = testStage;
            } finally {
                latch.countDown();
            }
        });
        
        latch.await(5, TimeUnit.SECONDS);
        return stageHolder[0];
    }

    /**
     * Runs an action on the JavaFX Application Thread and waits for completion.
     */
    protected void runOnFxThreadAndWait(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);
        // 스레드 간 예외 전달을 위한 배열
        Throwable[] exceptionHolder = new Throwable[1];

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                // 1. 예외를 캡처하고 콘솔에 즉시 출력 (디버깅용)
                exceptionHolder[0] = t;
                System.err.println("❌ JavaFX Thread 내부 에러 발생:");
                t.printStackTrace(); 
            } finally {
                // 2. 에러가 나더라도 반드시 래치를 해제하여 타임아웃 방지
                latch.countDown();
            }
        });

        try {
            // 타임아웃 시간을 10초로 유지
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timeout waiting for FX thread (Logic took too long or Deadlock)");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for FX thread", e);
        }

        // 3. 캡처된 예외가 있다면 메인 스레드에서 다시 던져서 테스트 실패 처리
        if (exceptionHolder[0] != null) {
            if (exceptionHolder[0] instanceof RuntimeException) {
                throw (RuntimeException) exceptionHolder[0];
            } else {
                throw new RuntimeException("Exception occurred in FX Thread", exceptionHolder[0]);
            }
        }
    }

    /**
     * Runs an action on the JavaFX Application Thread without waiting.
     */
    protected void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    /**
     * Waits for a condition to be true on the FX thread.
     */
    protected void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
