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

    public Path<V, E> mergeWith(Path<V, E> p2) {
        Path<V, E> p1 = this;
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
