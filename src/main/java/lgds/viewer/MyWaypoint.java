package lgds.viewer;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

import java.awt.*;

/**
 * Created by alessandrozonta on 31/01/2017.
 * extension of the way point to give them a color and a name
 */
public class MyWaypoint extends DefaultWaypoint {
    private String label;
    private Color color;
    private Double diameter = 10d;

    /**
     * Constructor of the class
     * @param label the text
     * @param color the color
     * @param coord the coordinate
     * @param diameter diameter circle
     */
    public MyWaypoint(String label, Color color, GeoPosition coord, Double diameter)
    {
        super(coord);
        this.label = label;
        this.color = color;
        this.diameter = diameter;
    }

    /**
     * @return the label text
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Set the colour of the point
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Set the label/name of the point
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }


    /**
     * Get diameter of the Point
     * @return double value = diameter
     */
    public Double getDiameter() {
        return diameter;
    }

    /**
     * Set diameter of the Point
     * @param diameter of the point in Double
     */
    public void setDiameter(Double diameter) {
        this.diameter = diameter;
    }
}
