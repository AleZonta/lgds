package lgds.simulator;

import lgds.POI.POI;
import lgds.load_track.LoadIDSATrack;
import lgds.load_track.LoadTrack;
import lgds.load_track.Traces;
import lgds.people.Agent;
import lgds.routing.PathFinderGraphHopper;
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
    private Traces storage; //class that loads the track from file
    private List<Agent> participant; //list of all the agents participating into the simulation
    private List<POI> listOfPOIs; //list of all the POIs
    private PathFinderGraphHopper pf; //graphHopper instance

    /**
     * default constructor
     */
    public Simulator(){
        this.storage = new LoadTrack();
//        this.storage = new LoadIDSATrack();
        this.participant = new ArrayList<>();
        this.listOfPOIs = new ArrayList<>();
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
        //load GraphHopper
        this.pf = new PathFinderGraphHopper();
        this.pf.load();

        //retrieve all the tracks from file
        Trajectories tra = this.storage.loadTrajectories();
        //shuffle it
        tra.shuffle();
        //load with the POIs
        tra.computePOIs(number);
        //this.listOfPOIs = tra.getListOfPOIs();
        //check ration trajectory
        tra.analiseAndCheckTrajectory();
        //checking if all the POIs are usable
        tra.getListOfPOIs().stream().forEach(poi -> {
            if(this.pf.isContained(poi.getLocation())){
                this.listOfPOIs.add(poi);
            }
        });
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


    /**
     * method to test if graphHopper works with our data
     */
    public void work(){
    //run the simulator
        while (this.participant.stream().filter(agent -> !agent.getDead()).toArray().length != 0) {
            this.participant.parallelStream().filter(agent -> !agent.getDead()).forEach(agent -> {
                System.out.println("Step agent..");
                agent.doStep();
                if(!agent.getDead()) {
                    this.listOfPOIs.stream().forEach(poi -> {
                        this.pf.getDirection(agent.getCurrentPosition(), poi.getLocation());
                        if(this.pf.retTotalDistance()!=null){
                            System.out.println(this.pf.retTotalDistance().toString());
                        }else{
                            System.out.println("null");
                        }

                    });
                }
            });
        }
    }

}
