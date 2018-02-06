package lgds.Distance;

import lgds.trajectories.Point;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

/**
 * Created by alessandrozonta on 03/01/2017.
 */
public class Distance implements DistanceMeasure {
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * Source -> http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @param doubles first point location
     * @param doubles1 second point location
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
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);

    }


    /**
     * Compute distance using the method before
     * @param firstPoint first point
     * @param secondPoint second point
     * @return distance in Meters
     */
    public double compute(Point firstPoint, Point secondPoint){
        return this.compute(new double[] {firstPoint.getLatitude(), firstPoint.getLongitude()} , new double[] {secondPoint.getLatitude(), secondPoint.getLongitude()});
    }

}
