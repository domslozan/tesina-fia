import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class TileIterator implements Iterator<Rectangle2D> {
    private final WorldMap wm;
    private final double tileSize;
    private int nextTile;

    public TileIterator(WorldMap wm, double tileSize) {
	this.wm = wm;
	this.tileSize = tileSize;
	nextTile = 0;
    }

    // The tiles are numbered left-right then up-down starting from 0
    private Rectangle2D tile(int tileNumber) {
	double w = wm.getBorder().getWidth();
	double h = wm.getBorder().getHeight();
	int nw = (int) Math.floor(w / tileSize);
	return new Rectangle2D.Double(tileSize * (tileNumber % nw),
				      tileSize * Math.floor(tileNumber / nw),
				      tileSize, tileSize);
    }

    private int tileCount() {
	double w = wm.getBorder().getWidth();
	double h = wm.getBorder().getHeight();
	return (int) Math.floor(w / tileSize) * (int) Math.floor(h / tileSize);
    }

    public boolean hasNext() {
	return nextTile < tileCount();
    }

    public Rectangle2D next() {
	if (! hasNext()) throw new NoSuchElementException();
	return tile(nextTile++);
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }
}
