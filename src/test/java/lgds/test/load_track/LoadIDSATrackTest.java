
package lgds.test.load_track;

import lgds.load_track.LoadIDSATrack;
import org.junit.Test;



/**
 * Created by alessandrozonta on 28/10/2016.
 */
public class LoadIDSATrackTest {
    @Test
    /**
     * Check if it loads everything without exception from the data set
     */
    public void loadTrajectories() throws Exception {
        LoadIDSATrack track = new LoadIDSATrack();
        track.loadTrajectories();
    }

    @Test
    /**
     * Test if the method is able to load a single line
     */
    public void loadTrajectory() throws Exception {

    }

}
