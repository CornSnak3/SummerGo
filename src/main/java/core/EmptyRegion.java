package core;

import java.util.HashSet;
import java.util.Set;

class EmptyRegion {
    private Board board;
    Set<Intersection> intersections;

    EmptyRegion(Board board, Intersection intersection) {
        this.board = board;
        this.intersections = new HashSet<>();
        this.intersections.add(intersection);
        makeEmptyRegionFromIntersection(intersection);
    }

    void makeEmptyRegionFromIntersection(Intersection intersection) {
        for (Intersection i : board.getNeighbours(intersection)) {
            if (i.getColor() == StoneColor.EMPTY) {
                if (!intersections.contains(i)) {
                    intersections.add(i);
                    makeEmptyRegionFromIntersection(i);
                }
            }
        }
    }

    int size() {
        return intersections.size();
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Intersection i : intersections)
            hashCode += i.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof EmptyRegion)
            return (this.hashCode() == obj.hashCode());
        return false;
    }
}
