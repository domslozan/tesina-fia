package algorithms;

import graph.Utils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.WeightedGraph;

public class DStar<V, E> implements Pathfinder<V, E> {

    private static enum NodeType {

        NEW, OPEN, CLOSED
    }
    private final WeightedGraph<V, E> graph;
    private V goal, start;
    private boolean isFirstRun;
    private final PriorityQueue<V> open;
    private final Map<V, Double> hs, ks;
    private final Map<V, V> parents;
    private final Set<V> closed;

    public DStar(WeightedGraph<V, E> graph) {
        this.graph = graph;

        this.hs = new HashMap<V, Double>();
        this.ks = new HashMap<V, Double>();
        this.parents = new HashMap<V, V>();
        this.closed = new HashSet<V>();
        this.open = new PriorityQueue<V>(11, queueComparator());
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
    public Path<V, E> findPath(V start, V goal) {
        if (this.goal != goal) {
            this.goal = goal;
            reset();
        }
        boolean found = false;
        if (isFirstRun) {
            this.start = start;
            insert(goal, 0);
            double kmin = 0;
            while (nodeType(start) != NodeType.CLOSED && kmin != -1) {
                kmin = processState();
            }
            found = nodeType(start) == NodeType.CLOSED;
        } else {
            if (this.start == start) {
                throw new RuntimeException("Could not terminate correctly if called with the same start twice?");
            }
            this.start = start;
            double kmin = getKMin();
            while (kmin < h(start)) {
                kmin = processState();
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

    @Override
    public void updatedEdgeWeight(E edge) {
        V x = graph.getEdgeTarget(edge);
        if (nodeType(x) == NodeType.CLOSED) {
            insert(x, h(x));
        }
    }

    private void insert(V x, double h_new) {
        if (nodeType(x) == NodeType.OPEN) {
            open.remove(x);
            ks.put(x, Math.min(h_new, ks.get(x)));
        } else if (nodeType(x) == NodeType.CLOSED) {
            ks.put(x, Math.min(h_new, h(x)));
        } else {
            ks.put(x, h_new);
        }
        set_h(x, h_new);
        open.offer(x);
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