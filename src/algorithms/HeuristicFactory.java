package algorithms;

public interface HeuristicFactory<V> {

    Heuristic<V> newHeuristic(V goal);
}
