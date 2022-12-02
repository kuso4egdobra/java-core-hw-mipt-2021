package ru.korotkov;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public final class Generator {

    private final ExecutorService generator = Executors.newSingleThreadExecutor();
    private final PriorityBlockingQueue<Boat> tunnelBuffer;
    private final int numBoatsToGenerate;
    private final int timeToGenerateBoatMs;

    public Generator(PriorityBlockingQueue<Boat> tunnelBuffer, int numBoatsToGenerate, int timeToGenerateBoatMs) {
        this.tunnelBuffer = tunnelBuffer;
        this.numBoatsToGenerate = numBoatsToGenerate;
        this.timeToGenerateBoatMs = timeToGenerateBoatMs;
    }

    public void run() {
        for (int i = 0; i < numBoatsToGenerate; i++) {
            generator.submit(() -> {
                try {
                    Boat boatNew = new Boat(Boat.Type.randomType(), Boat.Capacity.randomCapacity());
                    Thread.sleep(timeToGenerateBoatMs);
                    tunnelBuffer.put(boatNew);

                    System.out.println(Thread.currentThread().getName() + " " + boatNew + " generated");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    public void shutdownNow() {
        generator.shutdownNow();
    }
}
