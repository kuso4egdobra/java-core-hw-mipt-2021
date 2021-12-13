package ru.korotkov;

class Checker {
    private boolean isQueen;
    private int x;
    private int y;
    private boolean isWhite;
    private boolean isKilled = false;

    public boolean isQueen() {
        return isQueen;
    }

    public void setQueen(boolean queen) {
        isQueen = queen;
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

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    Checker(boolean isQueen, int x, int y, boolean isWhite) {
        this.isQueen = isQueen;
        this.x = x;
        this.y = y;
        this.isWhite = isWhite;
    }

    public void setCell(int xCell, int yCell) {
        x = xCell;
        y = yCell;
    }

    public String getStringCell() {
        String caseUpperLower = isQueen ? "A" : "a";
        String xStr = Character.toString(x - 1 + caseUpperLower.codePointAt(0));
        String yStr = String.valueOf(y);
        return xStr + yStr;
    }

    public boolean isKilled() {
        return isKilled;
    }

    public void setKilled(boolean killed) {
        isKilled = killed;
    }
}
