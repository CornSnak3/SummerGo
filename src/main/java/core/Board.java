package core;

import java.util.HashSet;
import java.util.Set;

public class Board {

    private final MoveHistory moveHistory;

    private Intersection[][] intersections;
    private final int boardSize;

    private int passCount;

    // -1 for BLACK, 0 for EMPTY, 1 for WHITE
    private int currentPlayer;

    private int blackCaptures;
    private int whiteCaptures;

    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.moveHistory = new MoveHistory(this);
        this.intersections = new Intersection[boardSize][boardSize];
        for (int x = 0; x < boardSize; x++)
            for (int y = 0; y < boardSize; y++)
                intersections[x][y] = new Intersection(this, x, y);
        this.passCount = 0;
        this.currentPlayer = -1;
        this.blackCaptures = this.whiteCaptures = 0;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void makeMove(int x, int y) {

        Intersection intersection = getIntersection(x, y);
        if (intersection.getState() != 0)
            return;

        Set<Intersection> neighbours = getNeighbours(intersection);

        Set<StoneChain> adjacentPlayerStoneChains = new HashSet<>();
        Set<StoneChain> adjacentOpponentStoneChains = new HashSet<>();
        for (Intersection i : neighbours) {
            int state = i.getState();
            if (state == currentPlayer) {
                adjacentPlayerStoneChains.add(new StoneChain(i));
            } else if (state == -currentPlayer){
                adjacentOpponentStoneChains.add(new StoneChain(i));
            }
        }

        Set<StoneChain> deadStoneChains = new HashSet<>();
        for (StoneChain stoneChain : adjacentOpponentStoneChains)
            if (stoneChain.getLibertiesCount() == 1)
                deadStoneChains.add(stoneChain);

        /**
         * Suicidal move check.
         *
         */
        if (deadStoneChains.size() == 0)
            for (StoneChain stoneChain : adjacentPlayerStoneChains)
                if (stoneChain.getLibertiesCount() == 1)
                   return;

        /**
         * Now we know that move is legal and we can proceed
         */

        for (StoneChain stoneChain : deadStoneChains)
            stoneChain.die();

        intersections[x][y].setState(currentPlayer);

        moveHistory.addMove(x, y, currentPlayer, blackCaptures, whiteCaptures);

        changePlayer();
    }

    //TODO pass mechanics

    public void undo() {
        var boardState = moveHistory.undo();
        proceedMoveHistory(boardState);
    }

    public void redo() {
        var boardState = moveHistory.redo();
        proceedMoveHistory(boardState);
    }

    private void proceedMoveHistory(int[][] boardState) {
        if (boardState != null) {
            Move move = moveHistory.getCurrentMove();
            blackCaptures = move.getBlackCaptures();
            whiteCaptures = move.getWhiteCaptures();
            for (int x = 0; x < boardSize; x++)
                for (int y = 0; y < boardSize; y++)
                    intersections[x][y].setState(boardState[x][y]);
        }
    }


    private void changePlayer() {
        currentPlayer = (currentPlayer == -1) ? 1 : -1;
    }

    public boolean isOnBoard(int x, int y) {
        return (x > 0 || y > 0 || x < boardSize || y < boardSize);
    }

    public Intersection getIntersection(int x, int y) {
        if (!isOnBoard(x, y))
            throw new IllegalArgumentException("Out of board");
        return intersections[x][y];
    }

    Set<Intersection> getNeighbours(Intersection i) {
        Set<Intersection> neighbours = new HashSet<>();
        int x = i.getX();
        int y = i.getY();
        if (x > 0)
            neighbours.add(intersections[x - 1][y]);
        if (x < boardSize - 1)
            neighbours.add(intersections[x + 1][y]);
        if (y > 0)
            neighbours.add(intersections[x][y - 1]);
        if (y < boardSize - 1)
            neighbours.add(intersections[x][y + 1]);

        return neighbours;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Black: ").append(blackCaptures).append("\tWhite: ").append(whiteCaptures).append('\n');
        sb.append("  ");
        for (int i = 0; i < boardSize; i++)
            sb.append(i).append(' ');
        sb.append('\n');
        for (int y = 0; y < boardSize; y++) {
            sb.append(y).append(' ');
            for (int x = 0; x < boardSize; x++) {
                sb.append(getIntersection(x, y)).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public Intersection[][] getIntersections() {
        return intersections;
    }

    /**
     * Class representing a stone chain. Used in on-the-fly move calculation.
     *
     */
    private class StoneChain {

        Set<Intersection> stones;

        StoneChain(Intersection intersection) {
            this.stones = new HashSet<>();
            this.stones.add(intersection);
            makeStoneChainFromIntersection(intersection);
        }

        @Override
        public int hashCode() {
            int hashCode = 0;
            for (Intersection i : stones)
                hashCode += i.hashCode();
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof StoneChain)
                return this.hashCode() == obj.hashCode();
            return false;
        }

        void makeStoneChainFromIntersection(Intersection intersection) {
            for (Intersection i : getNeighbours(intersection)) {
                if (i.getState() == intersection.getState()) {
                    if (!stones.contains(i)) {
                        stones.add(i);
                        makeStoneChainFromIntersection(i);
                    }
                }
            }
        }

        int getLibertiesCount() {
            Set<Intersection> liberties = new HashSet<>();
            for (Intersection i : stones)
                for (Intersection j : getNeighbours(i))
                    if (j.getState() == 0)
                        liberties.add(j);

            return liberties.size();
        }

        void die() {
            for (Intersection i : stones) {
                i.setState(0);
                if (currentPlayer == -1)
                    blackCaptures++;
                else
                    whiteCaptures++;
            }
        }
    }

}
