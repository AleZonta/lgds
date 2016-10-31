package lgds.routing;


import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.*;

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
 */
public class PathFinder {
    private GeoApiContext context; //context of the Google API
    private DirectionsResult direction; //the last direction found

    /**
     * Constructor that initialise the connection with Google API
     */
    public PathFinder(){
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
    private void load(){
        String path = Paths.get(".").toAbsolutePath().normalize().toString() + "/config.conf";
        BufferedReader brTest = null;
        String apiKey = "";
        try {
            brTest = new BufferedReader(new FileReader(path));
            apiKey = brTest.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.context = new GeoApiContext().setApiKey(apiKey);
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


}
