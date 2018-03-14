package lgds.load_track;

import lgds.POI.POI;
import lgds.config.ConfigFile;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alessandro Zonta on 10/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadETHPedestrian implements Traces {
    private String source; //location of the gps data

    /**
     * Scan the folder and load in memory all the trajectories
     * Load position trajectories reading the path from file
     */
    public LoadETHPedestrian(){
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
     * DATASET: ETH Walking Pedestrians (EWAP)
     * he actual annotation is stored in the obsmat.txt file. Each line has this format
     * [frame_number pedestrian_ID pos_x pos_z pos_y v_x v_z v_y ]
     * however pos_z and v_z (direction perpendicular to the ground) are not used.
     * The positions and velocities are in meters and are obtained with the homography matrix stored in H.txt .
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

        List<List<Double>> data = new ArrayList<>();
        //now need to analise every line with the information in it
        for(String line: allTheLines){
            String[] division = line.split(" ");
            List<Double> lineWithData = new ArrayList<>();
            for(String st: division){
                if(!st.equals("")) {
                    double val = new BigDecimal(st).doubleValue();
                    lineWithData.add(val);
                }
            }
            data.add(lineWithData);
        }

        class lineOfData {
            private double x;
            private double y;
            private int id;

            private lineOfData(double x, double y, int id) {
                this.x = x;
                this.y = y;
                this.id = id;
            }

            private double getX(){return this.x;}


            private void convertToGPSPosition(){
                Point centralPoint = new Point(47.376345, 8.547657);
                double newX = centralPoint.getLatitude() + this.x / 10000;
                double newY = centralPoint.getLongitude() + this.y / 10000;
                this.x = newX;
                this.y = newY;

            }
        }

        List<lineOfData> lines = new ArrayList<>();
        //let's start from the bottom left point
        for(List<Double> line: data){
            double x = line.get(2);
            double y = line.get(4);
            int id = line.get(1).intValue();
            lineOfData lOd = new lineOfData(x, y, id);
            lOd.convertToGPSPosition();
            lines.add(lOd);
        }

        List<Integer> ids = lines.stream().map(x -> x.id).distinct().collect(Collectors.toList());
        for(Integer id: ids){
            List<lineOfData> currentLines = lines.stream().filter(x -> x.id == id).collect(Collectors.toList());


            //I have seen several trajectories with a lot of equals point. I do not want it
            Map<Double, Long> valueCount = currentLines.stream().collect(Collectors.groupingBy(lineOfData::getX, Collectors.counting()));
            if(valueCount.keySet().size() < currentLines.size()) continue;

            //skip if the line is too short
            if (currentLines.size() <= 3) continue;

            for(lineOfData line: currentLines) {
                //bounding box
                minValue.setLatitude(Math.min(minValue.getLatitude(), line.x));
                minValue.setLongitude(Math.min(minValue.getLongitude(), line.x));
                maxValue.setLatitude(Math.max(maxValue.getLatitude(), line.y));
                maxValue.setLongitude(Math.max(maxValue.getLongitude(), line.y));
            }

            Trajectory trajectory = new Trajectory();
            trajectory.setFullLoad(Boolean.TRUE);

            // timestep 0.4 seconds
            String nowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            trajectory.setFirstPoint(new Point(currentLines.get(0).x, currentLines.get(0).y, 0d,0d, nowDate, nowDate));

            //Remove the first trajectory
            currentLines = currentLines.subList(1, currentLines.size());

            double timeToAdd = 0.0;
            for(lineOfData line: currentLines){
                timeToAdd += 0.4;
                Point p = new Point(line.x, line.y, 0d,0d, nowDate, nowDate);
                String date = p.addTimeToPoint(timeToAdd);
                Point pReal = new Point(line.x, line.y, 0d,0d, date , date);
                trajectory.addPoint(pReal);
            }

            Point lastPoint = new Point(currentLines.get(currentLines.size() - 1).x, currentLines.get(currentLines.size() - 1).y, 0d,0d, nowDate, nowDate);
            String date = lastPoint.addTimeToPoint(timeToAdd);
            Point lastPointReal = new Point(currentLines.get(currentLines.size() - 1).x, currentLines.get(currentLines.size() - 1).y, 0d,0d, date, date);

            trajectory.setLastPoint(lastPointReal);
            //always add the end point to the list that contains all the POIs
            trajectories.addPOItoTotalList(new POI(lastPointReal));
            //set path
            trajectory.setPath("Trajectory-" + id.toString());
            //set number of points
            trajectory.setSize(currentLines.size());




            trajectories.addTrajectory(trajectory);

        }
        trajectories.setRootAndWhWorld(new Point(minValue.getLatitude(),minValue.getLongitude()), new Point(maxValue.getLatitude() - minValue.getLatitude(), maxValue.getLongitude() - minValue.getLongitude()));


        return trajectories;
    }

    @Override
    public Point loadTrajectory(String path, Integer position) {
        return null;
    }



}
