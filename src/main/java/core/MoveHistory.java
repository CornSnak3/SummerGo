package core;

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
        precedingMoves.push(new Move(x, y, player, blackCaptures, whiteCaptures, board));
        currentMove = precedingMoves.peek();
        // TODO game tree
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
        return subsequentMoves.size();
    }

    public Move getCurrentMove() {
        return currentMove;
    }
}
