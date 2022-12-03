package core;

import static core.Digraph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import core.Utils.Metric;
import disp.Display;

public class Colony {
    public class Ant {
        private List<Edge> pathEdges = new ArrayList<Edge>();
        private Set<Vertex> unvisitedVertices;
        private List<Vertex> keyVertices = new ArrayList<Vertex>();

        private Path path;

        public void wander(Vertex start) {
            pathEdges.clear();
            unvisitedVertices = dg.vertexSet();
            unvisitedVertices.remove(start);
            keyVertices.clear();
            keyVertices.add(start);

            Vertex currentVertex = start;
            Vertex nextVertex;
            do {
                // make an informed choice of the next vertex
                if (unvisitedVertices.isEmpty()) {
                    nextVertex = start;
                } else {
                    nextVertex = chooseNextVertex(currentVertex);
                }

                keyVertices.add(nextVertex);
                Path p = getPrecomputedPath(currentVertex, nextVertex);
                for (Edge e : p.edges) {
                    unvisitedVertices.remove(e.getDst());
                    pathEdges.add(e);
                }

                currentVertex = nextVertex;
            } while (nextVertex != start);

            path = new Path(pathEdges);
        }

        private int numNewVertices(Path p) {
            int total = 0;
            for (Vertex v : p.vertices()) {
                if (unvisitedVertices.contains(v)) {
                    total++;
                }
            }
            return total;
        }

        // tau is the pheremone value
        // eta is the average number of new vertices per 1000ft of the shortest path between a and b
        private float desirability(Vertex a, Vertex b) {
            float tau = getPheromones(a, b);
            Path shortestPath = getPrecomputedPath(a, b);
            float eta = 1000f * numNewVertices(shortestPath) / (float) shortestPath.getLength(parameters.metric);
            return (float)(Math.pow(tau, parameters.alpha) * Math.pow(eta, parameters.beta));
        }

        public List<Vertex> getKeyVertices() { return keyVertices; }

        private Vertex chooseNextVertex(Vertex currentVertex) {
            Vertex[] candidates = unvisitedVertices.toArray(new Vertex[0]);
            Map<Vertex, Float> scores = new HashMap<>();
            float target = 0;
            for (Vertex v : candidates) {
                float des = desirability(currentVertex, v) + parameters.temperature;
                target += des;
                scores.put(v, des);
            }
            // weighted random choice
            target *= Math.random();
            int i;
            for (i = 0; target >= 0 && i < candidates.length; i++) {
                target -= scores.get(candidates[i]);
            }
            return candidates[i - 1];
        }

        public Path getPath() {
            return path;
        }
    }

    public static record Parameters(Metric metric, float Q, float rho, float temperature, float alpha, float beta, int best) {}

    private Digraph dg;
    private Parameters parameters;

    private Map<Vertex, HashMap<Vertex, Path>> shortestPaths = new HashMap<>();
    private Map<Vertex, HashMap<Vertex, Float>> pheromones = new HashMap<>();

    private List<Ant> ants = new ArrayList<Ant>();


    public Colony(Digraph dg, Parameters parameters) {
        this.dg = dg;
        this.parameters = parameters;

        // precompute shortest paths
        Djikstra dj = new Djikstra(dg);
        for (Vertex va : dg.vertexSet()) {
            shortestPaths.put(va, new HashMap<Vertex, Path>());
            pheromones.put(va, new HashMap<>());
            for (Vertex vb : dg.vertexSet()) {
                if (!va.equals(vb)) {
                    shortestPaths.get(va).put(vb, dj.shortestPath(va, vb, parameters.metric));
                    setPheromones(va, vb, 1f);
                }
            }
        }
    }

    private Path getPrecomputedPath(Vertex a, Vertex b) {
        return shortestPaths.get(a).get(b);
    }

    private float getPheromones(Vertex a, Vertex b) {
        return pheromones.get(a).get(b);
    }

    private void setPheromones(Vertex a, Vertex b, float value) {
        pheromones.get(a).put(b, value);
    }

    public void addAnts(int numAnts) {
        for (int i = 0; i < numAnts; i++) {
            ants.add(new Ant());
        }
    }

    private void wander(boolean multithreaded, int iteration) {
        List<Vertex> vertices = new ArrayList<Vertex>(dg.vertexSet());
        int b = iteration == 1 ? 0 : parameters.best;
        if (multithreaded) {
            Thread[] threads = new Thread[ants.size() - b];
            for (int i = b; i < ants.size(); i++) {
                int j = i;
                threads[i-b] = new Thread(() -> { ants.get(j).wander(vertices.get((int)(Math.random() * vertices.size()))); });
                // threads[i] = new Thread(() -> { ants.get(j).wander(vertices.get(0)); });
                threads[i-b].start();
            }

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (int i = b; i < ants.size(); i++) {
                ants.get(i).wander(vertices.get((int)(Math.random() * vertices.size())));
                // ants.get(i).wander(vertices.get(0));
            }
        }
        
    }

    public void updatePheromones() {
        // evaporate
        for (Vertex a : dg.vertexSet()) {
            for (Vertex b : dg.vertexSet()) {
                if (a != b) {
                    setPheromones(a, b, Math.max(0.5f, parameters.rho * getPheromones(a, b)));
                }
            }
        }

        // get global minimum path length
        int bestPathLength = ants.get(0).getPath().getLength(parameters.metric);
        for (Ant ant : ants) {
            int l = ant.getPath().getLength(parameters.metric);
            if (l < bestPathLength) {
                bestPathLength = l;
            }
        }

        // deposit
        for (Ant ant : ants) {
            List<Vertex> keyVertices = ant.getKeyVertices();
            for (int i = 0; i < keyVertices.size() - 1; i++) {
                Vertex a = keyVertices.get(i);
                Vertex b = keyVertices.get(i + 1);

                float prevPheremoneValue = getPheromones(a, b);
                float additionalPheremoneValue = (float)Math.pow(bestPathLength / (float)ant.getPath().getLength(parameters.metric), parameters.Q);
                setPheromones(a, b, prevPheremoneValue + additionalPheremoneValue);

                // also add pheremones to the same path but in the other direction
                prevPheremoneValue = getPheromones(b, a);
                setPheromones(b, a, prevPheremoneValue + additionalPheremoneValue);
            }
        }
    }

    public void learn(int iterations, boolean multithreaded, Display disp, int dispUpdateFrequency) {
        for (int i = 1; i <= iterations; i++) {
            wander(multithreaded, i);
            updatePheromones();
            sortAnts();
            System.out.println(i + ": " + ants.get(0).path.getLength(parameters.metric));
            if (disp != null && i % dispUpdateFrequency == 0) {
                disp.updatePath(bestPath());
            }
        }
    }

    private void sortAnts() {
        ants.sort((a, b) -> a.getPath().getLength(parameters.metric) - b.getPath().getLength(parameters.metric));
    }

    public Path bestPath() {
        sortAnts();
        return ants.get(0).path;
    }

}
