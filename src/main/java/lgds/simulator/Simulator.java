package lgds.simulator;

import lgds.load_track.LoadTrack;
import lgds.people.Agent;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alessandrozonta on 26/09/16.
 * This class simulate the movement of the person
 */
public class Simulator implements SimulatorInterface {
    private LoadTrack storage; //class that loads the track from file
    private List<Agent> participant; //list of all the agents participating into the simulation

    /**
     * default constructor
     */
    public Simulator(){
        this.storage = new LoadTrack();
        this.participant = new ArrayList<>();
    }

    /**
     * getter for the agent participating at the simulation
     * @return list of agent that I am going to simulate
     */
    public List<Agent> getParticipant() {
        return participant;
    }

    /**
     * Initialise the class loading all the tracks from file and building the list of Agent
     * @param number maximum number of agent to load
     */
    public void init(Integer number){
        //retrieve all the tracks from file
        Trajectories tra = this.storage.loadTrajectories();
        //shuffle it
        tra.shuffle();
        //now I am choosing only the first $number trajectories
        List<Trajectory> actualTrajectories = tra.getTrajectories().stream().limit(number).collect(Collectors.toList());
        //prepare the id of the agent
        List<Integer> id = new ArrayList<>();
        for(int i = 0; i < number; i++) id.add(i);
        //create the agents
        id.stream().forEach(integer -> this.participant.add(new Agent(integer, actualTrajectories.get(integer), this.storage)));
    }


    /**
     * run the simulation. Move all the agents until all of them reach the end of their trajectory
     */
    public void run(){
        //run the simulator
        while (this.participant.stream().filter(agent -> !agent.getDead()).toArray().length != 0) {
            this.participant.parallelStream().filter(agent -> !agent.getDead()).forEach(Agent::doStep);
        }
    }

}
