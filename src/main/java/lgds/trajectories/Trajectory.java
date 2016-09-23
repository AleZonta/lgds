package lgds.trajectories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class contains a single trajectory
 * Keeping in memory the entire trajectory is too difficult in term of memory so i keep only the first point, the last point and the path to the sourse so i can retrieve it dynamically
 */
public class Trajectory {
    private List<Point> points; // all the points of the trajectories
    private Point firstPoint; //first point of the track
    private Point lastPoint; //last point of the track
    private String path; //path where to find this trajectory
    private Integer size; //size of the trajectory

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

    /**
     * getter for first point of the trajectory
     * @return the first point
     */
    public Point getFirstPoint() { return this.firstPoint; }

    /**
     * setter for the first point of the trajectory
     * @param firstPoint the first point
     */
    public void setFirstPoint(Point firstPoint) { this.firstPoint = firstPoint; }

    /**
     * getter for last point of the trajectory
     * @return the last point
     */
    public Point getLastPoint() { return this.lastPoint; }

    /**
     * setter for the last point of the trajectory
     * @param lastPoint the last point
     */
    public void setLastPoint(Point lastPoint) { this.lastPoint = lastPoint; }

    /**
     * getter for the path where to find of the trajectory
     * @return the path pointing where the trajectory is
     */
    public String getPath() { return this.path; }

    /**
     * setter for the path where to find of the trajectory
     * @param path the path pointing where the trajectory is
     */
    public void setPath(String path) { this.path = path; }

    /**
     * getter for the size of the trajectory
     * @return the size of the trajectory
     */
    public Integer getSize() { return this.size; }

    /**
     * setter for the size of the trajectory
     * @param size the size of the trajectory
     */
    public void setSize(Integer size) { this.size = size; }
}
