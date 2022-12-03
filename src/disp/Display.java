package disp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Digraph.Edge;
import core.Digraph.Path;

public class Display {

    static final int MAP_WIDTH_FEET    = 5521; // Width in feet of map
    static final int MAP_HEIGHT_FEET   = 4369; // Height in feet of map
    static final int MAP_WIDTH_PIXELS  = 2528; // Width in pixels of map
    static final int MAP_HEIGHT_PIXELS = 2000; // Height in pixels of map
    
    static final int CROP_LEFT = 150; // Pixels cropped from left of map
    static final int CROP_DOWN = 125; // Pixels cropped from top of map

    List<Point> points = new ArrayList<Point>();
    JFrame frame;

    public Display() {
        frame = new JFrame();
        frame.add(new MapVisualizerPane(0.5f));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setTitle("Path Visualizer");
        frame.setVisible(true);
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
    
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
    
        return dimg;
    }  

    public void updatePath(Path path) {
        points.clear();
        for (Edge e : path.edges) {
            points.add(convertCoordinates(e.getSrc().getX(), e.getSrc().getY()));
            points.add(convertCoordinates(e.getDst().getX(), e.getDst().getY()));
        }
        frame.repaint();
    }

    public static Point convertCoordinates(int x, int y) {
        int x2 = (int) (x * ((double) MAP_HEIGHT_PIXELS / MAP_HEIGHT_FEET)) - CROP_LEFT;
        int y2 = (int) (y * ((double) MAP_WIDTH_PIXELS / MAP_WIDTH_FEET)) - CROP_DOWN;
        return new Point(x2, y2);
    }

    public class MapVisualizerPane extends JPanel {

        private BufferedImage image;
        private float scale;

        public MapVisualizerPane(float scale) {
            this.scale = scale;
            try {
                image = ImageIO.read(new File("resources/BrandeisMapLabeledCropped.jpg"));
                image = Display.resize(image, (int)(image.getWidth() * scale), (int)(image.getHeight() * scale));
            } catch (IOException ex) { ex.printStackTrace(); }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(image.getWidth(), image.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            g2d.drawImage(image, 0, 0, this);
            
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(4));
            for (int i = 0; i < points.size()-1; i+=2) {
                g2d.drawLine((int)(points.get(i).x * scale), (int)(points.get(i).y * scale), (int)(points.get(i+1).x * scale), (int)(points.get(i+1).y * scale));
            }
            g2d.dispose();
        }

    }

}