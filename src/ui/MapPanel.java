package ui;

import algorithms.Pathfinder;
import graph.Path;
import graph.Utils;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.Tile;
import tiling.WallListTileMap;

public class MapPanel extends JPanel {

    private final WallListTileMap map;
    private List<PathfinderWrapper> pathfinders;
    private final Map<PathfinderWrapper, Path<Tile, DefaultWeightedEdge>> paths;
    private final Map<PathfinderWrapper, BufferedImage> fillImages;
    private final FillListener listener;
    private Tile start, goal;
    private MapLegend legend;


    public MapPanel(WallListTileMap map) {
        super();
        this.map = map;

        this.paths = new HashMap<PathfinderWrapper, Path<Tile, DefaultWeightedEdge>>();
        this.fillImages = new HashMap<PathfinderWrapper, BufferedImage>();
        this.listener = new FillListener();

        this.setPreferredSize(new Dimension((int) map.getWidth(), (int) map.getHeight()));
        this.addMouseListener(newMouseListener());

    }

    public void setLegend(MapLegend l) {
        this.legend = l;
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

    private MouseListener newLegendElementListener(final PathfinderWrapper pf) {
        return new MouseInputAdapter() {

            @Override
            public void mouseEntered(MouseEvent me) {
                setDrawDetail(pf);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                unsetDrawDetail(pf);
                repaint();
            }
        };
    }
    private boolean drawDetail = false;
    private Path<Tile, DefaultWeightedEdge> pathDetail;
    private BufferedImage fillDetail;

    private void setDrawDetail(PathfinderWrapper pf) {
        drawDetail = true;
        pathDetail = paths.get(pf);
        fillDetail = fillImages.get(pf);
    }

    private void unsetDrawDetail(PathfinderWrapper pf) {
        drawDetail = false;
    }

    private void setupPathfinders() {
        paths.clear();
        fillImages.clear();
        listener.clear();
        legend.clearElements();
        pathfinders = PathfinderFactory.allPathfinders(map);
        for (PathfinderWrapper pf : pathfinders) {
            pf.addListener(listener);

            legend.addElement(pf, newLegendElementListener(pf));


        }


    }

    private void updatePaths() {

        if (start == null || goal == null) {
            return;
        }
        setupPathfinders();
        for (PathfinderWrapper pf : pathfinders) {
            BufferedImage fill = new BufferedImage((int) map.getWidth(), (int) map.getHeight(), BufferedImage.TYPE_INT_ARGB);
            fillImages.put(pf, fill);
            listener.setGraphics(pf, fill.createGraphics());
            Path<Tile, DefaultWeightedEdge> path = pf.findPath(start, goal);
            Tile current = followPath(path, pf);
            while (!(current == goal)) {
                Path<Tile, DefaultWeightedEdge> restOfPath = pf.findPath(current, goal);
                path = path.mergeWith(restOfPath);
                current = followPath(restOfPath, pf);
            }
            paths.put(pf, path);
            System.err.println(pf.getName() + ": path cost = " + path.cost() + ", opened nodes = " + listener.openedCount(pf));
        }
    }

    private Tile followPath(Path<Tile, DefaultWeightedEdge> path, Pathfinder<Tile, DefaultWeightedEdge> pf) {
        Iterator<Tile> i = path.iterator();
        Tile n = i.next();
        while (i.hasNext()) {
            Map<DefaultWeightedEdge, Double> wrongEdges = Utils.wrongEdges(pf.getGraph(), n);
            if (!wrongEdges.isEmpty()) {
                for (Map.Entry<DefaultWeightedEdge, Double> e : wrongEdges.entrySet()) {
                    pf.updateGraphEdge(e.getKey(), e.getValue());
                }
                return n;
            }
            n = i.next();
        }
        return n;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        drawTiles((Graphics2D) g.create());
        drawWalls((Graphics2D) g.create());
        drawHiddenWalls((Graphics2D) g.create());

        if (drawDetail) {
            ((Graphics2D) g.create()).drawImage(fillDetail, null, null);
        }


        drawPaths((Graphics2D) g.create());
        if (drawDetail) {


            Graphics2D gc = (Graphics2D) g.create();
            gc.setColor(Color.RED);
            gc.setStroke(new BasicStroke(3f));
            if (pathDetail.length() > 0) {
                Iterator<Tile> i = pathDetail.iterator();
                Point2D last = i.next().getCenter();
                while (i.hasNext()) {
                    Point2D n = i.next().getCenter();
                    gc.drawLine((int) last.getX(), (int) last.getY(), (int) n.getX(), (int) n.getY());
                    last = n;
                }
            }
        }


        drawEndpoints((Graphics2D) g.create());


    }

    private void drawFill(Graphics2D g, PathfinderWrapper pfw) {
        if (fillImages.isEmpty()) {
            return;
        }
        BufferedImage fill = fillImages.get(pfw);
        g.drawImage(fill, null, null);
    }

    private void drawEndpoints(Graphics2D g) {
        g.setStroke(new BasicStroke(3f));
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

    private void drawHiddenWalls(Graphics2D g) {
        Iterator<Shape> i = map.hiddenWallIterator();
        g.setColor(Color.GRAY);
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
        for (Map.Entry<PathfinderWrapper, Path<Tile, DefaultWeightedEdge>> e : paths.entrySet()) {
            g.setColor(e.getKey().getPathColor());
            g.setStroke(e.getKey().getPathStroke());

            if (e.getValue().length() > 0) {
                Iterator<Tile> i = e.getValue().iterator();
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
