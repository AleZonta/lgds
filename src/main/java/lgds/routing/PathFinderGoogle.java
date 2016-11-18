package lgds.routing;


import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import lgds.config.ConfigFile;
import lgds.trajectories.Point;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.awt.SystemColor.text;

/**
 * Created by alessandrozonta on 28/09/16.
 * Class that loads the path finder system with the map of the place
 * It uses Google API
 */
public class PathFinderGoogle implements Routing {
    private GeoApiContext context; //context of the Google API
    private DirectionsResult direction; //the last direction found

    /**
     * Constructor that initialise the connection with Google API
     */
    public PathFinderGoogle(){
        this.load();
    }

    /**
     * getter for GeoApiContext
     * @return GeoApiContext
     */
    public GeoApiContext getContext() { return context; }

    /**
     * getter for the last direction found
     * @return The direction found
     */
    public DirectionsResult getDirection() {
        return direction;
    }

    /**
     * Load Google services for the path planning
     */
    @Override
    public void load(){
        // load config file
        ConfigFile conf = new ConfigFile();
        String apiKey = "";
        try {
            conf.loadFile();
            apiKey = conf.getGoogleAPIKey();
            this.context = new GeoApiContext().setApiKey(apiKey);
        } catch (Exception e){
            this.context = null;
        }

    }

    /**
     * Method that return the direction from source to destination
     * @param source source of the trajectory
     * @param destination destination point
     */
    public void getDirection(LatLng source, LatLng destination){
        try {
            this.direction = DirectionsApi.newRequest(this.context).destination(destination).origin(source).units(Unit.METRIC).mode(TravelMode.WALKING).await();
        } catch (Exception e) {
            this.direction = null;
        }
    }

    /**
     * Method that return the total distance of the trajectory found
     * @return Double value of the distance in metres
     */
    @Override
    public Double retTotalDistance(){
        if(this.direction != null) {
            return (double) this.direction.routes[0].legs[0].distance.inMeters;
        }
        return null;
    }

    /**
     * Return the step to go from source to destination of the latest found trajectory
     * @return List<DirectionsStep>
     */
    public List<DirectionsStep> retDirectionStep(){
        if(this.direction != null) {
            DirectionsStep[] steps = this.direction.routes[0].legs[0].steps;
            List<DirectionsStep> stepList = new ArrayList<>();
            for (int i = 0; i < steps.length; i++) {
                stepList.add(steps[i]);
            }
            return stepList;
        }
        return null;
    }

    /**
     * Method that return the direction from source to destination
     * @param latSource lat source of the trajectory
     * @param lngSource lng source of the trajectory
     * @param latDest lat destination of the trajectory
     * @param lngDest lng destination of the trajectory
     */
    public void getDirection(Double latSource, Double lngSource, Double latDest, Double lngDest){
        LatLng sour = new LatLng(latSource,lngSource);
        LatLng des = new LatLng(latDest,lngDest);
        this.getDirection(sour, des);
    }

    /**
     * Method that return the direction from source to destination
     * @param source source for the trajectory
     * @param destination destination of the trajectory
     */
    @Override
    public void getDirection(Point source, Point destination){
        LatLng sour = new LatLng(source.getLatitude(), source.getLongitude());
        LatLng des = new LatLng(destination.getLatitude(), destination.getLongitude());
        this.getDirection(sour, des);
    }

    /**
     * Return the center point of the trajectory
     * Hack for idsa
     * @return lgds Point
     */
    @Override
    public Point getFirstWayPointOfTrajectory(){
        List<DirectionsStep> res = this.retDirectionStep();
        if(res != null){
            return new Point(res.get(res.size() / 2).startLocation.lat,res.get(res.size() / 2).startLocation.lng);
        }else{
            return null;
        }
    }

    /**
     * Check if the POI is included into the boundary
     */
    @Override
    public Boolean isContained(Point point){
        return Boolean.TRUE;
    }

}
