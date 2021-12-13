package ru.korotkov.exceptions;

public class InvalidOrdinaryMoveException extends RuntimeException {
    public InvalidOrdinaryMoveException() {
        super("invalid move");
    }
}

