package algorithms;

public interface Pathfinder<V, E> {

    Path<V, E> findPath(V start, V goal);

    void updatedEdgeWeight(E edge);
}