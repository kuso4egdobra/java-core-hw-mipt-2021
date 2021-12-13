package ru.korotkov.exceptions;

public class NotBusyCellException extends RuntimeException {
    public NotBusyCellException() {
        super("not busy cell");
    }
}
