package lgds.simulator;

import lgds.people.Agent;

import java.util.List;

/**
 * Created by alessandrozonta on 27/09/16.
 * Interface for the simulator
 */
public interface SimulatorInterface {

    /**
     * getter for the agent participating at the simulation
     * @return list of agent that I am going to simulate
     */
    List<Agent> getParticipant();

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
