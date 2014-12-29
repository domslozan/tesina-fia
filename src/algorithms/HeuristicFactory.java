package algorithms;

public interface HeuristicFactory<V> {

    Heuristic<V> makeHeuristic(V goal);
}
