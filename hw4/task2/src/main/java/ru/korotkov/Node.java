package ru.korotkov;

import java.io.FileWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public final class Node {
    private static final int NUM_THREADS = 3;

    private final int nodeId;

    private final int coreId;
    private final FileWriter file;

    private Node nextNode;
    private Node coreNode;

    private int numData = 0;

    private Condition endProc = null;
    private Lock lock = null;

    private final ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

    private final PriorityBlockingQueue<DataPackage> bufferStack = new PriorityBlockingQueue<>();

    private PriorityBlockingQueue<DataPackage> allData;

    /**
     * Сюда идёт запись времени прохода каждого пакета данных.
     * Используется в RingProcessor.averageTime() для подсчета среднего времени
     * прохода данных к координатору.
     */
    private PriorityBlockingQueue<Long> timeList;

    Node(int nodeId, int coreId, FileWriter file) {
        this.nodeId = nodeId;

        this.file = file;

        this.coreId = coreId;

        if (nodeId == coreId) {
            allData = new PriorityBlockingQueue<>();
            timeList = new PriorityBlockingQueue<>();
        }
    }

    public void addReadyData(DataPackage dataPackage) {
        if (nodeId == coreId) {
            allData.put(dataPackage);

            long spentTimeForDestination = dataPackage.getDestinationTime();
            timeList.put(spentTimeForDestination);

            // Сообщить о возможности завершения RingProcessor
            if (timeList.size() == numData) {
                lock.lock();
                try {
                    endProc.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public void setNumData(int numData) {
        this.numData = numData;
    }

    public void setCondition(Condition condition) {
        endProc = condition;
    }

    public PriorityBlockingQueue<Long> getTimeList() {
        return timeList;
    }

    public void setNextNode(Node node) {
        nextNode = node;
    }

    public void setCoreNode(Node node) {
        coreNode = node;
    }

    public long getId() {
        return nodeId;
    }

    public int getSizeBuffer() {
        return bufferStack.size();
    }

    public void addData(DataPackage dataPackage) {
        dataPackage.setStartTimeInBuffer();
        bufferStack.put(dataPackage);
    }

    /** Запускаются 3 потока, каждый пытается считать данные из буффера,
     * если буффер пустой, то происходит ожидание появления данных при вызове метода take().
     */
    private void runTread() {
        executorService.submit(() -> {
            DataPackage data = null;
            try {
//                System.out.println(nodeId + " " + "try take data");
                data = bufferStack.take();

                if (data.getDestinationNode() == nodeId) {
                    System.out.println("NodeId: " + nodeId + " " + "is destination for data '" + data.getData() + "'");
                    file.write("NodeId: " + nodeId + " " + "is destination for data '" + data.getData() + "'\n");

                    data.setEndDestinationTime();

                    System.out.println("data: '" + data.getData() + "' - avg time in buffer: "
                            + data.averageTimeInBuffer() + " secs");
                    file.write("data: '" + data.getData() + "' - avg time in buffer: "
                            + data.averageTimeInBuffer() + " secs\n");

                    coreNode.addReadyData(data);
//                    System.out.println("Sent data to core node");
                } else {
//                    System.out.println(nodeId + " " + "took data " + data.getData());
                    Thread.sleep(1);
                    data.setEndTimeInBuffer();

                    System.out.println("Sent data '" + data.getData() + "' from nodeId "
                            + nodeId + " to " + nextNode.getId());
                    file.write("Sent data '" + data.getData() + "' from nodeId "
                            + nodeId + " to " + nextNode.getId() + "\n");

                    nextNode.addData(data);
                }

                this.runTread();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return data;
            }
            return data;
        });
    }

    /**
     * Начало работы узла. То есть из Node.bufferStack берётся пакет с данными
     * и отправляется на обработку, после чего передаётся следующему узлу.
     * Тут заключена логика, согласно которой обрабатываться может только 3 пакета данных одновременно.
     */
    public void run() {
        for (int i = 0; i < NUM_THREADS; i++) {
            runTread();
        }
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }
}

