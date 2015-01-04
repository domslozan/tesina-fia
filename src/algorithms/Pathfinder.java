package algorithms;

import graph.Path;
import org.jgrapht.WeightedGraph;

public interface Pathfinder<V, E> {

    Path<V, E> findPath(V start, V goal);

    WeightedGraph<V, E> getGraph();

    void updateGraphEdge(E e, double weight);

    void addListener(PathfinderEventListener<V, E> listener);
}