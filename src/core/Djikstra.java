package core;
import static core.Digraph.*;
import static core.Utils.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Djikstra {

    private Digraph dg;
    private Map<Vertex, Integer> vertexScores = new HashMap<Vertex, Integer>();
    private Set<Vertex> markedVertices = new HashSet<Vertex>();
    private Map<Vertex, Edge> backPointers = new HashMap<Vertex, Edge>();
    private VertexHeap heap = new VertexHeap(vertexScores);

    public Djikstra(Digraph dg) { this.dg = dg; }

    public Path shortestPath(Vertex start, Vertex finish, Metric metric) { 
        List<Edge> path = new LinkedList<Edge>();
        vertexScores.clear();
        markedVertices.clear();
        backPointers.clear();
        heap.clear();

        // mark start vertex and update neighbors
        vertexScores.put(start, 0);
        mark(start, metric);
        // continue until heap is empty (or until finish is found)
        while (!heap.isEmpty()) {
            // pop min edge
            Vertex currentVertex = heap.popMin();
            // if it's the end, we can stop early
            if (currentVertex.equals(finish)) {
                // assemble the path from backpointers
                Edge e = backPointers.get(finish);
                while (e != null) {
                    path.add(0, e); 
                    e = backPointers.get(e.getSrc());
                }
                // stop early
                break; 
            }
            // mark the vertex and update its neighbors
            mark(currentVertex, metric);
        }
        return new Path(path); 
    }

    private void mark(Vertex v, Metric metric) {
        markedVertices.add(v);

        for (Edge neighboringEdge : dg.neighboringEdges(v)) {
            Vertex neighbor = neighboringEdge.getDst();
            if (markedVertices.contains(neighbor)) { continue; } // skip if marked
            // compute new cost
            int newScore = vertexScores.get(v) + computeCost(neighboringEdge, metric);

            if (!heap.contains(neighbor)) { // vertex hasn't been discovered
                vertexScores.put(neighbor, newScore); 
                backPointers.put(neighbor, neighboringEdge);
                heap.insert(neighbor);
            } else if (heap.updateIfSmaller(neighbor, newScore)) { // if this path is better, update vertex and backpointer
                backPointers.put(neighbor, neighboringEdge);
            }
            
        }
    }

}
