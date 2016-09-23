package lgds.trajectories;

import java.util.ArrayList;
import java.util.List;

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

}
