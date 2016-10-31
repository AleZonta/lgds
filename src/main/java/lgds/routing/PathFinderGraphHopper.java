package lgds.routing;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import lgds.trajectories.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Created by alessandrozonta on 31/10/2016.
 * Class that loads the path finder system with the map of the place
 * It uses GraphHopper
 */
public class PathFinderGraphHopper implements Routing{
    private File osmFile; //location of the osm file
    private File graphLocation; //location of graphHopper
    private GraphHopper hopper; //Instance for the path finder
    private GHResponse rsp; //Response for the path


    /**
     * constructor of the class
     * Set the paths for the folder and the osmFile
     */
    public PathFinderGraphHopper(){
        String path = Paths.get(".").toAbsolutePath().normalize().toString() + "/source.conf";
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(path));
            String source = brTest.readLine();
            this.osmFile = new File(source,"beijing_china.osm.pbf");
            this.graphLocation = new File(source,"osm");
        } catch (IOException e) {
            this.osmFile = null;
            this.graphLocation = null;
        }
        this.hopper = null;
        this.rsp = null;
    }

    /**
     * Getter for the instance of GraphHopper
     */
    public GraphHopper getHopper() { return this.hopper; }

    /**
     * Getter for the response
     */
    public GHResponse getRsp() { return this.rsp; }

    /**
     * Load GraphHopper for the path planning
     */
    @Override
    public void load() {
        // create singleton
        this.hopper = new GraphHopper().forServer();
        this.hopper.setOSMFile(this.osmFile.getAbsolutePath());
        // where to store graphhopper files?
        this.hopper.setGraphHopperLocation(this.graphLocation.getAbsolutePath());
        this.hopper.setEncodingManager(new EncodingManager("car"));
        // now this can take minutes if it imports or a few seconds for loading
        // of course this is dependent on the area you import
        this.hopper.importOrLoad();
    }

    /**
     * Method that return the total distance of the trajectory found
     * @return Double value of the distance in metres
     */
    @Override
    public Double retTotalDistance() {
        return this.rsp.getBest().getDistance();
    }

    /**
     * Method that return the direction from source to destination
     * @param source source for the trajectory
     * @param destination destination of the trajectory
     */
    @Override
    public void getDirection(Point source, Point destination) {
        // simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
        GHRequest req = new GHRequest(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude()).
                setWeighting("fastest").
                setVehicle("car").
                setLocale(Locale.US);
        this.rsp = this.hopper.route(req);

        // first check for errors
        if(rsp.hasErrors()) {
            // handle them!
            this.rsp = null;
        }
    }

    /**
     * Return the center point of the trajectory
     * Hack for idsa
     * @return lgds Point
     */
    public Point getCenterPointOfTrajectory(){
        return new Point(this.rsp.getBest().getPoints().getLatitude(this.rsp.getBest().getPoints().size()/2),this.rsp.getBest().getPoints().getLongitude(this.rsp.getBest().getPoints().size()/2));
    }
}
