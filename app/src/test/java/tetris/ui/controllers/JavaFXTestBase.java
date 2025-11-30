package tetris.ui.controllers;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for JavaFX controller tests using TestFX.
 * Ensures the JavaFX toolkit is initialized and provides helper methods.
 */
@ExtendWith(ApplicationExtension.class)
public abstract class JavaFXTestBase {
    private static final AtomicBoolean TOOLKIT_INITIALIZED = new AtomicBoolean(false);
    protected FxRobot robot = new FxRobot();
    protected Stage stage;

    @BeforeAll
    static void initToolkit() throws Exception {
        if (TOOLKIT_INITIALIZED.compareAndSet(false, true)) {
            // Set headless mode properties
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            
            // Initialize JavaFX toolkit
            try {
                FxToolkit.registerPrimaryStage();
            } catch (TimeoutException e) {
                // Fallback to Platform.startup if FxToolkit fails
                CountDownLatch latch = new CountDownLatch(1);
                Platform.startup(latch::countDown);
                latch.await(5, TimeUnit.SECONDS);
            }
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
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        
        try {
            if (!latch.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timeout waiting for FX thread");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for FX thread", e);
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
