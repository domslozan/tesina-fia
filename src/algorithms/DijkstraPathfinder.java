package algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.*;

public class DijkstraPathfinder<V, E> {
    public Path findPath(Graph<V, E> graph, V start, V goal) {
        return new Path(graph, start, goal);
    }

    public class Path {
        private class DijkstraVertex {
            private final V vertex;

            private double pathCost;
            private DijkstraVertex parent;
            private boolean closed;

            private DijkstraVertex(V originalVertex, double pathCost, DijkstraVertex parent) {
                if (originalVertex == null) throw new IllegalArgumentException("original vertex cannot be null");
                this.vertex = originalVertex;
                this.parent = parent;
                if (pathCost < 0) throw new IllegalArgumentException("Vertex with negative path cost");
                this.pathCost = pathCost;
                this.closed = false;
            }

            public V getVertex() {
                return vertex;
            }

            public double getPathCost() {
                return pathCost;
            }

            public void setPathCost(double pathCost) {
                if (pathCost < 0) throw new IllegalArgumentException("Cannot set negative path cost");
                this.pathCost = pathCost;
            }

            public DijkstraVertex getParent() {
                return parent;
            }

            public void setParent(DijkstraVertex parent) {
                this.parent = parent;
            }

            public boolean isClosed() {
                return closed;
            }

            public void close() {
                closed = true;
            }
        }

        private final Map<V, DijkstraVertex> v2dv;

        private DijkstraVertex makeDijkstraVertex(V vertex, double pathCost, DijkstraVertex parent) {
            DijkstraVertex dv = new DijkstraVertex(vertex, pathCost, parent);
            DijkstraVertex old = v2dv.put(vertex, dv);
            if (old != null) throw new RuntimeException("Dijkstra vertices should never be recreated");
            return dv;
        }

        private Comparator<DijkstraVertex> openQueueComparator() {
            return new Comparator<DijkstraVertex>() {
                @Override
                public int compare(DijkstraVertex v1, DijkstraVertex v2) {
                    return Double.compare(v1.getPathCost(), v2.getPathCost());
                }
            };
        }

        private Set<DijkstraVertex> neighborsOf(DijkstraVertex dv) {
            V realVertex = dv.getVertex();
            HashSet<DijkstraVertex> neighbors = new HashSet<DijkstraVertex>();
            for (E edge : graph.edgesOf(realVertex)) {
                V target = graph.getEdgeTarget(edge);
                if (target != realVertex) {
                    DijkstraVertex n = v2dv.get(target);
                    if (n == null)
                        n = makeDijkstraVertex(target, Double.MAX_VALUE, null);
                    neighbors.add(n);
                }
            }
            return neighbors;
        }

        private final Graph<V, E> graph;
        private final V start;
        private final V goal;
        
        private final PriorityQueue<DijkstraVertex> open;

        private boolean hasRun;
        private boolean hasPath;

        private Path(Graph<V, E> graph, V start, V goal) {
            if (graph == null) throw new IllegalArgumentException("Graph cannot be null");
            this.graph = graph;
            if (!graph.containsVertex(start)) throw new IllegalArgumentException("Start vertex not part of graph");
            this.start = start;
            if (!graph.containsVertex(goal)) throw new IllegalArgumentException("Goal vertex not part of graph");
            this.goal = goal;
            v2dv = new HashMap<V, DijkstraVertex>();
            open = new PriorityQueue<DijkstraVertex>(32, openQueueComparator());
            hasPath = false;
            hasRun = false;
        }

        private DijkstraVertex findPath(){
            DijkstraVertex s0 = makeDijkstraVertex(start, 0, null);
            open.offer(s0);
            while (!open.isEmpty()) {
                DijkstraVertex s = open.poll();
                if (s.getVertex().equals(goal)) {
                    hasPath = true;
                    hasRun = true;
                    return s;
                }
                s.close();
                for (DijkstraVertex t: neighborsOf(s)) {
                    if (!t.isClosed()) {
                        updateVertex(s, t);
                    }
                }
            }
            hasRun = true;
            return null;
        }

        private void updateVertex(DijkstraVertex s, DijkstraVertex t) {
            E edge = graph.getEdge(s.getVertex(), t.getVertex());
            double newPathCost = s.getPathCost() + graph.getEdgeWeight(edge);
            if (newPathCost < t.getPathCost()) {
                t.setPathCost(newPathCost);
                t.setParent(s);
                if (open.contains(t))
                    open.remove(t);
                open.offer(t);
            }
        }

        public List<V> getVertices() {
            DijkstraVertex goalDV;
            if (!hasRun) findPath();
            if (!hasPath) return new LinkedList<V>();
            goalDV = v2dv.get(goal);
            LinkedList<V> list = new LinkedList<V>();
            list.addFirst(goal);
            DijkstraVertex next = goalDV.getParent();
            while (next != null) {
                list.addFirst(next.getVertex());
                next = next.getParent();
            }
            return list;
        }

        public Iterator<V> iterator() {
            return getVertices().iterator();
        }
    }
}