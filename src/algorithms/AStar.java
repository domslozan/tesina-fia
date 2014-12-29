package algorithms;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.Graph;

public class AStar<V, E> {

    private final HeuristicFactory<V> hf;

    public AStar(HeuristicFactory<V> hf) {
        this.hf = hf;
    }

    public List<V> getPath(Graph<V, E> graph, V start, V goal) {
        return new Path(graph, start, goal, hf.makeHeuristic(goal)).findVertices();
    }

    private class Path {

        private final Map<V, VertexData<V>> data;
        private final PriorityQueue<V> open;
        private final Heuristic<V> h;
        private final Graph<V, E> graph;
        private final V start, goal;

        public Path(Graph<V, E> graph, V start, V goal, Heuristic<V> h) {
            this.graph = graph;
            this.start = start;
            this.goal = goal;
            this.h = h;
            this.data = new HashMap<V, VertexData<V>>();
            this.open = new PriorityQueue<V>(11, new Comparator<V>() {

                @Override
                public int compare(V v, V u) {
                    VertexData<V> vdata = Path.this.data.get(v);
                    VertexData<V> udata = Path.this.data.get(u);
                    return Double.compare(vdata.g + Path.this.h.value(v), udata.g + Path.this.h.value(u));
                }
            });
        }

        public List<V> findVertices() {
            boolean found = buildPath();
            LinkedList<V> list = new LinkedList<V>();
            if (found) {
                V next = goal;
                while (next != null) {
                    list.addFirst(next);
                    next = getParent(next);
                }
            }
            return list;
        }

        private boolean isClosed(V s) {
            VertexData<V> sdata = data.get(s);
            if (sdata == null) {
                return false;
            }
            return sdata.closed;
        }

        private void setG(V s, double g) {
            VertexData<V> sdata = data.get(s);
            if (sdata == null) {
                sdata = new VertexData<V>(g, null);
            }
            sdata.g = g;
            data.put(s, sdata);
        }

        public boolean isOpen(V s) {
            VertexData<V> sdata = data.get(s);
            if (sdata == null) {
                return false;
            }
            return !sdata.closed;
        }

        private void close(V s) {
            VertexData<V> sdata = data.get(s);
            sdata.closed = true;
            data.put(s, sdata);
        }

        private void setParent(V s, V parent) {
            VertexData<V> sdata = data.get(s);
            if (sdata == null) {
                sdata = new VertexData<V>(Double.POSITIVE_INFINITY, parent);
            }
            sdata.parent = parent;
            data.put(s, sdata);
        }

        private boolean buildPath() {
            setG(start, 0);
            setParent(start, null);
            open.offer(start);
            while (!open.isEmpty()) {
                V s = open.poll();
                if (s.equals(goal)) {
                    return true;
                }
                close(s);
                for (V t : neighborsOf(s)) {
                    if (!isClosed(t)) {
                        if (!isOpen(t)) {
                            setG(t, Double.POSITIVE_INFINITY);
                            setParent(t, null);
                            open.offer(t);
                        }
                        updateVertex(s, t);
                    }
                }
            }
            return false;
        }

        private Set<V> neighborsOf(V s) {
            Set<V> neighbors = new HashSet<V>();
            for (E edge : graph.edgesOf(s)) {
                V target = graph.getEdgeTarget(edge);
                if (target != s) {
                    neighbors.add(target);
                }
            }
            return neighbors;
        }

        private double getG(V s) {
            VertexData<V> sdata = data.get(s);
            return sdata.g;
        }

        private V getParent(V s) {
            VertexData<V> sdata = data.get(s);
            return sdata.parent;
        }

        private void updateVertex(V s, V t) {
            E edge = graph.getEdge(s, t);
            double newPathCost = getG(s) + graph.getEdgeWeight(edge);
            if (newPathCost < getG(t)) {
                setG(t, newPathCost);
                setParent(t, s);
                if (isOpen(t)) {
                    open.remove(t);
                }
                open.offer(t);
            }
        }
    }

    private static class VertexData<V> {
        //public V vertex;

        public double g = Double.POSITIVE_INFINITY;
        public V parent = null;
        public boolean closed = false;

        public VertexData(double g, V parent) {
            this.g = g;
            this.parent = parent;
        }
    }
}
