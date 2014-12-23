import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;
import java.awt.Dimension;

class MapComponent extends JComponent {
    private List<Shape> walls;

    public MapComponent() {
	walls = new ArrayList<Shape>();

	// Map borders (600x400 border width = 10)
	walls.add(new Rectangle2D.Double(-5, -5, 610, 10));
	walls.add(new Rectangle2D.Double(-5, 395, 610, 10));
	walls.add(new Rectangle2D.Double(-5, -5, 10, 410));
	walls.add(new Rectangle2D.Double(595, -5, 10, 410));
	
	// Map walls
	walls.add(new Rectangle2D.Double(295, 0, 10, 305));
	walls.add(new Rectangle2D.Double(0, 295, 195, 10));
	walls.add(new Rectangle2D.Double(245, 295, 50, 10));

	walls.add(new Rectangle2D.Double(350, 150, 100, 100));
	setPreferredSize(new Dimension(600,400));
    }

    protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2d = (Graphics2D) g;
	//Rectangle2D border = new Rectangle2D.Double(0,0,600,400);
	//g2d.draw(border);
	Iterator<Shape> i = walls.iterator();
	while (i.hasNext()) {
	    Shape s = i.next();
	    g2d.fill(s);
	}
    }
}

public class Main {
    public static void main(String[] argv) {
	JFrame frame = new JFrame("Demo");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(new MapComponent());
	frame.pack();
	frame.setVisible(true);
    }
}