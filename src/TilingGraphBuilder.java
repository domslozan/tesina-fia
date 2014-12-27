import java.util.Iterator;
import org.jgrapht.graph.*;

public class TilingGraphBuilder {
    TileWorldMap worldMap;

    public TilingGraphBuilder(WorldMap wm, double tileSize) {
	this.worldMap = new TileWorldMap(wm, tileSize);
    }

    public DefaultDirectedWeightedGraph<TileWorldMap.Tile, DefaultWeightedEdge> buildGraph() {
	DefaultDirectedWeightedGraph<TileWorldMap.Tile, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph(DefaultWeightedEdge.class);
	Iterator<TileWorldMap.Tile> i = worldMap.tileIterator();
	while (i.hasNext()) {
	    TileWorldMap.Tile t = i.next();
	    graph.addVertex(t);
	}
	
	i = worldMap.tileIterator();
	while (i.hasNext()) {
	    TileWorldMap.Tile t = i.next();
	    Iterator <TileWorldMap.Tile> j = worldMap.adjacentTiles(t).iterator();
	    while (j.hasNext()) {
		TileWorldMap.Tile u = j.next();
		if (!worldMap.intersectsWall(u))
		    graph.addEdge(t, u);
	    }
	}
	return graph;
    }

    public TileWorldMap getTilingWorldMap() {
        return worldMap;
    }
}