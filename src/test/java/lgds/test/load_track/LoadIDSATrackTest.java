
package lgds.test.load_track;

import lgds.load_track.LoadIDSATrack;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Created by alessandrozonta on 28/10/2016.
 */
public class LoadIDSATrackTest {
    @Test
    public void convertFromUTMtoDeg() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Point randomPointOnTheMap = new Point(2629.43, 4165.63);
        Point root = new Point(588636.0471215437, 5763563.815469695);
        Point result = track.convertFromUTMtoDeg(randomPointOnTheMap,root);
        Point expected = new Point(52.0526439, 4.3310119);
        assertEquals(expected.getLatitude(), result.getLatitude());
        assertEquals(expected.getLongitude(), result.getLongitude());
    }

    @Test
    /**
     * Check if it loads everything without exception from the data set
     */
    public void loadTrajectories() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        Trajectories tra = track.loadTrajectories();
        assertNotNull(tra.getTrajectories());

    }

    @Test
    /**
     * Test if the method is able to load a single line
     */
    public void loadTrajectory() throws Exception {

    }

}
