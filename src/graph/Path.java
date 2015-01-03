package graph;

import java.util.Iterator;
import java.util.List;
import org.jgrapht.WeightedGraph;

public class Path<V, E> implements Iterable<V> {

    private final List<V> vertices;
    private final WeightedGraph<V, E> graph;

    public Path(WeightedGraph<V, E> graph, List<V> vertices) {
        if (vertices.isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }
        this.graph = graph;
        this.vertices = vertices;
    }

    public List<V> getVertices() {
        return vertices;
    }

    public WeightedGraph<V, E> getGraph() {
        return graph;
    }

    public int length() {
        return vertices.size();
    }

    public double cost() {
        Iterator<V> i = vertices.iterator();
        V last = i.next();
        double cost = 0;
        while (i.hasNext()) {
            V next = i.next();
            E edge = graph.getEdge(last, next);
            if (edge == null) {
                throw new RuntimeException("This path is broken");
            }
            cost += graph.getEdgeWeight(edge);
            last = next;
        }
        return cost;
    }

    @Override
    public Iterator<V> iterator() {
        return vertices.iterator();
    }
}
