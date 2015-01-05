package ui;

import algorithms.AStar;
import algorithms.DStar;
import algorithms.Dijkstra;
import algorithms.EuclideanDistanceHeuristicFactory;
import algorithms.OctileHeuristicFactory;
import graph.TilingGraphBuilder;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;
import tiling.WallListTileMap;

public class PathfinderFactory {

    private PathfinderFactory() {
    }

    public static enum Algorithm {

        DIJKSTRA, ASTAR_ED, DSTAR, ASTAR_OD
    }

    public static PathfinderWrapper newPathfinder(Algorithm algo, DefaultDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph) {
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
            case ASTAR_OD:
                return new PathfinderWrapper(
                        new AStar<Tile, DefaultWeightedEdge>(graph, new OctileHeuristicFactory()),
                        "A* with octile distance heuristic",
                        Color.MAGENTA);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static List<PathfinderWrapper> allPathfinders(DefaultDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph) {
        ArrayList<PathfinderWrapper> list = new ArrayList<PathfinderWrapper>();
        for (Algorithm a : Algorithm.values()) {
            list.add(newPathfinder(a, graph));
        }
        return list;
    }

    public static List<PathfinderWrapper> allPathfinders(WallListTileMap map) {
        ArrayList<PathfinderWrapper> list = new ArrayList<PathfinderWrapper>();
        for (Algorithm a : Algorithm.values()) {
            list.add(newPathfinder(a, new TilingGraphBuilder(map).buildGraph()));
        }
        return list;
    }
}
