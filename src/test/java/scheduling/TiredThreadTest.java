package scheduling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class TiredThreadTest {

    @Test
    public void testNewTaskRunsToCompletionAndShutsDown() throws Exception {
        TiredThread thread = new TiredThread(7, 1.1);
        CountDownLatch latch = new CountDownLatch(1);
        thread.start();
        thread.newTask(() -> latch.countDown());
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        thread.shutdown();
        thread.join(1000);
        assertFalse(thread.isBusy());
    }
    
    @Test
    public void testNewTaskWhileBusyThrowsException() throws Exception {
        TiredThread thread = new TiredThread(42, 0.9);
        CountDownLatch latch = new CountDownLatch(1);
        thread.start();
        thread.newTask(() -> {
            try {
                latch.countDown();
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        });
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertThrows(IllegalStateException.class, () -> thread.newTask(() -> {}));
        thread.shutdown();
        thread.join(1000);
    }
}
