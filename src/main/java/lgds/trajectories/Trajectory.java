package lgds.trajectories;

import KalmanFilter.FixedLagSmoother;
import KalmanFilter.kalman_filter.StateVector;
import lgds.Distance.Distance;
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
    private FixedLagSmoother smoother; //Smoother System
    private boolean usingSmoother;


    /**
     * Default constructor
     * default behaviour, no smoother
     */
    public Trajectory(){
        this.points = new ArrayList<>();
        this.currentReadPosition = null;
        this.reachEndFile = Boolean.FALSE;
        this.smoother = null;
        this.usingSmoother = false;
    }

    /**
     * Constructor one parameter
     * If using or not the smoother system
     * @param usingSmoother boolean value if I am using the smoother or not
     */
    public Trajectory(boolean usingSmoother){
        this.points = new ArrayList<>();
        this.currentReadPosition = null;
        this.reachEndFile = Boolean.FALSE;
        this.smoother = null;
        this.usingSmoother = usingSmoother;
        if(usingSmoother){
            this.smoother = new FixedLagSmoother(2);
        }else{
            this.smoother = null;
        }
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
        if(this.usingSmoother) this.smoother.setInitialPosition(new KalmanFilter.Point.Point(firstPoint.getLatitude(), firstPoint.getLongitude()));
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
            try {
                point = this.points.get(this.currentReadPosition);
            }catch (Exception e){
                String a = "dsf";
                point = null;
            }
        }else{
            point = storage.loadTrajectory(this.path, this.currentReadPosition);
            this.points.add(point);
        }
        //am i using the smoother system?
        if(this.usingSmoother){
            Point smoothedPoint = null;
            try {
                this.smoother.smooth(point.getLatitude(), point.getLongitude()); //smooth the point
                StateVector x = this.smoother.getSmoothedPoint(); //get the new point
                if(x != null) smoothedPoint = new Point(x.getX(), x.getY(), point.getAltitude(), point.getDated(), point.getDates(), point.getTime()); //return the point smoothed
            } catch (Exception e) {
                //not smoothed since the windows hasn't reached yet
                if (!e.getMessage().equals("Not Smoothed")) {
                    //problem with the smoother
                    e.printStackTrace();
                }
            }
            if (smoothedPoint != null) point = smoothedPoint;
        }
        if(this.usingSmoother){
            if(this.currentReadPosition == this.points.size() - 1){
                this.reachEndFile = Boolean.TRUE;
                if(this.usingSmoother) this.smoother.setEnd();
            }
        }
        //check if I reach the end
        if (point.equals(this.lastPoint)){
            this.reachEndFile = Boolean.TRUE;
            if(this.usingSmoother) this.smoother.setEnd();
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


    /**
     * Compute the ratio between the number of step and the actual distance
     * @return the ratio of this trajectory
     */
    public Double computeRatio(){
        Integer numberOfStep = this.size;
        //actual distance of the trajectory
        Distance dis = new Distance();
        Double actualDistance = dis.compute(this.firstPoint, this.lastPoint);
        //Compute the ration between the distance and the number of steps
//        System.out.println("Distance " + actualDistance + " number of step " + numberOfStep + " ratio " + actualDistance / numberOfStep);
        return actualDistance / numberOfStep;
    }


    /**
     * Reset reading for the trajectory
     */
    public void resetReading(){
        this.points = new ArrayList<>();
        this.points.add(this.firstPoint);
        this.currentReadPosition = 0;
        this.reachEndFile = Boolean.FALSE;
    }

    /**
     * Reset the reading but not the points
     */
    public void softResetTrajectory(){
        this.currentReadPosition = 0;
        this.reachEndFile = Boolean.FALSE;
    }



}
