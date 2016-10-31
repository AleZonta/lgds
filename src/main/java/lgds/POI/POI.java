package lgds.POI;

import lgds.trajectories.Point;

/**
 * Created by alessandrozonta on 31/10/2016.
 */
public class POI {
    private Point location; //Location of the point of interest

    /**
     * Constructor with one parameter
     * @param location location of the poi
     */
    public POI(Point location){
        this.location = location;
    }

    /**
     * getter for the location
     * @return Point where this POI is located
     */
    public Point getLocation() {
        return location;
    }

}
