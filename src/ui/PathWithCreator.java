package ui;

import graph.Path;
import java.util.List;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;

public class PathWithCreator extends Path<Tile, DefaultWeightedEdge> {

    private final PathfinderWrapper creator;

    private PathWithCreator(WeightedGraph<Tile, DefaultWeightedEdge> graph, List<Tile> vertices, PathfinderWrapper creator) {
        super(graph, vertices);
        this.creator = creator;
    }

    public PathfinderWrapper getCreator() {
        return creator;
    }

    public static PathWithCreator newPathWithCreator(Path<Tile, DefaultWeightedEdge> path, PathfinderWrapper pf) {
        return new PathWithCreator(path.getGraph(), path.getVertices(), pf);
    }
}
