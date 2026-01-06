package scheduling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class TiredExecutorTest {

    private TiredExecutor executor;

    @AfterEach
    public void tearDown() throws Exception {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    public void testSubmitAllRunsTasks() throws Exception {
        executor = new TiredExecutor(3);
        AtomicInteger counter = new AtomicInteger(0);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tasks.add(counter::incrementAndGet);
        }
        executor.submitAll(tasks);
        assertEquals(5, counter.get());
    }

    @Test
    public void testSubmitOneTask() throws Exception {
        executor = new TiredExecutor(2);
        CountDownLatch latch = new CountDownLatch(1);
        executor.submit(() -> latch.countDown());
        latch.await();
        assertTrue(latch.getCount() == 0);
    }
}
