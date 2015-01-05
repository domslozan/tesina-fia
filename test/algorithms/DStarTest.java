package algorithms;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import graph.Path;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class DStarTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        testgraph = new DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        for (int i = 0; i < 12; i++) {
            testgraph.addVertex(i);
        }
        for (int i = 0; i < 6; i++) {
            testgraph.addEdge(i, i + 6);
            testgraph.addEdge(i + 6, i);
        }

        for (int i = 0; i < 5; i++) {
            testgraph.addEdge(i, i + 1);
            testgraph.addEdge(i + 6, i + 6 + 1);
        }
        for (int i = 5; i > 0; i--) {
            testgraph.addEdge(i, i - 1);
            testgraph.addEdge(i + 6, i + 6 - 1);
        }

        for (DefaultWeightedEdge e : testgraph.edgeSet()) {
            testgraph.setEdgeWeight(e, 10);
        }
    }

    @After
    public void tearDown() {
        testgraph = null;
    }
    private DefaultDirectedWeightedGraph<Integer, DefaultWeightedEdge> testgraph;

    /**
     * Test of findPath method, of class Pathfinder.
     */
    @Test
    public void testFindPath() {
        DStar pf = new DStar(testgraph);
        Path<Integer, DefaultWeightedEdge> path = pf.findPath(0, 11);
        assertEquals(path.cost(), 60, 0.001);
    }

    @Test
    public void singleEdgeRaise() {
        DStar pf = new DStar(testgraph);
        Path<Integer, DefaultWeightedEdge> path = pf.findPath(0, 11);
        pf.updateGraphEdge(testgraph.getEdge(1, 2), 1000);
        assertEquals(testgraph.getEdgeWeight(testgraph.getEdge(1, 2)), 1000, 0.001);
        Path<Integer, DefaultWeightedEdge> restofpath = pf.findPath(1, 11);
        Path<Integer, DefaultWeightedEdge> fullpath = path.mergeWith(restofpath);
        assertEquals(fullpath.cost(), 60, 0.001);
        assertTrue(fullpath.getVertices().contains(1));
        assertTrue(fullpath.getVertices().contains(11));
        assertTrue(fullpath.getVertices().contains(0));
        assertFalse(fullpath.getVertices().contains(2));
    }

    @Test
    public void bottleneckRaise() {
        DStar pf = new DStar(testgraph);
        Path<Integer, DefaultWeightedEdge> path = pf.findPath(0, 11);
        pf.updateGraphEdge(testgraph.getEdge(1, 2), 100);
        pf.updateGraphEdge(testgraph.getEdge(7, 8), 100);
        assertEquals(testgraph.getEdgeWeight(testgraph.getEdge(1, 2)), 100, 0.001);
        assertEquals(testgraph.getEdgeWeight(testgraph.getEdge(7, 8)), 100, 0.001);
        Path<Integer, DefaultWeightedEdge> restofpath = pf.findPath(1, 11);
        Path<Integer, DefaultWeightedEdge> fullpath = path.mergeWith(restofpath);
        assertEquals(fullpath.cost(), 150, 0.001);
        assertTrue(fullpath.getVertices().contains(1));
        assertTrue(fullpath.getVertices().contains(11));
        assertTrue(fullpath.getVertices().contains(0));
    }

    @Test
    public void startBottleneckRaise() {
        DStar pf = new DStar(testgraph);
        Path<Integer, DefaultWeightedEdge> path = pf.findPath(0, 11);
        pf.updateGraphEdge(testgraph.getEdge(1, 2), 100);
        pf.updateGraphEdge(testgraph.getEdge(7, 8), 100);
        assertEquals(testgraph.getEdgeWeight(testgraph.getEdge(1, 2)), 100, 0.001);
        assertEquals(testgraph.getEdgeWeight(testgraph.getEdge(7, 8)), 100, 0.001);
        Path<Integer, DefaultWeightedEdge> restofpath = pf.findPath(0, 11);
        Path<Integer, DefaultWeightedEdge> fullpath = path.mergeWith(restofpath);
        assertEquals(fullpath.cost(), 150, 0.001);
        assertTrue(fullpath.getVertices().contains(11));
        assertTrue(fullpath.getVertices().contains(0));
    }
}
