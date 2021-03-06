package lgds.trajectories;

import lgds.Distance.Distance;
import lgds.POI.POI;
import lgds.config.ConfigFile;
import lgds.load_track.Traces;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class stores the collection of trajectories
 */
public class Trajectories {
    private List<Trajectory> trajectories; // all the trajectories
    private List<? extends Cluster<POI>> listOfPOIsClustered; //list of all the POIs
    private List<POI> listOfPOIs; //list of all the POIs of the selected subsection
    private List<POI> totalListOfPOIs; //list of all the end destination of the trajectories
    private Point utmRoot; //root of the word
    private Point whWorld; // width and height of the word
    private int DBSCANratio; //ratio dbscan
    private boolean trainTest; //if I am dividing the trajectories in training and readFileAndCreateDB set
    private List<Trajectory> trainTrajectories; // all the trajectories
    private List<Trajectory> testTrajectories; // all the trajectories


    /**
     * Default constructor
     */
    public Trajectories(){
        this.trajectories = new ArrayList<>();
        this.listOfPOIs = new ArrayList<>();
        this.listOfPOIsClustered = new ArrayList<>();
        this.totalListOfPOIs = new ArrayList<>();
        // load config file
        ConfigFile conf = new ConfigFile();
        try {
            conf.loadFile();
            this.DBSCANratio = conf.getDBSCANradio();
        } catch (Exception e) {
            this.DBSCANratio = 40;
        }
        this.trainTest = Boolean.FALSE;
        this.trainTrajectories = new ArrayList<>();
        this.testTrajectories = new ArrayList<>();
    }

    /**
     * Add trajectory to the collection of trajectories
     * @param trajectory next trajectory
     */
    public void addTrajectory(Trajectory trajectory){
        this.trajectories.add(trajectory);
    }


    /**
     * Add poi to the total list of POIs
     * @param poi poi that is added to the list
     */
    public void addPOItoTotalList(POI poi) { this.totalListOfPOIs.add(poi); }

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
     * Compute the list of POIs from the trajectories.
     * Every destinations are POIs
     * Before adding the poI I should cluster the location to be sure there are not more than n points in the same location.
     * @param number number of trace that I am considering
     * @param general if true I select only POIs from the subset loaded, if false I select randomly from the list with all the POIs
     * @param increaseNumber If general is false i add the increaseNumber of POIs to the list
     */
    public void computePOIs(Integer number, Boolean general, Integer increaseNumber){
        //store temporarily all the POIs here
        List<POI> appo_list = new ArrayList<>();
        this.trajectories.stream().limit(number).forEach(trajectory -> appo_list.add(new POI(trajectory.getLastPoint())));
        this.trajectories.stream().limit(number).forEach(trajectory -> this.listOfPOIs.add(new POI(trajectory.getLastPoint())));

        //if general is True I add increaseNumber of POIs to the final number of POIs
        if (general) {
            //if the number is positive I will add that number of POI
            Random rand = new Random(23);
            //check increaseNumber
            if (increaseNumber + number > this.totalListOfPOIs.size()) {
                increaseNumber = this.totalListOfPOIs.size() - number - 1;
            }
            addPOI(increaseNumber, rand, appo_list);
        }

        //System.out.println("Clustering the POI...");
        Distance dis = new Distance();
        //dbscan parameters (distance in metres, minimum number of element, distance measure)
        Clusterer<POI> dbscan = new DBSCANClusterer<POI>(this.DBSCANratio, 0, dis);
        this.listOfPOIsClustered = dbscan.cluster(appo_list);
        //System.out.println("From " + appo_list.size() + " to " + this.listOfPOIsClustered.size() + " number of POIs");
    }


    /**
     * add point
     * @param increaseNumber If general is false i add the increaseNumber of POIs to the list
     * @param rand random number
     * @param appo_list  store temporarily all the POIs here
     */
    private void addPOI(Integer increaseNumber, Random rand, List<POI> appo_list){
        for (int i = 0; i < increaseNumber; i++) {
            Integer val = rand.nextInt(this.totalListOfPOIs.size());
            POI selected = this.totalListOfPOIs.get(val);
            POI finalSelected = selected;
            //check if the POI selected is already inside
            boolean present = appo_list.stream().anyMatch(poi -> poi.equals(finalSelected));
            while (present) {
                val = rand.nextInt(this.totalListOfPOIs.size());
                selected = this.totalListOfPOIs.get(val);
                POI finalSelected1 = selected;
                present = appo_list.stream().anyMatch(poi -> poi.equals(finalSelected1));
            }
            //if not present inside add the POI to the two different lists
            appo_list.add(selected);
            this.listOfPOIs.add(selected);
        }
    }

