package lgds.test.viewer;

import lgds.trajectories.Point;
import lgds.viewer.View;
import org.junit.Test;

/**
 * Created by alessandrozonta on 31/01/2017.
 */
public class ViewTest {
    @Test
    public void addPointTrajectory() throws Exception {
        View v = new View();
        Point a = new Point(5d,5d);
        v.setAlpha(10d);
        Point b = new Point(6d,6d);
        v.addPointTrajectory(a);
        v.addPointTrajectory(b);
    }

    @Test
    /**
     * test if the method run works
     */
    public void showMap() throws Exception {
        View v = new View();
        v.showMap();
    }
}
