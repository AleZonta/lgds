package lgds.viewer;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;


/**
 * Created by alessandrozonta on 31/01/2017.
 * Drow my personal point in the map
 */
public class CircleWaypointRenderer implements WaypointRenderer<MyWaypoint> {

    /**
     * Draw a circle in the position given by the caller
     * @param g graphics2D drawer in java
     * @param viewer viewer where to show the circles
     * @param w characteristics of the data
     */
    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer viewer, MyWaypoint w)
    {
        g = (Graphics2D)g.create();

        Point2D point = viewer.getTileFactory().geoToPixel(w.getPosition(), viewer.getZoom());

        //find position
        int x = (int)point.getX();
        int y = (int)point.getY();

        //Compute radius and diameter
        Double radius = w.getDiameter()/2;
        Double diameter = w.getDiameter();
        g.setColor(w.getColor());
        g.setStroke(new BasicStroke(1f));

        /* Enable anti-aliasing and pure stroke */
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        /* Construct a shape and draw it */
        Ellipse2D.Double shape = new Ellipse2D.Double(x - radius, y - radius, diameter, diameter);
        g.setColor(w.getColor());
        g.fill(shape);
        g.setColor(Color.black);
        g.draw(shape);

        //g.fillOval(x - radius.intValue(), y - radius.intValue(), diameter.intValue(), diameter.intValue());


        g.dispose();
    }

}
