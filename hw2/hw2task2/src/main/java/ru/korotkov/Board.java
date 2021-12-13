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

    private final ArrayList<Tower> towers = new ArrayList<>();

    /**
     * Добавление башен на доску
     * @param checkersToAdd массив клеток, на которых находятся башни игрока
     */
    public void addTowers(String[] checkersToAdd) {
        for (String s : checkersToAdd) {
            String[] splitData = s.split("_");
            int positionX = splitData[0].toLowerCase().codePointAt(0) - "a".codePointAt(0) + 1;
            int positionY = Integer.parseInt(splitData[0].substring(1));

            Tower tower = new Tower(positionX, positionY);

            Checker checker;
            for (int i = 0; i < splitData[1].length(); i++) {
                boolean isWhite = splitData[1].toLowerCase().charAt(i) == 'w';
                if (splitData[1].codePointAt(i) >= "A".codePointAt(0)
                        && splitData[1].codePointAt(i) <= "Z".codePointAt(0)) {
                    checker = new Checker(true, isWhite);
                } else {
                    checker = new Checker(false, isWhite);
                }
                tower.addChecker(checker);
            }

            towers.add(tower);
        }
    }

    public void addTurnOrdinary(String fromToMove) {
        String[] move = fromToMove.split("-");
        addTurn(move[0].substring(0, 2), move[1].substring(0, 2), true);
    }

    public void addTurnAttack(String fromToMove) {
        String[] moves = fromToMove.split(":");
        for (int i = 1; i < moves.length; i++) {
            addTurn(moves[i - 1].substring(0, 2), moves[i].substring(0, 2), false);
            deleteEmptyTowers();
        }

        checkForNeedToAttack(
                moves[moves.length - 1].toLowerCase().codePointAt(0) - "a".codePointAt(0) + 1,
                Integer.parseInt(moves[moves.length - 1].substring(1, 2))
        );
        resetAttackTowers();
    }

    private void resetAttackTowers() {
        for (Tower tower: towers) {
            tower.setAttacked(false);
        }
    }

    private void deleteEmptyTowers() {
        towers.removeIf(tower -> tower.getHeight() == 0);
    }

    /**
     * Метод осуществляет ходы в партии, при этом производятся проверки на валидацию хода
     * @param from клетка, откуда производится передвижение башни
     *             передается строка regex-вида "[a-zA-Z]\d"
     * @param to клетка, куда производится передвижение башни
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
        int towerTurnIndex = getTowerIndex(xFrom, yFrom);
        if (isOrdinary) {

            boolean colorCheckerTurn = towers.get(towerTurnIndex).isWhite();
            for (Tower tower: towers) {
                if (tower.isWhite() == colorCheckerTurn) {
                    checkForNeedToAttack(tower.getX(), tower.getY());
                }
            }
            checkForInvalidOrdinaryMove(xFrom, yFrom, xTo, yTo);
        } else {
            Tower enemyTower = checkForNotBusyCell(xFrom, yFrom, xTo, yTo);
            checkForInvalidAttackMove(xFrom, yFrom, xTo, yTo);

            towers.get(towerTurnIndex).addChecker(enemyTower.attacked());
        }

        towers.get(towerTurnIndex).setCell(xTo, yTo);

        if (yTo == 1 && !towers.get(towerTurnIndex).isWhite() || yTo == SIZE_DESK
                && towers.get(towerTurnIndex).isWhite()) {
            towers.get(towerTurnIndex).setQueen(true);
        }
    }

    private int getEnemyCheckerIndex(int xAttacker, int yAttacker, int xEnemy, int yEnemy) {
        int attackerIndex = getTowerIndex(xAttacker, yAttacker);
        int enemyIndex = getTowerIndex(xEnemy, yEnemy);
        if (enemyIndex >= 0 && towers.get(attackerIndex).isWhite() == towers.get(enemyIndex).isWhite()) {
            enemyIndex = -1;
        }
        return enemyIndex;
    }

    private void checkForNeedToAttack(int xFrom, int yFrom) {
        int enemy1 = getEnemyCheckerIndex(xFrom, yFrom, xFrom - 1, yFrom - 1);
        int cellAfterEnemy1 = getTowerIndex(xFrom - 2, yFrom - 2);
        int enemy2 = getEnemyCheckerIndex(xFrom, yFrom, xFrom + 1, yFrom + 1);
        int cellAfterEnemy2 = getTowerIndex(xFrom + 2, yFrom + 2);
        int enemy3 = getEnemyCheckerIndex(xFrom, yFrom, xFrom - 1, yFrom + 1);
        int cellAfterEnemy3 = getTowerIndex(xFrom - 2, yFrom + 2);
        int enemy4 = getEnemyCheckerIndex(xFrom, yFrom, xFrom + 1, yFrom - 1);
        int cellAfterEnemy4 = getTowerIndex(xFrom + 2, yFrom - 2);

        if (enemy1 >= 0 && cellAfterEnemy1 == -1 && !towers.get(enemy1).isAttacked()) {
            throw new NeedToAttackException();
        } else if (enemy2 >= 0 && cellAfterEnemy2 == -1 && !towers.get(enemy2).isAttacked()) {
            throw new NeedToAttackException();
        } else if (enemy3 >= 0 && cellAfterEnemy3 == -1 && !towers.get(enemy3).isAttacked()) {
            throw new NeedToAttackException();
        } else if (enemy4 >= 0 && cellAfterEnemy4 == -1 && !towers.get(enemy4).isAttacked()) {
            throw new NeedToAttackException();
        }

        int attackChecker = getTowerIndex(xFrom, yFrom);
        if (towers.get(attackChecker).isQueen()) {
            int enemy;
            int cellAfterEnemy;
            for (int x = xFrom + 1, y = yFrom + 1; x <= 7 && y <= 7; x++, y++) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getTowerIndex(x + 1, y + 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !towers.get(enemy).isAttacked()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
            for (int x = xFrom - 1, y = yFrom - 1; x >= 2 && y >= 2; x--, y--) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getTowerIndex(x - 1, y - 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !towers.get(enemy).isAttacked()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
            for (int x = xFrom - 1, y = yFrom + 1; x >= 2 && y <= 7; x--, y++) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getTowerIndex(x - 1, y + 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !towers.get(enemy).isAttacked()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
            for (int x = xFrom + 1, y = yFrom - 1; x <= 7 && y >= 2; x++, y--) {
                enemy = getEnemyCheckerIndex(xFrom, yFrom, x, y);
                cellAfterEnemy = getTowerIndex(x + 1, y - 1);
                if (enemy >= 0 && cellAfterEnemy == -1 && !towers.get(enemy).isAttacked()) {
                    throw new NeedToAttackException();
                }
                if (enemy >= 0) {
                    break;
                }
            }
        }
    }

    private void checkForEnemyCell(int xFrom, int yFrom, int xEnemy, int yEnemy) {
        int attackerIndex = getTowerIndex(xFrom, yFrom);
        int enemyIndex = getTowerIndex(xEnemy, yEnemy);

        if (towers.get(attackerIndex).isWhite() == towers.get(enemyIndex).isWhite()) {
            throw new NotEnemyAttack();
        }
    }

    private Tower checkForNotBusyCell(int xFrom, int yFrom, int xTo, int yTo) {
        int indexCheckerFrom = getTowerIndex(xFrom, yFrom);
        int xDir = (xTo - xFrom) / Math.abs(xTo - xFrom);
        int yDir = (yTo - yFrom) / Math.abs(yTo - yFrom);
        int xEnemy = xTo - xDir;
        int yEnemy = yTo - yDir;
        int enemyIndex = getTowerIndex(xEnemy, yEnemy);

        if (!towers.get(indexCheckerFrom).isQueen() && getTowerIndex(xEnemy, yEnemy) == -1) {
            throw new NotBusyCellException();
        } else if (towers.get(indexCheckerFrom).isQueen()) {
            int numEnemies = 0;
            for (int x = xFrom + xDir, y = yFrom + yDir; x != xTo && y != yTo; x += xDir, y += yDir) {
                int checkerIndex = getTowerIndex(x, y);
                if (checkerIndex >= 0
                        && towers.get(checkerIndex).isWhite() != towers.get(indexCheckerFrom).isWhite()) {
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

        return towers.get(enemyIndex);
    }

    private void checkForInvalidAttackMove(int xFrom, int yFrom, int xTo, int yTo) {
        int indexCheckerFrom = getTowerIndex(xFrom, yFrom);
        if (!towers.get(indexCheckerFrom).isQueen()
                && (Math.abs(xFrom - xTo) != 2 || Math.abs(yFrom - yTo) != 2)
        ) {
            throw new InvalidAttackMoveException();
        } else if (towers.get(indexCheckerFrom).isQueen()
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
        for (Tower tower : towers) {
            if (tower.isWhite() == isWhite) {
                res.add(tower.getStringCell());
            }
        }
        Collections.sort(res);
        return res;
    }

    private void checkForInvalidOrdinaryMove(int xFrom, int yFrom, int xTo, int yTo) {
        int indexCheckerFrom = getTowerIndex(xFrom, yFrom);
        if (!towers.get(indexCheckerFrom).isQueen()
                && (Math.abs(xFrom - xTo) != 1 || Math.abs(yFrom - yTo) != 1)) {
            throw new InvalidOrdinaryMoveException();
        } else if (towers.get(indexCheckerFrom).isQueen()
                && Math.abs(xFrom - xTo) != Math.abs(yFrom - yTo)) {
            throw new InvalidOrdinaryMoveException();
        }
    }

    private void checkForWhiteCell(int xTo, int yTo) {
        if ((yTo - xTo) % 2 != 0) {
            throw new WhiteCellException();
        }
    }

    private int getTowerIndex(int x, int y) {
        if (x < 1 || x > SIZE_DESK || y < 1 || y > SIZE_DESK) {
            return CELL_NOT_EXISTS;
        }

        int res = -1;
        for (int i = 0; i < towers.size() && res < 0; i++) {
            Tower checker = towers.get(i);
            if (checker.getX() == x && checker.getY() == y) {
                res = i;
            }
        }

        return res;
    }

    private void checkForBusyCell(int xTo, int yTo) {
        if (getTowerIndex(xTo, yTo) > -1) {
            throw new BusyCellException();
        }
    }

}
