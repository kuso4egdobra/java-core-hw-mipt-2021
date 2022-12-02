package ru.korotkov;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class Dock {
    static final int SPEED_OF_FULLING_BOAT = 100;
    private final ExecutorService dock = Executors.newSingleThreadExecutor();
    private final PriorityBlockingQueue<Boat> boatBuffer;
    private static final AtomicInteger NUM_FULLED_BOATS = new AtomicInteger(0);

    private final Lock lock;
    private final Condition condition;

    public Dock(PriorityBlockingQueue<Boat> boatBuffer, Lock lock, Condition condition) {
        this.boatBuffer = boatBuffer;
        this.lock = lock;
        this.condition = condition;
    }

    public AtomicInteger getNumFulledBoats() {
        return NUM_FULLED_BOATS;
    }

    public void run() {
        dock.submit(() -> {
            try {
                Boat boat = boatBuffer.take();
                System.out.println(Thread.currentThread().getName() + " "
                        + boat + " begin fulling");
                int timeToSleep = boat.getCapacity().getValue() * SPEED_OF_FULLING_BOAT;
                Thread.sleep(timeToSleep);
                System.out.println(Thread.currentThread().getName() + " "
                        + boat + " fulled boat");
                NUM_FULLED_BOATS.incrementAndGet();
                // Сигнализирование о возможной обработки всех кораблей
                signal();
                run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void signal() {
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void shutdownNow() {
        dock.shutdownNow();
    }
}
