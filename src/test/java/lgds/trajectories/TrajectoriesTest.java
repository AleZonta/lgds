package lgds.trajectories;

import lgds.load_track.LoadIDSATrack;
import lgds.load_track.LoadTrackJson;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 01/08/2017.
 */
public class TrajectoriesTest {
    @Test
    public void analiseSpeedTrajectories() throws Exception {
//        LoadIDSAJson track = new LoadIDSAJson();
//        Trajectories tra = track.loadTrajectories();

//        LoadTrackJson track0 = new LoadTrackJson();
//        Trajectories tra0 = track0.loadTrajectories();

//        LoadETHPedestrian track = new LoadETHPedestrian();
//        Trajectories tra = track.loadTrajectories();

        LoadTrackJson track = new LoadTrackJson();
        Trajectories tra = track.loadTrajectories();


        tra.analiseSpeedTrajectories(1,track);
    }

    @Test
    public void makeTrajectoriesSeemReal() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Trajectories tra = track.loadTrajectories();
        tra.makeTrajectoriesSeemReal(track);
    }

    @Test
    public void getUtmRoot() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Trajectories tra = track.loadTrajectories();
        assertNotNull(tra.getUtmRoot());
        System.out.println(tra.getUtmRoot());
        System.out.println(tra.getUtmRoot().getLatitude()+tra.getWhWorld().getLatitude());
        System.out.println(tra.getUtmRoot().getLongitude()+tra.getWhWorld().getLongitude());
    }

    @Test
    public void getWhWorld() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Trajectories tra = track.loadTrajectories();
        assertNotNull(tra.getWhWorld());
        System.out.println(tra.getWhWorld());
    }

    @Test
    public void createTrainingAndTest() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Trajectories tra = track.loadTrajectories();
        assertNotNull(tra.getTrajectories());
        tra.createTrainingAndTest();

    }

    @Test
    public void switchToTrain() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Trajectories tra = track.loadTrajectories();
        assertNotNull(tra.getTrajectories());
        Integer number = tra.getTrajectories().size();
        System.out.println(number);
        try{
            tra.switchToTrain();
        }catch (Exception e){
            assertEquals("No training set created!", e.getMessage());
        }
        tra.createTrainingAndTest();
        tra.switchToTrain();
        assertNotNull(tra.getTrajectories());
        Integer newNumber = tra.getTrajectories().size();
        assertNotEquals(newNumber,number);
        System.out.println(newNumber);
    }

    @Test
    public void switchToTest() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Trajectories tra = track.loadTrajectories();
        assertNotNull(tra.getTrajectories());
        Integer number = tra.getTrajectories().size();
        System.out.println(number);
        try{
            tra.switchToTest();
        }catch (Exception e){
            assertEquals("No training set created!", e.getMessage());
        }
        tra.createTrainingAndTest();
        tra.switchToTest();
        assertNotNull(tra.getTrajectories());
        Integer newNumber = tra.getTrajectories().size();
        assertNotEquals(newNumber,number);
        System.out.println(newNumber);
    }

}