package core;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for stone chain. Used to calculate dead stones on-the-fly
 *
 */
class StoneChain {

    private Board board;
    Set<Intersection> stones;
    StoneColor color;

    StoneChain(Board board, Intersection intersection) {
        this.board = board;
        this.stones = new HashSet<>();
        this.stones.add(intersection);
        this.color = intersection.getColor();
        makeStoneChainFromIntersection(intersection);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Intersection i : stones)
            hashCode += i.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof StoneChain)
            return (this.hashCode() == obj.hashCode());
        return false;
    }

    void makeStoneChainFromIntersection(Intersection intersection) {
        for (Intersection i : board.getNeighbours(intersection)) {
            if (i.getColor() == intersection.getColor() && i.getColor() != StoneColor.EMPTY) {
                if (!stones.contains(i)) {
                    stones.add(i);
                    makeStoneChainFromIntersection(i);
                }
            }
        }
    }

    int getLibertiesCount() {
        Set<Intersection> liberties = new HashSet<>();
        for (Intersection i : stones)
            for (Intersection j : board.getNeighbours(i))
                if (j.getColor() == StoneColor.EMPTY)
                    liberties.add(j);

        return liberties.size();
    }

    void die() {
        for (Intersection i : stones) {
            i.setColor(StoneColor.EMPTY);
            if (board.getCurrentPlayer() == board.getPlayerOne())
                board.getPlayerOne().changeCapturedStones(1);
            else
                board.getPlayerTwo().changeCapturedStones(1);
        }
    }

    int size() {
        return this.stones.size();
    }

}
