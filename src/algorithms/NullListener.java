package algorithms;

public class NullListener<V, E> implements PathfinderEventListener<V, E> {

    @Override
    public void openVertex(Pathfinder<V, E> source, V v) {
    }

    @Override
    public void closeVertex(Pathfinder<V, E> source, V v) {
    }
}