    /**
     * Compute the list of POIs from the trajectories.
     * Every destinations are POIs
     * @param tra trajectory to use to produce the POIs
     * @param general if true I select only POIs from the subset loaded, if false I select randomly from the list with all the POIs
     * @param increaseNumber If general is false i add the increaseNumber of POIs to the list
     */
    public void computePOIs(List<Trajectory> tra, Boolean general, Integer increaseNumber){
        //store temporarily all the POIs here
        List<POI> appo_list = new ArrayList<>();
        tra.forEach(trajectory -> appo_list.add(new POI(trajectory.getLastPoint())));
        tra.forEach(trajectory -> this.listOfPOIs.add(new POI(trajectory.getLastPoint())));

        //if general is True I add increaseNumber of POIs to the final number of POIs
        if (general) {
            //if the number is positive I will add that number of POI
            Random rand = new Random(23);
            //check increaseNumber
            if (increaseNumber + tra.size() > this.totalListOfPOIs.size()) {
                increaseNumber = this.totalListOfPOIs.size() - tra.size() - 1;
            }
            addPOI(increaseNumber, rand, appo_list);
        }


        //System.out.println("Clustering the POI...");
        Distance dis = new Distance();
        //dbscan parameters (distance in metres, minimum number of element, distance measure)
        Clusterer<POI> dbscan = new DBSCANClusterer<POI>(this.DBSCANratio, 0, dis);
        this.listOfPOIsClustered = dbscan.cluster(appo_list);
        //System.out.println("From " + appo_list.size() + " to " + this.listOfPOIsClustered.size() + " POIs");

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
        Integer initialTrajectories = this.trajectories.size();
        //remove if the ratio is fewer that the minumun Ratio
        this.trajectories.removeIf(trajectory -> trajectory.computeRatio() < minumumRatio);
        Integer endTrajectories = this.trajectories.size();
        System.out.println("From " + initialTrajectories.toString() + " to " + endTrajectories.toString() + " trajectories");
    }

    /**
     * Return distance between points using inner Class Distance
     * @param doubles first point
     * @param doubles1 second point
     * @return Distance between two points in Double
     */
    public Double retDistanceUsingDistanceClass(double[] doubles, double[] doubles1){
        Distance dis = new Distance();
        return dis.compute(doubles, doubles1);
    }


    /**
     * Create the training and readFileAndCreateDB set
     */
    public void createTrainingAndTest(){
        this.trainTest = Boolean.TRUE;
        //I am creating 80% of the trajectories as training and 20% as readFileAndCreateDB
        this.trajectories.forEach(trajectory -> {
            Double val = ThreadLocalRandom.current().nextDouble();
            if(val <= 0.8){
                this.trainTrajectories.add(trajectory);
            }else{
                this.testTrajectories.add(trajectory);
            }
        });
    }

    /**
     * Create the training and readFileAndCreateDB set
     * Limit the total number of entries
     * @param number number of entries
     */
    public void createTrainingAndTest(Integer number){
        this.trainTest = Boolean.TRUE;
        //I am creating 80% of the trajectories as training and 20% as readFileAndCreateDB
        if(number > this.trajectories.size()){
            this.createTrainingAndTest();
        }else{
            for (int i = 0; i < number; i++){
                Double val = ThreadLocalRandom.current().nextDouble();
                if(val <= 0.8){
                    this.trainTrajectories.add(this.trajectories.get(i));
                }else{
                    this.testTrajectories.add(this.trajectories.get(i));
                }
            }
        }
    }



    /**
     * Switch total trajectories to the train subset
     * @exception Exception if the training readFileAndCreateDB is not created
     */
    public void switchToTrain() throws Exception {
        if(!this.trainTest) throw new Exception("No training set created!");
        this.trajectories = this.trainTrajectories;
    }

    /**
     * Switch total trajectories to the readFileAndCreateDB subset
     *  @exception Exception if the training readFileAndCreateDB is not created
     */
    public void switchToTest() throws Exception {
        if(!this.trainTest) throw new Exception("No training set created!");
        this.trajectories = this.testTrajectories;
    }


    /**
     * Skip some of the points
     * Expecially for IDSA
     * 1 timestep every 5
     * @param storage {@link Traces} object
     */
    public void makeTrajectoriesSeemReal(Traces storage){
        List<Trajectory> realTrajectories = new ArrayList<>();
        this.trajectories.forEach(tra -> {
            //check the trajectory
            Trajectory newTrajectory = new Trajectory();

            Point currentPosition = tra.getNextPoint(storage);
            newTrajectory.addPoint(currentPosition);
            int size = 1;
            int count = 1;
            while(currentPosition!= null){
                currentPosition = tra.getNextPoint(storage);
                count ++;
                if(count == 5){
                    newTrajectory.addPoint(currentPosition);
                    size ++;
                    count = 0;
                }
            }
            newTrajectory.setSize(size);
            realTrajectories.add(newTrajectory);

//            System.out.println(tra.getSize() + " ----> " + newTrajectory.getSize());
        });
        this.trajectories = realTrajectories;
    }


