package ru.korotkov;

import ru.korotkov.exceptions.BusyCellException;
import ru.korotkov.exceptions.InvalidAttackMoveException;
import ru.korotkov.exceptions.NotBusyCellException;
import ru.korotkov.exceptions.NotEnemyAttack;
import ru.korotkov.exceptions.InvalidOrdinaryMoveException;
import ru.korotkov.exceptions.WhiteCellException;
import ru.korotkov.exceptions.NeedToAttackException;
import ru.korotkov.exceptions.TryToGoThroughFriendException;

import java.util.ArrayList;
import java.util.Collections;

class Board {
    public static final int SIZE_DESK = 8;
    public static final int CELL_NOT_EXISTS = -2;

    private final ArrayList<Checker> checkers = new ArrayList<>();

    /**
     * Добавление шашек одного цвета на доску
     * @param checkersToAdd массив клеток, на которых находятся шашки игрока
     * @param isWhite флаг белые/черные шашки
     */
    public void addCheckers(String[] checkersToAdd, boolean isWhite) {
        for (String s : checkersToAdd) {
            boolean isQueen = s.matches("[A-Z]\\d");
            String checkerToAdd = s.toLowerCase();
            Checker checker = new Checker(
                    isQueen,
                    checkerToAdd.codePointAt(0) - "a".codePointAt(0) + 1,
                    Integer.parseInt(checkerToAdd.substring(1)),
                    isWhite);

            checkers.add(checker);
        }
    }

    public void addTurnOrdinary(String fromToMove) {
        String[] move = fromToMove.split("-");
        addTurn(move[0], move[1], true);
    }

    public void addTurnAttack(String fromToMove) {
        String[] moves = fromToMove.split(":");
        for (int i = 1; i < moves.length; i++) {
            addTurn(moves[i - 1], moves[i], false);
        }

        checkForNeedToAttack(
                moves[moves.length - 1].toLowerCase().codePointAt(0) - "a".codePointAt(0) + 1,
                Integer.parseInt(moves[moves.length - 1].substring(1))
        );
        killCheckers();
    }

    private void killCheckers() {
        checkers.removeIf(Checker::isKilled);
    }

    /**
     * Метод осуществляет ходы в партии, при этом производятся проверки на валидацию хода
     * @param from клетка, откуда производится передвижение шашки
     *             передается строка regex-вида "[a-zA-Z]\d"
     * @param to клетка, куда производится передвижение шашки
     *           передается строка regex-вида "[a-zA-Z]\d"
     * @param isOrdinary флаг хода (обычный/атакующий)
     */
    public void addTurn(String from, String to, boolean isOrdinary) {
        String fromLower = from.toLowerCase();
        String toLower = to.toLowerCase();
        int xFrom = fromLower.codePointAt(0) - "a".codePointAt(0) + 1;
        int yFrom = Integer.parseInt(fromLower.substring(1));
        int xTo = toLower.codePointAt(0) - "a".codePointAt(0) + 1;
        int yTo = Integer.parseInt(toLower.substring(1));

        checkForBusyCell(xTo, yTo);
        checkForWhiteCell(xTo, yTo);
        if (isOrdinary) {
            int checkerTurnIndex = getCheckerIndex(xFrom, yFrom);
            boolean colorCheckerTurn = checkers.get(checkerTurnIndex).isWhite();
            for (Checker checker: checkers) {
                if (checker.isWhite() == colorCheckerTurn) {
                    checkForNeedToAttack(checker.getX(), checker.getY());
                }
            }
            checkForInvalidOrdinaryMove(xFrom, yFrom, xTo, yTo);
        } else {
            Checker enemy = checkForNotBusyCell(xFrom, yFrom, xTo, yTo);
//            checkForEnemyCell(xFrom, yFrom, xEnemy, yEnemy);
            checkForInvalidAttackMove(xFrom, yFrom, xTo, yTo);
            enemy.setKilled(true);
        }

        int indexChecker = getCheckerIndex(xFrom, yFrom);
        checkers.get(indexChecker).setCell(xTo, yTo);

        if (yTo == 1 && !checkers.get(indexChecker).isWhite() || yTo == SIZE_DESK
                && checkers.get(indexChecker).isWhite()) {
            checkers.get(indexChecker).setQueen(true);
        }
    }

