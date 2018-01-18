package lgds.load_track;

import lgds.POI.POI;
import lgds.config.ConfigFile;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Alessandro Zonta on 17/01/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadIDSAJson implements Traces {
    private String source; //location of the gps data
    private boolean limitation; // do I load only trajectories on title file?


    /**
     * Load position trajectories reading the path from file
     */
    public LoadIDSAJson(){
        // load config file
        ConfigFile conf = new ConfigFile();
        try {
            conf.loadFile();
            this.source = conf.getIDSATraces();
            this.limitation = conf.getTranslate();
        } catch (Exception e){
            this.source = null;
            this.limitation = false;
        }

    }

    /**
     * Scan the folder (location read from a file) and load in memory all the trajectories
     * This method is specific for the IDSA simulator's traces
     * It is a JSON file containing all the trajectories
     * It calculates the root of the word and its height and width
     *
     *
     * if limitation is true I will load only certain trajectory and save a point every 30
     *
     * @return trajectories -> class containing all the trajectories loaded
     */
    @Override
    public Trajectories loadTrajectories() {
        Trajectories trajectories = new Trajectories();
        //initialise to zero the value for the root and height and width
        Point minValue = new Point(Double.MAX_VALUE,Double.MAX_VALUE);
        Point maxValue = new Point(Double.MIN_VALUE,Double.MIN_VALUE);


        List<String> allowedTrajectories = new ArrayList<>();
        if(this.limitation){
            String fileName = Paths.get(".").toAbsolutePath().normalize().toString() + "/goodTrajectories";
            try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
                stream.forEach(allowedTrajectories::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //file is a json file, need to parse it and than I can read it
        FileReader reader;
        try {
            reader = new FileReader(this.source);
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);


            Integer t = ((Long) jsonObject.get("size")).intValue();

            IntStream.range(0, t).forEach(i -> {

                String name = "trajectory-" + i;

                if (!this.limitation || this.limitation && allowedTrajectories.stream().anyMatch(n -> n.equals(name))) {
                    Trajectory trajectory = new Trajectory();
                    //set full loaded to true
                    trajectory.setFullLoad(Boolean.TRUE);


                    JSONArray tra = (JSONArray) ((JSONObject) jsonObject.get(name)).get("trajectory");

                    List<Point> allThePoints = new ArrayList<>();
                    for (JSONArray aTra : (Iterable<JSONArray>) tra) {
                        Point p = new Point((Double) aTra.get(0), (Double) aTra.get(1));
                        minValue.setLatitude(Math.min(minValue.getLatitude(), p.getLatitude()));
                        minValue.setLongitude(Math.min(minValue.getLongitude(), p.getLongitude()));
                        maxValue.setLatitude(Math.max(maxValue.getLatitude(), p.getLatitude()));
                        maxValue.setLongitude(Math.max(maxValue.getLongitude(), p.getLongitude()));
                        allThePoints.add(p);
                    }

                    List<Point> allThePointsCorrected = new ArrayList<>();
                    if(this.limitation){
                        for(int j = 0; j < allThePoints.size(); j += 15){
                            allThePointsCorrected.add(allThePoints.get(j));
                        }
                    }else{
                        allThePointsCorrected.addAll(allThePoints);
                    }

                    trajectory.setFirstPoint(allThePointsCorrected.remove(0));
                    allThePointsCorrected.forEach(point -> trajectory.addPoint(point));
                    trajectory.setLastPoint(allThePointsCorrected.get(allThePointsCorrected.size() - 1));
                    //always add the end point to the list that contains all the POIs
                    trajectories.addPOItoTotalList(new POI(trajectory.getLastPoint()));
                    //set number of points
                    trajectory.setSize(allThePointsCorrected.size());
                    trajectories.addTrajectory(trajectory);
                }
            });


        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        trajectories.setRootAndWhWorld(new Point(minValue.getLatitude(),minValue.getLongitude()), new Point(maxValue.getLatitude() - minValue.getLatitude(), maxValue.getLongitude() - minValue.getLongitude()));

        return trajectories;
    }

    /**
     * Load a specific line of the trajectory from the file
     * @param path path of the file containing all the point of the trajectory
     * @param position position that we have already reached
     * @return the new point read from file
     */
    @Override
    public Point loadTrajectory(String path, Integer position) {
        return null;
    }
}