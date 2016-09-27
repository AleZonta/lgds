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
    private Trajectories tra; //keep track of all the trajectories

    /**
     * default constructor
     */
    public Simulator(){
        this.storage = new LoadTrack();
        this.participant = new ArrayList<>();
        this.tra = null;
    }

    /**
     * getter for the agent participating at the simulation
     * @return list of agent that I am going to simulate
     */
    public List<Agent> getParticipant() {
        return participant;
    }

    /**
     * getter for all the trajectories
     * @return all the trajectories
     */
    @Override
    public Trajectories getTra() {
        return tra;
    }

    /**
     * Initialise the class loading all the tracks from file and building the list of Agent
     * @param number maximum number of agent to load
     */
    @Override
    public void init(Integer number){
        //retrieve all the tracks from file
        this.tra = this.storage.loadTrajectories();
        //shuffle it
        this.tra.shuffle();
        //now I am choosing only the first $number trajectories
        List<Trajectory> actualTrajectories = this.tra.getTrajectories().stream().limit(number).collect(Collectors.toList());
        //prepare the id of the agent
        List<Integer> id = new ArrayList<>();
        for(int i = 0; i < number; i++) id.add(i);
        //create the agents
        id.stream().forEach(integer -> this.participant.add(new Agent(integer, actualTrajectories.get(integer), this.storage)));
        //compute all the POIs
        this.tra.computePOIs(number);
    }


    /**
     * run the simulation. Move all the agents until all of them reach the end of their trajectory
     */
    @Override
    public void run(){
        //run the simulator
        while (this.participant.stream().filter(agent -> !agent.getDead()).toArray().length != 0) {
            this.participant.parallelStream().filter(agent -> !agent.getDead()).forEach(Agent::doStep);
        }
    }

}
