package algorithms;

public interface PathfinderEventListener<V, E> {

    void openVertex(Pathfinder<V, E> source, V v);

    void closeVertex(Pathfinder<V, E> source, V v);
}
