package tiling;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public interface TileMap extends Iterable<Tile> {

    Rectangle2D asRectangle(Tile t);

    double getTileSize();

    Point2D getCenter(Tile t);

    List<Point2D> getCorners(Tile t);

    int tileCount();

    double centerDistance(Tile t, Tile u);

    boolean isBlocked(Tile t);

    List<Tile> adjacentTiles(Tile t);
}
