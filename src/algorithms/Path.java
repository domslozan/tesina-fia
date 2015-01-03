package algorithms;

import java.util.Iterator;
import java.util.List;
import org.jgrapht.Graph;

public class Path<V, E> implements Iterable<V> {

    private final List<V> vertices;
    private final Graph<V, E> graph;

    public Path(Graph<V, E> graph, List<V> vertices) {
        this.graph = graph;
        this.vertices = vertices;
    }

    public List<V> getVertices() {
        return vertices;
    }

    public int length() {
        return vertices.size();
    }

    public double cost() {
        if (length() == 0) {
            throw new RuntimeException();
        }
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
