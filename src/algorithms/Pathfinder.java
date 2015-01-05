package algorithms;

import graph.Path;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

public interface Pathfinder<V, E> {

    Path<V, E> findPath(V start, V goal);

    DefaultDirectedWeightedGraph<V, E> getGraph();

    void updateGraphEdge(E e, double weight);

    void addListener(PathfinderEventListener<V, E> listener);
}