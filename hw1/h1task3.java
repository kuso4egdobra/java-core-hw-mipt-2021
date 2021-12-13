package com.company;

import java.util.Scanner;

public class Main3 {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int n = in.nextInt();
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                a[i] = in.nextInt();
            }
            int m = in.nextInt();
            int[] b = new int[m];
            for (int i = 0; i < m; i++) {
                b[i] = in.nextInt();
            }

            int k = in.nextInt();

            int ptrA = 0;
            int ptrB = m - 1;

            int counter = 0;
            while (ptrA < n && ptrB >= 0) {
                int sum = a[ptrA] + b[ptrB] - k;
                if (sum == 0) {
                    counter++;
                    ptrA++;
                } else if (sum > 0) {
                    ptrB--;
                } else {
                    ptrA++;
                }
            }
            System.out.println(counter);
        }
    }
}
