package ru.korotkov;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class App {
    private App() { }

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("input.txt");
        List<String> inputData = Files.readAllLines(path, StandardCharsets.UTF_8);
        String[] white = inputData.get(0).split(" ");
        String[] black = inputData.get(1).split(" ");
        Board board = new Board();
        board.addCheckers(white, true);
        board.addCheckers(black, false);

        try {
            List<String> turns = inputData.subList(2, inputData.size());
            for (String s : turns) {
                String[] turn = s.split(" ");
                String whiteTurn = turn[0];
                String blackTurn = turn[1];

//                System.out.println(whiteTurn);
                if (whiteTurn.contains(":")) {
                    board.addTurnAttack(whiteTurn);
                } else {
                    board.addTurnOrdinary(whiteTurn);
                }

//                System.out.println(blackTurn);
                if (blackTurn.contains(":")) {
                    board.addTurnAttack(blackTurn);
                } else {
                    board.addTurnOrdinary(blackTurn);
                }
            }

            ArrayList<String> whiteCheckers = board.getWhiteCheckers();
            ArrayList<String> blackCheckers = board.getBlackCheckers();
            for (String s: whiteCheckers) {
                System.out.print(s + ' ');
            }
            System.out.println();
            for (String s: blackCheckers) {
                System.out.print(s + ' ');
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
