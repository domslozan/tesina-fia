package graph;

import java.util.Iterator;
import org.jgrapht.graph.*;
import tiling.Tile;
import tiling.TileMap;

public class TilingGraphBuilder {

    private final TileMap worldMap;

    public TilingGraphBuilder(TileMap wm) {
        this.worldMap = wm;
    }

    public DefaultDirectedWeightedGraph<Tile, DefaultWeightedEdge> buildGraph() {
        DefaultDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph(DefaultWeightedEdge.class);
        Iterator<Tile> i = worldMap.iterator();
        while (i.hasNext()) {
            Tile t = i.next();
            graph.addVertex(t);
        }

        i = worldMap.iterator();
        while (i.hasNext()) {
            Tile t = i.next();
            Iterator<Tile> j = worldMap.adjacentTiles(t).iterator();
            while (j.hasNext()) {
                Tile u = j.next();
                if (!worldMap.isBlocked(u) && !worldMap.isBlocked(t)) {
                    DefaultWeightedEdge e = graph.addEdge(t, u);
                    graph.setEdgeWeight(e, worldMap.centerDistance(t, u));
                }
            }
        }
        return graph;
    }

    public TileMap getTileMap() {
        return worldMap;
    }
}