package ru.korotkov;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.io.FileWriter;
import java.io.IOException;

public class Task2Test {
    @Test
    void dataPackage() {
        DataPackage data = new DataPackage(1, "data");

        data.setStartTimeInBuffer();
        data.setEndTimeInBuffer();

        Assertions.assertThat(data.averageTimeInBuffer()).isGreaterThanOrEqualTo(0);
        Assertions.assertThat(data.getData()).isEqualTo("data");
    }

    @Test
    void node() throws IOException, InterruptedException {
        Node node = new Node(0, 0, new FileWriter("123"));
        int numData = 10;
        for (int i = 0; i < numData; i++) {
            node.addData(new DataPackage(0, "data"));
        }
        node.setCoreNode(node);


        node.run();
        Thread.sleep(1000);

        Assertions.assertThat(node.getTimeList().size()).isEqualTo(numData);
    }
}
