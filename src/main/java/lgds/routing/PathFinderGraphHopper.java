package lgds.routing;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.BBox;
import lgds.Distance.Distance;
import lgds.config.ConfigFile;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    private BBox bounds; //Bounds of the map
    private Point destination; //source of the place

    /**
     * constructor of the class
     * Set the paths for the folder and the osmFile
     */
    public PathFinderGraphHopper(){
        // load config file
        ConfigFile conf = new ConfigFile();
        try {
            conf.loadFile();
            this.osmFile = new File(conf.getGraphHopperPath(), conf.getGraphHopperName());
            this.graphLocation = new File(conf.getGraphHopperPath(),"osm");
        } catch (Exception e){
            this.osmFile = null;
            this.graphLocation = null;
        }
        this.hopper = null;
        this.rsp = null;
        this.destination = null;
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

        //retrieve bounds of the loaded maps
        this.bounds = this.hopper.getGraphHopperStorage().getBaseGraph().getBounds();
    }

    /**
     * Method that return the total distance of the trajectory found
     * @return Double value of the distance in metres
     */
    @Override
    public Double retTotalDistance() {
        try {
            if (this.rsp == null){
                return null;
            }
            return this.rsp.getBest().getDistance();
        }catch (Exception e){

            return 999999.0;

        }
    }

    /**
     * Method that return the direction from source to destination
     * @param source source for the trajectory
     * @param destination destination of the trajectory
     */
    @Override
    public void getDirection(Point source, Point destination) {
        //Check if source and destination are inside the bounds of the maps
        if (this.bounds.contains(source.getLatitude(), source.getLongitude())) {
            if (this.bounds.contains(destination.getLatitude(), destination.getLongitude())) {
                this.destination = destination;
                // simple configuration of the request object, see the GraphHopperServlet classs for more possibilities.
                GHRequest req = new GHRequest(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude()).
                        setWeighting("fastest").
                        setVehicle("car").
                        setLocale(Locale.US);
                this.rsp = this.hopper.route(req);

                // first check for errors
                if (this.rsp.hasErrors()) {
                    //I know two different errors
                    //Connection between locations not found
                    //Cannot find point
                    //Compute distance with Distance Measure
                    Distance dis = new Distance();
                    PathWrapper wr = new PathWrapper();
                    wr.setDistance(99999999.0);
                    PointList list = new PointList();
                    list.add(0.0,0.0);
                    list.add((source.getLatitude() + destination.getLatitude())/2,(source.getLongitude() + destination.getLongitude())/2);
                    list.add(0.0,0.0);
                    wr.setPoints(list);
                    this.rsp = new GHResponse();
                    this.rsp.add(wr);
                }
            } else {
                this.rsp = null;
            }
        } else {
            this.rsp = null;
        }
    }



    /**
     * Return the center point of the trajectory
     * Hack for idsa
     * @return lgds Point
     */
    @Override
    public Point getFirstWayPointOfTrajectory(){
        //check how long is the trajectory
        if(this.rsp.getBest().getPoints().size() >= 2){
            //return the first way point. The first point is the start of the trajectory, second point is the one that we need
            //also if it has only two points this code is correct. I return the destination
            try {
                return new Point(this.rsp.getBest().getPoints().getLatitude(1), this.rsp.getBest().getPoints().getLongitude(1));
            }catch (Exception s){
                return this.destination;
            }
        }else{
            //should be impossible to have less than two point
            return this.destination;
        }
    }


    /**
     * Return the entire trajectory
     * @return List<Point>
     */
    public List<Point> getTrajectory(){
        try {
            List<Point> listPoint = new ArrayList<>();
            this.rsp.getBest().getPoints().forEach(ghPoint3D -> listPoint.add(new Point(ghPoint3D.getLat(), ghPoint3D.getLon())));
            return listPoint;
        }catch (Exception s){
            return null;
        }

    }

    /**
     * Check if the POI is included into the boundary
     */
    @Override
    public Boolean isContained(Point point){
        if( this.bounds.contains(point.getLatitude(), point.getLongitude())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}

