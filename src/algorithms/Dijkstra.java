package algorithms;

public class Dijkstra<V, E> extends AStar<V, E> {

    public Dijkstra() {
        super(new NullHeuristicFactory<V>());
    }
}