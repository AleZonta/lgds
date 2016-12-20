package lgds.POI;

import lgds.trajectories.Point;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Created by alessandrozonta on 31/10/2016.
 */
public class POI implements Clusterable {
    private Point location; //Location of the point of interest
    private double[] points; //Need for extend Clusterable class

    /**
     * Constructor with one parameter
     * @param location location of the poi
     */
    public POI(Point location){
        this.location = location;
        this.points = new double[] { location.getLatitude(), location.getLongitude() };
    }

    /**
     * getter for the location
     * @return Point where this POI is located
     */
    public Point getLocation() {
        return location;
    }


    /**
     * Override method getPoint from Clusterable
     * @return vector double location
     */
    @Override
    public double[] getPoint() {
        return this.points;
    }
}
