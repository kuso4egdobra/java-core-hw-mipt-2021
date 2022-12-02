package ru.korotkov;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static ru.korotkov.Time.NANO_SECS;

/**
 * В конструкторе кольцо инициализируется, то есть создаются все узлы и данные на узлах.
 * В методе {@link RingProcessor#startProcessing()} запускается работа кольца - данные начинают
 * обрабатываться по часовой стрелке. Также производится логгирование в {@link RingProcessor#file}.
 * Вся работа должна быть потокобезопасной и с обработкой всех возможных исключений. Если необходимо,
 * разрешается создавать собственные классы исключений.
 */
public final class RingProcessor {

    private final int nodesAmount;
    private final int dataAmount;
    private int coreId = 0;
    private int numData = 0;
    private final FileWriter file;
    private final Random random = new Random();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private final List<Node> nodeList = new ArrayList<>();


    RingProcessor(int nodesAmount, int dataAmount, FileWriter logs) throws IOException {
        this.nodesAmount = nodesAmount;

        this.dataAmount = dataAmount;

        this.file = logs;

        System.out.println("Num nodes: " + nodesAmount);
        file.write("Num nodes: " + nodesAmount + "\n");

        init();
    }

    public void startProcessing() {
        for (Node node: nodeList) {
            node.run();
        }
    }

    public void stopProcessing() throws InterruptedException, IOException {
        waitAllDataGoToDestination();

        for (Node node: nodeList) {
            node.shutdownNow();
        }

        System.out.println("Avg time from start to destination: " + averageTime() + " secs");
        file.write("Avg time from start to destination: " + averageTime() + " secs\n");

        file.close();
    }

    private void init() throws IOException {
        coreId = random.nextInt(this.nodesAmount);

        System.out.println("CoreId: " + coreId);
        file.write("CoreId: " + coreId + "\n");

        for (int i = 0; i < this.nodesAmount; i++) {
            nodeList.add(new Node(i, coreId, file));
        }

        addRefsToNextNodes();
        setCoreForNodes();

        nodeList.get(coreId).setCondition(condition);
        nodeList.get(coreId).setLock(lock);

        addData();

        // Calc num data in each Node
        for (Node node : nodeList) {
            numData += node.getSizeBuffer();

            System.out.println("NodeId: " + node.getId() + " - num data: " + node.getSizeBuffer());
            file.write("NodeId: " + node.getId() + " - num data: " + node.getSizeBuffer() + "\n");
        }
        nodeList.get(coreId).setNumData(numData);

        System.out.println("********** running **********");
        file.write("********** running **********\n");
    }

    private void addData() {
        for (Node node : nodeList) {
            for (int i = 0; i < dataAmount; i++) {
                node.addData(new DataPackage(random.nextInt(this.nodesAmount), genRandString()));
            }
        }
    }

    private void setCoreForNodes() {
        for (int i = 0; i < this.nodesAmount; i++) {
            nodeList.get(i).setCoreNode(nodeList.get(coreId));
        }
    }

    private void addRefsToNextNodes() {
        for (int i = 0; i < this.nodesAmount - 1; i++) {
            nodeList.get(i).setNextNode(nodeList.get(i + 1));
        }
        nodeList.get(nodeList.size() - 1).setNextNode(nodeList.get(0));
    }

    private void waitAllDataGoToDestination() throws InterruptedException {
        lock.lock();
        try {
            while (nodeList.get(coreId).getTimeList().size() != numData) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    // Считается среднее время прохода.
    private double averageTime() {
        PriorityBlockingQueue<Long> timeList = nodeList.get(coreId).getTimeList();
        double sum = 0;
        for (Long num : timeList) {
            sum += num;
        }
        return sum / timeList.size() / NANO_SECS;
    }

    private String genRandString() {
        return RandomStringUtils.random(5, true, true);
    }
}
