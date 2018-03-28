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
 * Created by Alessandro Zonta on 20/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadUCYDataset implements Traces {
    private String source; //location of the gps data

    /**
     * Scan the folder and load in memory all the trajectories
     * Load position trajectories reading the path from file
     */
    public LoadUCYDataset(){
        // load config file
        ConfigFile conf = new ConfigFile();
        try {
            conf.loadFile();
            this.source = conf.getSource();


        } catch (Exception e){
            this.source = null;
        }
    }

    /**
     * Scan the folder and load in memory all the trajectories
     * DATASET: UCY datasets
     *
     *
     * The tracked file consists of a series of splines that describe the moving behavior of a person in a video.
     * Comments in the file start with a '-' and end at the end of the line.
     *
     * The number of splines can be found in the first line of the file.
     * Then immediately after that, each spline is defined in the following format:
     *
     * Number_of_control_points_N
     * x y frame_number gaze_direction   \
     * x y frame_number gaze_direction    \
     * ....                                >>> N control points
     * x y frame_number gaze_direction    /
     * x y frame_number gaze_direction   /
     *
     * Number_of_control_points_K
     * x y frame_number gaze_direction   \
     * x y frame_number gaze_direction    \
     * ....                                >>> K control points
     * x y frame_number gaze_direction    /
     * x y frame_number gaze_direction   /
     *
     * ....
     * ...
     *
     * x, y: the position of the person in pixel space, where (0, 0) is the center of the frame.
     * frame_number: the time (frames)at which the position was tracked
     * gaze_direction: the viewing direction of the person in degrees (0 degrees means the person is looking upwards)
     *
     * @return Trajectories -> class containing all the trajectories loaded
     */
    @Override
    public Trajectories loadTrajectories() {
        //initialise to zero the value for the root and height and width
        Point minValue = new Point(Double.MAX_VALUE,Double.MAX_VALUE);
        Point maxValue = new Point(Double.MIN_VALUE,Double.MIN_VALUE);
        Trajectories trajectories = new Trajectories();
        //load the file


        List<String> allTheLines = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(this.source))) {
            stream.forEach(allTheLines::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        class lineOfData {
            private double x;
            private double y;
            private double frameNumber;
            private double time;
            private int id;

            private lineOfData(double x, double y, int id, double frameNumber, double time) {
                this.x = x;
                this.y = y;
                this.id = id;
                this.frameNumber = frameNumber;
                this.time = time;
            }


//            private void convertToGPSPosition(){
//                Point centralPoint = new Point(47.376345, 8.547657);
//                double newX = centralPoint.getLatitude() + this.x / 100000;
//                double newY = centralPoint.getLongitude() + this.y / 100000;
//                this.x = newX;
//                this.y = newY;
//
//            }
        }

        //first line contains how many splines there are in the file
        String firstLine = allTheLines.remove(0);
        String[] division = firstLine.split("-");
        int numberOfSplines = Integer.valueOf(division[0].trim());


        List<Double> xx = new ArrayList<>();
        List<Double> yy = new ArrayList<>();

        List<List<lineOfData>> totalLines = new ArrayList<>();
        for(int i = 0; i < numberOfSplines; i++){
            List<lineOfData> lines = new ArrayList<>();
            division = allTheLines.remove(0).split("-");
            int numberOfControlPoints = Integer.valueOf(division[0].trim());
            double oldTime = 0d;
            for(int j = 0; j < numberOfControlPoints; j++){
                division = allTheLines.remove(0).split(" ");
                double x = Double.valueOf(division[0]);
                double y = Double.valueOf(division[1]);
                xx.add(x);
                yy.add(y);

                double frameNumber = Double.valueOf(division[2]);

                double time = 0d;
                if(j == 0){
                    time = 0d;
                    if(frameNumber != 0) {
                        time = frameNumber / 25;
                        oldTime = time;
                    }
                }else{
                    double hereTime = frameNumber / 25;
                    time = hereTime - oldTime;
                    oldTime = hereTime;
                }
                // max two decimals
                int newValueForTime = (int)(time * 100);
                time = (double) newValueForTime / 100;

                lineOfData lOd = new lineOfData(x, y, numberOfSplines, frameNumber, time);
                lines.add(lOd);
            }
            totalLines.add(lines);
        }


        return null;
    }


    @Override
    public Point loadTrajectory(String path, Integer position) {
        return null;
    }
}
