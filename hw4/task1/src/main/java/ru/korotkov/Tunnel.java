package ru.korotkov;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public final class Tunnel {

    static final int TIME_TO_GO_THROUGH_TUNNEL_MS = 1000;
    static final int MAX_NUM_BOATS_IN_TUNNEL = 5;

    private final ExecutorService tunnelExecutor = Executors.newFixedThreadPool(MAX_NUM_BOATS_IN_TUNNEL);

    private final PriorityBlockingQueue<Boat> tunnelBuffer;
    private final ArrayList<PriorityBlockingQueue<Boat>> docksBuffer;

    public Tunnel(PriorityBlockingQueue<Boat> tunnelBuffer, ArrayList<PriorityBlockingQueue<Boat>> docksBuffer) {
        this.tunnelBuffer = tunnelBuffer;
        this.docksBuffer = docksBuffer;
    }

    private void runThread() {
        tunnelExecutor.submit(() -> {
                    try {
                        Boat boat = tunnelBuffer.take();
                        Thread.sleep(TIME_TO_GO_THROUGH_TUNNEL_MS);
                        System.out.println(Thread.currentThread().getName() + " "
                                + boat + " went through tunnel");

                        docksBuffer.get(boat.getType().ordinal()).put(boat);
                        runThread();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
        );
    }

    public void run() {
        for (int i = 0; i < MAX_NUM_BOATS_IN_TUNNEL; i++) {
            runThread();
        }
    }

    public void shutdownNow() {
        tunnelExecutor.shutdownNow();
    }
}
