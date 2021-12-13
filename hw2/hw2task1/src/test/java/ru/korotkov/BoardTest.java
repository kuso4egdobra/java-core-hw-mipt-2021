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
        String[] whiteCheckers = {"A1", "b2", "C9", "f5"};
        ArrayList<String> whiteCheckersArrList = new ArrayList<>(List.of(whiteCheckers));

        board.addCheckers(whiteCheckers, true);
        Collections.sort(whiteCheckersArrList);

        Assertions.assertThat(board.getWhiteCheckers()).isEqualTo(whiteCheckersArrList);
    }

    @Test
    void addWhiteBlackCheckers() {
        String[] whiteCheckers = {"B1", "b2", "A9", "f5"};
        String[] blackCheckers = {"D2", "b3", "A1", "g7"};
        ArrayList<String> whiteCheckersArrList = new ArrayList<>(List.of(whiteCheckers));
        ArrayList<String> blackCheckersArrList = new ArrayList<>(List.of(blackCheckers));

        board.addCheckers(whiteCheckers, true);
        board.addCheckers(blackCheckers, false);
        Collections.sort(whiteCheckersArrList);
        Collections.sort(blackCheckersArrList);

        Assertions.assertThat(board.getWhiteCheckers()).isEqualTo(whiteCheckersArrList);
        Assertions.assertThat(board.getBlackCheckers()).isEqualTo(blackCheckersArrList);
    }

    @Test
    void addTurnBusyCellException() {
        String[] whiteCheckers = {"B1", "b3", "A9", "f5"};
        String[] blackCheckers = {"D2", "b2", "A1", "g7"};
        String[] turns = {"B1-b3"};

        Exception exception = assertThrows(BusyCellException.class, () -> {
            board.addCheckers(whiteCheckers, true);
            board.addCheckers(blackCheckers, false);
            board.addTurnOrdinary(turns[0]);
        });

        String expectedMessage = "busy cell";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void addTurnWhiteCellException() {
        String[] whiteCheckers = {"A1", "b3", "A9", "f5"};
        String[] blackCheckers = {"D2", "b2", "A1", "g7"};
        String[] turns = {"A1-a4"};

        Exception exception = assertThrows(WhiteCellException.class, () -> {
            board.addCheckers(whiteCheckers, true);
            board.addCheckers(blackCheckers, false);
            board.addTurnOrdinary(turns[0]);
        });

        String expectedMessage = "white cell";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    void addTurnNeedToAttackException() {
        String[] whiteCheckers = {"A1", "b3", "A9", "f5"};
        String[] blackCheckers = {"D2", "b2", "A1", "g7"};
        String[] turns = {"A1-a3"};

        Exception exception = assertThrows(NeedToAttackException.class, () -> {
            board.addCheckers(whiteCheckers, true);
            board.addCheckers(blackCheckers, false);
            board.addTurnOrdinary(turns[0]);
        });

        String expectedMessage = "invalid move";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}