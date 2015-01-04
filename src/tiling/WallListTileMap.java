package tiling;

import java.awt.Shape;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WallListTileMap extends BaseTileMap {

    private final List<Shape> walls;
    private final List<Shape> hiddenWalls;
    private final double x, y;
    private final int width, height;
    private final double tileSize;

    public WallListTileMap(double tileSize, int width, int height) {
        this(tileSize, width, height, 0, 0, null, null);
    }

    public WallListTileMap(double tileSize, int width, int height, double x, double y, Collection<Shape> walls) {
        this(tileSize, width, height, x, y, walls, null);
    }

    public WallListTileMap(double tileSize, int width, int height, double x, double y, Collection<Shape> walls, Collection<Shape> hidden_walls) {
        if (tileSize <= 0) {
            throw new IllegalArgumentException("Tile size must be positive");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }

        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.walls = new LinkedList<Shape>();
        if (walls != null) {
            this.walls.addAll(walls);
        }
        this.hiddenWalls = new LinkedList<Shape>();
        if (hidden_walls != null) {
            this.walls.addAll(hidden_walls);
        }
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public int getTileWidth() {
        return width;
    }

    @Override
    public int getTileHeight() {
        return height;
    }

    @Override
    public double getTileSize() {
        return tileSize;
    }

    @Override
    public boolean isBlocked(Tile t) {
        for (Shape s : walls) {
            if (s.intersects(asRectangle(t))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isReallyBlocked(Tile t) {
        if (isBlocked(t)) {
            return true;
        }
        for (Shape s : hiddenWalls) {
            if (s.intersects(asRectangle(t))) {
                return true;
            }
        }
        return false;
    }

    public Iterator<Shape> wallIterator() {
        return walls.iterator();
    }

    public Iterator<Shape> hiddenWallIterator() {
        return hiddenWalls.iterator();
    }
}