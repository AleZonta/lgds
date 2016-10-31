package lgds.people;

import lgds.load_track.LoadTrack;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;

/**
 * Created by alessandrozonta on 26/09/16.
 * This class will implement the agent in our simulation
 */
public class Agent implements AgentInterface{
    private final Integer id; //Id of the agent
    private final Trajectory trajectory; //Trajectory of the agent that has to follow
    private Point currentPosition; //Current position of the agent
    private LoadTrack storage; //agent needs this to load his next position
    private Boolean dead; //true if it ends to move on the trajectory

    /**
     * Constructor with tho parameters. It builds the class with an Id and his trajectory
     * @param id agent's id
     * @param trajectory agent's trajectory that it will follow
     * @param storage class used to load the next position
     */
    public Agent(Integer id, Trajectory trajectory, LoadTrack storage){
        this.id = id;
        this.trajectory = trajectory;
        this.currentPosition = null;
        this.storage = storage;
        this.dead = Boolean.FALSE;
    }

    /**
     * move the agent to the next position
     */
    public void doStep(){
        //retrieve next point
        this.currentPosition = this.trajectory.getNextPoint(this.storage);
        if(this.currentPosition == null){
            //ended the trajectory
            this.dead = Boolean.TRUE;
        }
    }

    /**
     * getter for the field that is indicating if the agent is still alive
     * @return false if it is still alive or true if it ends to follow the trajectory
     */
    public Boolean getDead() {
        return dead;
    }
}
