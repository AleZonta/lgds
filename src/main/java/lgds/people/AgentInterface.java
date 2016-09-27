package lgds.people;

import lgds.load_track.LoadTrack;
import lgds.trajectories.Trajectory;

/**
 * Created by alessandrozonta on 27/09/16.
 * Interface for the agent
 */
public interface AgentInterface {

    /**
     * move the agent to the next position
     */
    void doStep();

    /**
     * getter for the field that is indicating if the agent is still alive
     * @return false if it is still alive or true if it ends to follow the trajectory
     */
    Boolean getDead();
}
