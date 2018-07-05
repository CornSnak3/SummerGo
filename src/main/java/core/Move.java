package core;

public class Move {
    private int player;
    private int x, y;
    private int blackCaptures, whiteCaptures;

    public Move(int x, int y, int player, int blackCaptures, int whiteCaptures) {
        this.x = x;
        this.y = y;
        this.player = player;
        this.blackCaptures = blackCaptures;
        this.whiteCaptures = whiteCaptures;
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
}
