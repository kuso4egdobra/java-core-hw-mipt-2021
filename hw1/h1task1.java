package com.company;

import java.util.Scanner;

public class Main1 {

    public static int[] readArray(int len, Scanner in) {
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = in.nextInt();
        }
        return arr;
    }

    public static void main(String[] args) {

        try (Scanner in = new Scanner(System.in)) {
            var n = in.nextInt();
            var a = readArray(n, in);
            var b = readArray(n, in);

            int maxJ = b[n - 1];
            int indexMaxNum = n - 1;
            int[] indexB = new int[n];
            indexB[n - 1] = indexMaxNum;
            for (int j = n - 2; j >= 0; j--) {
                if (maxJ > b[j])
                    b[j] = maxJ;
                else {
                    maxJ = b[j];
                    indexMaxNum = j;
                }
                indexB[j] = indexMaxNum;
            }

            int maxSum = a[0] + b[0];
            int maxIndex = 0;
            for (int i = 1; i < n; i++) {
                int sum = a[i] + b[i];
                if (sum > maxSum) {
                    maxSum = sum;
                    maxIndex = i;
                }
            }

            System.out.println(maxIndex + " " + indexB[maxIndex]);
        }
    }
}
