package lgds.load_track;

import org.junit.Test;

/**
 * Created by Alessandro Zonta on 17/01/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadIDSAJsonTest {

    @Test
    public void loadTrajectories() {
        LoadIDSAJson loader = new LoadIDSAJson();
        loader.loadTrajectories();
    }

    @Test
    public void loadTrajectory() {
    }
}