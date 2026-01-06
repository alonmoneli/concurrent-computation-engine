package scheduling;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class TiredThread extends Thread implements Comparable<TiredThread> {

    private static final Runnable POISON_PILL = () -> {}; // Special task to signal shutdown

    private final int id; // Worker index assigned by the executor
    private final double fatigueFactor; // Multiplier for fatigue calculation

    private final AtomicBoolean alive = new AtomicBoolean(true); // Indicates if the worker should keep running

    // Single-slot handoff queue; executor will put tasks here
    private final BlockingQueue<Runnable> handoff = new ArrayBlockingQueue<>(1);

    private final AtomicBoolean busy = new AtomicBoolean(false); // Indicates if the worker is currently executing a task

    private final AtomicLong timeUsed = new AtomicLong(0); // Total time spent executing tasks
    private final AtomicLong timeIdle = new AtomicLong(0); // Total time spent idle
    private final AtomicLong idleStartTime = new AtomicLong(0); // Timestamp when the worker became idle

    public TiredThread(int id, double fatigueFactor) {
        this.id = id;
        this.fatigueFactor = fatigueFactor;
        this.idleStartTime.set(System.nanoTime());
        setName(String.format("FF=%.2f", fatigueFactor));
    }

    public int getWorkerId() {
        return id;
    }

    public double getFatigue() {
        return fatigueFactor * timeUsed.get();
    }

    public boolean isBusy() {
        return busy.get();
    }

    public long getTimeUsed() {
        return timeUsed.get();
    }

    public long getTimeIdle() {
        return timeIdle.get();
    }

    public void setBusyToFalse() {
        this.busy.set(false);
    }

    /**
     * Assign a task to this worker.
     * This method is non-blocking: if the worker is not ready to accept a task,
     * it throws IllegalStateException.
     */
    public void newTask(Runnable task) {
       // TODO
       if (task != null) {
        if (alive.get()) {
            if (busy.compareAndSet(false, true)) {
                if (!handoff.offer(task)) {
                    busy.set(false);
                    throw new IllegalStateException("Shnitzel");
                }
            }
            else {
                throw new IllegalStateException("Busy");
            }
        }
        else {
            throw new IllegalStateException("Dead");
        }
       }

    //    if (this.busy.get() || !this.alive.get()) {
    //     throw new IllegalStateException("worker is busy or not active");
    //    }
    //    if(!handoff.offer(task)){
    //     throw new IllegalStateException("worker not ready");
    //    }
    //     this.busy.set(true);
    //     this.timeIdle.addAndGet(System.nanoTime() -this.idleStartTime.get());
    }

    /**
     * Request this worker to stop after finishing current task.
     * Inserts a poison pill so the worker wakes up and exits.
     */
    public void shutdown() {
       // TODO
       this.handoff.offer(POISON_PILL);
    }

    @Override
    public void run() {
       // TODO
       while (this.alive.get()) {
        try{
            Runnable task = handoff.take();
            if (task == POISON_PILL) {
            this.alive.set(false);
            break;
            }
            long start= System.nanoTime();
            timeIdle.addAndGet(start - idleStartTime.get());
            try{
                task.run();
            }
            finally{
                long finish = System.nanoTime();
                timeUsed.addAndGet(finish-start);
                busy.set(false);
                idleStartTime.set(System.nanoTime());
            }
        }
        catch(InterruptedException e){
            alive.set(false);
            Thread.currentThread().interrupt();            
        }
        }
    }

    @Override
    public int compareTo(TiredThread o) {
        // TODO
        double thisFatigue = getFatigue();
        double otherFatigue = o.getFatigue();
        if (thisFatigue > otherFatigue) {
            return 1;
        }
        else if(thisFatigue < otherFatigue){
            return -1;
        }
        return 0;
    }
}
