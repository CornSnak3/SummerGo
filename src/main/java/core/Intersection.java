package core;

import static core.StoneColor.*;

/**
 * Class for intersection. Stores intersection coordinates and current color
 */
public class Intersection {
    private final int x, y;
    // Representation of intersection color (-1 for Black, 0 for Empty, 1 for White)
    private StoneColor color;

    public Intersection(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = EMPTY;
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

    public void setColor(StoneColor color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime * x) + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Intersection))
            return false;
        return (this.hashCode() == obj.hashCode());
    }

    @Override
    public String toString() {
        switch(color) {
            case BLACK:
                return "B";
            case EMPTY:
                return "+";
            case WHITE:
                return "W";
        }
        throw new IllegalArgumentException("Illegal interception color");
    }
}
