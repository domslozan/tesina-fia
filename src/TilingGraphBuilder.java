import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import org.jgrapht.graph.*;
import java.util.List;

public class TilingGraphBuilder {
    WorldMap worldMap;
    double tileSize;

    public TilingGraphBuilder(WorldMap wm, double tileSize) {
	this.worldMap = wm;
	this.tileSize = tileSize;
    }

    public DefaultDirectedWeightedGraph<Point2D, DefaultWeightedEdge> buildGraph() {
	DefaultDirectedWeightedGraph<Point2D, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph(DefaultWeightedEdge.class);
	Iterator<Rectangle2D> i = new TileIterator(worldMap, tileSize);
	while (i.hasNext()) {
	    Rectangle2D r = i.next();
	    Point2D center = new Point2D.Double(r.getCenterX(), r.getCenterY());
	    graph.addVertex(center);
	}
	
	// FIXME:  Gli errori di arrontondamento sui double possono far cambiare i centri
	i = new TileIterator(worldMap, tileSize);
	while (i.hasNext()) {
	    Rectangle2D r = i.next();
	    Point2D center = new Point2D.Double(r.getCenterX(), r.getCenterY());
	    
	    Point2D l = new Point2D.Double(r.getCenterX() - tileSize, r.getCenterY());
	    if (graph.containsVertex(l))
		graph.addEdge(center, l);
	    Point2D u = new Point2D.Double(r.getCenterX(), r.getCenterY() - tileSize);
	    if (graph.containsVertex(u))
		graph.addEdge(center, u);
	    Point2D right = new Point2D.Double(r.getCenterX() + tileSize, r.getCenterY());
	    if (graph.containsVertex(right))
		graph.addEdge(center, right);
	    Point2D d = new Point2D.Double(r.getCenterX(), r.getCenterY() + tileSize);
	    if (graph.containsVertex(d))
		graph.addEdge(center, d);
	}
	return graph;
    }
}