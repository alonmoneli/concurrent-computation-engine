package scheduling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class TiredExecutorTest {

    private TiredExecutor pool;

    @AfterEach
    public void tearDown() throws Exception {
        if (pool != null) {
            pool.shutdown();
        }
    }

    @Test
    public void testSubmitAllExecutesEveryTask() throws Exception {
        pool = new TiredExecutor(4);
        AtomicInteger counter = new AtomicInteger(0);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            tasks.add(counter::incrementAndGet);
        }
        pool.submitAll(tasks);
        assertEquals(7, counter.get());
    }

    @Test
    public void testSubmitSingleTaskCompletes() throws Exception {
        pool = new TiredExecutor(2);
        CountDownLatch latch = new CountDownLatch(1);
        pool.submit(() -> latch.countDown());
        latch.await();
        assertTrue(latch.getCount() == 0);
    }

    @Test
    public void testShutdownStopsWorkers() throws Exception {
        pool = new TiredExecutor(3);
        CountDownLatch latch = new CountDownLatch(3);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            tasks.add(latch::countDown);
        }
        pool.submitAll(tasks);
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        pool.shutdown();
        boolean threwOnSubmit = false;
        try {
            pool.submit(() -> {});
        } catch (RuntimeException ex) {
            threwOnSubmit = true;
        }
        assertTrue(threwOnSubmit);
    }
}
