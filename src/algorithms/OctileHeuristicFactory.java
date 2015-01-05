/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.awt.geom.Point2D;
import tiling.Tile;

/**
 *
 * @author ricc
 */
public class OctileHeuristicFactory implements HeuristicFactory<Tile> {

    @Override
    public Heuristic<Tile> newHeuristic(final Tile goal) {
        return new Heuristic<Tile>() {

            @Override
            public double value(Tile vertex) {
                Point2D p1 = vertex.getCenter();
                Point2D p2 = goal.getCenter();
                return Math.max(Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getY() - p2.getY()));
            }
        };
    }
}
