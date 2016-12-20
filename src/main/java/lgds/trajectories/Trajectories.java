package lgds.trajectories;

import lgds.POI.POI;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class stores the collection of trajectories
 */
public class Trajectories {
    private List<Trajectory> trajectories; // all the trajectories
    private List<? extends Cluster<POI>> listOfPOIsClustered; //list of all the POIs
    private List<POI> listOfPOIs; //list of all the POIs
    private Point utmRoot; //root of the word
    private Point whWorld; // width and height of the word

    /**
     * Default constructor
     */
    public Trajectories(){
        this.trajectories = new ArrayList<>();
        this.listOfPOIs = new ArrayList<>();
        this.listOfPOIsClustered = new ArrayList<>();
    }

    /**
     * Add trajectory to the collection of trajectories
     * @param trajectory next trajectory
     */
    public void addTrajectory(Trajectory trajectory){
        this.trajectories.add(trajectory);
    }

    /**
     * getter for the trajectories field
     * @return the list with all the trajectories
     */
    public List<Trajectory> getTrajectories() {
        return trajectories;
    }

    /**
     * shuffle the list of trajectories so all the one that correspond at the same person are not all near each other
     * shuffle in a predictive way so repetetive repetition have always the same order
     */
    public void shuffle(){
        Collections.shuffle(this.trajectories, new Random(10));
    }

    /**
     * getter for the list of POIS
     * @return the list of POIs
     */
    public List<POI> getListOfPOIs() {
        return this.listOfPOIs;
    }

    /**
     * getter for the list of POIS after the clustering procedure
     * @return the list of POIs clustered
     */
    public List<? extends Cluster<POI>> getListOfPOIsClustered() {
        return this.listOfPOIsClustered;
    }

    /**
     * Nested class to override the compute distance method
     */
    class Distance implements DistanceMeasure {

        /**
         * Calculate distance between two points in latitude and longitude taking
         * into account height difference. If you are not interested in height
         * difference pass 0.0. Uses Haversine method as its base.
         * Source -> http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
         *
         * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
         * el2 End altitude in meters
         * @returns Distance in Meters
         */
        @Override
        public double compute(double[] doubles, double[] doubles1) {
            double lat1 = doubles[0];
            double lat2 = doubles1[0];
            double lon1 = doubles[1];
            double lon2 = doubles1[1];
            double el1 = 0.0;
            double el2 = 0.0;

            final int R = 6371; // Radius of the earth
            Double latDistance = Math.toRadians(lat2 - lat1);
            Double lonDistance = Math.toRadians(lon2 - lon1);
            Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convert to meters

            double height = el1 - el2;

            distance = Math.pow(distance, 2) + Math.pow(height, 2);

            return Math.sqrt(distance);

        }
    }

    /**
     * Compute the list of POIs from the trajectories.
     * Every destinations are POIs
     * Before adding the poI I should cluster the location to be sure there are not more than n points in the same location.
     * @param number number of trace that I am considering
     */
    public void computePOIs(Integer number){
        //store temporarily all the POIs here
        List<POI> appo_list = new ArrayList<>();
        this.trajectories.stream().limit(number).forEach(trajectory -> appo_list.add(new POI(trajectory.getLastPoint())));
        this.trajectories.stream().limit(number).forEach(trajectory -> this.listOfPOIs.add(new POI(trajectory.getLastPoint())));

        System.out.println("Clustering the POI...");
        Distance dis = new Distance();
        //dbscan parameters (distance in metres, minimum number of element, distance measure)
        Clusterer<POI> dbscan = new DBSCANClusterer<POI>(40, 0, dis);
        this.listOfPOIsClustered = dbscan.cluster(appo_list);
        System.out.println("From " + number.toString() + " to " + this.listOfPOIsClustered.size() + " number of POIs");
    }

    /**
     * Compute the list of POIs from the trajectories.
     * Every destinations are POIs
     * @param tra trajectory to use to produce the POIs
     */
    public void computePOIs(List<Trajectory> tra){
        //store temporarily all the POIs here
        List<POI> appo_list = new ArrayList<>();
        tra.stream().forEach(trajectory -> appo_list.add(new POI(trajectory.getLastPoint())));
        tra.stream().forEach(trajectory -> this.listOfPOIs.add(new POI(trajectory.getLastPoint())));

        System.out.println("Clustering the POI...");
        Distance dis = new Distance();
        //dbscan parameters (distance in metres, minimum number of element, distance measure)
        Clusterer<POI> dbscan = new DBSCANClusterer<POI>(40, 0, dis);
        this.listOfPOIsClustered = dbscan.cluster(appo_list);
        System.out.println("From " + tra.size() + " to " + this.listOfPOIsClustered.size() + " POIs");

    }

    /**
     * set the root of the word and its dimension
     * @param utmRoot root coordinate of the origin of the word
     * @param whWorld width and height of the word
     */
    public void setRootAndWhWorld(Point utmRoot, Point whWorld){
        this.utmRoot = utmRoot;
        this.whWorld = whWorld;
    }

    /**
     * getter for the root coordinate
     * @return root coordinate
     */
    public Point getUtmRoot() {
        return utmRoot;
    }

    /**
     * getter for the dimension of the world
     * @return the dimension of the world in a Point object
     */
    public Point getWhWorld() {
        return whWorld;
    }


    /**
     * Remove from the trajectories the ones that have ration not acceptable
     * hardcoded minimum ratio -> 4.0
     */
    public void analiseAndCheckTrajectory() {
        System.out.println("Analysing thw trajectories ...");
        //hardcoded minimum ration need
        Double minumumRatio = 4.0;
        Distance dis = new Distance();
        Integer initialTrajectories = this.trajectories.size();
        //remove if the ratio is fewer that the minumun Ratio
        this.trajectories.removeIf(trajectory -> trajectory.computeRatio(dis) < minumumRatio);
        Integer endTrajectories = this.trajectories.size();
        System.out.println("From " + initialTrajectories.toString() + " to " + endTrajectories.toString() + " trajectories");
    }
}
