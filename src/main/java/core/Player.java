package core;

public class Player {
    private int id;
    private String name;
    private StoneColor color;
    private int capturedStones;

    public Player(String name, StoneColor color) {
        this.name = name;
        this.color = color;
        this.id = (color == StoneColor.BLACK) ? 1 : 2;
        this.capturedStones = 0;
    }

    public void setCapturedStones(int i) {
        capturedStones = i;
    }

    public void changeCapturedStones(int i) {
        this.capturedStones += i;
    }

    public int getCapturedStones() {
        return capturedStones;
    }

    public String getName() {
        return name;
    }

    public StoneColor getColor() {
        return color;
    }

    public StoneColor getOpponent() {
        return color == StoneColor.BLACK ? StoneColor.WHITE : StoneColor.BLACK;
    }

    public int getId() {
        return id;
    }
}
