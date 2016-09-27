package lgds.simulator;

import lgds.trajectories.Trajectories;

/**
 * Created by alessandrozonta on 27/09/16.
 * Interface for the simulator
 */
public interface SimulatorInterface {

    /**
     * getter for all the trajectories
     * @return all the trajectories
     */
    Trajectories getTra();

    /**
     * Initialise the class loading all the tracks from file and building the list of Agent
     * @param number maximum number of agent to load
     */
    void init(Integer number);

    /**
     * run the simulation. Move all the agents until all of them reach the end of their trajectory
     */
    void run();
}
