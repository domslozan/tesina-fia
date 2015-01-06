package graph;

import java.util.Iterator;
import java.util.List;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;
import tiling.TileMap;

public class AnyAnglePath extends Path<Tile, DefaultWeightedEdge> {

    public AnyAnglePath(WeightedGraph<Tile, DefaultWeightedEdge> graph, List<Tile> vertices) {
        super(graph, vertices);
    }

    @Override
    public double cost() {
        Iterator<Tile> i = getVertices().iterator();
        Tile last = i.next();
        TileMap map = last.getMap();
        double cost = 0;
        while (i.hasNext()) {
            Tile next = i.next();
            cost += map.centerDistance(last, next);
            last = next;
        }
        return cost;
    }
}
