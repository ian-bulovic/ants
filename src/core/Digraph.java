package core;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.Utils.Metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Digraph {
    public static class Vertex {
        private String name;
        private int id;

        private String label;
        private int x, y;

        public Vertex(String name, int id, String label, int x, int y) {
            this.name = name;
            this.id = id;
            this.label = label;
            this.x = x;
            this.y = y;
        }

        public String getLabel() { return label; }
        public int getX() { return x; }
        public int getY() { return y; }

        public String toString() { return name; }
        
        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof Vertex && obj.hashCode() == hashCode());
        }
    }

    public static class Edge {
        private Vertex src;
        private Vertex dst;
        private int length;
        private String name;
        private int id;
        private int angle;
        private String direction;
        private char edgeType;

        public Edge(Vertex src, Vertex dst, int length, String name, int id, int angle, String direction, char edgeType) {
            this.src = src;
            this.dst = dst;
            this.length = length;
            this.name = name;
            this.id = id;
            this.angle = angle;
            this.direction = direction;
            this.edgeType = edgeType;
        }

        public Vertex getSrc() { return src; }
        public Vertex getDst() { return dst; }
        public int getLength() { return length; }
        public int getAngle() { return angle; }
        public String getDirection() { return direction; }
        public char getEdgeType() { return edgeType; }

        public String toString() { return name; }

        @Override
        public int hashCode() { 
            return id; 
        }

        @Override
        public boolean equals(Object obj) { 
            return (obj instanceof Vertex && obj.hashCode() == hashCode()); 
        }
    }

    public static class Path {
        public final List<Edge> edges;

        private int length = 0;
        private Metric metric = null;

        public Path(List<Edge> edges) { this.edges = new ArrayList<>(edges); }

        public int getLength(Metric metric) {
            if (this.metric == metric) { return length; }
            this.metric = metric;
            length = Utils.pathCost(edges, metric);
            return length;
        }

        public int numEdges() { return edges.size(); }

        public Vertex[] vertices() {
            Vertex[] vs = new Vertex[edges.size() + 1];
            vs[0] = edges.get(0).src;
            for (int i = 1; i < vs.length; i++) {
                vs[i] = edges.get(i-1).dst;
            }
            return vs;
        }

        @Override
        public int hashCode() {
            return edges.hashCode();
        }
    }

    private Map<Vertex, List<Edge>> neighbors = new HashMap<Vertex, List<Edge>>();

    public String toString() {
        StringBuffer s = new StringBuffer();
        for (Vertex v : neighbors.keySet()) {
            s.append("\n    " + v + " -> " + neighbors.get(v));
        }
        return s.toString();
    }

    public void addVertex(String name, int id, String label, int x, int y) {
        addVertex(new Vertex(name, id, label, x, y));
    }

    public void addVertex(Vertex vertex) {
        if (neighbors.containsKey(vertex)) {
            return;
        }
        neighbors.put(vertex, new ArrayList<Edge>());
    }
    
    public void addEdge(Vertex from, Vertex to, int length, String name, int id, int angle, String direction, char edgeType) {
        this.addVertex(from);
        this.addVertex(to);
        neighbors.get(from).add(new Edge(from, to, length, name, id, angle, direction, edgeType));
    }

    public List<Edge> neighboringEdges(Vertex vertex) {
        List<Edge> list = new ArrayList<Edge>();
        for(Edge e : neighbors.get(vertex)) {
            list.add(e);
        }
        return list;
    }

    public List<Vertex> neighboringVertices(Vertex vertex) {
        List<Vertex> list = new ArrayList<Vertex>();
        for(Edge e : neighbors.get(vertex)) {
            list.add(e.dst);
        }
        return list;
    }

    public Edge getEdge(Vertex from, Vertex to) {
        for(Edge e : neighbors.get(from)){
            if(e.dst.equals(to)) {
                return e;
            }
        }
        return null;
    }

    public Vertex getVertex(int id) {
        for (Vertex v : neighbors.keySet()) {
            if(v.hashCode() == id) {
                return v;
            }
        }
        return null;
    }

    public Set<Vertex> vertexSet() {
        return new HashSet<Vertex>(neighbors.keySet());
    }
}
