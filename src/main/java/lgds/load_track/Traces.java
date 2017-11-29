package lgds.load_track;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;

/**
 * Created by alessandrozonta on 28/10/2016.
 * Interface for the class that will load the trace from file
 */
public interface Traces {

    /**
     * Scan the folder (location read from a file on the constructor) and load in memory all the trajectories
     * It calculates the root of the word and its height and width
     *
     * @return trajectories -> class containing all the trajectories loaded
     */
    Trajectories loadTrajectories();

    /**
     * Load a specific line of the trajectory from the file
     * @param path path of the file containing all the point of the trajectory
     * @param position position that we have already reached
     * @return the new point read from file
     */
    Point loadTrajectory(String path, Integer position);

}
