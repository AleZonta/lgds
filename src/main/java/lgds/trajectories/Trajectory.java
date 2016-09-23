package lgds.trajectories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class contains a single trajectory
 */
public class Trajectory {
    private List<Point> points; // all the points of the trajectories

    /**
     * Default constructor
     */
    public Trajectory(){
        this.points = new ArrayList<>();
    }

    /**
     * Add point to the trajectory
     * @param point next point of the trajectory
     */
    public void addPoint(Point point){
        this.points.add(point);
    }

}
