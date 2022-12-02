package ru.korotkov;


import java.util.concurrent.PriorityBlockingQueue;

import static ru.korotkov.Time.NANO_SECS;

public final class DataPackage implements Comparable<DataPackage> {
    private final int destinationNode;

    private final String data;

    private final long startDestinationTime;

    private long endDestinationTime;

    private double startTimeInBuffer = 0;

    // Необходимо для подсчета среднего времени пребывания заявки в узле
    private final PriorityBlockingQueue<Double> timeInBuffer = new PriorityBlockingQueue<>();

    DataPackage(int destinationNode, String data) {
        this.destinationNode = destinationNode;

        this.data = data;

        // Фиксируется время, когда создаётся пакет данных. Необходимо для
        // вычисления времени доставки до узла назначения.
        startDestinationTime = System.nanoTime();
    }

    public void setEndDestinationTime() {
        endDestinationTime = System.nanoTime();
    }

    public double averageTimeInBuffer() {
        double sum = 0;
        for (Double num : timeInBuffer) {
            sum += num;
        }
        return sum / timeInBuffer.size() / NANO_SECS;
    }

    public void setStartTimeInBuffer() {
        startTimeInBuffer = System.nanoTime();
    }

    public void setEndTimeInBuffer() {
        timeInBuffer.put(System.nanoTime() - startTimeInBuffer);
    }

    public long getDestinationTime() {
        return endDestinationTime - startDestinationTime;
    }

    public int getDestinationNode() {
        return destinationNode;
    }

    public long getStartDestinationTime() {
        return startDestinationTime;
    }

    public String getData() {
        return data;
    }

    // Необходимо для использования PriorityBlockingQueue
    @Override
    public int compareTo(DataPackage o) {
        return data.compareTo(o.data);
    }
}

