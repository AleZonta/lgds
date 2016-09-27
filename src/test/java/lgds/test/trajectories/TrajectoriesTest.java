package lgds.test.trajectories;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 27/09/16.
 */
public class TrajectoriesTest {
    @Test
    /**
     * test if I can add correctly one trajectory to the list
     * @throws Exception
     */
    public void addTrajectory() throws Exception {
        Trajectory tra = new Trajectory();
        tra.addPoint(new Point(0.0,0.0,0.0,0.0,"",""));
        tra.setFirstPoint(new Point(0.0,0.0,0.0,0.0,"",""));
        Trajectories trajectories = new Trajectories();
        trajectories.addTrajectory(tra);

        assertEquals(1,trajectories.getTrajectories().size());
        assertEquals(new Double(0.0),trajectories.getTrajectories().get(0).getFirstPoint().getLongitude());
        assertEquals(new Double(0.0),trajectories.getTrajectories().get(0).getFirstPoint().getLatitude());
    }

    @Test
    /**
     * Test if retrieve correctly the list of trajectories
     * @throws Exception
     */
    public void getTrajectories() throws Exception {
        Trajectory tra = new Trajectory();
        tra.addPoint(new Point(0.0,0.0,0.0,0.0,"",""));
        tra.setFirstPoint(new Point(0.0,0.0,0.0,0.0,"",""));
        Trajectories trajectories = new Trajectories();
        trajectories.addTrajectory(tra);

        assertEquals(1,trajectories.getTrajectories().size());
        assertEquals(new Double(0.0),trajectories.getTrajectories().get(0).getFirstPoint().getLongitude());
        assertEquals(new Double(0.0),trajectories.getTrajectories().get(0).getFirstPoint().getLatitude());
    }

    /**
     * Test if it computes correctly the of POIs
     * @throws Exception
     */
    @Test
    public void computePOIs() throws Exception {
        Trajectory tra = new Trajectory();
        tra.setLastPoint(new Point(0.0,0.0,0.0,0.0,"",""));
        Trajectory tra1 = new Trajectory();
        tra1.setLastPoint(new Point(1.0,1.0,1.0,1.0,"",""));
        Trajectories trajectories = new Trajectories();
        trajectories.addTrajectory(tra);
        trajectories.addTrajectory(tra1);

        trajectories.computePOIs(2);

        assertEquals(2,trajectories.getListOfPOIs().size());
        assertEquals(new Double(0.0),trajectories.getListOfPOIs().get(0).getLocation().getLatitude());
        assertEquals(new Double(1.0),trajectories.getListOfPOIs().get(1).getLocation().getLatitude());
    }

}
