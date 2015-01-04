package algorithms;

import graph.Path;
import graph.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.AbstractBaseGraph;

public class DStar<V, E> implements Pathfinder<V, E> {

    private static enum NodeType {

        NEW, OPEN, CLOSED
    }
    private final WeightedGraph<V, E> graph;
    private final PriorityQueue<V> open;
    private final Map<V, Double> hs, ks;
    private final Map<V, V> parents;
    private final Set<V> closed;
    private V goal, start;
    private boolean isFirstRun;
    private List<PathfinderEventListener<V, E>> listeners;

    public DStar(WeightedGraph<V, E> graph) {
        this.graph = (WeightedGraph) ((AbstractBaseGraph) graph).clone();

        this.hs = new HashMap<V, Double>();
        this.ks = new HashMap<V, Double>();
        this.parents = new HashMap<V, V>();
        this.closed = new HashSet<V>();
        this.open = new PriorityQueue<V>(11, queueComparator());

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
    public WeightedGraph<V, E> getGraph() {
        return graph;
    }

    @Override
    public Path<V, E> findPath(V start, V goal) {
        if (this.goal != goal) {
            this.goal = goal;
            reset();
        }
        this.start = start;
        boolean found = false;
        if (isFirstRun) {
            insert(goal, 0);
            double kmin = getKMin();
            while (nodeType(start) != NodeType.CLOSED && kmin >= 0) {
                kmin = processState();
            }
            found = nodeType(start) == NodeType.CLOSED;
        } else {
            double kmin = getKMin();
            while (kmin < h(start) && kmin >= 0) {
                kmin = processState();
            }
            if (kmin < 0) {
                throw new RuntimeException("Does not terminate correctly!");
            }
            found = true;
        }

        LinkedList<V> list = new LinkedList<V>();
        if (found) {
            V next = start;
            while (next != null) {
                list.addLast(next);
                next = parent(next);
            }
        }
        return new Path(graph, list);
    }

    private Comparator<V> queueComparator() {
        return new Comparator<V>() {

            @Override
            public int compare(V v, V u) {
                double vk = ks.get(v);
                double uk = ks.get(u);
                return Double.compare(vk, uk);
            }
        };
    }

    @Override
    public void updateGraphEdge(E edge, double weight) {
        graph.setEdgeWeight(edge, weight);
        V x = graph.getEdgeTarget(edge);
        if (nodeType(x) == NodeType.CLOSED) {
            insert(x, h(x));
        }
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

    private void insert(V x, double h_new) {
        if (nodeType(x) == NodeType.OPEN) {
            open.remove(x);
            ks.put(x, Math.min(h_new, ks.get(x)));
            set_h(x, h_new);
            open.offer(x);
        } else if (nodeType(x) == NodeType.CLOSED) {
            ks.put(x, Math.min(h_new, h(x)));
            set_h(x, h_new);
            open.offer(x);
            callOpenVertex(x);
        } else {
            ks.put(x, h_new);
            set_h(x, h_new);
            open.offer(x);
            callOpenVertex(x);
        }
    }

    private void reset() {
        open.clear();
        hs.clear();
        ks.clear();
        closed.clear();
        parents.clear();
        isFirstRun = true;
    }

    private double edgeCost(V x, V y) {
        E e = graph.getEdge(y, x);
        return graph.getEdgeWeight(e);
    }

    private double processState() {
        V x = minState();
        if (x == null) {
            return -1;
        }
        double k_old = getKMin();
        delete(x);
        if (k_old < h(x)) {
            for (V y : Utils.neighborsOf(graph, x)) {
                if (h(y) <= k_old && h(x) > h(y) + edgeCost(y, x)) {
                    set_parent(x, y);
                    set_h(x, h(y) + edgeCost(y, x));
                }
            }
        }
        if (k_old == h(x)) {
            for (V y : Utils.neighborsOf(graph, x)) {
                if ((nodeType(y) == NodeType.NEW)
                        || (parent(y) == x && h(y) != h(x) + edgeCost(x, y))
                        || (parent(y) != x && h(y) > h(x) + edgeCost(x, y))) {
                    set_parent(y, x);
                    insert(y, h(x) + edgeCost(x, y));
                }
            }
        } else {
            for (V y : Utils.neighborsOf(graph, x)) {
                if ((nodeType(y) == NodeType.NEW)
                        || (parent(y) == x && h(y) != h(x) + edgeCost(x, y))) {
                    set_parent(y, x);
                    insert(y, h(x) + edgeCost(x, y));
                } else {
                    if (parent(y) != x && h(y) > h(x) + edgeCost(x, y)) {
                        insert(x, h(x));
                    } else if (parent(y) != x && h(x) > h(y) + edgeCost(y, x)
                            && nodeType(y) == NodeType.CLOSED && h(y) > k_old) {
                        insert(y, h(y));
                    }
                }
            }
        }
        return getKMin();
    }

    private double getKMin() {
        V top = open.peek();
        if (top == null) {
            return -1;
        }
        return ks.get(top);
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

    private void delete(V x) {
        V top = open.poll();
        if (top != x) {
            throw new RuntimeException("D* should only remove from the top");
        }
        closed.add(x);
        callCloseVertex(x);
    }

    private V minState() {
        return open.peek();
    }

    private double h(V x) {
        Double d = hs.get(x);
        if (d == null) {
            return Double.NaN;
        }
        return d;
    }

    private void set_h(V x, double h) {
        hs.put(x, h);
    }

    private V parent(V x) {
        return parents.get(x);
    }

    private void set_parent(V x, V y) {
        parents.put(x, y);
    }
}
