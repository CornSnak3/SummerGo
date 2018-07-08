package core;

import core.exception.OutOfBoardException;
import core.exception.UnsupportedFileFormatException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Board {

    private final MoveHistory moveHistory;

    private final int boardSize;
    private double komi;
    private Intersection[][] intersections;

    // Necessary for ko rule implementation
    private StoneChain lastCapturedStone;

    private boolean wasPreviousMovePass;
    private boolean isGameOver;

    // Represents player that makes move next (-1 for Black, 1 for White)
    private int currentPlayer;

    // Numbers of captured stones
    private int blackCaptures;
    private int whiteCaptures;

    public Board(int boardSize, double komi) {
        this.boardSize = boardSize;
        this.komi = komi;
        this.moveHistory = new MoveHistory(this);
        this.wasPreviousMovePass = false;
        this.isGameOver = false;
        this.currentPlayer = -1;
        this.blackCaptures = this.whiteCaptures = 0;

        // Initialize intersections
        this.intersections = new Intersection[boardSize][boardSize];
        for (int x = 0; x < boardSize; x++)
            for (int y = 0; y < boardSize; y++)
                intersections[x][y] = new Intersection(x, y);
    }

    /**
     * Make move at (x, y)
     * @param x first coordinate
     * @param y second coordinate
     */
    public boolean makeMove(int x, int y) {
        if (isGameOver)
            return false;

        Intersection intersection = getIntersection(x, y);

        // Check whether intersection is occupied
        if (intersection.getState() != 0)
            return false;

        Set<Intersection> neighbours = getNeighbours(intersection);

        // Get all adjacent stone chains
        Set<StoneChain> adjacentPlayerStoneChains = new HashSet<>();
        Set<StoneChain> adjacentOpponentStoneChains = new HashSet<>();
        int currentMoveLiberties = 0;
        for (Intersection i : neighbours) {
            int state = i.getState();
            if (state == currentPlayer)
                adjacentPlayerStoneChains.add(new StoneChain(i));
            else if (state == -currentPlayer)
                adjacentOpponentStoneChains.add(new StoneChain(i));
            else
                currentMoveLiberties++;
        }

        // Get all opponent chains that die after a move
        Set<StoneChain> deadStoneChains = new HashSet<>();
        for (StoneChain stoneChain : adjacentOpponentStoneChains)
            if (stoneChain.getLibertiesCount() == 1)
                deadStoneChains.add(stoneChain);

        // Check whether move is suicidal
        if (deadStoneChains.size() == 0 && currentMoveLiberties == 0) {
            if (adjacentPlayerStoneChains.size() == 0) {
                return false;
            } else {
                int adjacentStoneLiberties = 0;
                for (StoneChain stoneChain : adjacentPlayerStoneChains)
                    if (stoneChain.getLibertiesCount() == 1)
                        adjacentStoneLiberties++;
                if (adjacentStoneLiberties == adjacentPlayerStoneChains.size())
                    return false;
            }
        }

        // Check whether move is ko repetition
        if (deadStoneChains.size() == 1) {
            StoneChain stoneChain = new StoneChain(intersection);
            if (stoneChain.equals(lastCapturedStone))
                return false;
            stoneChain = deadStoneChains.iterator().next();
            lastCapturedStone = (stoneChain.size() == 1) ? stoneChain : null;
        } else {
            lastCapturedStone = null;
        }

        // Remove all dead stones from board
        for (StoneChain stoneChain : deadStoneChains)
            stoneChain.die();


        intersections[x][y].setState(currentPlayer);
        moveHistory.addMove(x, y, currentPlayer, blackCaptures, whiteCaptures);
        wasPreviousMovePass = false;
        changePlayer();
        return true;
    }

    /**
     * Pass
     */
    public void makePass() {
        if (wasPreviousMovePass)
            gameOver();

        moveHistory.addMove(-1, -1, currentPlayer, blackCaptures, whiteCaptures);
        changePlayer();
    }

    //TODO scoring
    private void gameOver() {
        isGameOver = true;
        Scorer scorer = new Scorer();
        scorer.processScore();
        String winner = (scorer.blackPoints > scorer.whitePoints) ? "BLACK" : "WHITE";
        System.out.println("White points: " + scorer.whitePoints + "\tBlack points: " + scorer.blackPoints + "\nWinner is " + winner);
    }

    public void undo() {
        proceedMoveHistory(moveHistory.undo());
    }

    public void redo() {
        proceedMoveHistory(moveHistory.redo());
    }

    /**
     * Utility function for undo/redo mechanics
     * @param boardState board state retrieved from undo() or redo() method
     */
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

    /**
     * Get intersection for the given coordinates
     * @param x
     * @param y
     * @return Intersection corresponding to coordinates (x, y)
     */
    public Intersection getIntersection(int x, int y) {
        if (!isOnBoard(x, y))
            throw new OutOfBoardException("Coordinate (" + x + ", " + y +") is out of board");
        return intersections[x][y];
    }

    /**
     * Get neighbouring intersections for given intersection
     * @param intersection
     * @return Set of neighbouring intersectons
     */
    Set<Intersection> getNeighbours(Intersection intersection) {
        Set<Intersection> neighbours = new HashSet<>();
        int x = intersection.getX();
        int y = intersection.getY();
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

    public double getKomi() {
        return komi;
    }

    public Intersection[][] getIntersections() {
        return intersections;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    /**
     * Save current game in file
     * @param file file name
     * @throws IOException
     */
    public void saveGame(File file) throws IOException {
        moveHistory.saveGame(file);
    }

    /**
     * Get Board object which represents position and move sequence from file
     * @param file file name
     * @return Board object
     * @throws IOException
     * @throws UnsupportedFileFormatException If file is invalid or contains illegal positions
     */
    public static Board loadGame(File file) throws IOException, UnsupportedFileFormatException {
        return MoveHistory.loadGame(file);
    }

    /**
     * Class for stone chain. Used to calculate dead stones on-the-fly
     *
     */
    private class StoneChain {

        Set<Intersection> stones;
        int player;

        StoneChain(Intersection intersection) {
            this.stones = new HashSet<>();
            this.stones.add(intersection);
            this.player = intersection.getState();
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
            if (obj == null)
                return false;
            if (obj instanceof StoneChain)
                return (this.hashCode() == obj.hashCode());
            return false;
        }

        void makeStoneChainFromIntersection(Intersection intersection) {
            for (Intersection i : getNeighbours(intersection)) {
                if (i.getState() == intersection.getState() && i.getState() != 0) {
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

        int size() {
            return this.stones.size();
        }

    }

    /**
     * We assume that all dead stones are marked correctly
     */
    private class Scorer {
        Set<StoneChain> blackStoneChains;
        Set<StoneChain> whiteStoneChains;
        Set<StoneChain> emptyRegions;
        double whitePoints, blackPoints;

        Scorer() {
            this.blackStoneChains = new HashSet<>();
            this.whiteStoneChains = new HashSet<>();
            this.emptyRegions = new HashSet<>();
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    Intersection intersection = getIntersection(x, y);
                    switch (intersection.getState()) {
                        case 0:
                            emptyRegions.add(new StoneChain(intersection));
                            break;
                        case -1:
                            blackStoneChains.add(new StoneChain(intersection));
                            break;
                        case 1:
                            whiteStoneChains.add(new StoneChain(intersection));
                            break;
                    }
                }
            }
        }

        void processScore() {
            for (StoneChain emptyRegion : emptyRegions) {
                int owner = getOwnerOfARegion(emptyRegion);
                if (owner == -1)
                    blackPoints += emptyRegion.size();
                else if (owner == 1)
                    whitePoints += emptyRegion.size();
            }
            whitePoints += komi;
            blackPoints += blackCaptures;
            whitePoints += whiteCaptures;
        }

        int getOwnerOfARegion(StoneChain emptyRegion) {
            int owner = 0;
            for (Intersection intersection : emptyRegion.stones)
                for (Intersection i : getNeighbours(intersection))
                    if ((owner = i.getState()) != 0)
                        return owner;
            return owner;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Black: ").append(blackCaptures).append("   White: ").append(whiteCaptures).append('\n');
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
        sb.append("Move number: ").append(moveHistory.getMoveNumber()).append('\n');
        return sb.toString();
    }

}
