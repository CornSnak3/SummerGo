package core;

public class Move {
    private StoneColor color;
    private int x, y;
    private int blackCaptures, whiteCaptures;
    private final StoneColor[][] boardState;

    public Move(int x, int y, StoneColor color, int blackCaptures, int whiteCaptures, Board board) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.blackCaptures = blackCaptures;
        this.whiteCaptures = whiteCaptures;
        int size = board.getBoardSize();
        this.boardState = new StoneColor[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++)
                this.boardState[i][j] = board.getIntersection(i, j).getColor();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public StoneColor getColor() {
        return color;
    }

    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    public StoneColor[][] getBoardState() {
        return boardState;
    }
}
