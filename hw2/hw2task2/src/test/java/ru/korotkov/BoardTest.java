package ru.korotkov;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import ru.korotkov.exceptions.BusyCellException;
import ru.korotkov.exceptions.NeedToAttackException;
import ru.korotkov.exceptions.WhiteCellException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BoardTest {
    Board board;

    @BeforeEach
    void initBoard() {
        board = new Board();
    }

    @Test
    void addWhiteCheckers() {
        String[] whiteCheckers = {"a1_W", "b2_wb", "c9_Wbwbwb", "f5_wbwbwbwbww"};
        ArrayList<String> whiteCheckersArrList = new ArrayList<>(List.of(whiteCheckers));

        board.addTowers(whiteCheckers);
        Collections.sort(whiteCheckersArrList);

        Assertions.assertThat(board.getWhiteCheckers()).isEqualTo(whiteCheckersArrList);
    }

    @Test
    void addWhiteBlackCheckers() {
        String[] whiteCheckers = {"a1_W", "b2_wb", "c9_Wbwbwb", "f5_wbwbwbwbww"};
        String[] blackCheckers = {"d2_bwww", "b3_BwbwbwbB", "a1_bbbWWW", "g7_bbbw"};
        ArrayList<String> whiteCheckersArrList = new ArrayList<>(List.of(whiteCheckers));
        ArrayList<String> blackCheckersArrList = new ArrayList<>(List.of(blackCheckers));

        board.addTowers(whiteCheckers);
        board.addTowers(blackCheckers);
        Collections.sort(whiteCheckersArrList);
        Collections.sort(blackCheckersArrList);

        Assertions.assertThat(board.getWhiteCheckers()).isEqualTo(whiteCheckersArrList);
        Assertions.assertThat(board.getBlackCheckers()).isEqualTo(blackCheckersArrList);
    }

    @Test
    void addTurnBusyCellException() {
        String[] whiteCheckers = {"a1_W", "b2_wb", "c9_Wbwbwb", "f5_wbwbwbwbww"};
        String[] blackCheckers = {"d2_bwww", "b3_BwbwbwbB", "a1_bbbWWW", "g7_bbbw"};
        String[] turns = {"a1_W-d2_bwww"};

        Exception exception = assertThrows(BusyCellException.class, () -> {
            board.addTowers(whiteCheckers);
            board.addTowers(blackCheckers);
            board.addTurnOrdinary(turns[0]);
        });

        String expectedMessage = "busy cell";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void addTurnWhiteCellException() {
        String[] whiteCheckers = {"a1_W", "b2_wb", "c9_Wbwbwb", "f5_wbwbwbwbww"};
        String[] blackCheckers = {"d2_bwww", "b3_BwbwbwbB", "a1_bbbWWW", "g7_bbbw"};
        String[] turns = {"a1_W-a4_W"};

        Exception exception = assertThrows(WhiteCellException.class, () -> {
            board.addTowers(whiteCheckers);
            board.addTowers(blackCheckers);
            board.addTurnOrdinary(turns[0]);
        });

        String expectedMessage = "white cell";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void addTurnNeedToAttackException() {
        String[] whiteCheckers = {"a1_W", "c9_Wbwbwb", "f5_wbwbwbwbww"};
        String[] blackCheckers = {"d4_bwww", "b3_BwbwbwbB", "a6_bbbWWW", "g7_bbbw"};
        String[] turns = {"a1_W-b2_W"};

        Exception exception = assertThrows(NeedToAttackException.class, () -> {
            board.addTowers(whiteCheckers);
            board.addTowers(blackCheckers);
            board.addTurnOrdinary(turns[0]);
        });

        String expectedMessage = "invalid move";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}