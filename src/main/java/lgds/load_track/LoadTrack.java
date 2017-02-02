package lgds.load_track;

import lgds.POI.POI;
import lgds.config.ConfigFile;
import lgds.routing.Routing;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class loads the trajectories from a file
 */
public class LoadTrack implements Traces {
    private String source; //location of the gps data
    private Double max_length; //max length trajectory
    /**
     * Scan the folder (hardcoded location) and load in memory all the trajectories
     * Load position trajectories reading the path from file
     */
    public LoadTrack(){
        // load config file
        ConfigFile conf = new ConfigFile();
        try {
            conf.loadFile();
            this.source = conf.getGeoLifeTrace();
            Double length = conf.getMaxLength();
            //if length is set to 999999.0 it means no limit
            if(length == 999999.0){
                this.max_length = Double.MAX_VALUE;
            }else{
                this.max_length = length;
            }

        } catch (Exception e){
            this.source = null;
            this.max_length = Double.MAX_VALUE;
        }
    }


    /**
     * Scan the folder (hardcoded location) and load in memory all the trajectories
     * This method is specific for the Geolife trajectories 1.3 download data
     * Every subfolder is a different person
     * Every person has a folder trajectory containing all his trajectories
     * Every files is a trajectory track
     * This method will load all the trajectories of one person in more trajectories from more people
     * Check User Guide pdf file to learn how the trajectories are saved
     *
     * @return Trajectories -> class containing all the trajectories loaded
     */
    public Trajectories loadTrajectories(){
        Trajectories trajectories = new Trajectories();
        //load the file
        File sourceFile = new File(this.source);
        //initialise to zero the value for the root and height and width
        Point minValue = new Point(Double.MAX_VALUE,Double.MAX_VALUE);
        Point maxValue = new Point(Double.MIN_VALUE,Double.MIN_VALUE);
        //find all the subdirectories
        File[] directories = sourceFile.listFiles(File::isDirectory);
        //count the total trajectory read
        final Integer[] totalTrajectories = {0};
        for(int i = 0; i < directories.length; i++){
            //list with all the path of all the trajectories of this person
            List<Path> result = new ArrayList<>();
            Path dir = Paths.get(directories[i].getAbsolutePath() + "/Trajectory");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.plt")) {
                for (Path entry : stream) {
                    result.add(entry);
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            //read all the trajectory files and load them
            result.stream().forEach(path -> {
                //list containing all the single line of the file
                //in the single line of the file there are the important info
                List<String> list = new ArrayList<>();
                try (Stream<String> stream = Files.lines(path)) {
                    //br returns as stream and convert it into a List
                    list = stream.collect(Collectors.toList());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //first six lines are useless, I can delete them
                for(int j = 0; j < 6; j++) list.remove(0);
                //now every line is important
                //the format is: latitude(decimal degrees),longitude(decimal degrees),0,altitude(feet),date(number of days since 12/30/1899),date(string),time(string)
                //I am going to transform the string into the correct values
                Trajectory trajectory = new Trajectory();
                //set full loaded to true
                trajectory.setFullLoad(Boolean.FALSE);
                //set first point
                List<String> firstElement = Arrays.asList(list.get(0).split(","));
                trajectory.setFirstPoint(new Point(Double.parseDouble(firstElement.get(0)),Double.parseDouble(firstElement.get(1)),Double.parseDouble(firstElement.get(3)),Double.parseDouble(firstElement.get(4)),firstElement.get(5),firstElement.get(6)));
                //set last point
                List<String> lastElement = Arrays.asList(list.get(list.size() -1 ).split(","));
                Point lastPoint = new Point(Double.parseDouble(lastElement.get(0)),Double.parseDouble(lastElement.get(1)),Double.parseDouble(lastElement.get(3)),Double.parseDouble(lastElement.get(4)),lastElement.get(5),lastElement.get(6));
                trajectory.setLastPoint(lastPoint);
                //always add the end point to the list that contains all the POIs
                trajectories.addPOItoTotalList(new POI(lastPoint));
                //set path
                trajectory.setPath(path.toString());
                //set number of points
                trajectory.setSize(list.size());

                //determine geo bounding box
                list.stream().forEach(s -> {
                    List<String> el = Arrays.asList(s.split(","));
                    Double lat = Double.parseDouble(el.get(0));
                    Double lon = Double.parseDouble(el.get(1));
                    minValue.setLatitude(Math.min(minValue.getLatitude(), lat));
                    minValue.setLongitude(Math.min(minValue.getLongitude(), lon));
                    maxValue.setLatitude(Math.max(maxValue.getLatitude(), lat));
                    maxValue.setLongitude(Math.max(maxValue.getLongitude(), lon));
                });

                //determine distance between points
                List<String> el = Arrays.asList(list.get(0).split(","));
                Double latSource = Double.parseDouble(el.get(0));
                Double lonSource = Double.parseDouble(el.get(1));
                Double total = 0d;
                for(int z = 1; z < list.size(); z++){
                    List<String> elDestination = Arrays.asList(list.get(z).split(","));
                    Double latDestination = Double.parseDouble(elDestination.get(0));
                    Double lonDestination = Double.parseDouble(elDestination.get(1));
                    total += trajectories.retDistanceUsingDistanceClass(new double[] {latSource, lonSource}, new double[] {latDestination, lonDestination});
                    latSource = latDestination;
                    lonSource = lonDestination;
                }

                totalTrajectories[0]++;

                //discriminate length trajectory
                //only if shorter than max_length i will add the trajectory
                if(total < this.max_length) {
                    //I also want a lowerbound for number of step -> I found example with 5 steps (I do not like them)
                    //hardcoded minimum number of steps -> 30?
                    //if(list.size() > 30) {
                    trajectories.addTrajectory(trajectory);
                    //}
                }

            });
        }
        trajectories.setRootAndWhWorld(new Point(minValue.getLatitude(), minValue.getLongitude()), new Point(maxValue.getLatitude() - minValue.getLatitude(), maxValue.getLongitude() - minValue.getLongitude()));

        System.out.println("Max length selected is " + this.max_length);
        System.out.println("From " + totalTrajectories[0] + " to " + trajectories.getTrajectories().size() + " trajectories loaded");
        return trajectories;
    }

    /**
     * Load a specific line of the trajectory from the file
     * @param path path of the file containing all the point of the trajectory
     * @param position position that we have already reached
     * @return the new point read from file
     */
    public Point loadTrajectory(String path, Integer position){
        String line = null; //line that I need
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            //br returns as stream and convert it into a List
            line = stream.skip(position.longValue() + 6L).findFirst().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //set first point
        List<String> element = Arrays.asList(line.split(","));
        return new Point(Double.parseDouble(element.get(0)),Double.parseDouble(element.get(1)),Double.parseDouble(element.get(3)),Double.parseDouble(element.get(4)),element.get(5),element.get(6));
    }

}
