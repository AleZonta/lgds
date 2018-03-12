package lgds.trajectories;

import java.time.LocalTime;

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
     * setter for latitude
     * @param latitude in double
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * getter for latitude
     * @return return the latitude in double
     */
    public Double getLatitude() {
        return latitude;
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


        try {
            return point.time.equals(this.time) && point.dates.equals(this.dates) &&
                    point.dated.equals(this.dated) && point.altitude.equals(this.altitude) &&
                    point.latitude.equals(this.latitude) && point.longitude.equals(this.longitude);
        }catch (Exception e){
            return point.latitude.equals(this.latitude) && point.longitude.equals(this.longitude);
        }
    }

    /**
     * Override method print object to string
     * @return
     */
    @Override
    public String toString() {
        return "(" + this.latitude + ", " + this.longitude + ")";
    }


    /**
     * Subtract the time
     * @param secondPoint point in the past that is subtract to this point
     * @return integer second of difference between two times
     * @throws Exception if something goes wrong
     */
    public Integer differenceInTime(Point secondPoint) throws Exception {
        //this - second
        LocalTime timeThis = LocalTime.parse(this.getTime());
        LocalTime timeSecond = LocalTime.parse(secondPoint.getTime());
        Integer seconds = 0;
        Integer minutes = 0;
        Integer hours = 0;
        Integer firstSeconds = timeThis.getSecond();
        Integer secondSeconds = timeSecond.getSecond();
        Integer firstMinutes = timeThis.getMinute();
        Integer secondMinutes = timeSecond.getMinute();
        Integer firstHours = timeThis.getHour();
        Integer secondHours = timeSecond.getHour();
        if(firstSeconds >= secondSeconds){
            seconds = firstSeconds - secondSeconds;
        } else {
            if(firstMinutes > 0){
                firstMinutes--;
                firstSeconds += 60;
                seconds = firstSeconds - secondSeconds;
            }else{
                if(firstHours > 0){
                    firstHours--;
                    firstMinutes += 60;
                    firstMinutes--;
                    firstSeconds += 60;
                    seconds = firstSeconds - secondSeconds;
                }else{
                    if(firstHours == 0) {
                        firstSeconds += 60;
                        seconds = firstSeconds - secondSeconds;
                    }else {
                        throw new Exception("Error with the data");
                    }
                }
            }
        }
        if(firstMinutes >= secondMinutes){
            minutes = firstMinutes - secondMinutes;
        }else{
            if(firstHours > 0){
                firstHours--;
                firstMinutes += 60;
                minutes = firstMinutes - secondMinutes;
            }else{
                if(firstHours != 0) {
                    throw new Exception("Error with the data");
                }
            }
        }
        if(firstHours >= secondHours){
            hours = firstHours - secondHours;
        }else{
            if(firstHours != 0) {
                throw new Exception("Error with the data");
            }
        }
        Integer hoursToSecond = hours * 60 * 60;
        Integer minutesToSecond = minutes * 60;
        return seconds + hoursToSecond + minutesToSecond;
    }


    /**
     * Add tot second to the time expressed
     * @param addend time to add
     * @return second value of the current time plus the time to add
     */
    public String addTimeToPoint(Double addend){
        //this + addend
        LocalTime timeThis = LocalTime.parse(this.getTime());
        return timeThis.plusNanos(new Double(addend * 1000000000.0).longValue()).toString();
    }

    /**
     * Deep copy of the object
     * @return new object with the same characteristics
     */
    public Point deepCopy(){
        return new Point(this.latitude, this.longitude, this.altitude, this.dated, this.dates, this.time);
    }

    /**
     * Computes the Euclidean distance from this point to the other.
     *
     * @param o1 other point.
     * @return euclidean distance.
     */
    public double euclideanDistance(Point o1) {
        return euclideanDistance(o1, this);
    }

    /**
     * Computes the Euclidean distance from one point to the other.
     *
     * @param o1 first point.
     * @param o2 second point.
     * @return euclidean distance.
     */
    private double euclideanDistance(Point o1, Point o2) {
        return Math.sqrt(Math.pow((o1.getLatitude() - o2.getLatitude()), 2) + Math.pow((o1.getLongitude() - o2.getLongitude()), 2));
    }
}
