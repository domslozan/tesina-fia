package algorithms;

import graph.AnyAnglePath;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
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
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;
import tiling.TileMap;
import ui.MapPanel;

public class ThetaStar implements Pathfinder<Tile, DefaultWeightedEdge> {

    private static enum NodeType {

        NEW, OPEN, CLOSED
    }
    private final DefaultDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph;
    private Tile start, goal;
    private final Map<Tile, Double> gs;
    private final Map<Tile, Tile> parents;
    private final Set<Tile> closed;
    private final PriorityQueue<Tile> open;
    private List<PathfinderEventListener<Tile, DefaultWeightedEdge>> listeners;

    public ThetaStar(DefaultDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph) {
        this.graph = graph;

        this.open = new PriorityQueue<Tile>(11, queueComparator());
        this.gs = new HashMap<Tile, Double>();
        this.parents = new HashMap<Tile, Tile>();
        this.closed = new HashSet<Tile>();

        this.listeners = new ArrayList<PathfinderEventListener<Tile, DefaultWeightedEdge>>();
    }

    @Override
    public void addListener(PathfinderEventListener<Tile, DefaultWeightedEdge> listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        listeners.add(listener);
    }

    @Override
    public DefaultDirectedWeightedGraph<Tile, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    @Override
    public AnyAnglePath findPath(Tile start, Tile goal) {
        reset();
        this.start = start;
        this.goal = goal;
        boolean found = buildPath();
        LinkedList<Tile> list = new LinkedList<Tile>();
        if (found) {
            Tile next = goal;
            while (next != start) {
                list.addFirst(next);
                next = getParent(next);
            }
            list.addFirst(start);
        }
        return new AnyAnglePath(graph, list);
    }

    @Override
    public void updateGraphEdge(DefaultWeightedEdge edge, double weight) {
        graph.setEdgeWeight(edge, weight);
    }

    private Comparator<Tile> queueComparator() {
        return new Comparator<Tile>() {

            @Override
            public int compare(Tile v, Tile u) {
                TileMap m = v.getMap();
                return Double.compare(gs.get(v) + m.centerDistance(goal, v), gs.get(u) + m.centerDistance(goal, u));
            }
        };
    }

    private NodeType nodeType(Tile x) {
        if (closed.contains(x)) {
            return NodeType.CLOSED;
        }
        if (open.contains(x)) {
            return NodeType.OPEN;
        }
        return NodeType.NEW;
    }

    private Tile getParent(Tile s) {
        return parents.get(s);
    }

    private void setParent(Tile s, Tile parent) {
        parents.put(s, parent);
    }

    private double getG(Tile s) {
        return gs.get(s);
    }

    private void setG(Tile s, double g) {
        gs.put(s, g);
    }

    private void reset() {
        open.clear();
        closed.clear();
        gs.clear();
        parents.clear();
    }

    private void callOpenVertex(Tile x) {
        for (PathfinderEventListener<Tile, DefaultWeightedEdge> pel : listeners) {
            pel.openVertex(this, x);
        }
    }

    private void callCloseVertex(Tile x) {
        for (PathfinderEventListener<Tile, DefaultWeightedEdge> pel : listeners) {
            pel.closeVertex(this, x);
        }
    }

    private boolean buildPath() {
        setG(start, 0);
        setParent(start, start);
        open.offer(start);
        callOpenVertex(start);

        while (!open.isEmpty()) {
            Tile s = open.poll();
            if (s.equals(goal)) {
                return true;
            }
            closed.add(s);
            callCloseVertex(s);
            for (Tile t : Graphs.predecessorListOf(graph, s)) {
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

    private double c(Tile s, Tile t) {
        return s.getMap().centerDistance(s, t);
    }

    private void updateVertex(Tile s, Tile t) {
        Line2D path2 = new Line2D.Double(getParent(s).getCenter(), t.getCenter());
        if (!s.getMap().intersectsBlocked(path2)) {
            if (getG(getParent(s)) + c(getParent(s), t) < getG(t)) {
                setG(t, getG(getParent(s)) + c(getParent(s), t));
                setParent(t, getParent(s));
                if (nodeType(t) == NodeType.OPEN) {
                    open.remove(t);
                    open.offer(t);
                } else {
                    open.offer(t);
                    callOpenVertex(t);
                }
            }
        } else {


            DefaultWeightedEdge edge = graph.getEdge(s, t);
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
}
