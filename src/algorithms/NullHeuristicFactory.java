package algorithms;

public class NullHeuristicFactory<V> implements HeuristicFactory<V> {

    @Override
    public Heuristic<V> makeHeuristic(V goal) {
        return new Heuristic<V>() {

            @Override
            public double value(V vertex) {
                return 0;
            }
        };
    }
}
