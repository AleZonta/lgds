package lgds.POI;

import lgds.trajectories.Point;
import org.apache.commons.math3.ml.clustering.Clusterable;

/**
 * Created by alessandrozonta on 31/10/2016.
 */
public class POI implements Clusterable {
    private Point location; //Location of the point of interest
    private double[] points; //Need for extend Clusterable class
    private Double charge; //charge of the POI

    /**
     * Constructor with one parameter
     * @param location location of the poi
     */
    public POI(Point location){
        this.location = location;
        this.points = new double[] { location.getLatitude(), location.getLongitude() };
        this.charge = 0.0;
    }

    /**
     * Construnctor with also the charge
     * @param location location of the POI
     * @param charge charge of the POI
     */
    public POI(Point location, Double charge){
        this.location = location;
        this.points = new double[] { location.getLatitude(), location.getLongitude() };
        this.charge = charge;
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

    /**
     * Return the Charge of the POI
     * @return the charge in Double value
     */
    public Double getCharge() {
        return charge;
    }

    /**
     * Set the charge of the POI
     * @param charge charge to set in Double
     */
    public void setCharge(Double charge) {
        this.charge = charge;
    }

    /**
     * override method equals
     * @param o object to compare with this POI
     * @return if the two POIs are the same or not
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Point)) {
            return false;
        }

        POI point = (POI) o;
        return this.location.equals(point.getLocation());
    }
}
