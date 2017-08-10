package lgds.trajectories;

import lgds.load_track.LoadIDSATrack;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by alessandrozonta on 01/08/2017.
 */
public class TrajectoriesTest {
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