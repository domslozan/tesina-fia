package ui;

import graph.Path;

import graph.TilingGraphBuilder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import tiling.Tile;
import tiling.WallListTileMap;

public class MapPanel extends JPanel {

    private final WallListTileMap map;
    private final List<PathfinderWrapper> pathfinders;
    private final List<PathWithCreator> paths;
    private final List<PFBufferedImage> fillImages;
    private final FillListener listener;
    private Tile start, goal;

    public MapPanel(WallListTileMap map) {
        this.map = map;
        ListenableDirectedWeightedGraph<Tile, DefaultWeightedEdge> graph = new TilingGraphBuilder(map).buildGraph();
        this.pathfinders = PathfinderFactory.allPathfinders(graph);

        this.paths = new ArrayList<PathWithCreator>();
        this.fillImages = new ArrayList<PFBufferedImage>();
        this.listener = new FillListener();

        this.setPreferredSize(new Dimension((int) map.getWidth(), (int) map.getHeight()));
        this.addMouseListener(newMouseListener());
        for (PathfinderWrapper pf : pathfinders) {
            pf.addListener(listener);
        }
    }

    private MouseListener newMouseListener() {
        return new MouseInputAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                Point point = me.getPoint();
                switch (me.getButton()) {
                    case MouseEvent.BUTTON1:
                        start = map.getTileContaining(new Point2D.Double(point.x, point.y));
                        updatePaths();
                        repaint();
                        break;
                    case MouseEvent.BUTTON3:
                        goal = map.getTileContaining(new Point2D.Double(point.x, point.y));
                        updatePaths();
                        repaint();
                        break;
                }
            }
        };
    }

    private void updatePaths() {
        paths.clear();
        fillImages.clear();
        listener.clear();
        if (start == null || goal == null) {
            return;
        }
        for (PathfinderWrapper pf : pathfinders) {
            PFBufferedImage fill = new PFBufferedImage((int) map.getWidth(), (int) map.getHeight(), BufferedImage.TYPE_INT_ARGB, pf);
            fillImages.add(fill);
            listener.setGraphics(pf, fill.createGraphics());
            Path<Tile, DefaultWeightedEdge> path = pf.findPath(start, goal);
            paths.add(PathWithCreator.newPathWithCreator(path, pf));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawTiles((Graphics2D) g.create());
        drawWalls((Graphics2D) g.create());

        drawFill((Graphics2D) g.create(), pathfinders.get(0));

        drawPaths((Graphics2D) g.create());
        drawEndpoints((Graphics2D) g.create());

        
    }

    private void drawFill(Graphics2D g, PathfinderWrapper pfw) {
        if (fillImages.isEmpty()) return;
        BufferedImage fill = fillImages.get(2);
        g.drawImage(fill, null, null);
    }

    private void drawEndpoints(Graphics2D g) {
        g.setStroke(new BasicStroke(2f));
        g.setColor(Color.RED);
        if (start != null) {
            Rectangle2D r = start.asRectangle();
            g.drawOval((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
        }
        if (goal != null) {
            Rectangle2D r = goal.asRectangle();
            g.fillOval((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
        }
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
        while (i.hasNext()) {
            g.draw(i.next().asRectangle());
        }
    }

    private void drawPaths(Graphics2D g) {
        for (PathWithCreator p : paths) {
            PathfinderWrapper pf = p.getCreator();
            g.setColor(pf.getPathColor());
            g.setStroke(pf.getPathStroke());

            if (p.length() > 0) {
                Iterator<Tile> i = p.iterator();
                Point2D last = i.next().getCenter();
                while (i.hasNext()) {
                    Point2D n = i.next().getCenter();
                    g.drawLine((int) last.getX(), (int) last.getY(), (int) n.getX(), (int) n.getY());
                    last = n;
                }
            }
        }
    }
}