    /**
     * Analise the speed of the trajectories in order to find various parameter for the speed
     * @param type integer parameter indicating which dataset I am loading
     * @param storage storage object {@link Traces}
     * @throws Exception if type is not correct
     */
    public void analiseSpeedTrajectories(int type, Traces storage) throws Exception {
        //type = 0 -> IDSA -> time = 0.2
        //type = 1 -> Geolife
        double time = 0;
        if(type == 0){
            time = 0.2;
        }//else{
            //the time is going to be computed during the computation
            //throw new Exception("Not yet implemented");
        //}
        if(type == 2){
            time = 0.4;
        }
        List<Double> speeds = new ArrayList<>();
        List<Double> spaceTotals = new ArrayList<>();
        List<Double> times = new ArrayList<>();
        for(Trajectory tra: this.trajectories){
            Point p = tra.getFirstPoint();
            Point p1 = tra.getNextPoint(storage);
            while (p1 != null){
                //speed = space / time
                double space = this.retDistanceUsingDistanceClass(new double[]{p.getLatitude(), p.getLongitude()}, new double[]{p1.getLatitude(), p1.getLongitude()});

                if(type == 1){
                    //need to compute the time between the two points
                    LocalTime pTime = LocalTime.parse(p.getTime());
                    LocalTime p1Time = LocalTime.parse(p1.getTime());
                    time = Duration.between(pTime, p1Time).toMillis() / 1000;
                    if(time < 0) {
                        //the two dates are over the day
                        LocalTime localTime2 = LocalTime.of(23, 59, 59);
                        double halftime = Duration.between(pTime, localTime2).toMillis() / 1000;
                        LocalTime localTime3 = LocalTime.of(0, 0, 1);
                        double halfSecontime = Duration.between(localTime3, p1Time).toMillis() / 1000;
                        time = halfSecontime + halftime + 2;
                    }
                    times.add(time);
                }
                times.add(time);

                spaceTotals.add(space);
                if(time == 0){
                    speeds.add(0d);
                }else{
                    speeds.add(space/time);
                }
                //next two points
                p = p1.deepCopy();
                p1 = tra.getNextPoint(storage);

            }

        }



        System.out.println("Speed");
        System.out.println("max -> " + speeds.stream().mapToDouble(Double::doubleValue).max().getAsDouble() + "m/s -> " + speeds.stream().mapToDouble(Double::doubleValue).max().getAsDouble() * 3.6 + "km/h");
        System.out.println("min -> " + speeds.stream().mapToDouble(Double::doubleValue).min().getAsDouble() + "m/s -> " + speeds.stream().mapToDouble(Double::doubleValue).min().getAsDouble() * 3.6 + "km/h");
        System.out.println("average -> " + speeds.stream().mapToDouble(Double::doubleValue).average().getAsDouble() + "m/s -> " + speeds.stream().mapToDouble(Double::doubleValue).average().getAsDouble() * 3.6 + "km/h");
        DoubleStream sortedAges = speeds.stream().mapToDouble(Double::doubleValue).sorted();
        double median = speeds.size()%2 == 0?
                sortedAges.skip(speeds.size()/2-1).limit(2).average().getAsDouble():
                sortedAges.skip(speeds.size()/2).findFirst().getAsDouble();
        System.out.println("median -> " + median + "m/s -> " + median * 3.6 + "km/h");

        System.out.println("----------");

        System.out.println("Time");
        System.out.println("max -> " + times.stream().mapToDouble(Double::doubleValue).max().getAsDouble() + "s");
        System.out.println("min -> " + times.stream().mapToDouble(Double::doubleValue).min().getAsDouble() + "s");
        System.out.println("average -> " + times.stream().mapToDouble(Double::doubleValue).average().getAsDouble() + "s");
        sortedAges = times.stream().mapToDouble(Double::doubleValue).sorted();
        median = times.size()%2 == 0?
                sortedAges.skip(times.size()/2-1).limit(2).average().getAsDouble():
                sortedAges.skip(times.size()/2).findFirst().getAsDouble();
        System.out.println("median -> " + median + "m/s -> " + median * 3.6 + "km/h");
        System.out.println("----------");

        System.out.println("Space");
        System.out.println("max -> " + spaceTotals.stream().mapToDouble(Double::doubleValue).max().getAsDouble() + "m");
        System.out.println("min -> " + spaceTotals.stream().mapToDouble(Double::doubleValue).min().getAsDouble() + "m");
        System.out.println("average -> " + spaceTotals.stream().mapToDouble(Double::doubleValue).average().getAsDouble() + "m");
        sortedAges = spaceTotals.stream().mapToDouble(Double::doubleValue).sorted();
        median = spaceTotals.size()%2 == 0?
                sortedAges.skip(spaceTotals.size()/2-1).limit(2).average().getAsDouble():
                sortedAges.skip(spaceTotals.size()/2).findFirst().getAsDouble();
        System.out.println("median -> " + median + "m/s -> " + median * 3.6 + "km/h");
    }



}
