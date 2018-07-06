package core;

/**
 * Class for intersection. Stores intersection coordinates and current state
 */
public class Intersection {
    private final int x, y;
    // Representation of intersection state (-1 for Black, 0 for Empty, 1 for White)
    private int state;

    public Intersection(int x, int y) {
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
    public boolean equals(Object obj) {
        if (!(obj instanceof Intersection))
            return false;
        return (this.hashCode() == obj.hashCode());
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
