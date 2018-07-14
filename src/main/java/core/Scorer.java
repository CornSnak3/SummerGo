package core;

import java.util.HashSet;
import java.util.Set;

/**
 * We assume that all dead stones are marked correctly
 */
class Scorer {
    private Board board;
    Set<StoneChain> blackStoneChains;
    Set<StoneChain> whiteStoneChains;
    Set<StoneChain> deadBlackStones;
    Set<StoneChain> deadWhiteStones;
    Set<EmptyRegion> emptyRegions;
    double whitePoints, blackPoints;

    Scorer(Board board) {
        this.board = board;
        this.blackStoneChains = new HashSet<>();
        this.whiteStoneChains = new HashSet<>();
        this.deadBlackStones = new HashSet<>();
        this.deadWhiteStones = new HashSet<>();
        this.emptyRegions = new HashSet<>();
        for (int x = 0; x < board.getBoardSize(); x++) {
            for (int y = 0; y < board.getBoardSize(); y++) {
                Intersection intersection = board.getIntersection(x, y);
                switch (intersection.getColor()) {
                    case BLACK:
                        blackStoneChains.add(new StoneChain(board, intersection));
                        break;
                    case WHITE:
                        whiteStoneChains.add(new StoneChain(board, intersection));
                        break;
                    case EMPTY:
                        break;
                }
            }
        }
    }

    void processScore() {
        for (int x = 0; x < board.getBoardSize(); x++)
            for (int y = 0; y < board.getBoardSize(); y++)
                if (board.getIntersection(x, y).getColor() == StoneColor.EMPTY)
                    emptyRegions.add(new EmptyRegion(board, board.getIntersection(x, y)));
        for (EmptyRegion emptyRegion : emptyRegions) {
            StoneColor owner = getOwnerOfARegion(emptyRegion);
            if (owner == StoneColor.BLACK)
                blackPoints += emptyRegion.size();
            else if (owner == StoneColor.WHITE)
                whitePoints += emptyRegion.size();
        }
        whitePoints += board.getKomi();
        blackPoints += board.getPlayerOne().getCapturedStones();
        for (StoneChain stoneChain : deadBlackStones)
            blackPoints += stoneChain.size();
        for (StoneChain stoneChain : deadWhiteStones)
            whitePoints += stoneChain.size();
        whitePoints += board.getPlayerTwo().getCapturedStones();
    }

    StoneColor getOwnerOfARegion(EmptyRegion emptyRegion) {
        StoneColor owner = StoneColor.EMPTY;
        for (Intersection intersection : emptyRegion.intersections)
            for (Intersection i : board.getNeighbours(intersection))
                if ((owner = i.getColor()) != StoneColor.EMPTY)
                    return owner;
        return owner;
    }

    void flipDeathStatus(Intersection intersection) {
        for (StoneChain stoneChain : blackStoneChains) {
            if (stoneChain.stones.contains(intersection)) {
                blackStoneChains.remove(stoneChain);
                deadBlackStones.add(stoneChain);
            }
        }
        for (StoneChain stoneChain : deadBlackStones) {
            if (stoneChain.stones.contains(intersection)) {
                blackStoneChains.add(stoneChain);
                deadBlackStones.remove(stoneChain);
            }
        }
        for (StoneChain stoneChain : whiteStoneChains) {
            if (stoneChain.stones.contains(intersection)) {
                whiteStoneChains.remove(stoneChain);
                deadWhiteStones.add(stoneChain);
            }
        }
        for (StoneChain stoneChain : deadWhiteStones) {
            if (stoneChain.stones.contains(intersection)) {
               whiteStoneChains.add(stoneChain);
                deadWhiteStones.remove(stoneChain);
            }
        }
    }
}
