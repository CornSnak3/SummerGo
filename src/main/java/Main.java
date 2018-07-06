import core.Board;
import core.MoveHistory;

public class Main {
    public static void main(String[] args) {
        /*Board board = new Board(9, 6.5);
        System.out.println(board);
        board.makeMove(0, 0);
        System.out.println(board);
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
        board.save("test.save");*/
        try {
            Board board = MoveHistory.loadGame("test.save");
            System.out.println(board);
        } catch (Exception e) {

        }


    }
}
