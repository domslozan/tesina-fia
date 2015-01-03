package ui;

import algorithms.CountingListener;
import algorithms.Pathfinder;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;

public class FillListener extends CountingListener<Tile, DefaultWeightedEdge> {

    private final Map<PathfinderWrapper, Graphics2D> graphics;

    public FillListener() {
        this.graphics = new HashMap<PathfinderWrapper, Graphics2D>();
    }

    public void setGraphics(PathfinderWrapper pfw, Graphics2D g) {
        graphics.put(pfw, g);
    }

    @Override
    public void clear() {
        super.clear();
        graphics.clear();
    }

    @Override
    public void openVertex(Pathfinder<Tile, DefaultWeightedEdge> source, Tile v) {
        super.openVertex(source, v);
        if (!(source instanceof PathfinderWrapper)) {
            throw new IllegalArgumentException();
        }
        PathfinderWrapper pfw = (PathfinderWrapper) source;
        Graphics2D g = graphics.get(pfw);
        if (g != null) {
            g.setColor(pfw.getOpenColor());
            g.fill(v.asRectangle());
        }
    }

    @Override
    public void closeVertex(Pathfinder<Tile, DefaultWeightedEdge> source, Tile v) {
        super.openVertex(source, v);
        if (!(source instanceof PathfinderWrapper)) {
            throw new IllegalArgumentException();
        }
        PathfinderWrapper pfw = (PathfinderWrapper) source;
        Graphics2D g = graphics.get(pfw);
        if (g != null) {
            g.setColor(pfw.getClosedColor());
            g.fill(v.asRectangle());
        }
    }
}
