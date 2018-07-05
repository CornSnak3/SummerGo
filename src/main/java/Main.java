import core.Board;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(9);
        board.makeMove(0, 0);
        board.makeMove(0, 2);
        board.makeMove(0, 1);
        board.makeMove(1, 2);
        board.makeMove(1, 1);
        board.makeMove(2, 1);
        board.makeMove(2, 0);
        board.makeMove(3, 0);
        board.makeMove(7, 7);
        board.makeMove(1, 0);
        System.out.println(board);
        board.undo();
        System.out.println(board);
        board.redo();
        System.out.println(board);

    }
}
