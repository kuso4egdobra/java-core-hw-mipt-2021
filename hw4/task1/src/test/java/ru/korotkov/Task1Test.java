package ru.korotkov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Task1Test {
    private PriorityBlockingQueue<Boat> tunnelBuffer;
    private ArrayList<PriorityBlockingQueue<Boat>> docksBuffer;

    private Lock lock;
    private Condition condition;

    @BeforeEach
    void init() {
        tunnelBuffer = new PriorityBlockingQueue<>();
        docksBuffer = new ArrayList<>(Arrays.asList(
                new PriorityBlockingQueue<>(),
                new PriorityBlockingQueue<>(),
                new PriorityBlockingQueue<>()
        ));

        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    @Test
    void docks() throws InterruptedException {
        int numBoats = 5;
        Generator generator = new Generator(tunnelBuffer, numBoats, 1);
        generator.run();

        Thread.sleep(1000);

        Tunnel tunnel = new Tunnel(tunnelBuffer, docksBuffer);
        tunnel.run();

        Thread.sleep(10000);

        ArrayList<Dock> docks = new ArrayList<>();
        for (int i = 0; i < Boat.Type.values().length; i++) {
            docks.add(new Dock(docksBuffer.get(i), lock, condition));
        }
        for (Dock dock : docks) {
            dock.run();
        }

        Thread.sleep(20000);

        int numFulledBoats = docks.get(0).getNumFulledBoats().get();

        Assertions.assertThat(numFulledBoats).isEqualTo(numBoats);
    }

    @Test
    void generator() throws InterruptedException {
        int numBoats = 100;
        Generator generator = new Generator(tunnelBuffer, numBoats, 1);
        generator.run();

        Thread.sleep(1000);

        Assertions.assertThat(tunnelBuffer.size()).isEqualTo(numBoats);
    }

    @Test
    void tunnel() throws InterruptedException {
        int numBoats = 30;
        Generator generator = new Generator(tunnelBuffer, numBoats, 1);
        generator.run();

        Thread.sleep(1000);

        Tunnel tunnel = new Tunnel(tunnelBuffer, docksBuffer);
        tunnel.run();

        Thread.sleep(10000);

        int sizeDocks = 0;
        for (PriorityBlockingQueue<Boat> buffer : docksBuffer) {
            sizeDocks += buffer.size();
        }

        Assertions.assertThat(sizeDocks).isEqualTo(numBoats);
    }
}
