package core;

public class Intersection {
    private final Board board;
    private final int x, y;
    // -1 for BLACK, 0 for EMPTY, 1 for WHITE
    private int state;

    public Intersection(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.state = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        if (state != -1 && state !=0 && state != 1)
            throw new IllegalArgumentException("Illegal interception state");
        this.state = state;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime * x) + y;
    }

    @Override
    public String toString() {
        switch(state) {
            case -1:
                return "B";
            case 0:
                return "+";
            case 1:
                return "W";
        }
        throw new IllegalArgumentException("Illegal interception state");
    }
}
