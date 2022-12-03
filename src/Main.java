import java.io.File;

import core.Colony;
import core.Colony.Parameters;
import core.Digraph;
import core.GraphBuilder;
import core.Utils;
import core.Utils.Metric;
import disp.Display;

public class Main {
    public static void main(String[] args) throws Exception {
        Utils.startTimer();
        
        Digraph dg = GraphBuilder.buildGraphFromFiles(new File("mapdata/vertices.txt"), new File("mapdata/edges.txt"), false);
        
        Parameters parameters = new Parameters(Metric.WALK_DISTANCE, 1f, 0.8f, 0.1f,3f,3f, 2);
        Colony colony = new Colony(dg, parameters);
        colony.addAnts(1024);
        colony.learn(20, true, new Display(), 1);
        
        Utils.exportRoute(colony.bestPath());
    }
}
