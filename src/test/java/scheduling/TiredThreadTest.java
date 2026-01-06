package scheduling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class TiredThreadTest {

    private TiredThread executionUnit;

    @AfterEach
    public void tearDown() throws InterruptedException {
        if (executionUnit != null) {
            executionUnit.shutdown();
            executionUnit.join(500);
        }
    }

    @Test
    public void shouldExecuteTaskAndBecomeIdle() throws Exception {
        executionUnit = new TiredThread(10, 1.0);
        CountDownLatch completionSignal = new CountDownLatch(1);
        
        executionUnit.start();

        Runnable simpleTask = completionSignal::countDown;
        executionUnit.newTask(simpleTask);

        boolean isFinished = completionSignal.await(500, TimeUnit.MILLISECONDS);
        assertTrue(isFinished);
        assertEquals(false, executionUnit.isBusy());
    }

    @Test
    public void shouldRejectTaskWhenAlreadyRunning() throws Exception {
        executionUnit = new TiredThread(5, 1.0);
        CountDownLatch startSignal = new CountDownLatch(1);
        executionUnit.start();

        Runnable blockingTask = () -> {
            try {
                startSignal.countDown();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        executionUnit.newTask(blockingTask);

        boolean started = startSignal.await(500, TimeUnit.MILLISECONDS);
        assertTrue(started);

        Runnable dummyTask = () -> {};
        assertThrows(IllegalStateException.class, () -> executionUnit.newTask(dummyTask));
    }
}