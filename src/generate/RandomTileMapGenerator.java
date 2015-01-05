package generate;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import tiling.BaseTileMap;
import tiling.Tile;
import tiling.TileMap;

public class RandomTileMapGenerator {

    private final Random rand;
    private final double tileSize;
    private final int width, height;
    private double blockedTileProb;

    public RandomTileMapGenerator(double tileSize, int width, int height, double blockedTileProb) {
        if (tileSize <= 0) {
            throw new IllegalArgumentException("Tile size must be positive");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive");
        }
        if (blockedTileProb < 0 || blockedTileProb > 1) {
            throw new IllegalArgumentException("Blocked tile probability must be in [0,1]");
        }
        rand = new Random();
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.blockedTileProb = blockedTileProb;
    }

    private BitSet generateBitSet(int length) {
        BitSet bs = new ExportableBitSet(length);
        for (int i = 0; i < length; i++) {
            bs.set(i, rand.nextDouble() < blockedTileProb);
        }
        return bs;
    }

    public JSONObject asJSONObject(TileMap m) {
        RandomTileMap map = (RandomTileMap) m;
        JSONObject obj = new JSONObject();
        obj.put("tile_size", tileSize);
        obj.put("width", width);
        obj.put("height", height);

        JSONObject bitset = new JSONObject();
        bitset.put("size", map.blocked.length());
        bitset.put("bits", new String(Base64.encodeBase64(map.blocked.toByteArray())));
        obj.put("blocked_map", bitset);
        return obj;
    }

    public void writeMapSet(List<TileMap> maps, String filename) throws IOException {
        JSONArray array = new JSONArray();
        for (TileMap m : maps) {
            array.add(asJSONObject((RandomTileMap) m));
        }
        FileWriter f = new FileWriter(filename);
        f.write(array.toString());
        f.close();
    }

    public TileMap generate() {
        return new RandomTileMap(generateBitSet(width * height));
    }

    private class RandomTileMap extends BaseTileMap {

        private final ExportableBitSet blocked;

        public RandomTileMap(BitSet blocked) {
            this.blocked = (ExportableBitSet) blocked.clone();
        }

        @Override
        public double getX() {
            return 0;
        }

        @Override
        public double getY() {
            return 0;
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
            BaseTile tile = (BaseTile) t;
            return blocked.get(tile.getIndex());
        }
    }

    public static void main(String[] args) {
        RandomTileMapGenerator gen = new RandomTileMapGenerator(10.0, 100, 100, 0.25);
        ArrayList<TileMap> maps = new ArrayList<TileMap>();
        for (int i = 0; i < 500; i++) {
            maps.add(gen.generate());
        }
        try {
            gen.writeMapSet(maps, "maps_100x100_25.json");

            DefaultDirectedWeightedGraph<Object, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<Object, DefaultWeightedEdge>(DefaultWeightedEdge.class);
            RandomGraphGenerator<Object,DefaultWeightedEdge> rand = new RandomGraphGenerator<Object, DefaultWeightedEdge>(10, 10);
            rand.generateGraph(graph, new ClassBasedVertexFactory<Object>(Object.class), null);

            FileOutputStream fio = new FileOutputStream("testgraph.txt");
            ObjectOutputStream oout = new ObjectOutputStream(fio);
            oout.writeObject(graph);
            oout.close();
            fio.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
