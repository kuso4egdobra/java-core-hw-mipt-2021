package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main4 {
    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            int n = in.nextInt();
            int k = in.nextInt();

            ArrayList<Integer> arr = new ArrayList<>();
            for (int i = 0; i < n; i++)
                arr.add(i + 1);

            int ptr = 0;
            for (int i = 0; i < n - 1; i++) {
                ptr = (ptr + k - 1) % arr.size();
                arr.remove(ptr);
            }
            System.out.println(arr.get(0));
        }
    }
}
