import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import java.util.List;

class TileIterator implements Iterator<Rectangle2D> {
    private final double worldWidth, worldHeight, tileSize;
    private int nextTile;

    public TileIterator(double worldWidth, double worldHeight, double tileSize) {
	this.worldWidth = worldWidth;
	this.worldHeight = worldHeight;
	this.tileSize = tileSize;
	nextTile = 0;
    }

    // The tiles are numbered left-right then up-down starting from 0
    private Rectangle2D tile(int tileNumber) {
	return null;
    }

    public boolean hasNext() {
	return false;
    }

    public Rectangle2D next() {
	return null;
    }

    public void remove() {
	
    }
}

public class GraphBuilder {
    private GraphBuilder() {}

    public static DefaultDirectedWeightedGraph build(List<Shape> walls, int worldWidth, int worldHeight) {
	return null;
    }
}