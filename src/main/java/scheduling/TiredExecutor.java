package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        // TODO
        workers = new TiredThread[numThreads];
        Random rnd = new Random();
        double fatigue; 
        for(int i=0; i < numThreads ; i++){
            fatigue = 0.5+rnd.nextDouble();
            workers[i]=new TiredThread(i,fatigue);
            idleMinHeap.add(workers[i]);
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        // TODO
        final TiredThread worker;
        synchronized(this){
            try{
                while(idleMinHeap.isEmpty()) {
                    this.wait();
                }
            }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    return;
            }
            worker = idleMinHeap.poll();
            inFlight.getAndIncrement();
        }
        Runnable wrapped =() ->{
                try{
                    task.run();
                }finally{
                    synchronized (this){
                        worker.setBusyToFalse();
                        idleMinHeap.offer(worker);
                        inFlight.decrementAndGet();
                        notifyAll();
                    }
                }
            };
        try{
            worker.newTask(wrapped);
        }catch(RuntimeException e){
                synchronized(this){
                    this.inFlight.decrementAndGet();
                    idleMinHeap.add(worker);
                    notifyAll();
                }        
                throw e;

            }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        // TODO: submit tasks one by one and wait until all finish
        Iterator<Runnable> it =tasks.iterator();
        while (it.hasNext()) {
            submit(it.next());
        }
        synchronized(this){
            try{
                while (this.inFlight.get() > 0) {
                    this.wait();
                }
                
            } catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public void shutdown() throws InterruptedException {
        // TODO
        for(TiredThread worker : workers){
            worker.shutdown();
        }
        this.inFlight.set(0);
        for(TiredThread worker : workers){
            worker.join();
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        String report = "";
        for(TiredThread worker : workers){
            report += "worker: " + worker.getWorkerId()
            + "  used=" + worker.getTimeUsed()
            + "  idle=" + worker.getTimeIdle()
            + "  fatigue=" + worker.getFatigue()
            + System.lineSeparator();
        }
        return report;
    }
}