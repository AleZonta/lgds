package lgds.trajectories;

import lgds.POI.POI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class stores the collection of trajectories
 */
public class Trajectories {
    private List<Trajectory> trajectories; // all the trajectories
    private List<POI> listOfPOIs; //list of all the POIs
    private Point utmRoot; //root of the word
    private Point whWorld; // width and height of the word

    /**
     * Default constructor
     */
    public Trajectories(){
        this.trajectories = new ArrayList<>();
        this.listOfPOIs = new ArrayList<>();
    }

    /**
     * Add trajectory to the collection of trajectories
     * @param trajectory next trajectory
     */
    public void addTrajectory(Trajectory trajectory){
        this.trajectories.add(trajectory);
    }

    /**
     * getter for the trajectories field
     * @return the list with all the trajectories
     */
    public List<Trajectory> getTrajectories() {
        return trajectories;
    }

    /**
     * shuffle the list of trajectories so all the one that correspond at the same person are not all near each other
     * shuffle in a predictive way so repetetive repetition have always the same order
     */
    public void shuffle(){
        Collections.shuffle(this.trajectories, new Random(10));
    }

    /**
     * getter for the list of POIS
     * @return the list of POIs
     */
    public List<POI> getListOfPOIs() {
        return this.listOfPOIs;
    }

    /**
     * Compute the list of POIs from the trajectories.
     * Every destinations are POIs
     * @param number number of trace that I am considering
     */
    public void computePOIs(Integer number){
        this.trajectories.stream().limit(number).forEach(trajectory -> listOfPOIs.add(new POI(trajectory.getLastPoint())));
    }

    /**
     * set the root of the word and its dimension
     * @param utmRoot root coordinate of the origin of the word
     * @param whWorld width and height of the word
     */
    public void setRootAndWhWorld(Point utmRoot, Point whWorld){
        this.utmRoot = utmRoot;
        this.whWorld = whWorld;
    }

    /**
     * getter for the root coordinate
     * @return root coordinate
     */
    public Point getUtmRoot() {
        return utmRoot;
    }

    /**
     * getter for the dimension of the world
     * @return the dimension of the world in a Point object
     */
    public Point getWhWorld() {
        return whWorld;
    }
}
