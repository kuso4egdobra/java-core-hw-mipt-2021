package ru.korotkov;

import java.util.ArrayList;

public final class Tower {
    private final ArrayList<Checker> checkers = new ArrayList<>();
    private int x;
    private int y;
    private boolean isAttacked = false;

    public Tower(int xCell, int yCell) {
        setCell(xCell, yCell);
    }

    public Checker getChecker() {
        return checkers.get(0);
    }

    public boolean isWhite() {
        return checkers.get(0).isWhite();
    }

    public boolean isQueen() {
        return checkers.get(0).isQueen();
    }

    public void addChecker(Checker checker) {
        checkers.add(checker);
    }

    public Checker attacked() {
        Checker attackedChecker = checkers.get(0);
        checkers.remove(0);
        setAttacked(true);
        return attackedChecker;
    }

    public int getHeight() {
        return checkers.size();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCell(int xCell, int yCell) {
        x = xCell;
        y = yCell;
    }

    public String getStringCell() {
        StringBuilder b = new StringBuilder(checkers.size() + 3);
        String xStr = Character.toString(x - 1 + "a".codePointAt(0));
        String yStr = String.valueOf(y);
        b.append(xStr).append(yStr).append("_");

        for (Checker checker: checkers) {
            String color = checker.isWhite() ? "w" : "b";
            String res = checker.isQueen() ? color.toUpperCase() : color;
            b.append(res);
        }

        return b.toString();
    }

    public void setQueen(boolean b) {
        checkers.get(0).setQueen(b);
    }

    public boolean isAttacked() {
        return isAttacked;
    }

    public void setAttacked(boolean b) {
        isAttacked = b;
    }
}
