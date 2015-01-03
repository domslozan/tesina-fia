package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import tiling.Tile;

public class Utils {

    private Utils() {
    }

    public static <V, E> Set<V> neighborsOf(Graph<V, E> graph, V s) {
        Set<V> neighbors = new HashSet<V>();
        for (E edge : graph.edgesOf(s)) {
            V target = graph.getEdgeTarget(edge);
            if (target != s) {
                neighbors.add(target);
            }
        }
        return neighbors;
    }

    public static <E> Map<E, Double> wrongEdges(WeightedGraph<Tile, E> graph, Tile vertex) {
        HashMap<E, Double> we = new HashMap<E, Double>();
        for (E e : graph.edgesOf(vertex)) {
            if (graph.getEdgeSource(e).equals(vertex)) {
                boolean blocked = vertex.getMap().isBlocked(vertex);
                double weight = graph.getEdgeWeight(e);
                Tile target = graph.getEdgeTarget(e);
                double real_weight = vertex.getMap().centerDistance(vertex, target);
                if ((blocked && weight < Double.MAX_VALUE)
                        || (!blocked && weight != real_weight)) {
                    we.put(e, real_weight);
                }
            }
        }
        return we;
    }
}
