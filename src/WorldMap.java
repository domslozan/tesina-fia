import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WorldMap {
    private List<Shape> walls;
    private Rectangle2D border;

    public WorldMap(Collection<Shape> walls, Rectangle2D worldBorder) {
	this.walls = new ArrayList();
	this.walls.addAll(walls);
	this.border = worldBorder;
    } 

    public Iterator<Shape> wallIterator() {
	return walls.iterator();
    }

    public Rectangle2D getBorder() {
	return border;
    }

    // public List<Shape> borderAsWall(double wallWidth) {
    // 	List<Shape> w = new ArrayList<Shape>();
    // 	w.add(new Rectangle2D.Double(border));
    // }
}