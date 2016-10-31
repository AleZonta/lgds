package lgds.test.routing;

import com.google.maps.model.LatLng;
import lgds.routing.PathFinderGoogle;
import lgds.trajectories.Point;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 28/09/16.
 */
public class PathFinderGoogleTest {
    /**
     * test if it returns the list of the steps to go from source to destination
     * @throws Exception
     */
    @Test
    public void retDirectionStep() throws Exception {
        PathFinderGoogle pf = new PathFinderGoogle();
        assertNull(pf.retDirectionStep());
        LatLng sour = new LatLng(52.333636,4.869611);
        LatLng des = new LatLng(52.320891,4.875542);
        pf.getDirection(sour,des);
        assertNotNull(pf.retDirectionStep());
    }

    /**
     * Test if tit is able to return the distance
     * @throws Exception
     */
    @Test
    public void retTotalDistance() throws Exception {
        PathFinderGoogle pf = new PathFinderGoogle();
        assertNull(pf.retTotalDistance());
        LatLng sour = new LatLng(52.333636,4.869611);
        LatLng des = new LatLng(52.320891,4.875542);
        pf.getDirection(sour,des);
        assertEquals(new Double(2292.0), pf.retTotalDistance());
    }

    /**
     * Method to test if the service is able to find the direction from source to destination
     * @throws Exception
     */
    @Test
    public void getDirection() throws Exception {
        PathFinderGoogle pf = new PathFinderGoogle();
        assertNull(pf.getDirection());
        LatLng sour = new LatLng(52.333636,4.869611);
        LatLng des = new LatLng(52.320891,4.875542);
        pf.getDirection(sour,des);
        assertNotNull(pf.getDirection().routes);

        pf.getDirection(new Point(null,null),new Point(null,null));
        assertNull(pf.getDirection());
    }

    /**
     * This method test if it loads correctly everything
     * @throws Exception
     */
    @Test
    public void load() throws Exception {
        PathFinderGoogle pf = new PathFinderGoogle();
        assertNotNull(pf.getContext());
    }

}