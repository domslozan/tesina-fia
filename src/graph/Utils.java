package graph;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;

public class Utils {

    private Utils() {
    }

    public static Map<DefaultWeightedEdge, Double> wrongEdges(WeightedGraph<Tile, DefaultWeightedEdge> graph, Tile vertex) {
        HashMap<DefaultWeightedEdge, Double> we = new HashMap<DefaultWeightedEdge, Double>();
        for (DefaultWeightedEdge e : graph.edgesOf(vertex)) {
            if (graph.getEdgeSource(e).equals(vertex)) {
                Tile target = graph.getEdgeTarget(e);
                boolean blocked = vertex.getMap().isReallyBlocked(target);
                double weight = graph.getEdgeWeight(e);
                double real_weight = vertex.getMap().centerDistance(vertex, target);
                if (blocked && weight < 1000000000d) {
                    we.put(e, 1000000000d);
                } else if ((!blocked && Math.abs(weight - real_weight) > 0.01)) {
                    we.put(e, real_weight);
                }
            }
        }
        return we;
    }
}
