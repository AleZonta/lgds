package lgds.load_track;

import lgds.config.ConfigFile;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
public class LoadTrackJson extends LoadJson implements Traces {
    private String source; //location of the gps data
    private boolean limitation; // do I load only trajectories on title file?

    /**
     * Load position trajectories reading the path from file
     */
    public LoadTrackJson(){
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
            String fileName = Paths.get(".").toAbsolutePath().normalize().toString() + "/goodTrajectoriesGeolife";
            try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
                stream.forEach(allowedTrajectories::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //file is a json file, need to parse it and than I can read it
        super.readFile(this.source,this.limitation,allowedTrajectories,minValue,maxValue,trajectories);

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
