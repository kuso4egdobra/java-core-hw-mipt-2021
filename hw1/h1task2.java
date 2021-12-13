package com.company;

import java.util.Scanner;
import static java.lang.Math.abs;

public class Main2 {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int n = in.nextInt();
            int nPlus1 = n + 1;
            int[] coordinateX = new int[n + 2];
            int[] coordinateY = new int[n + 2];

            for (int i = 1; i < nPlus1; i++) {
                coordinateX[i] = in.nextInt();
                coordinateY[i] = in.nextInt();
            }

            coordinateX[nPlus1] = coordinateX[1];
            coordinateY[nPlus1] = coordinateY[1];
            coordinateX[0] = coordinateX[n];
            coordinateY[0] = coordinateY[n];

            int sum = 0;
            for (int i = 1; i < nPlus1; i++)
                sum += coordinateX[i] * (coordinateY[i - 1] - coordinateY[i + 1]);

            System.out.println(abs(sum / 2.));
        }
    }
}
