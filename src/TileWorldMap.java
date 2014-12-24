import java.util.Iterator;
import java.util.NoSuchElementException;
import java.awt.geom.*;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

public class TileWorldMap extends WorldMap {
    public class Tile {
	private final int index;

	public Tile(int index) {
	    if (index < 0 || index >= tileCount()) throw new RuntimeException();
	    this.index = index;
	}

	public Rectangle2D getRectangle() {
	    double offsetX = TileWorldMap.this.getX();
	    double offsetY = TileWorldMap.this.getY();
	    double w = TileWorldMap.this.getWidth();
	    double l = getSize();
	    int nw = (int) Math.floor(w / l);
	    return new Rectangle2D.Double(l * (index % nw),
					  l * Math.floor(index / nw),
					  l, l);
	}

	public double getSize() {
	    return tileSize;
	}

        @Override
	    public boolean equals(Object other) {
	    if (! (other instanceof Tile)) return false;
	    return this.index == ((Tile) other).index;
	}

        @Override
	    public int hashCode() {
            int hash = 3;
            hash = 79 * hash + this.index;
            return hash;
        }

	public Point2D getCenter() {
	    return new Point2D.Double(getRectangle().getCenterX(), getRectangle().getCenterY());
	}
    }

    private final double tileSize;

    public TileWorldMap(WorldMap wm, double tileSize) {
	super(wm.walls, wm.getX(), wm.getY(), wm.getWidth(), wm.getHeight());
	this.tileSize = tileSize;
    }
    
    public Iterator<Tile> tileIterator() {
	return new Iterator<Tile>() {
	    private int nextTile = 0;
	    
            @Override
		public boolean hasNext() {
		return nextTile < tileCount();
	    }
	    
            @Override
		public Tile next() {
		if (! hasNext()) throw new NoSuchElementException();
		return new Tile(nextTile++);
	    }
	    
            @Override
		public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }

    public int tileCount() {
	double w = getWidth();
	double h = getHeight();
	return (int) Math.floor(w / tileSize) * (int) Math.floor(h / tileSize);
    }
    
    public boolean intersectsWall(Tile t) {
	Iterator<Shape> i = wallIterator();
	while (i.hasNext()) {
	    if (i.next().intersects(t.getRectangle())) return true;
	}
	return false;
    }

    private enum Direction {
	NW (-1, -1), 
            N (0,-1), 
            NE (1,-1),
            E (1,0),
            SE (1,1), 
            S (0,1), 
            SW (-1,1), 
            W (-1,0);
	
	private final double x, y;
	
	private Direction(double x, double y) {
	    this.x = x;
	    this.y = y;
	}

	public Point2D move(Point2D start, double magnitude) {
	    double theta = Math.atan2(y, x);
	    //double mod = magnitude / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	    return new Point2D.Double(start.getX() + magnitude * Math.cos(theta), start.getY() + magnitude * Math.sin(theta));
	}
    }

    public List<Tile> adjacentTiles(Tile t) {
	ArrayList<Tile> list = new ArrayList<Tile>();
	int nw = (int) Math.floor(getWidth() / tileSize);
	Rectangle2D border = getBorder();
	Point2D p;

	p = Direction.NW.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index - nw - 1));
	
	p = Direction.N.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index - nw));

	p = Direction.NE.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index - nw + 1));
	
	p = Direction.E.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index + 1));
	
	p = Direction.SE.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index + nw + 1));
	
	p = Direction.S.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index + nw));
	
	p = Direction.SW.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index + nw - 1));
	
	p = Direction.W.move(t.getCenter(), 5/4 * tileSize);
	if (border.contains(p))
	    list.add(new Tile(t.index - 1));

	return list;
    }
}