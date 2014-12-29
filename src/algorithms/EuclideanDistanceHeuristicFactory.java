package algorithms;

import tiling.Tile;

public class EuclideanDistanceHeuristicFactory implements HeuristicFactory<Tile> {

    @Override
    public Heuristic<Tile> makeHeuristic(final Tile goal) {
        return new Heuristic<Tile>() {

            @Override
            public double value(Tile vertex) {
                return goal.getMap().centerDistance(goal, vertex);
            }
        };
    }
}
