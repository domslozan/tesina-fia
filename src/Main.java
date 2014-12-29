
import algorithms.AStar;
import algorithms.Dijkstra;
import algorithms.EuclideanDistanceHeuristicFactory;
import graph.TilingGraphBuilder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.*;
import org.jgrapht.generate.*;
import org.jgrapht.*;
import tiling.Tile;
import tiling.TileMap;
import tiling.WallListTileMap;

class MapComponent extends JComponent {

    private WallListTileMap map;
    private List<Point2D> path;
    //private BufferedImage image;

    public MapComponent(WallListTileMap map) {
        this.map = map;
        //image = new BufferedImage((int) wm.getBorder().getWidth(), (int) wm.getBorder().getHeight(), BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension((int) map.getWidth(), (int) map.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        drawTiles(g2d);
        drawWalls(g2d);
        drawPath(g2d);
    }

    private void drawWalls(Graphics2D g) {
        Iterator<Shape> i = map.wallIterator();
        g.setColor(Color.BLACK);
        while (i.hasNext()) {
            Shape s = i.next();
            g.fill(s);
        }
    }

    private void drawTiles(Graphics2D g) {
        Iterator<Tile> i = map.iterator();
        g.setColor(Color.GRAY);
        //g.setStroke(new BasicStroke(0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[]{5f, 5f}, 0f));
        while (i.hasNext()) {
            g.draw(i.next().asRectangle());
        }
    }

    private void drawPath(Graphics2D g) {
        if (path == null) {
            return;
        }
        g.setColor(Color.RED);
        Iterator<Point2D> i = path.iterator();
        Point2D last = i.next();
        while (i.hasNext()) {
            Point2D n = i.next();
            g.drawLine((int) last.getX(), (int) last.getY(), (int) n.getX(), (int) n.getY());
            last = n;
        }
    }

    public void setPath(List<Point2D> path) {
        this.path = path;
    }
}

public class Main {

    public static final double TILE_SIZE = 10;

    public static void main(String[] argv) {
        List<Shape> walls = new ArrayList<Shape>();
        walls.add(new Rectangle2D.Double(295, 0, 10, 305));
        walls.add(new Rectangle2D.Double(0, 295, 195, 10));
        walls.add(new Rectangle2D.Double(245, 295, 50, 10));

        walls.add(new Rectangle2D.Double(350, 150, 100, 100));

        WallListTileMap map = new WallListTileMap(TILE_SIZE, 60, 40, 0, 0, walls);
        TilingGraphBuilder tgb = new TilingGraphBuilder(map);

        Graph<Tile, DefaultWeightedEdge> tileGraph = tgb.buildGraph();

        try {
            DOTExporter de = new DOTExporter();
            FileWriter out = new FileWriter("tilegraph.dot");
            de.export(out, tileGraph);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        Tile start = map.getTileContaining(new Point2D.Double(5, 5));
        Tile goal = map.getTileContaining(new Point2D.Double(595, 5));

        AStar<Tile, DefaultWeightedEdge> astar = new AStar<Tile, DefaultWeightedEdge>(new EuclideanDistanceHeuristicFactory());
        Dijkstra<Tile, DefaultWeightedEdge> dijkstra = new Dijkstra<Tile, DefaultWeightedEdge>();
        List<Tile> vertices = dijkstra.getPath(tileGraph, start, goal);

        ArrayList<Point2D> pointPath = new ArrayList<Point2D>();
        for (Tile t : vertices) {
            pointPath.add(t.getCenter());
        }

        MapComponent mc = new MapComponent(map);
        mc.setPath(pointPath);

        JFrame frame = new JFrame("Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mc);
        frame.pack();
        frame.setVisible(true);

        //random_graph();
    }
//    public static void random_graph() {
//	RandomGraphGenerator<Integer, DefaultEdge> rgg = new RandomGraphGenerator<Integer, DefaultEdge>(100, 300);
//	DefaultDirectedGraph<Integer, DefaultEdge> graph = new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
//
//	VertexFactory<Integer> fac = new VertexFactory<Integer>() {
//	    private int n = 0;
//
//	    public Integer createVertex() {
//		return new Integer(n++);
//	    }
//	};
//
//	rgg.generateGraph(graph, fac, null);
//
//	try {
//	    DOTExporter de = new DOTExporter();
//	    FileWriter out = new FileWriter("randomgraph.dot");
//	    de.export(out, graph);
//	    out.close();
//	}
//	catch (IOException e) {
//	    throw new RuntimeException();
//	}
//    }
}