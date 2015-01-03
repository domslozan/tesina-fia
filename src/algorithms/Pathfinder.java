package algorithms;

import graph.Path;
import org.jgrapht.event.GraphListener;

public interface Pathfinder<V, E> extends GraphListener<V, E> {

    Path<V, E> findPath(V start, V goal);

    void addListener(PathfinderEventListener<V, E> listener);
}