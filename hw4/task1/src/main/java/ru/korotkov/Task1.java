package ru.korotkov;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Task1 {
    private Task1() {

    }

    static final int TIME_TO_GENERATE_BOAT_MS = 300;
    public static final int NUM_BOATS_TO_GENERATE = 10;

    private static final Lock LOCK = new ReentrantLock();
    private static final Condition CONDITION = LOCK.newCondition();


    public static void main(String[] args) throws InterruptedException {


        PriorityBlockingQueue<Boat> tunnelBuffer = new PriorityBlockingQueue<>();
        ArrayList<PriorityBlockingQueue<Boat>> docksBuffer = new ArrayList<>(Arrays.asList(
                new PriorityBlockingQueue<>(),
                new PriorityBlockingQueue<>(),
                new PriorityBlockingQueue<>()
        ));

        Generator generator = new Generator(tunnelBuffer, NUM_BOATS_TO_GENERATE, TIME_TO_GENERATE_BOAT_MS);
        Tunnel tunnel = new Tunnel(tunnelBuffer, docksBuffer);
        ArrayList<Dock> docks = new ArrayList<>();
        for (int i = 0; i < Boat.Type.values().length; i++) {
            docks.add(new Dock(docksBuffer.get(i), LOCK, CONDITION));
        }

        generator.run();
        tunnel.run();
        for (Dock dock : docks) {
            dock.run();
        }

        AtomicInteger numFulledBoats = docks.get(0).getNumFulledBoats();
        waitFullingBoats(numFulledBoats);


        generator.shutdownNow();
        tunnel.shutdownNow();
        for (Dock dock : docks) {
            dock.shutdownNow();
        }
    }

    private static void waitFullingBoats(AtomicInteger numFulledBoats) throws InterruptedException {
        LOCK.lock();
        try {
            while (NUM_BOATS_TO_GENERATE != numFulledBoats.get()) {
                CONDITION.await();
            }
        } finally {
            LOCK.unlock();
        }
    }
}
