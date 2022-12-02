package ru.korotkov;

import java.io.FileWriter;
import java.io.IOException;

public final class Task2 {
    private Task2() {

    }

    public static void main(String[] args) throws InterruptedException, IOException {
        RingProcessor processor = new RingProcessor(10, 3, new FileWriter("logPath"));

        processor.startProcessing();

        processor.stopProcessing();
    }

}