    private int getEnemyCheckerIndex(int xAttacker, int yAttacker, int xEnemy, int yEnemy) {
        int attackerIndex = getCheckerIndex(xAttacker, yAttacker);
        int enemyIndex = getCheckerIndex(xEnemy, yEnemy);
        if (enemyIndex >= 0 && checkers.get(attackerIndex).isWhite() == checkers.get(enemyIndex).isWhite()) {
            enemyIndex = -1;
        }
        return enemyIndex;
    }

    private void checkForNeedToAttack(int xFrom, int yFrom) {
        int enemy1 = getEnemyCheckerIndex(xFrom, yFrom, xFrom - 1, yFrom - 1);
        int cellAfterEnemy1 = getCheckerIndex(xFrom - 2, yFrom - 2);
        int enemy2 = getEnemyCheckerIndex(xFrom, yFrom, xFrom + 1, yFrom + 1);
        int cellAfterEnemy2 = getCheckerIndex(xFrom + 2, yFrom + 2);
        int enemy3 = getEnemyCheckerIndex(xFrom, yFrom, xFrom - 1, yFrom + 1);
        int cellAfterEnemy3 = getCheckerIndex(xFrom - 2, yFrom + 2);
        int enemy4 = getEnemyCheckerIndex(xFrom, yFrom, xFrom + 1, yFrom - 1);
        int cellAfterEnemy4 = getCheckerIndex(xFrom + 2, yFrom - 2);

        if (enemy1 >= 0 && cellAfterEnemy1 == -1 && !checkers.get(enemy1).isKilled()) {
            throw new NeedToAttackException();
        } else if (enemy2 >= 0 && cellAfterEnemy2 == -1 && !checkers.get(enemy2).isKilled()) {
            throw new NeedToAttackException();
        } else if (enemy3 >= 0 && cellAfterEnemy3 == -1 && !checkers.get(enemy3).isKilled()) {
            throw new NeedToAttackException();
        } else if (enemy4 >= 0 && cellAfterEnemy4 == -1 && !checkers.get(enemy4).isKilled()) {
            throw new NeedToAttackException();
        }

        int attackChecker = getCheckerIndex(xFrom, yFrom);
        if (checkers.get(attackChecker).isQueen()) {
            int enemy;
            int cellAfterEnemy;
            for (int x = xFrom + 1, y = yFrom + 1; x <= 7 && y <= 7; x++, y++) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getCheckerIndex(x + 1, y + 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !checkers.get(enemy).isKilled()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
            for (int x = xFrom - 1, y = yFrom - 1; x >= 2 && y >= 2; x--, y--) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getCheckerIndex(x - 1, y - 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !checkers.get(enemy).isKilled()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
            for (int x = xFrom - 1, y = yFrom + 1; x >= 2 && y <= 7; x--, y++) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getCheckerIndex(x - 1, y + 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !checkers.get(enemy).isKilled()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
            for (int x = xFrom + 1, y = yFrom - 1; x <= 7 && y >= 2; x++, y--) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getCheckerIndex(x + 1, y - 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !checkers.get(enemy).isKilled()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
        }
    }

    private void checkForEnemyCell(int xFrom, int yFrom, int xEnemy, int yEnemy) {
        int attackerIndex = getCheckerIndex(xFrom, yFrom);
        int enemyIndex = getCheckerIndex(xEnemy, yEnemy);

        if (checkers.get(attackerIndex).isWhite() == checkers.get(enemyIndex).isWhite()) {
            throw new NotEnemyAttack();
        }
    }

    private Checker checkForNotBusyCell(int xFrom, int yFrom, int xTo, int yTo) {
        int indexCheckerFrom = getCheckerIndex(xFrom, yFrom);
        int xDir = (xTo - xFrom) / Math.abs(xTo - xFrom);
        int yDir = (yTo - yFrom) / Math.abs(yTo - yFrom);
        int xEnemy = xTo - xDir;
        int yEnemy = yTo - yDir;
        int enemyIndex = getCheckerIndex(xEnemy, yEnemy);

        if (!checkers.get(indexCheckerFrom).isQueen() && getCheckerIndex(xEnemy, yEnemy) == -1) {
            throw new NotBusyCellException();
        } else if (checkers.get(indexCheckerFrom).isQueen()) {
            int numEnemies = 0;
            for (int x = xFrom + xDir, y = yFrom + yDir; x != xTo && y != yTo; x += xDir, y += yDir) {
                int checkerIndex = getCheckerIndex(x, y);
                if (checkerIndex >= 0
                        && checkers.get(checkerIndex).isWhite() != checkers.get(indexCheckerFrom).isWhite()) {
                    numEnemies += 1;
                    enemyIndex = checkerIndex;
                } else if (checkerIndex >= 0) {
                    throw new TryToGoThroughFriendException();
                }
            }
            if (numEnemies != 1) {
                throw new InvalidAttackMoveException();
            }
        }

        return checkers.get(enemyIndex);
    }

    private void checkForInvalidAttackMove(int xFrom, int yFrom, int xTo, int yTo) {
        int indexCheckerFrom = getCheckerIndex(xFrom, yFrom);
        if (!checkers.get(indexCheckerFrom).isQueen()
                && (Math.abs(xFrom - xTo) != 2 || Math.abs(yFrom - yTo) != 2)
        ) {
            throw new InvalidAttackMoveException();
        } else if (checkers.get(indexCheckerFrom).isQueen()
                && Math.abs(xFrom - xTo) != Math.abs(yFrom - yTo)
        ) {
            throw new InvalidAttackMoveException();
        }
    }

    public ArrayList<String> getWhiteCheckers() {
        return getCheckers(true);
    }

    public ArrayList<String> getBlackCheckers() {
        return getCheckers(false);
    }

    private ArrayList<String> getCheckers(boolean isWhite) {
        ArrayList<String> res = new ArrayList<>();
        for (Checker checker : checkers) {
            if (checker.isWhite() == isWhite) {
                res.add(checker.getStringCell());
            }
        }
        Collections.sort(res);
        return res;
    }

    private void checkForInvalidOrdinaryMove(int xFrom, int yFrom, int xTo, int yTo) {
        int indexCheckerFrom = getCheckerIndex(xFrom, yFrom);
        if (!checkers.get(indexCheckerFrom).isQueen()
                && (Math.abs(xFrom - xTo) != 1 || Math.abs(yFrom - yTo) != 1)) {
            throw new InvalidOrdinaryMoveException();
        } else if (checkers.get(indexCheckerFrom).isQueen()
                && Math.abs(xFrom - xTo) != Math.abs(yFrom - yTo)) {
            throw new InvalidOrdinaryMoveException();
        }
    }

    private void checkForWhiteCell(int xTo, int yTo) {
        if ((yTo - xTo) % 2 != 0) {
            throw new WhiteCellException();
        }
    }

    private int getCheckerIndex(int x, int y) {
        if (x < 1 || x > SIZE_DESK || y < 1 || y > SIZE_DESK) {
            return CELL_NOT_EXISTS;
        }

        int res = -1;
        for (int i = 0; i < checkers.size() && res < 0; i++) {
            Checker checker = checkers.get(i);
            if (checker.getX() == x && checker.getY() == y) {
                res = i;
            }
        }

        return res;
    }

    private void checkForBusyCell(int xTo, int yTo) {
        if (getCheckerIndex(xTo, yTo) > -1) {
            throw new BusyCellException();
        }
    }

}
