import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.*;

class MapComponent extends JComponent {
    private WorldMap wm;
    public MapComponent(WorldMap wm) {
	this.wm = wm;
	setPreferredSize(new Dimension((int) wm.getBorder().getWidth(),(int) wm.getBorder().getHeight()));
    }
    
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2d = (Graphics2D) g;
	//g2d.draw(wm.getBorder());
	Iterator<Shape> i = wm.wallIterator();
	while (i.hasNext()) {
	    Shape s = i.next();
	    g2d.fill(s);
	}

	test_draw_tiles(g2d);
    }


    private void test_draw_tiles(Graphics2D g) {
	Iterator<Rectangle2D> i = new TileIterator(wm, 25);
	while (i.hasNext()) {
	    g.draw(i.next());
	}
    }
}

public class Main {
    public static void main(String[] argv) {
	List<Shape> walls = new ArrayList<Shape>();
	walls.add(new Rectangle2D.Double(295, 0, 10, 305));
	walls.add(new Rectangle2D.Double(0, 295, 195, 10));
	walls.add(new Rectangle2D.Double(245, 295, 50, 10));

	walls.add(new Rectangle2D.Double(350, 150, 100, 100));

	WorldMap wm = new WorldMap(walls, new Rectangle2D.Double(0,0,600,400));
	TilingGraphBuilder tgb = new TilingGraphBuilder(wm, 25);
	
	DefaultDirectedWeightedGraph<Point2D, DefaultWeightedEdge> tileGraph = tgb.buildGraph();

	try {
	    DOTExporter de = new DOTExporter();
	    FileWriter out = new FileWriter("tilegraph.dot");
	    de.export(out, tileGraph);
	    out.close();
	}
	catch (IOException e) {
	    throw new RuntimeException();
	}

	JFrame frame = new JFrame("Demo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(new MapComponent(wm));
	frame.pack();
	frame.setVisible(true);
    }

}