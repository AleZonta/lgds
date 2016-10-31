package lgds.load_track;

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
<<<<<<< HEAD
public class LoadTrack implements Traces {
    private String source; //location of the gps data

    /**
     * Load position trajectories reading the path from file
     */
    public LoadTrack(){
        String path = Paths.get(".").toAbsolutePath().normalize().toString() + "/source.conf";
        try {
            BufferedReader brTest = new BufferedReader(new FileReader(path));
            this.source = brTest.readLine();
        } catch (IOException e) {
            this.source = null;
        }
    }

    /**
     * Scan the folder (location read from a file) and load in memory all the trajectories
     * This method is specific for the Geolife trajectories 1.3 download data
=======
public class LoadTrack {
    private String source = "/Users/alessandrozonta/Downloads/Geolife Trajectories 1.3/Data"; //location of the gps data

    /**
     * Scan the folder (hardcoded location) and load in memory all the trajectories
     * This method is specific for the Geolife Trajectories 1.3 download data
>>>>>>> parent of c004689... Added POI and world dimensions
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
        //find all the subdirectories
        File[] directories = sourceFile.listFiles(File::isDirectory);
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
                //set first point
                List<String> firstElement = Arrays.asList(list.get(0).split(","));
                trajectory.setFirstPoint(new Point(Double.parseDouble(firstElement.get(0)),Double.parseDouble(firstElement.get(1)),Double.parseDouble(firstElement.get(3)),Double.parseDouble(firstElement.get(4)),firstElement.get(5),firstElement.get(6)));
                //set last point
                List<String> lastElement = Arrays.asList(list.get(list.size() -1 ).split(","));
                trajectory.setLastPoint(new Point(Double.parseDouble(lastElement.get(0)),Double.parseDouble(lastElement.get(1)),Double.parseDouble(lastElement.get(3)),Double.parseDouble(lastElement.get(4)),firstElement.get(5),lastElement.get(6)));
                //set path
                trajectory.setPath(path.toString());
                //set number of points
                trajectory.setSize(list.size());

                trajectories.addTrajectory(trajectory);

            });
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        //set first point
        List<String> element = Arrays.asList(line.split(","));
        return new Point(Double.parseDouble(element.get(0)),Double.parseDouble(element.get(1)),Double.parseDouble(element.get(3)),Double.parseDouble(element.get(4)),element.get(5),element.get(6));
    }

}
