package core;

import core.exception.OutOfBoardException;
import core.exception.UnsupportedFileFormatException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Board {

    // Scoring
    Scorer scorer;

    private final MoveHistory moveHistory;

    private final int boardSize;
    private double komi;

    // Current board state
    private Intersection[][] intersections;

    // Necessary for ko rule implementation
    private StoneChain lastCapturedStone;

    private boolean wasPreviousMovePass;
    private boolean isGameOver;

    // Player that makes move next (-1 for Black, 1 for White)
    private Player playerOne, playerTwo;
    private Player currentPlayer;

    public Board(int boardSize, double komi, String playerOneName, String playerTwoName) {
        this.boardSize = boardSize;
        this.komi = komi;
        this.playerOne = new Player(playerOneName, StoneColor.BLACK);
        this.playerTwo = new Player(playerTwoName, StoneColor.WHITE);
        this.moveHistory = new MoveHistory(this);
        this.wasPreviousMovePass = false;
        this.isGameOver = false;

        this.currentPlayer = playerOne;
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
        if (intersection.getColor() != StoneColor.EMPTY)
            return false;

        Set<Intersection> neighbours = getNeighbours(intersection);

        // Get all adjacent stone chains
        Set<StoneChain> adjacentPlayerStoneChains = new HashSet<>();
        Set<StoneChain> adjacentOpponentStoneChains = new HashSet<>();
        int currentMoveLiberties = 0;
        for (Intersection i : neighbours) {
            StoneColor intersectionColor = i.getColor();
            if (intersectionColor == currentPlayer.getColor())
                adjacentPlayerStoneChains.add(new StoneChain(this, i));
            else if (intersectionColor == currentPlayer.getOpponent())
                adjacentOpponentStoneChains.add(new StoneChain(this, i));
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
            StoneChain stoneChain = new StoneChain(this, intersection);
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


        intersections[x][y].setColor(currentPlayer.getColor());
        moveHistory.addMove(x, y, currentPlayer.getColor(), playerOne.getCapturedStones(), playerTwo.getCapturedStones());
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

        moveHistory.addMove(-1, -1, currentPlayer.getColor(), playerOne.getCapturedStones(), playerTwo.getCapturedStones());
        wasPreviousMovePass = true;
        changePlayer();
    }

    public void flipDeadStones(int x, int y) {
        Intersection intersection = getIntersection(x, y);

    }

    //TODO scoring
    private void gameOver() {
        isGameOver = true;
        scorer = new Scorer(this);
        //        String winner = (scorer.blackPoints > scorer.whitePoints) ? "BLACK" : "WHITE";
        //System.out.println("White points: " + scorer.whitePoints + "\tBlack points: " + scorer.blackPoints + "\nWinner is " + winner);
    }

    public double getResult() {
        scorer.processScore();
        return scorer.whitePoints - scorer.blackPoints;
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
    private void proceedMoveHistory(StoneColor[][] boardState) {
        if (boardState != null) {
            Move move = moveHistory.getCurrentMove();
            playerOne.setCapturedStones(move.getBlackCaptures());
            playerTwo.setCapturedStones(move.getWhiteCaptures());
            for (int x = 0; x < boardSize; x++)
                for (int y = 0; y < boardSize; y++)
                    intersections[x][y].setColor(boardState[x][y]);
        }
    }


    private void changePlayer() {
        currentPlayer = (currentPlayer == playerOne) ? playerTwo : playerOne;
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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getBlackCaptures() {
        return playerOne.getCapturedStones();
    }

    public int getWhiteCaptures() {
        return playerTwo.getCapturedStones();
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public boolean isGameOver() {
        return isGameOver;
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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Black: ").append(playerOne.getCapturedStones()).append("   White: ").append(playerTwo.getCapturedStones()).append('\n');
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
