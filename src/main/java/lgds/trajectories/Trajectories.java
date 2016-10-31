package lgds.trajectories;

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

    /**
     * Default constructor
     */
    public Trajectories(){
        this.trajectories = new ArrayList<>();
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
<<<<<<< HEAD
        Collections.shuffle(this.trajectories, new Random(10));
=======
        Collections.shuffle(this.trajectories);
>>>>>>> parent of c004689... Added POI and world dimensions
    }
}
