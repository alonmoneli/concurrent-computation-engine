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
    public void testNewTaskRuns() throws Exception {
        TiredThread worker = new TiredThread(0, 1.0);
        CountDownLatch latch = new CountDownLatch(1);
        worker.start();
        worker.newTask(() -> latch.countDown());
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        worker.shutdown();
        worker.join(1000);
        assertFalse(worker.isBusy());
    }

    @Test
    public void testNewTaskWhileBusyThrows() throws Exception {
        TiredThread worker = new TiredThread(1, 1.0);
        CountDownLatch latch = new CountDownLatch(1);
        worker.start();
        worker.newTask(() -> {
            try {
                latch.countDown();
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        });
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        assertThrows(IllegalStateException.class, () -> worker.newTask(() -> {}));
        worker.shutdown();
        worker.join(1000);
    }
}
