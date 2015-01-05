
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import tiling.WallListTileMap;
import ui.MapPanel;

public class Main {

    private Main() {
    }
    private static final double TILE_SIZE = 10;
    private static WallListTileMap map;

    private static List<Shape> walls() {
        List<Shape> walls = new ArrayList<Shape>();
       
        walls.add(new Rectangle2D.Double(0, 295, 195, 10));
        walls.add(new Rectangle2D.Double(245, 295, 50, 10));
        walls.add(new Rectangle2D.Double(350, 150, 100, 150));
        walls.add(new Rectangle2D.Double(350, 90, 160, 10));
        walls.add(new Rectangle2D.Double(500, 120, 10, 260));
        return walls;
    }

    private static List<Shape> hiddenWalls() {
        List<Shape> walls = new ArrayList<Shape>();
        walls.add(new Rectangle2D.Double(295, 0, 10, 305));
        return walls;
    }

    public static void main(String[] argv) {
        map = new WallListTileMap(TILE_SIZE, 60, 40, 0, 0, walls(),hiddenWalls());
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                buildUI();
            }
        });

    }

    private static void buildUI() {
        JFrame frame = new JFrame("Pathfinding demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MapPanel mapPanel = new MapPanel(Main.map);
        frame.getContentPane().add(mapPanel, BorderLayout.CENTER);

        JPanel top = new JPanel();
        top.add(new MapLegend());
        frame.add(top, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }
}

class MapLegend extends JPanel {

    public MapLegend() {
        super();
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

        add(new LegendElement("Theta*", Color.RED, new BasicStroke(4f)));
        add(new LegendElement("A* with Post Smoothing", Color.BLACK, new BasicStroke(4f)));
    }
}

class LegendElement extends JPanel {

    private final String text;
    private final Color color;
    private final Stroke stroke;
    private final JLabel label;
    private final JPanel line;

    public LegendElement(String text, Color color, Stroke stroke) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.color = color;
        this.text = text;
        this.stroke = stroke;

        //this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.line = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setStroke(LegendElement.this.stroke);
                g2d.setColor(LegendElement.this.color);
                g2d.drawLine(0, this.getHeight() / 2, this.getWidth(), this.getHeight() / 2);
            }
        };
        line.setPreferredSize(new Dimension(15, 10));
        this.label = new JLabel(text);
        this.add(line);
        this.add(Box.createHorizontalStrut(5));
        this.add(label);
    }
}