package lgds.load_track;

import org.junit.Test;

/**
 * Created by Alessandro Zonta on 07/02/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadTrackJsonTest {

    @Test
    public void loadTrajectories() {
        LoadTrackJson loader = new LoadTrackJson();
        loader.loadTrajectories();
    }

    @Test
    public void loadTrajectory() {
    }
}