package core;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GraphBuilder {

    public static Digraph buildGraphFromFiles(File vertexFile, File edgeFile, boolean includeDebug) throws FileNotFoundException {
        Digraph dg = new Digraph();
        try (Scanner vfScanner = new Scanner(vertexFile)) {
            while (vfScanner.hasNextLine()) {
                String line = vfScanner.nextLine().trim();
                if (line.length() == 0 || line.startsWith("//")) {
                    continue;
                }

                if (line.startsWith("[DEBUG] ")) {
                    if (includeDebug) {
                        line = line.substring(8); // trim the debug flag
                    } else {
                        continue;
                    }
                }

                Scanner lineScanner = new Scanner(line);

                int id = lineScanner.nextInt();
                String label = lineScanner.next();
                int x = lineScanner.nextInt();
                int y = lineScanner.nextInt();
                String name = lineScanner.nextLine().replaceAll("\"", "").trim(); // remove quotes

                lineScanner.close();

                dg.addVertex(name, id, label, x, y);
            }
        }

        try (Scanner efScanner = new Scanner(edgeFile)) {
            while (efScanner.hasNextLine()) {
                String line = efScanner.nextLine().trim();
                if (line.length() == 0 || line.startsWith("//")) {
                    continue;
                }

                if (line.startsWith("[DEBUG] ")) {
                    if (includeDebug) {
                        line = line.substring(8); // trim the debug flag
                    } else {
                        continue;
                    }
                }

                Scanner lineScanner = new Scanner(line);

                int id = lineScanner.nextInt(); // edge id
                lineScanner.next(); // vertex label 1
                lineScanner.next(); // vertex label 2
                int srcId = lineScanner.nextInt(); // source vertex id
                int dstId = lineScanner.nextInt(); // destination vertex id
                int length = lineScanner.nextInt();
                int angle = lineScanner.nextInt();
                String dir = lineScanner.next();
                char edgeType = lineScanner.next().charAt(1);
                if (edgeType == 'x') {
                    edgeType = 'F';
                }
                String name = lineScanner.nextLine().replaceAll("\"", "").trim(); // remove quotes
                
                lineScanner.close();

                dg.addEdge(dg.getVertex(srcId), dg.getVertex(dstId), length, name, id, angle, dir, edgeType);
            }
        }

        return dg;
    }

}
