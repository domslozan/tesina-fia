package ui;

import algorithms.Pathfinder;
import algorithms.PathfinderEventListener;
import graph.Path;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;

public class PathfinderWrapper implements Pathfinder<Tile, DefaultWeightedEdge> {

    private final Pathfinder<Tile, DefaultWeightedEdge> pf;
    private final String name;
    private Color color;
    private PathfinderEventListener<Tile, DefaultWeightedEdge> local_listener;

    public PathfinderWrapper(Pathfinder<Tile, DefaultWeightedEdge> pf, String name, Color color) {
        this.pf = pf;
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getPathColor() {
        return color;
    }

    public Stroke getPathStroke() {
        return new BasicStroke(2f);
    }

    public Color getOpenColor() {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(color.getAlpha() - 50, 0));
    }

    public Color getClosedColor() {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(color.getAlpha() - 100, 0));
    }

    @Override
    public Path<Tile, DefaultWeightedEdge> findPath(Tile start, Tile goal) {
        return pf.findPath(start, goal);
    }

    @Override
    public void addListener(PathfinderEventListener<Tile, DefaultWeightedEdge> listener) {
        local_listener = listener;
        pf.addListener(new PathfinderEventListener<Tile, DefaultWeightedEdge>() {

            @Override
            public void openVertex(Pathfinder<Tile, DefaultWeightedEdge> source, Tile v) {
                local_listener.openVertex(PathfinderWrapper.this, v);
            }

            @Override
            public void closeVertex(Pathfinder<Tile, DefaultWeightedEdge> source, Tile v) {
                local_listener.closeVertex(PathfinderWrapper.this, v);
            }
        });
    }

    @Override
    public void edgeAdded(GraphEdgeChangeEvent<Tile, DefaultWeightedEdge> gece) {
        pf.edgeAdded(gece);
    }

    @Override
    public void edgeRemoved(GraphEdgeChangeEvent<Tile, DefaultWeightedEdge> gece) {
        pf.edgeRemoved(gece);
    }

    @Override
    public void vertexAdded(GraphVertexChangeEvent<Tile> gvce) {
        pf.vertexAdded(gvce);
    }

    @Override
    public void vertexRemoved(GraphVertexChangeEvent<Tile> gvce) {
        pf.vertexRemoved(gvce);
    }
}
