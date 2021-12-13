package ru.korotkov.exceptions;

public class InvalidAttackMoveException extends RuntimeException {
    public InvalidAttackMoveException() {
        super("invalid move");
    }
}
