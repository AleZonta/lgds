package lgds.routing;


import lgds.trajectories.Point;

/**
 * Created by alessandrozonta on 31/10/2016.
 * Interface for routing services
 */
public interface Routing {

    /**
     * Method that loads the service
     */
    void load();

    /**
     * Method that compute the direction
     * @param source point from start the direction
     * @param destination point of the destination
     */
    void getDirection(Point source, Point destination);

    /**
     * Method that return the total distance of the trajectory found
     * @return Double value of the distance in metres
     */
    Double retTotalDistance();

    /**
    * Return the center point of the trajectory
    */
    Point getCenterPointOfTrajectory();

    /**
     * Check if the POI is included into the boundary
     */
    Boolean isContained(Point point);

}
