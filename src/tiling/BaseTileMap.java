package tiling;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class BaseTileMap implements TileMap {

    protected class BaseTile implements Tile {

        private final int index;

        private BaseTile(int index) {
            if (index < 0 || index >= getMap().tileCount()) {
                throw new IndexOutOfBoundsException("Tile number " + index + " does not exist");
            }
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public TileMap getMap() {
            return BaseTileMap.this;
        }

        @Override
        public Rectangle2D asRectangle() {
            return getMap().asRectangle(this);
        }

        @Override
        public double getSize() {
            return getMap().getTileSize();
        }

        @Override
        public Point2D getCenter() {
            return getMap().getCenter(this);
        }

        @Override
        public List<Point2D> getCorners() {
            return getMap().getCorners(this);
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof BaseTile)) {
                return false;
            }
            return this.index == ((BaseTile) other).index;
        }

        @Override
        public int hashCode() {
            return 997 * (this.index) ^ 991 * (getMap().hashCode());
        }

        @Override
        public String toString() {
            return "Tile(" + index + ")";
        }
    }
    
    @Override
    public Rectangle2D asRectangle(Tile t) {
        BaseTile tile = (BaseTile) t;
        double offsetX = getX();
        double offsetY = getY();
        double w = getWidth();
        double l = getTileSize();
        int nw = (int) Math.floor(w / l);
        return new Rectangle2D.Double(l * (tile.index % nw),
                l * Math.floor(tile.index / nw),
                l, l);
    }

    public abstract double getX();

    public abstract double getY();

    public abstract int getTileWidth();

    public abstract int getTileHeight();

    public double getWidth() {
        return getTileSize() * getTileWidth();
    }

    public double getHeight() {
        return getTileSize() * getTileHeight();
    }

    @Override
    public Point2D getCenter(Tile t) {
        return new Point2D.Double(asRectangle(t).getCenterX(), asRectangle(t).getCenterY());
    }

    @Override
    public List<Point2D> getCorners(Tile t) {
        ArrayList<Point2D> list = new ArrayList<Point2D>(4);
        PathIterator i = asRectangle(t).getPathIterator(null);
        double[] points = new double[6];
        int type;
        while (!i.isDone()) {
            type = i.currentSegment(points);
            switch (type)
            {
                case PathIterator.SEG_MOVETO:
                    list.add(0, new Point2D.Double(points[0], points[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    list.add(new Point2D.Double(points[0], points[1]));
                    break;
                case PathIterator.SEG_CLOSE:
                    list.add (list.get(0));
                    break;
            }
            i.next();
        }
        if (list.size() != 4) throw new RuntimeException();
        return list;
    }

    @Override
    public int tileCount() {
        return getTileWidth()*getTileHeight();
    }

    @Override
    public double centerDistance(Tile t, Tile u) {
        return t.getCenter().distance(u.getCenter());
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
            return new Point2D.Double(start.getX() + magnitude * Math.cos(theta), start.getY() + magnitude * Math.sin(theta));
        }
    }

    public Rectangle2D getBorder() {
        return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public List<Tile> adjacentTiles(Tile t) {
        ArrayList<Tile> list = new ArrayList<Tile>(8);
        BaseTile tile = (BaseTile) t;
        int nw = getTileWidth();
        Rectangle2D border = getBorder();
        Point2D p;

        p = Direction.NW.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index - nw - 1));

	p = Direction.N.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index - nw));

	p = Direction.NE.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index - nw + 1));

	p = Direction.E.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index + 1));

	p = Direction.SE.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index + nw + 1));

	p = Direction.S.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index + nw));

	p = Direction.SW.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index + nw - 1));

	p = Direction.W.move(tile.getCenter(), 5/4 * getTileSize());
	if (border.contains(p))
	    list.add(new BaseTile(tile.index - 1));

	return list;
    }

    @Override
    public Iterator<Tile> iterator() {
        return new Iterator<Tile>() {
            private int nextTile = 0;
            
            @Override
            public boolean hasNext() {
                return nextTile < tileCount();
            }
	    
            @Override
            public Tile next() {
                if (! hasNext()) throw new NoSuchElementException();
                return new BaseTile(nextTile++);
	    }
	    
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}