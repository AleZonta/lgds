package lgds.test.load_track;

import lgds.load_track.LoadTrack;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 23/09/16.
 */
public class LoadTrackTest  {
    @Test
    /**
     * Test for LoadTrack Method
     * Check if it loads everything without exception from the data set
     */
    public void loadTrajectories() throws Exception {
        LoadTrack storage = new LoadTrack();
        Trajectories tra = storage.loadTrajectories();

        assertEquals(18670, tra.getTrajectories().size());
    }

    @Test
    /**
     * Test if the method is able to load a single line
     */
    public void loadTrajectory() throws Exception {
        LoadTrack storage = new LoadTrack();
        Trajectories tra = storage.loadTrajectories();

        Point p = storage.loadTrajectory(tra.getTrajectories().get(0).getPath(),tra.getTrajectories().get(0).getCurrentReadPosition() + 1);

        Point q = new Point(39.984683,116.31845,492.0,39744.1202546296,"2008-10-23","02:53:10");

        assertEquals(p.getAltitude(),q.getAltitude());
        assertEquals(p.getDated(),q.getDated());
        assertEquals(p.getDates(),q.getDates());
        assertEquals(p.getLatitude(),q.getLatitude());
        assertEquals(p.getLongitude(),q.getLongitude());
        assertEquals(p.getTime(),q.getTime());
    }


}
