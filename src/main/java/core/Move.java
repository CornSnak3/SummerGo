package core;

public class Move {
    private int player;
    private int x, y;
    private int blackCaptures, whiteCaptures;
    private final int[][] boardState;

    public Move(int x, int y, int player, int blackCaptures, int whiteCaptures, Board board) {
        this.x = x;
        this.y = y;
        this.player = player;
        this.blackCaptures = blackCaptures;
        this.whiteCaptures = whiteCaptures;
        int size = board.getBoardSize();
        this.boardState = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                this.boardState[i][j] = board.getIntersection(i, j).getState();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlayer() {
        return player;
    }

    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    public int[][] getBoardState() {
        return boardState;
    }
}
