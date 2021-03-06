package algorithms;

import graph.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

public class AStar<V, E> implements Pathfinder<V, E> {

    private static enum NodeType {

        NEW, OPEN, CLOSED
    }
    private final DefaultDirectedWeightedGraph<V, E> graph;
    private V start, goal;
    private Heuristic<V> h;
    private final Map<V, Double> gs;
    private final Map<V, V> parents;
    private final Set<V> closed;
    private final PriorityQueue<V> open;
    private final HeuristicFactory<V> hf;
    private List<PathfinderEventListener<V, E>> listeners;

    public AStar(DefaultDirectedWeightedGraph<V, E> graph, HeuristicFactory<V> hf) {
        this.graph = graph;
        this.hf = hf;

        this.open = new PriorityQueue<V>(11, queueComparator());
        this.gs = new HashMap<V, Double>();
        this.parents = new HashMap<V, V>();
        this.closed = new HashSet<V>();

        this.listeners = new ArrayList<PathfinderEventListener<V, E>>();
    }

    @Override
    public void addListener(PathfinderEventListener<V, E> listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        listeners.add(listener);
    }

    @Override
    public DefaultDirectedWeightedGraph<V, E> getGraph() {
        return graph;
    }

    @Override
    public Path<V, E> findPath(V start, V goal) {
        reset();
        h = hf.newHeuristic(goal);
        this.start = start;
        this.goal = goal;
        boolean found = buildPath();
        LinkedList<V> list = new LinkedList<V>();
        if (found) {
            V next = goal;
            while (next != null) {
                list.addFirst(next);
                next = getParent(next);
            }
        }
        return new Path(graph, list);
    }

    @Override
    public void updateGraphEdge(E edge, double weight) {
        graph.setEdgeWeight(edge, weight);
    }

    private Comparator<V> queueComparator() {
        return new Comparator<V>() {

            @Override
            public int compare(V v, V u) {
                return Double.compare(gs.get(v) + h.value(v), gs.get(u) + h.value(u));
            }
        };
    }

    private NodeType nodeType(V x) {
        if (closed.contains(x)) {
            return NodeType.CLOSED;
        }
        if (open.contains(x)) {
            return NodeType.OPEN;
        }
        return NodeType.NEW;
    }

    private V getParent(V s) {
        return parents.get(s);
    }

    private void setParent(V s, V parent) {
        parents.put(s, parent);
    }

    private double getG(V s) {
        return gs.get(s);
    }

    private void setG(V s, double g) {
        gs.put(s, g);
    }

    private void reset() {
        open.clear();
        closed.clear();
        h = null;
        gs.clear();
        parents.clear();
    }

    private void callOpenVertex(V x) {
        for (PathfinderEventListener<V, E> pel : listeners) {
            pel.openVertex(this, x);
        }
    }

    private void callCloseVertex(V x) {
        for (PathfinderEventListener<V, E> pel : listeners) {
            pel.closeVertex(this, x);
        }
    }

    private boolean buildPath() {
        setG(start, 0);
        setParent(start, null);
        open.offer(start);
        callOpenVertex(start);

        while (!open.isEmpty()) {
            V s = open.poll();
            if (s.equals(goal)) {
                return true;
            }
            closed.add(s);
            callCloseVertex(s);
            for (V t : Graphs.predecessorListOf(graph, s)) {
                if (!(nodeType(t) == NodeType.CLOSED)) {
                    if (!(nodeType(t) == NodeType.OPEN)) {
                        setG(t, Double.MAX_VALUE);
                        setParent(t, null);
                        open.offer(t);
                        callOpenVertex(t);
                    }
                    updateVertex(s, t);
                }
            }
        }
        return false;
    }

    private void updateVertex(V s, V t) {
        E edge = graph.getEdge(s, t);
        double newPathCost = getG(s) + graph.getEdgeWeight(edge);
        if (newPathCost < getG(t)) {
            setG(t, newPathCost);
            setParent(t, s);
            if (nodeType(t) == NodeType.OPEN) {
                open.remove(t);
                open.offer(t);
            } else {
                open.offer(t);
                callOpenVertex(t);
            }
        }
    }
}
