
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
import ui.MapLegend;
import ui.MapPanel;

public class Main {

    private Main() {
    }
    private static final double TILE_SIZE = 10*2;
    private static WallListTileMap map;

    private static List<Shape> walls() {
        List<Shape> walls = new ArrayList<Shape>();
       
        walls.add(new Rectangle2D.Double(0, 295, 195, 10));
        walls.add(new Rectangle2D.Double(245, 295, 50, 10));
         walls.add(new Rectangle2D.Double(295, 0, 10, 305));
        walls.add(new Rectangle2D.Double(350, 150, 100, 150));
        walls.add(new Rectangle2D.Double(350, 90, 160, 10));
        walls.add(new Rectangle2D.Double(500, 120, 10, 260));

         walls.add(new Rectangle2D.Double(700, 150, 150, 100));
       walls.add(new Rectangle2D.Double(700, 150 + 100 + 50, 150, 100));

       walls.add(new Rectangle2D.Double(700 + 150 - 20, 150 + 100, 20, 50));
        return walls;
    }

    private static List<Shape> hiddenWalls() {
        List<Shape> walls = new ArrayList<Shape>();
      
       //walls.add(new Rectangle2D.Double(700 + 150 - 20, 150 + 100, 20, 50));
        return walls;
    }

    public static void main(String[] argv) {
        map = new WallListTileMap(TILE_SIZE, 120/2, 80/2, 0, 0, walls(),hiddenWalls());
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
        MapLegend legend = new MapLegend();
        top.add(legend);
        mapPanel.setLegend(legend);
        frame.add(top, BorderLayout.NORTH);

        frame.pack();
        frame.setVisible(true);
    }
}

