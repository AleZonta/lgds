package lgds.load_track;

import org.junit.Test;

/**
 * Created by Alessandro Zonta on 20/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadUCYDatasetTest {

    @Test
    public void loadTrajectories() {
        LoadUCYDataset loader = new LoadUCYDataset();
        loader.loadTrajectories();
    }
}