package algorithms;

import org.jgrapht.WeightedGraph;

public class Dijkstra<V, E> extends AStar<V, E> {

    public Dijkstra(WeightedGraph<V, E> graph) {
        super(graph, new HeuristicFactory<V>() {

            @Override
            public Heuristic<V> newHeuristic(V goal) {
                return new Heuristic<V>() {

                    @Override
                    public double value(V vertex) {
                        return 0;
                    }
                };
            }
        });
    }
}