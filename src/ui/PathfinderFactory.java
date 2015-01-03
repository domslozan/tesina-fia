package ui;

import algorithms.AStar;
import algorithms.DStar;
import algorithms.Dijkstra;
import algorithms.EuclideanDistanceHeuristicFactory;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import tiling.Tile;

public class PathfinderFactory {

    private PathfinderFactory() {
    }

    public static enum Algorithm {

        DIJKSTRA, ASTAR_ED, DSTAR
    }

    public static PathfinderWrapper newPathfinder(Algorithm algo, ListenableDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph) {
        switch (algo) {
            case DIJKSTRA:
                return new PathfinderWrapper(
                        new Dijkstra<Tile, DefaultWeightedEdge>(graph),
                        "Dijkstra",
                        Color.ORANGE);
            case DSTAR:
                return new PathfinderWrapper(
                        new DStar<Tile, DefaultWeightedEdge>(graph),
                        "D*",
                        Color.GREEN);
            case ASTAR_ED:
                return new PathfinderWrapper(
                        new AStar<Tile, DefaultWeightedEdge>(graph, new EuclideanDistanceHeuristicFactory()),
                        "A* with euclidean distance heuristic",
                        Color.CYAN);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static List<PathfinderWrapper> allPathfinders(ListenableDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph) {
        ArrayList<PathfinderWrapper> list = new ArrayList<PathfinderWrapper>();
        for (Algorithm a : Algorithm.values()) {
            list.add(newPathfinder(a, graph));
        }
        return list;
    }
}
