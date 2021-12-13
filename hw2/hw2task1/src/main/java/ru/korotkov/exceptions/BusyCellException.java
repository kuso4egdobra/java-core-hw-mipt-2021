package ru.korotkov.exceptions;

public class BusyCellException extends RuntimeException {
    public BusyCellException() {
        super("busy cell");
    }
}


