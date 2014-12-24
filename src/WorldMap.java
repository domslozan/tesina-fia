import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WorldMap {
    protected List<Shape> walls;
    private final double x, y, width, height;

    public WorldMap(Collection<Shape> walls, double x, double y, double width, double height) {
	this.walls = new ArrayList();
	this.walls.addAll(walls);
	this.x = x;
	this.y = y;
	if (width <= 0) throw new RuntimeException();
	if (height <= 0) throw new RuntimeException();
	this.width = width;
	this.height = height;
    } 

    public Iterator<Shape> wallIterator() {
	return walls.iterator();
    }

    public Rectangle2D getBorder() {
	return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    public double getWidth() {
	return width;
    }

    public double getHeight() {
	return height;
    }
}