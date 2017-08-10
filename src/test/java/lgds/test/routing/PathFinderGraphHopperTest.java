package lgds.test.routing;

import lgds.routing.PathFinderGraphHopper;
import lgds.trajectories.Point;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        //assertNotNull(pf.getHopper());
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
//        Point destination = new Point(39.923964,116.365135);
        Point destination = new Point(39.937262, 116.433695);
        pf.getDirection(source,source);
//        assertEquals(new Double(7845.504092335778), pf.retTotalDistance());
        assertNotNull(pf.getFirstWayPointOfTrajectory());
    }


    /**
     * Method that return the direction from source to destination
     * * @throws Exception
     */
    @Test
    public void getDirection() throws Exception {
//        PathFinderGraphHopper pf = new PathFinderGraphHopper();
//        pf.load();
//        Point source = new Point(39.927985, 116.319976);
//        Point destination = new Point(39.864819, 116.436964);
//        pf.getDirection(source,destination);
//        assertNotNull(pf.getRsp());
//        System.out.println(pf.getRsp());
//
//        String COMMA_DELIMITER = ",";
//        String NEW_LINE_SEPARATOR = "\n";
//        String FILE_HEADER = "id,lat,long";
//        FileWriter fileWriter = new FileWriter("direction.csv");
//        //Write the CSV file header
//        fileWriter.append(FILE_HEADER);
//        //Add a new line separator after the header
//        fileWriter.append(NEW_LINE_SEPARATOR);
//        for(int i = 0; i < pf.getRsp().getAll().get(0).getPoints().size(); i++){
//            fileWriter.append(String.valueOf(i));
//            fileWriter.append(COMMA_DELIMITER);
//            fileWriter.append(String.valueOf(pf.getRsp().getAll().get(0).getPoints().getLatitude(i)));
//            fileWriter.append(COMMA_DELIMITER);
//            fileWriter.append(String.valueOf(pf.getRsp().getAll().get(0).getPoints().getLongitude(i)));
//            fileWriter.append(NEW_LINE_SEPARATOR);
//        }
//        fileWriter.flush();
//        fileWriter.close();
//
//
//        //If point outside the map how can I check this?
//        source = new Point(0.0,0.0);
//        destination = new Point(0.0,0.0);
//        pf.getDirection(source,destination);
//        assertNull(pf.getRsp());
    }


    @Test
    public void isContained() throws Exception {
        PathFinderGraphHopper pf = new PathFinderGraphHopper();
        pf.load();
        Point source = new Point(39.937887,116.433688);
        assertEquals(Boolean.TRUE, pf.isContained(source));
        Point destination = new Point(13.370826, 104.853356);
        assertEquals(Boolean.FALSE, pf.isContained(destination));

    }
    
}