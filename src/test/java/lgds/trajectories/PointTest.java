package lgds.trajectories;

import org.junit.Test;

/**
 * Created by Alessandro Zonta on 24/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class PointTest {
    @Test
    public void differenceInTime() throws Exception {
        Point a = new Point(39.9766333333333,116.3378,278.871391076115,39303.0250694444,"2007-08-09","00:36:06");
        Point b = new Point(39.97655,116.337916666667,278.871391076115,39303.0285763889,"2007-08-09","00:41:09");
    }

}