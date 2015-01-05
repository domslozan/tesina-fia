import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ricc
 */
public class StreamMain {
    public static void main(String[] args) {
        Graph graph = new SingleGraph("test");

        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
        graph.display();
    }
}
