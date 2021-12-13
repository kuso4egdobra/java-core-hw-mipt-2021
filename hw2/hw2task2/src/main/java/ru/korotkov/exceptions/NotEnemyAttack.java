package ru.korotkov.exceptions;

public class NotEnemyAttack extends RuntimeException {
    public NotEnemyAttack() {
        super("invalid move");
    }
}
