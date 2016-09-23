package lgds.trajectories;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class contains the single point of a trajectory
 */
public class Point {
    private final Double latitude;
    private final Double longitude;
    private final Double altitude;
    private final Double dated;
    private final String dates;
    private final String time;

    /**
     * Constructor with six parameters. All the parameters of the class
     * @param latitude latitude in decimal degrees of this point
     * @param longitude longitude in decimal degrees of this point
     * @param altitude altitude in feet of this point (-999 is not valid)
     * @param dated date, number of days with fractional part that have passed since 12/30/1899
     * @param dates date as a string
     * @param time time as a string
     */
    public Point(Double latitude, Double longitude, Double altitude, Double dated, String dates, String time){
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.dated = dated;
        this.dates = dates;
        this.time = time;
    }

}
