package lgds.map;

import lgds.trajectories.Point;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 09/08/2017.
 */
public class OsmosisLoaderTest {
    @Test
    public void obtainCoordinates() throws Exception {
        OsmosisLoader l = new OsmosisLoader();
        List<Point> p = l.obtainCoordinates();
        assertNotNull(p);
    }

    @Test
    public void readFileAndCreateDB() throws Exception {
        //OsmosisLoader l = new OsmosisLoader();
        //l.readFileAndCreateDB();
    }

    @Test
    public void obtainValues() throws Exception {
        OsmosisLoader l = new OsmosisLoader();
        List<String> result = l.obtainValues();
        assertNotNull(result);
        System.out.println(result.toString());
        assertEquals(90, result.size());
    }

    @Test
    public void findIfThereIsSomethingInPosition() throws Exception {
        OsmosisLoader l = new OsmosisLoader();
        String output = l.findIfThereIsSomethingInPosition(52.0937245, 5.1286816);
        assertEquals("amenity///parking", output);
        output = l.findIfThereIsSomethingInPosition(52.0937245, 5.1286817);
        assertEquals("null///null", output);
    }


}