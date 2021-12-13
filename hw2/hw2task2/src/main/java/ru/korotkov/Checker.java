package ru.korotkov;

class Checker {
    private boolean isQueen;
    private boolean isWhite;

    public boolean isQueen() {
        return isQueen;
    }

    public void setQueen(boolean queen) {
        isQueen = queen;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean white) {
        isWhite = white;
    }

    Checker(boolean isQueen, boolean isWhite) {
        this.isQueen = isQueen;
        this.isWhite = isWhite;
    }
}
