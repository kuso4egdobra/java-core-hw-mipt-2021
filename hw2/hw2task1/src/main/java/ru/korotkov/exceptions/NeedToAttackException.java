package ru.korotkov.exceptions;

public class NeedToAttackException extends RuntimeException {
    public NeedToAttackException() {
        super("invalid move");
    }
}
