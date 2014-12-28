package tiling;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public interface Tile {

    TileMap getMap();

    Rectangle2D asRectangle();

    double getSize();

    Point2D getCenter();

    List<Point2D> getCorners();
}
