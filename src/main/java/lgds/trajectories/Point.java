package lgds.trajectories;

/**
 * Created by alessandrozonta on 23/09/16.
 * This class contains the single point of a trajectory
 */
public class Point {
    private Double latitude;
    private Double longitude;
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

    /**
     * Constructor with only two parameters
     * @param latitude latitude in decimal degrees of this point
     * @param longitude longitude in decimal degrees of this point
     */
    public Point(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = null;
        this.dated = null;
        this.dates = null;
        this.time = null;
    }

    /**
     * getter for latitude
     * @return return the latitude in double
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * setter for latitude
     * @param latitude in double
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * getter for longitude
     * @return return the longitude in double
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * setter for longitude
     * @param longitude in double
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * getter for altitude
     * @return return the altitude in double
     */
    public Double getAltitude() {
        return altitude;
    }

    /**
     * getter for date
     * @return return the date in double
     */
    public Double getDated() {
        return dated;
    }

    /**
     * getter for date
     * @return return the date in string
     */
    public String getDates() {
        return dates;
    }

    /**
     * getter for time
     * @return return the time in double
     */
    public String getTime() {
        return time;
    }

    /**
     * override method equals
     * @param o object to compare with this Point
     * @return if the two points are the same or not
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Point)) {
            return false;
        }

        Point point = (Point) o;

        return point.time.equals(this.time) && point.dates.equals(this.dates) &&
                point.dated.equals(this.dated) && point.altitude.equals(this.altitude) &&
                point.latitude.equals(this.latitude) && point.longitude.equals(this.longitude);
    }

}
