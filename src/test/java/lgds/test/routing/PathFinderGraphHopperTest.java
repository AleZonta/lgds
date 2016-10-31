package lgds.test.routing;

import lgds.routing.PathFinderGraphHopper;
import lgds.trajectories.Point;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 31/10/2016.
 */
public class PathFinderGraphHopperTest {
    /**
     * Load GraphHopper for the path planning
     * @throws Exception
     */
    @Test
    public void load() throws Exception {
        PathFinderGraphHopper pf = new PathFinderGraphHopper();
        pf.load();
        assertNotNull(pf.getHopper());
    }

    /**
     * Method that return the total distance of the trajectory found
     * @throws Exception
     */
    @Test
    public void retTotalDistance() throws Exception {
        PathFinderGraphHopper pf = new PathFinderGraphHopper();
        pf.load();
        Point source = new Point(39.937887,116.433688);
        Point destination = new Point(39.923964,116.365135);
        pf.getDirection(source,destination);
        assertEquals(new Double(7845.504092335778), pf.retTotalDistance());
    }


    /**
     * Method that return the direction from source to destination
     * * @throws Exception
     */
    @Test
    public void getDirection() throws Exception {
        PathFinderGraphHopper pf = new PathFinderGraphHopper();
        pf.load();
        Point source = new Point(39.937887,116.433688);
        Point destination = new Point(39.923964,116.365135);
        pf.getDirection(source,destination);
        assertNotNull(pf.getRsp());
    }

}