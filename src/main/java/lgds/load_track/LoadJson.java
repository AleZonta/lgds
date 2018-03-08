package lgds.load_track;

import lgds.POI.POI;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessandro Zonta on 07/02/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public abstract class LoadJson implements Traces{

    /**
     * read the json file and create the trajectories
     * @param source where to read the json file
     * @param limitation am I using the limitation method
     * @param allowedTrajectories list of trajectories ID I am allowed to load
     * @param minValue {@link Point} min coordinates value
     * @param maxValue {@link Point} max coordinates value
     * @param trajectories {@link Trajectory} class containing all the trajectories
     */
    protected void readFile(String source, boolean limitation, List<String> allowedTrajectories, Point minValue, Point maxValue,Trajectories trajectories, boolean avoid){
        //file is a json file, need to parse it and than I can read it
        FileReader reader;
        try {
            reader = new FileReader(source);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);


            Integer t = ((Long) jsonObject.get("size")).intValue();

            for(int i = 0; i < t; i ++){
                String name = "trajectory-" + i;


                boolean cont = false;
                if (!limitation || limitation && allowedTrajectories.stream().anyMatch(n -> n.equals(name)) && !avoid) cont = true;
                if(avoid && allowedTrajectories.stream().noneMatch(n -> n.equals(name))) cont = true;

                if (cont) {
                    Trajectory trajectory = new Trajectory();
                    trajectory.setPath(name);
                    //set full loaded to true
                    trajectory.setFullLoad(Boolean.TRUE);


                    JSONArray tra = (JSONArray) ((JSONObject) jsonObject.get(name)).get("trajectory");

                    List<Point> allThePoints = new ArrayList<>();

                    boolean removeLimitation = false;
                    for (JSONArray aTra : (Iterable<JSONArray>) tra) {
                        Point p;
                        if(aTra.size() == 2) {
                            p = new Point((Double) aTra.get(0), (Double) aTra.get(1));
                        }else{
                            p = new Point((Double) aTra.get(0), (Double) aTra.get(1), (Double) aTra.get(2), (Double) aTra.get(3), (String) aTra.get(4), (String)aTra.get(5));
                            //if is Geolife I don't need the 15 limitation
                            removeLimitation = true;
                        }
                        minValue.setLatitude(Math.min(minValue.getLatitude(), p.getLatitude()));
                        minValue.setLongitude(Math.min(minValue.getLongitude(), p.getLongitude()));
                        maxValue.setLatitude(Math.max(maxValue.getLatitude(), p.getLatitude()));
                        maxValue.setLongitude(Math.max(maxValue.getLongitude(), p.getLongitude()));
                        allThePoints.add(p);
                    }

                    List<Point> allThePointsCorrected = new ArrayList<>();
                    boolean hereLimitation = true;
                    if(removeLimitation) hereLimitation = false;
                    if(!limitation) hereLimitation = false;
                    if(hereLimitation){
                        for(int j = 0; j < allThePoints.size(); j += 15){
                            allThePointsCorrected.add(allThePoints.get(j));
                        }
                    }else{
                        allThePointsCorrected.addAll(allThePoints);
                    }

                    trajectory.setFirstPoint(allThePointsCorrected.remove(0));
                    allThePointsCorrected.forEach(trajectory::addPoint);
                    trajectory.setLastPoint(allThePointsCorrected.get(allThePointsCorrected.size() - 1));
                    //always add the end point to the list that contains all the POIs
                    trajectories.addPOItoTotalList(new POI(trajectory.getLastPoint()));
                    //set number of points
                    trajectory.setSize(allThePointsCorrected.size());
                    trajectories.addTrajectory(trajectory);
                }
            }


        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        trajectories.setRootAndWhWorld(new Point(minValue.getLatitude(),minValue.getLongitude()), new Point(maxValue.getLatitude() - minValue.getLatitude(), maxValue.getLongitude() - minValue.getLongitude()));

    }
}
