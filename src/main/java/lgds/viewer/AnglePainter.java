package lgds.viewer;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by alessandrozonta on 02/02/2017.
 * Class that draws the angle's lines
 */
public class AnglePainter implements Painter<JXMapViewer> {
    private Color color = Color.BLUE;
    private boolean antiAlias = true;
    private final GeoPosition firstPoint;
    private final GeoPosition secondPoint;
    private final GeoPosition root;

    /**
     * Constructor with the two points
     * @param firstPoint first point where to start drawing
     * @param secondPoint second point where to end drawing
     * @param root starting point for drawing
     */
    public AnglePainter(GeoPosition firstPoint, GeoPosition secondPoint, GeoPosition root){
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
        this.root = root;
    }

    @Override
    public void paint(Graphics2D g, JXMapViewer map, int w, int h)
    {
        g = (Graphics2D) g.create();

        // convert from viewport to world bitmap
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // do the drawing
        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(4));

        drawRoute(g, map);

        // do the drawing again
        g.setColor(color);
        g.setStroke(new BasicStroke(2));

        drawRoute(g, map);

        g.dispose();
    }

    /**
     * @param g the graphics object
     * @param map the map
     */
    private void drawRoute(Graphics2D g, JXMapViewer map)
    {
        // convert geo-coordinate to world bitmap pixel
        Point2D first = map.getTileFactory().geoToPixel(this.firstPoint, map.getZoom());
        Point2D second = map.getTileFactory().geoToPixel(this.secondPoint, map.getZoom());
        Point2D root = map.getTileFactory().geoToPixel(this.root, map.getZoom());


        g.drawLine( (int) root.getX(), (int) root.getY(), (int) second.getX(), (int) second.getY());
        g.drawLine( (int) root.getX(), (int) root.getY(), (int) first.getX(), (int) first.getY());

    }
}
