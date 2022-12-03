package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import disp.Display;

public class Utils {

    
    public enum Metric {
        WALK_DISTANCE, WALK_TIME, SKATE_DISTANCE, SKATE_TIME
    }
    
    public static boolean canSkate(Digraph.Edge e) { 
        return e.getEdgeType() == Character.toUpperCase(e.getEdgeType()); 
    }
    
    private static final int WALK_SPEED = 272; // feet per minute walking on flat ground
    
    public static int computeCost(Digraph.Edge e, Metric metric) {
        char et = e.getEdgeType();
        switch (metric) {
            case WALK_DISTANCE: // fall through
            case SKATE_DISTANCE: return e.getLength();
            case WALK_TIME: et = Character.toLowerCase(et);
            case SKATE_TIME: break;
        }
        switch (et) {
            case 'F': return (int)(e.getLength() * WALK_SPEED * 2.0);
            case 'U': return (int)(e.getLength() * WALK_SPEED * 1.1);
            case 'D': return (int)(e.getLength() * WALK_SPEED * 5.0);
            case 'f': return (int)(e.getLength() * WALK_SPEED * 1.0);
            case 'u': return (int)(e.getLength() * WALK_SPEED * 0.9);
            case 'd': return (int)(e.getLength() * WALK_SPEED * 1.1);
            case 's': return (int)(e.getLength() * WALK_SPEED * 0.5);
            case 't': return (int)(e.getLength() * WALK_SPEED * 0.9);
            case 'b': return (int)(e.getLength() * WALK_SPEED * 1.0);
            default: throw new IllegalStateException("illegal edge type code '" + e.getEdgeType() + "'");
        }
    }
    
    public static int pathCost(List<Digraph.Edge> path, Metric metric) {
        int cost = 0;
        for (Digraph.Edge e : path) {
            cost += computeCost(e, metric);
        }
        return cost;
    }
    
    public static void exportRoute(Digraph.Path path) {
        try(
            PrintWriter uncropped = new PrintWriter(new File("output/uncropped.txt"));
            PrintWriter cropped = new PrintWriter(new File("output/cropped.txt"))
            ) {
                
                int mapWidthFeet    = 5521; /*Width in feet of map.*/
                int mapHeightFeet   = 4369; /*Height in feet of map.*/
                int mapWidthPixels  = 2528; /*Width in pixels of map.*/
                int mapHeightPixels = 2000; /*Height in pixels of map.*/
                
                int cropLeft = 150; /*Pixels cropped from left of map.*/
                int cropDown = 125; /*Pixels cropped from top of map.*/
                
                int v, w, x, y;
                int a, b, c, d;
                
                for (Digraph.Edge edge : path.edges) {
                    v = edge.getSrc().getX();
                    w = edge.getSrc().getY();
                    x = edge.getDst().getX();
                    y = edge.getDst().getY();
                    
                    a = (int) (v * ((double) mapHeightPixels / mapHeightFeet));
                    b = (int) (w * ((double) mapWidthPixels  / mapWidthFeet));
                    c = (int) (x * ((double) mapHeightPixels / mapHeightFeet));
                    d = (int) (y * ((double) mapWidthPixels  / mapWidthFeet));
                    
                    uncropped.println(a + " " + b + " " + c + " " + d);
                    
                    a -= cropLeft;
                    b -= cropDown;
                    c -= cropLeft;
                    d -= cropDown;

                    cropped.println(a + " " + b + " " + c + " " + d);
                }
                
            } catch(FileNotFoundException e) { }
        }
        
        public static void exportAndDisplayRoute(Digraph.Path path) {
            exportRoute(path);
            new Display();
        }
        
        private static long timer;
        public static void startTimer() {
            timer = System.currentTimeMillis();
        }
        public static long getTimer() {
            return System.currentTimeMillis() - timer;
        }
    }
    