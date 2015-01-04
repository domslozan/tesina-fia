package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
                Tile target = graph.getEdgeTarget(e);
                boolean blocked = vertex.getMap().isReallyBlocked(target);
                double weight = graph.getEdgeWeight(e);
                double real_weight = vertex.getMap().centerDistance(vertex, target);
                if (blocked && weight < Double.MAX_VALUE) {
                    we.put(e, Double.MAX_VALUE);
                } else if ((!blocked && weight != real_weight)) {
                    we.put(e, real_weight);
                }
            }
        }
        return we;
    }

    public static <V, E> Path<V, E> mergePaths(Path<V, E> p1, Path<V, E> p2) {
        V mergePoint = p2.getVertices().get(0);
        int i = p1.getVertices().indexOf(mergePoint);
        if (i == -1) {
            throw new IllegalArgumentException("This paths are not mergeable");
        }
        List<V> newVertices = p1.getVertices().subList(0, i);
        newVertices.addAll(i, p2.getVertices());
        return new Path<V, E>(p1.getGraph(), newVertices);
    }
}
