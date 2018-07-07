package core;

import core.exception.OutOfBoardException;
import core.exception.UnsupportedFileFormatException;

import java.io.*;
import java.util.Scanner;
import java.util.Stack;

public class MoveHistory {
    private Stack<Move> precedingMoves;
    private Stack<Move> subsequentMoves;
    private Move currentMove;
    private final Board board;


    public MoveHistory(Board board) {
        this.board = board;
        this.precedingMoves = new Stack<>();
        this.subsequentMoves = new Stack<>();
        this.currentMove = null;
    }

    public void addMove(int x, int y, int player, int blackCaptures, int whiteCaptures) {
        currentMove = new Move(x, y, player, blackCaptures, whiteCaptures, board);
        precedingMoves.push(currentMove);
        subsequentMoves.clear();
    }

    public int[][] undo() {
        return getIntersections(precedingMoves, subsequentMoves);
    }
    
    public int[][] redo() {
        return getIntersections(subsequentMoves, precedingMoves);
    }
    
    private int[][] getIntersections(Stack<Move> firstStack, Stack<Move> secondStack) {
        if (firstStack.isEmpty())
            return null;
        int x = firstStack.peek().getX();
        int y = firstStack.peek().getY();
        secondStack.push(firstStack.pop());
        currentMove = precedingMoves.peek();
        return currentMove.getBoardState();
    }


    public int getMoveNumber() {
        return precedingMoves.size();
    }

    public Move getCurrentMove() {
        return currentMove;
    }

    public void saveGame(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            StringBuilder sb = new StringBuilder();
            // Game conditions
            sb.append(board.getBoardSize()).append(',').append(board.getKomi()).append('\n');
            for (int i = 0; i < precedingMoves.size(); i++) {
                sb.append(precedingMoves.get(i).getX()).append(',').append(precedingMoves.get(i).getY()).append(';');
            }
            writer.write(sb.toString());
        }
    }

    public static Board loadGame(File file) throws UnsupportedFileFormatException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] gameConditions = reader.readLine().split(",");
            try {
                // Reading game conditions: board size and komi
                int boardSize = Integer.parseInt(gameConditions[0]);
                double komi = Double.parseDouble(gameConditions[1]);
                Board board = new Board(boardSize, komi);
                // Reading coordinates
                Scanner scanner = new Scanner(reader.readLine());
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String[] temp = scanner.next().split(",");
                    // Checks if both coordinates are present
                    if (temp.length != 2)
                        throw new UnsupportedFileFormatException(file.getName());
                    int x = Integer.parseInt(temp[0]);
                    int y = Integer.parseInt(temp[1]);
                    if (x == -1 && y == -1)
                        board.makePass();
                    board.makeMove(x, y);
                }
                return board;
            } catch (UnsupportedFileFormatException | NumberFormatException exc) {
                throw new UnsupportedFileFormatException("Save file " + file.getName() + " is broken");
            } catch (OutOfBoardException exc) {
                throw new UnsupportedFileFormatException("Save file " + file.getName() + " contains illegal coordinates");
            }
        }
    }
}
