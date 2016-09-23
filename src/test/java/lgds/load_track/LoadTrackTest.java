package lgds.load_track;

import lgds.trajectories.Trajectories;
import org.junit.Test;

/**
 * Created by alessandrozonta on 23/09/16.
 */
public class LoadTrackTest  {
    @Test
    /**
     * Test for LoadTrack Method
     * Check if it loads everything without exception from the data set
     */
    public void LoadTrack() throws Exception {
        LoadTrack storage = new LoadTrack();
        Trajectories tra = storage.loadTrajectories();

    }
}
