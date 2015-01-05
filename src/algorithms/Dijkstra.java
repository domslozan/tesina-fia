package algorithms;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

public class Dijkstra<V, E> extends AStar<V, E> {

    public Dijkstra(DefaultDirectedWeightedGraph<V, E> graph) {
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