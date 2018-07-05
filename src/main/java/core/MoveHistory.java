package core;

import java.util.Stack;

public class MoveHistory {
    private Stack<Move> precedingMoves;
    private Stack<Move> subsequentMoves;
    private Move currentMove;

    public MoveHistory() {
        this.precedingMoves = new Stack<>();
        this.subsequentMoves = new Stack<>();
        this.currentMove = null;
    }

    public void addMove(int x, int y, int player, int blackCaptures, int whiteCaptures) {
        precedingMoves.push(new Move(x, y, player, blackCaptures, whiteCaptures));
        currentMove = precedingMoves.peek();
    }

    public void undo() {
        if (precedingMoves.isEmpty())
            return;
        subsequentMoves.push(precedingMoves.pop());
        currentMove = precedingMoves.peek();
    }

    public void redo() {
        if (subsequentMoves.isEmpty())
            return;
        precedingMoves.push(subsequentMoves.pop());
        currentMove = precedingMoves.peek();
    }

    public int getMoveNumber() {
        return subsequentMoves.size();
    }
}
