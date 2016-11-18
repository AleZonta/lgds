package lgds.trajectories;

import lgds.load_track.LoadTrack;
import lgds.load_track.Traces;

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
    private Integer currentReadPosition; //current position reached reading the file with the trajectory
    private Boolean reachEndFile; //have I reached the end of the file?
    private Boolean fullLoad; //If true the trajectory is full loaded otherwise not

    /**
     * Default constructor
     */
    public Trajectory(){
        this.points = new ArrayList<>();
        this.currentReadPosition = null;
        this.reachEndFile = Boolean.FALSE;
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
    public void setFirstPoint(Point firstPoint) {
        this.firstPoint = firstPoint;
        this.points.add(this.firstPoint);
        this.currentReadPosition = 0;
    }

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

    /**
     * load next position
     * @return the Point with the next position
     * @param storage storage class that read the info from file. It is null if I reached the end of the track
     */
    public Point getNextPoint(Traces storage){
        //If there is no point anymore I have to signal that I ended my job
        if(this.reachEndFile) return null;
        this.currentReadPosition++;
        //if fullLoad is true I do not need to load from file but only read next position
        Point point;
        if (this.fullLoad){
            point = this.points.get(this.currentReadPosition);
        }else{
            point = storage.loadTrajectory(this.path, this.currentReadPosition);
            this.points.add(point);
        }
        //check if I reach the end
        if (point.equals(this.lastPoint)){
            this.reachEndFile = Boolean.TRUE;
        }
        return point;
    }

    /**
     * getter for the current position
     * @return the last position read
     */
    public Integer getCurrentReadPosition() {
        return currentReadPosition;
    }

    /**
     * setter for fullLoad
     * @param fullLoad True if The trajectory is loaded entirely. False if it will be incremental loading
     */
    public void setFullLoad(Boolean fullLoad) {
        this.fullLoad = fullLoad;
    }
}
