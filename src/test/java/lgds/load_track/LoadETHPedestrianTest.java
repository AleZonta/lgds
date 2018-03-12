package lgds.load_track;

import org.junit.Test;

/**
 * Created by Alessandro Zonta on 10/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadETHPedestrianTest {

    @Test
    public void loadTrajectories() {
        LoadETHPedestrian loader = new LoadETHPedestrian();
        loader.loadTrajectories();
    }

    @Test
    public void loadTrajectory() {
    }
}