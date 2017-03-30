package lgds.viewer;

import lgds.POI.POI;
import lgds.trajectories.Point;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCenter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by alessandrozonta on 31/01/2017.
 * should load the GUI for the map
 * now everything is set up for only the POIs and the trajectory selected -> Only one per time
 * REQUIRES INTERNET CONNECTION
 * TODO add the possibility to display more trajectories all together
 * TODO implement waypoint path version
 */
public class View {
    private final Map<GeoPosition, Double> listPoint; //list of point to update
    private GeoPosition mapFocus; //focus of the map
    private final JXMapViewer mapViewer; //map
    private final WaypointPainter<MyWaypoint> circleWayPointPainter;
    private final WaypointPainter<MyWaypoint> waypointWaypointPainter;
    private final List<GeoPosition> trajectory; //Point for the trajectory
    private Map<GeoPosition, Double> list_way_points; //list of way points
    private final List<GeoPosition> wayPoints; //Visualise the wayPoints -> If empty no way points to visualise
    private RoutePainter routePainter; //Painter fro visualise the route
    private GeoPosition previousPosition; //previous position used to compute the angle
    private Double alpha; //threshold angle

    /**
     * constructor for test
     */
    public View(){
        this.listPoint = new LinkedHashMap<>();
//        this.mapFocus = new GeoPosition(50,  7, 0, 8, 41, 0);
//        this.listPoint.put(new GeoPosition(50,  5, 0, 8, 14, 0), 1d);
//        this.listPoint.put(new GeoPosition(50,  0, 0, 8, 16, 0), 1.5d);
//        this.listPoint.put(new GeoPosition(49, 52, 0, 8, 39, 0), 1.8d);
//        this.listPoint.put(new GeoPosition(50,  6, 0, 8, 46, 0), 2d);
        this.mapFocus = null;
        this.mapViewer = new JXMapViewer();
        this.circleWayPointPainter = new WaypointPainter<>();
        this.waypointWaypointPainter = new WaypointPainter<>();
        this.trajectory = new ArrayList<>();
        this.wayPoints = new ArrayList<>();
        this.routePainter = null;
        this.previousPosition = null;
        this.alpha = 180.0;
        this.list_way_points = new HashMap<>();
    }

    /**
     * Contructor with the first list of POI
     * @param poiList list of POI to show
     * @param root root of the world
     */
    public View(List<POI> poiList, Point root){
        this.listPoint = new HashMap<>();
        this.mapFocus = new GeoPosition(root.getLatitude(), root.getLongitude());
        poiList.stream().forEach(poi -> this.listPoint.put(new GeoPosition(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()), poi.getCharge()));
        this.mapViewer = new JXMapViewer();
        this.circleWayPointPainter = new WaypointPainter<>();
        this.waypointWaypointPainter = new WaypointPainter<>();
        this.trajectory = new ArrayList<>();
        this.wayPoints = new ArrayList<>();
        this.routePainter = null;
        this.previousPosition = null;
        this.alpha = 180.0;
        this.list_way_points = new HashMap<>();
    }


    /**
     * Contructor with the first list of POI
     * @param poiList list of POI to show
     * @param root root of the world
     * @param firstPoint first point of the trajectory
     */
    public View(List<POI> poiList, Point root, Point firstPoint){
        this.listPoint = new HashMap<>();
        this.mapFocus = new GeoPosition(root.getLatitude(), root.getLongitude());
        poiList.stream().forEach(poi -> this.listPoint.put(new GeoPosition(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()), poi.getCharge()));
        this.mapViewer = new JXMapViewer();
        this.circleWayPointPainter = new WaypointPainter<>();
        this.waypointWaypointPainter = new WaypointPainter<>();
        this.trajectory = new ArrayList<>();
        this.trajectory.add(new GeoPosition(firstPoint.getLatitude(), firstPoint.getLongitude()));
        this.wayPoints = new ArrayList<>();
        this.routePainter = null;
        this.previousPosition = null;
        this.alpha = 180.0;
        this.list_way_points = new HashMap<>();
    }

    /**
     * Contructor with the first list of POI
     * @param poiList list of POI to show
     * @param root root of the world
     * @param firstPoint first point of the trajectory
     * @param alpha threshold angle
     */
    public View(List<POI> poiList, Point root, Point firstPoint, Double alpha){
        this.listPoint = new HashMap<>();
        this.mapFocus = new GeoPosition(root.getLatitude(), root.getLongitude());
        poiList.stream().forEach(poi -> this.listPoint.put(new GeoPosition(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()), poi.getCharge()));
        this.mapViewer = new JXMapViewer();
        this.circleWayPointPainter = new WaypointPainter<>();
        this.waypointWaypointPainter = new WaypointPainter<>();
        this.trajectory = new ArrayList<>();
        this.trajectory.add(new GeoPosition(firstPoint.getLatitude(), firstPoint.getLongitude()));
        this.wayPoints = new ArrayList<>();
        this.routePainter = null;
        this.previousPosition = null;
        this.alpha = alpha;
        this.list_way_points = new HashMap<>();
    }

    /**
     * Set the waypoints
     * @param wp lis of waypoints
     */
    public void setWayPoint(List<Point> wp){
        this.list_way_points.clear();
        wp.stream().forEach(point -> this.list_way_points.put(new GeoPosition(point.getLatitude(), point.getLongitude()), 40d));
        // Create waypoints from the geo-positions
        Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>();
        this.list_way_points.forEach((pos, charge) -> {
            String uniqueID = UUID.randomUUID().toString();
            waypoints.add(new MyWaypoint(uniqueID, Color.GREEN, pos, charge));
        });
        // Create a waypoint painter that takes all the waypoints
        this.waypointWaypointPainter.setWaypoints(waypoints);
        this.waypointWaypointPainter.setRenderer(new CircleWaypointRenderer());

        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
        painters.add(this.routePainter);
        painters.add(this.circleWayPointPainter);
        painters.add(this.waypointWaypointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        this.mapViewer.removeAll();
        this.mapViewer.setOverlayPainter(painter);
        this.mapViewer.repaint();

        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the charge of the poi to visualise
     * I am changing the diametre of the point
     * @param poiList list of the new poi with the new charges
     */
    public void setNewPOI(List<POI> poiList){


        // NewValue = (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin
        Double NewMax = 255.0;
        Double NewMin = 0.0;

        List<Double> values = new ArrayList<>();
        poiList.stream().forEach(poi -> values.add(poi.getCharge()));

        Double OldMin = values.stream().min(Comparator.naturalOrder()).get();
        Double OldMax = values.stream().max(Comparator.naturalOrder()).get();

        this.circleWayPointPainter.getWaypoints().stream().forEach(myWaypoint -> {
            Double charge = poiList.stream().filter(poi -> poi.getLocation().equals(myWaypoint.getPointPosition())).findFirst().get().getCharge();
            Double percent = (charge - OldMin) / (OldMax - OldMin);
//            myWaypoint.setDiameter((((charge - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin);
            myWaypoint.setDiameter(percent * (NewMax - NewMin) + NewMin);
        });

        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
        painters.add(this.routePainter);
        painters.add(this.circleWayPointPainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        this.mapViewer.removeAll();
        this.mapViewer.setOverlayPainter(painter);
        this.mapViewer.repaint();


        List<Double> values2 = new ArrayList<>();
        this.circleWayPointPainter.getWaypoints().stream().forEach(poi -> values2.add(poi.getDiameter()));
//        System.out.println(values2.toString());
    }

    /**
     * add a new point to the list of the trajectory
     * @param point point to add
     */
    public void addPointTrajectory(Point point){
        GeoPosition pos = new GeoPosition(point.getLatitude(), point.getLongitude());
        this.trajectory.add(pos);
        this.routePainter.addPos(pos);


        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);


        //Here I should also compute the angle of the movement
        //I need current position and previous position
        if (this.previousPosition == null){
            this.previousPosition = pos;
        }else{
            //I have the previous position so I can compute the angle

//            Double angle = Math.toDegrees(Math.atan2(pos.getLongitude() - this.previousPosition.getLongitude(), pos.getLatitude() - this.previousPosition.getLongitude()));
//            Double threshold = this.alpha / 2;
//
//            //Double distance = Math.sqrt(Math.pow(pos.getLatitude() - this.previousPosition.getLatitude(), 2) + Math.pow(pos.getLongitude() - this.previousPosition.getLongitude(), 2));
//            Double distance = 1d;
//
//            //find first of the two lines
//            //angle + threshold
//            //think about if more than 360
//            Double upperAngle = angle + threshold;
//            if(upperAngle > 360){
//                upperAngle = upperAngle - 360;
//            }
//
//            //x = origin + radius (distance) * cos (alpha)
//            //y = origin + radius (distance) * sin (alpha)
//            Double x = this.previousPosition.getLatitude() + distance * Math.cos(Math.toRadians(upperAngle));
//            Double y = this.previousPosition.getLongitude() + distance * Math.sin(Math.toRadians(upperAngle));
//
//            GeoPosition upPosition = new GeoPosition(x,y);
//
//            //find second point
//            //think about if it is less than 0
//            Double lowerAngle = angle - threshold;
//            if(lowerAngle < 0){
//                lowerAngle = 360 + lowerAngle;
//            }
//            Double x1 = this.previousPosition.getLatitude() + distance * Math.cos(Math.toRadians(lowerAngle));
//            Double y1 = this.previousPosition.getLongitude() + distance * Math.sin(Math.toRadians(lowerAngle));
//
//            GeoPosition downPosition = new GeoPosition(x1,y1);
//
//
//
//            //In the end I have to update it anyway
//            this.previousPosition = pos;
//
//            painters.add(new AnglePainter(upPosition, downPosition, pos));
        }


//
//        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//        this.mapViewer.repaint();
    }

    /**
     * Set the focus of the map
     * @param mapFocus center point of the map
     */
    public void setMapFocus(Point mapFocus) { this.mapFocus = new GeoPosition(mapFocus.getLatitude(), mapFocus.getLongitude()); }

    /**
     * Set the first list of the POI
     * @param poiList list of POI to visualise
     */
    public void setListPoint(List<POI> poiList) { poiList.stream().forEach(poi -> this.listPoint.put(new GeoPosition(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()), poi.getCharge())); }

    /**
     * Set threshold angle
     * @param alpha angle
     */
    public void setAlpha(Double alpha) { this.alpha = alpha; }

    /**
     * visualise the map of everything
     */
    public void showMap(){
        if (this.mapFocus == null || this.listPoint.size() == 0){
            throw new Error("map focus or list point is missing!");
        }
        // Create a TileFactoryInfo for Virtual Earth
        TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(8);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        LocalResponseCache.installResponseCache(info.getBaseURL(), cacheDir, false);

        // Setup JXMapViewer

        this.mapViewer.setTileFactory(tileFactory);



        // Set the focus
        this.mapViewer.setZoom(8);
        this.mapViewer.setAddressLocation(this.mapFocus);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(this.mapViewer);
        this.mapViewer.addMouseListener(mia);
        this.mapViewer.addMouseMotionListener(mia);
        this.mapViewer.addMouseListener(new CenterMapListener(this.mapViewer));
        this.mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(this.mapViewer));
        this.mapViewer.addKeyListener(new PanKeyListener(this.mapViewer));



        // Create waypoints from the geo-positions
        Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>();
        this.listPoint.forEach((pos, charge) -> {
            String uniqueID = UUID.randomUUID().toString();
            waypoints.add(new MyWaypoint(uniqueID, Color.BLACK, pos, charge));
        });

        // Create a waypoint painter that takes all the waypoints
        this.circleWayPointPainter.setWaypoints(waypoints);
        this.circleWayPointPainter.setRenderer(new CircleWaypointRenderer());

        //this.mapViewer.setOverlayPainter(this.circleWayPointPainter);

        this.routePainter = new RoutePainter(this.trajectory);


        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
        painters.add(this.routePainter);
        painters.add(this.circleWayPointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        this.mapViewer.setOverlayPainter(painter);



        // Display the viewer in a JFrame
        JFrame frame = new JFrame("Viewer for lgds_idsa");
        frame.getContentPane().add(this.mapViewer);
        frame.setSize(1440, 900);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



    public static void main(String[] args){
        View v = new View();
//        v.addPoint(39.987717,116.351772);
//        v.addPoint(39.967806,116.10154);
//        v.addPoint(39.9060483,116.2647166);
//        v.addPoint(40.076065,116.3234216);
//        v.addPoint(39.9859649,116.301);

        //v.addPoint(39.987717,116.351772); v.addPoint(40.5729666,116.7917116); v.addPoint(39.6429066,116.6541216); v.addPoint(39.9513016,116.2735116); v.addPoint(40.0857516,116.32605830);
        //v.addPoint(39.987717,116.351772); v.addPoint(40.5729666,116.7917116); v.addPoint(39.6429066,116.6541216); v.addPoint(39.9513016,116.2735116); v.addPoint(40.0857516,116.3260583);
//        v.addPoint(39.987717,116.351772); v.addPoint(40.5729666,116.7917116); v.addPoint(39.6429066,116.6541216); v.addPoint(39.9513016,116.2735116); v.addPoint(40.0857516,116.3260583); v.addPoint(39.9130233,116.6124199); v.addPoint(39.990635,116.2980416); v.addPoint(39.8740899,116.4462383);


        v.addPoint(39.987717,116.351772); v.addPoint(40.5729666,116.7917116); v.addPoint(39.6429066,116.6541216); v.addPoint(39.9513016,116.2735116); v.addPoint(40.0857516,116.3260583); v.addPoint(39.912876600000004,116.6124216); v.addPoint(39.990635,116.2980416); v.addPoint(39.8740899,116.4462383); v.addPoint(39.9931,116.440646); v.addPoint(39.976158,116.330731); v.addPoint(39.9754633,116.3318749); v.addPoint(40.0274549,116.4326599); v.addPoint(39.9868652425,116.448452661); v.addPoint(39.95769,116.3148766); v.addPoint(39.935096,116.431177); v.addPoint(39.9811633,116.3279); v.addPoint(39.974553212,116.443829096); v.addPoint(39.97547860555556,116.33057219444441); v.addPoint(39.927290199999995,116.4708541); v.addPoint(39.9579633,116.2856399); v.addPoint(39.95347,116.796754); v.addPoint(41.0148649,117.944655); v.addPoint(40.09335,116.483166666667); v.addPoint(40.076942953333344,116.32560964); v.addPoint(39.974575,116.3126083); v.addPoint(39.797798,116.518513); v.addPoint(39.979118959,116.303465459); v.addPoint(39.907610000000005,116.47161249999999); v.addPoint(39.971736,116.44123966666666); v.addPoint(39.9873666,116.3039216); v.addPoint(40.039063,116.32531); v.addPoint(40.0311583,116.4129266); v.addPoint(40.012029,116.321898); v.addPoint(40.040804,116.322007); v.addPoint(39.9366316,116.3478233); v.addPoint(39.9999049,116.3259966); v.addPoint(39.9423799,116.3013699); v.addPoint(39.9722766666667,116.3127); v.addPoint(39.999556600000005,116.37604160000001); v.addPoint(40.0866166666667,116.326916666667); v.addPoint(39.9902333333333,116.2977); v.addPoint(39.900523,116.386727); v.addPoint(39.964332,116.416241); v.addPoint(39.987783,116.447587); v.addPoint(39.987642,116.448986); v.addPoint(39.987714,116.352808); v.addPoint(39.96917,116.419924); v.addPoint(39.9481666666667,116.26615); v.addPoint(39.9363549,116.3475199); v.addPoint(31.5787266,120.3140566); v.addPoint(39.9748649,116.3316583); v.addPoint(39.9315883,116.4415683); v.addPoint(40.4601716,116.2701966); v.addPoint(39.9905199,116.4009716); v.addPoint(39.9801616,116.3511383); v.addPoint(39.9055483,116.2664983); v.addPoint(40.0619416,116.2530683); v.addPoint(22.6085766,114.0181299); v.addPoint(25.8031283,114.8817516); v.addPoint(40.0326983,116.3563433); v.addPoint(40.0236383,116.3555516); v.addPoint(39.9711433,116.30367); v.addPoint(39.9668716,116.3193333); v.addPoint(31.2196166666667,121.40255); v.addPoint(39.9211133,116.3015883); v.addPoint(40.181033,116.395099); v.addPoint(40.011262,116.323401); v.addPoint(39.9753,116.332716666667); v.addPoint(39.9862949,116.3160616); v.addPoint(40.0310916,116.3146966); v.addPoint(39.939481,116.348053); v.addPoint(40.0013666666667,116.323933333333); v.addPoint(40.031101,116.420205); v.addPoint(39.9924883,116.4143283); v.addPoint(39.9069433,116.2665866);
//
//        v.addPoint(39.987717,116.351772); v.addPoint(40.5729666,116.7917116); v.addPoint(39.6429066,116.6541216); v.addPoint(39.9513016,116.2735116); v.addPoint(40.0857516,116.3260583); v.addPoint(39.912876600000004,116.6124216); v.addPoint(39.990635,116.2980416); v.addPoint(39.8740899,116.4462383); v.addPoint(39.9931,116.440646); v.addPoint(39.976158,116.330731); v.addPoint(39.9754633,116.3318749); v.addPoint(40.02738436666667,116.43257326666667); v.addPoint(39.987009043375,116.44866061475); v.addPoint(39.95769,116.3148766); v.addPoint(39.935096,116.431177); v.addPoint(39.9811633,116.3279); v.addPoint(39.974553212,116.443829096); v.addPoint(39.975507609876544,116.33060865432097); v.addPoint(39.92724105,116.47094593); v.addPoint(39.9579633,116.2856399); v.addPoint(39.95347,116.796754); v.addPoint(41.0148649,117.944655); v.addPoint(40.09335,116.483166666667); v.addPoint(40.07697566666668,116.32559425714285); v.addPoint(39.974575,116.3126083); v.addPoint(39.797798,116.518513); v.addPoint(39.979118959,116.303465459); v.addPoint(39.907614,116.47167133333333); v.addPoint(39.97171536657144,116.441340847); v.addPoint(39.9873666,116.3039216); v.addPoint(40.039063,116.32531); v.addPoint(40.0311583,116.4129266); v.addPoint(40.012029,116.321898); v.addPoint(40.040804,116.322007); v.addPoint(39.9366316,116.3478233); v.addPoint(39.9999049,116.3259966); v.addPoint(39.9423799,116.3013699); v.addPoint(39.9722766666667,116.3127); v.addPoint(39.999561033333336,116.3760555); v.addPoint(40.0866166666667,116.326916666667); v.addPoint(39.9902333333333,116.2977); v.addPoint(39.900523,116.386727); v.addPoint(39.964332,116.416241); v.addPoint(39.987783,116.447587); v.addPoint(39.987694000000005,116.448995); v.addPoint(39.987714,116.352808); v.addPoint(39.96917,116.419924); v.addPoint(39.9481666666667,116.26615); v.addPoint(39.9363549,116.3475199); v.addPoint(31.5787266,120.3140566); v.addPoint(39.97469245,116.3317233); v.addPoint(39.9315883,116.4415683); v.addPoint(40.4601716,116.2701966); v.addPoint(39.9905199,116.4009716); v.addPoint(39.9801616,116.3511383); v.addPoint(39.9055483,116.2664983); v.addPoint(40.0619416,116.2530683); v.addPoint(22.6085766,114.0181299); v.addPoint(25.8031283,114.8817516); v.addPoint(40.0326983,116.3563433); v.addPoint(40.0236383,116.3555516); v.addPoint(39.97116495,116.3037083); v.addPoint(39.9668716,116.3193333); v.addPoint(31.2196166666667,121.40255); v.addPoint(39.9211133,116.3015883); v.addPoint(40.181033,116.395099); v.addPoint(40.011262,116.323401); v.addPoint(39.9753,116.332716666667); v.addPoint(39.9862949,116.3160616); v.addPoint(40.0310916,116.3146966); v.addPoint(39.939481,116.348053); v.addPoint(40.0013666666667,116.323933333333); v.addPoint(40.031101,116.420205); v.addPoint(39.99252995,116.41450245); v.addPoint(39.9068058,116.26658075); v.addPoint(39.9677945,116.1015375); v.addPoint(39.9060483,116.2647166); v.addPoint(40.0760372,116.32326163333335); v.addPoint(39.9859649,116.301); v.addPoint(39.9871616,116.303138675); v.addPoint(39.9797783333333,116.305656666667); v.addPoint(39.9073983,116.4709833); v.addPoint(39.9999528,116.3273946); v.addPoint(39.9100216,116.265975); v.addPoint(40.007623,116.319684); v.addPoint(39.974126,116.32719); v.addPoint(40.040087,116.321461); v.addPoint(39.9561538696289,116.275352478027); v.addPoint(40.005111,116.325819); v.addPoint(39.942268,116.196952); v.addPoint(40.0020333,116.3328449); v.addPoint(39.991376,116.326173); v.addPoint(39.9867133,116.3034366); v.addPoint(40.007738,116.318767); v.addPoint(40.06955,116.330233333333); v.addPoint(30.4754999,114.3582849); v.addPoint(39.9745833333333,116.321833333333); v.addPoint(39.895335,116.453059); v.addPoint(39.991125,116.327613); v.addPoint(40.0586083,116.4090933); v.addPoint(39.90929,116.3878116); v.addPoint(39.914317,116.351914); v.addPoint(40.0158666666667,116.436933333333); v.addPoint(40.006761,116.318794); v.addPoint(35.7151833333333,104.5252); v.addPoint(40.010828,116.322798); v.addPoint(40.0309585,116.42089100000001); v.addPoint(40.0710533,116.2994766); v.addPoint(39.9022783,116.4124949); v.addPoint(39.9145683,116.4654033); v.addPoint(40.967414,117.929573); v.addPoint(39.976242,116.429206); v.addPoint(40.0083166666667,116.47805); v.addPoint(39.9750166666667,116.32925); v.addPoint(40.010844,116.321537); v.addPoint(39.974198,116.33319); v.addPoint(39.900794,116.387233); v.addPoint(39.9880949,116.3034083); v.addPoint(30.6606882,104.0920516); v.addPoint(39.978545,116.331771666667); v.addPoint(40.1511516,116.0342799); v.addPoint(39.9731,116.338216666667); v.addPoint(40.006159,116.320425); v.addPoint(39.98065,116.328266666667); v.addPoint(39.988069,116.353305); v.addPoint(39.9887516,116.3063283); v.addPoint(26.389103,111.620315); v.addPoint(39.9269105,116.4719); v.addPoint(40.013511,116.296545); v.addPoint(40.0731383,116.3280633); v.addPoint(39.855362,116.433503); v.addPoint(39.8515716,116.3467833); v.addPoint(40.059869,116.401531); v.addPoint(40.9849183,117.4866133); v.addPoint(40.004874,116.331766); v.addPoint(32.926034,117.378946); v.addPoint(40.0159683,116.3414033); v.addPoint(39.9596333,116.29866); v.addPoint(40.074225,116.324894); v.addPoint(40.5272233,116.1584933); v.addPoint(40.064697,116.4058); v.addPoint(39.987297,116.449409); v.addPoint(39.135906,117.216848); v.addPoint(40.047012,116.32472); v.addPoint(39.999943,116.326806); v.addPoint(22.813629,108.334569); v.addPoint(39.971566,116.325046); v.addPoint(39.9847683,116.3195033); v.addPoint(39.97644996666665,116.33013331666649); v.addPoint(39.9904449,116.3279233); v.addPoint(39.9731966,116.2945116); v.addPoint(39.976652,116.331349); v.addPoint(39.967072,116.345204); v.addPoint(39.979931737,116.307554281); v.addPoint(39.9781733333333,116.332243333333); v.addPoint(39.9876233,116.3033249); v.addPoint(39.988845,116.390735); v.addPoint(40.003374,116.343822); v.addPoint(39.995616,116.393936); v.addPoint(39.969585,116.3075966); v.addPoint(39.972281,116.442085); v.addPoint(39.98908,116.3085799); v.addPoint(39.907966,116.472224); v.addPoint(39.98059,116.30875); v.addPoint(40.004417,116.554023); v.addPoint(39.97787,116.331271666667); v.addPoint(40.0414583,116.4291349); v.addPoint(39.9342833333333,116.319866666667); v.addPoint(39.996682,116.417218); v.addPoint(39.952362,116.801845); v.addPoint(39.970572,116.422138); v.addPoint(39.985902,116.348553); v.addPoint(36.059797,103.796352); v.addPoint(39.9774,116.330545); v.addPoint(39.97869,116.332505); v.addPoint(39.971631,116.304682); v.addPoint(39.97855,116.304845); v.addPoint(40.0870016,116.3160199); v.addPoint(39.992665,116.456291); v.addPoint(39.9826033,116.308785); v.addPoint(39.9067683,116.2657833); v.addPoint(39.9673666666667,116.293916666667); v.addPoint(40.0762199,116.3241116); v.addPoint(40.065989,116.404997); v.addPoint(40.0122199,116.258925); v.addPoint(39.9477283,116.4008566); v.addPoint(40.0166616,116.3417583); v.addPoint(39.84753,116.3910833); v.addPoint(39.98859,116.30257); v.addPoint(39.911855,116.4525433); v.addPoint(39.998589,116.357821); v.addPoint(39.928612,116.472511); v.addPoint(40.00375,116.3178083); v.addPoint(32.94685,117.358716666667); v.addPoint(39.94929,116.265967); v.addPoint(39.9685833333333,116.3043); v.addPoint(40.07204,116.416901); v.addPoint(39.9866499,116.3027633); v.addPoint(39.9436099,116.4002549); v.addPoint(39.85955825,116.2575516); v.addPoint(39.9889916,116.3101766); v.addPoint(40.001534,116.34707); v.addPoint(40.0639716,116.0605916); v.addPoint(40.028918,116.411497); v.addPoint(40.063802,116.406005); v.addPoint(39.90017,116.386632); v.addPoint(40.010081,116.314264); v.addPoint(39.979545711,116.303139393); v.addPoint(39.9791183333333,116.304823333333); v.addPoint(39.8591033,116.4063383);
//        v.addPoint(39.987717,116.351772); v.addPoint(40.0857516,116.3260583); v.addPoint(39.912876600000004,116.6124216); v.addPoint(39.976158,116.330731); v.addPoint(39.935096,116.431177); v.addPoint(39.974553212,116.443829096); v.addPoint(39.97547860555556,116.33057219444441); v.addPoint(41.0148649,117.944655); v.addPoint(40.0311583,116.4129266); v.addPoint(40.012029,116.321898); v.addPoint(40.040804,116.322007); v.addPoint(39.9366316,116.3478233); v.addPoint(39.9722766666667,116.3127); v.addPoint(39.9902333333333,116.2977); v.addPoint(39.900523,116.386727); v.addPoint(39.987783,116.447587); v.addPoint(39.987714,116.352808); v.addPoint(39.9905199,116.4009716); v.addPoint(22.6085766,114.0181299); v.addPoint(25.8031283,114.8817516); v.addPoint(39.9711433,116.30367); v.addPoint(39.9668716,116.3193333); v.addPoint(40.011262,116.323401); v.addPoint(40.0013666666667,116.323933333333); v.addPoint(39.9924883,116.4143283); v.addPoint(39.9069433,116.26658660);

        List<GeoPosition> list = new ArrayList<>();
        //list.add(new GeoPosition(39.981285,116.345558)); list.add(new GeoPosition(39.981316,116.345566)); list.add(new GeoPosition(39.981368,116.345423)); list.add(new GeoPosition(39.981398,116.345338)); list.add(new GeoPosition(39.981405,116.345274)); list.add(new GeoPosition(39.981396,116.345197)); list.add(new GeoPosition(39.981407,116.345097)); list.add(new GeoPosition(39.981469,116.34502)); list.add(new GeoPosition(39.981515,116.344944)); list.add(new GeoPosition(39.981573,116.344893)); list.add(new GeoPosition(39.981599,116.344961)); list.add(new GeoPosition(39.981612,116.344951)); list.add(new GeoPosition(39.981638,116.344962)); list.add(new GeoPosition(39.981651,116.344968)); list.add(new GeoPosition(39.981708,116.344855)); list.add(new GeoPosition(39.981789,116.344749)); list.add(new GeoPosition(39.981821,116.344694)); list.add(new GeoPosition(39.981823,116.344593)); list.add(new GeoPosition(39.981869,116.344464)); list.add(new GeoPosition(39.9818,116.344405)); list.add(new GeoPosition(39.981801,116.344354)); list.add(new GeoPosition(39.981911,116.344191)); list.add(new GeoPosition(39.981942,116.344102)); list.add(new GeoPosition(39.981964,116.344068)); list.add(new GeoPosition(39.981991,116.344046)); list.add(new GeoPosition(39.98201,116.343949)); list.add(new GeoPosition(39.981931,116.343746)); list.add(new GeoPosition(39.981959,116.343635)); list.add(new GeoPosition(39.981968,116.343566)); list.add(new GeoPosition(39.981886,116.343496)); list.add(new GeoPosition(39.981967,116.343393)); list.add(new GeoPosition(39.981943,116.343232)); list.add(new GeoPosition(39.981984,116.343196)); list.add(new GeoPosition(39.981987,116.343155)); list.add(new GeoPosition(39.982026,116.343165)); list.add(new GeoPosition(39.982102,116.343189)); list.add(new GeoPosition(39.982126,116.343221)); list.add(new GeoPosition(39.982142,116.343138)); list.add(new GeoPosition(39.982195,116.343089)); list.add(new GeoPosition(39.98215,116.342936)); list.add(new GeoPosition(39.982136,116.34291)); list.add(new GeoPosition(39.982159,116.342781)); list.add(new GeoPosition(39.982172,116.342675)); list.add(new GeoPosition(39.982168,116.342515)); list.add(new GeoPosition(39.982139,116.342239)); list.add(new GeoPosition(39.981924,116.341878)); list.add(new GeoPosition(39.981953,116.341702)); list.add(new GeoPosition(39.981905,116.341615)); list.add(new GeoPosition(39.98192,116.341499)); list.add(new GeoPosition(39.981771,116.341375)); list.add(new GeoPosition(39.981815,116.341302)); list.add(new GeoPosition(39.981842,116.341221)); list.add(new GeoPosition(39.981907,116.341181)); list.add(new GeoPosition(39.981876,116.341118)); list.add(new GeoPosition(39.981924,116.34109)); list.add(new GeoPosition(39.98199,116.341044)); list.add(new GeoPosition(39.981984,116.340936)); list.add(new GeoPosition(39.981996,116.340813)); list.add(new GeoPosition(39.982065,116.340664)); list.add(new GeoPosition(39.982193,116.340586)); list.add(new GeoPosition(39.982115,116.340464)); list.add(new GeoPosition(39.982044,116.340434)); list.add(new GeoPosition(39.982003,116.340403)); list.add(new GeoPosition(39.981923,116.340317)); list.add(new GeoPosition(39.981826,116.34022)); list.add(new GeoPosition(39.98161,116.340279)); list.add(new GeoPosition(39.981439,116.340362)); list.add(new GeoPosition(39.981353,116.340293)); list.add(new GeoPosition(39.981299,116.340132)); list.add(new GeoPosition(39.981222,116.34)); list.add(new GeoPosition(39.981184,116.339946)); list.add(new GeoPosition(39.981082,116.339948)); list.add(new GeoPosition(39.981016,116.33984)); list.add(new GeoPosition(39.980955,116.339826)); list.add(new GeoPosition(39.980858,116.339852)); list.add(new GeoPosition(39.980774,116.339844)); list.add(new GeoPosition(39.980748,116.339814)); list.add(new GeoPosition(39.980737,116.339807)); list.add(new GeoPosition(39.980646,116.339822)); list.add(new GeoPosition(39.980553,116.339824)); list.add(new GeoPosition(39.980424,116.339816)); list.add(new GeoPosition(39.980482,116.339665)); list.add(new GeoPosition(39.980491,116.339585)); list.add(new GeoPosition(39.980466,116.339494)); list.add(new GeoPosition(39.980419,116.339406)); list.add(new GeoPosition(39.980398,116.339343)); list.add(new GeoPosition(39.980404,116.339316)); list.add(new GeoPosition(39.980408,116.339301)); list.add(new GeoPosition(39.98041,116.339291)); list.add(new GeoPosition(39.980406,116.339311)); list.add(new GeoPosition(39.980406,116.339311)); list.add(new GeoPosition(39.980407,116.339305)); list.add(new GeoPosition(39.980433,116.339295)); list.add(new GeoPosition(39.980461,116.339289)); list.add(new GeoPosition(39.980474,116.339283)); list.add(new GeoPosition(39.980474,116.339283)); list.add(new GeoPosition(39.980476,116.339276)); list.add(new GeoPosition(39.980479,116.339264)); list.add(new GeoPosition(39.980425,116.339247)); list.add(new GeoPosition(39.9804,116.33929)); list.add(new GeoPosition(39.980413,116.339334)); list.add(new GeoPosition(39.980449,116.339416)); list.add(new GeoPosition(39.980805,116.340038)); list.add(new GeoPosition(39.981583,116.341358)); list.add(new GeoPosition(39.982485,116.342931)); list.add(new GeoPosition(39.983473,116.344615)); list.add(new GeoPosition(39.984591,116.346505)); list.add(new GeoPosition(39.985692,116.348398)); list.add(new GeoPosition(39.986783,116.350271)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772));

        //list.add(new GeoPosition(39.981285,116.345558)); list.add(new GeoPosition(39.981316,116.345566)); list.add(new GeoPosition(39.981368,116.345423)); list.add(new GeoPosition(39.981398,116.345338)); list.add(new GeoPosition(39.981405,116.345274)); list.add(new GeoPosition(39.981396,116.345197)); list.add(new GeoPosition(39.981407,116.345097)); list.add(new GeoPosition(39.981469,116.34502)); list.add(new GeoPosition(39.981515,116.344944)); list.add(new GeoPosition(39.981573,116.344893)); list.add(new GeoPosition(39.981599,116.344961)); list.add(new GeoPosition(39.981612,116.344951)); list.add(new GeoPosition(39.981638,116.344962)); list.add(new GeoPosition(39.981651,116.344968)); list.add(new GeoPosition(39.981708,116.344855)); list.add(new GeoPosition(39.981789,116.344749)); list.add(new GeoPosition(39.981821,116.344694)); list.add(new GeoPosition(39.981823,116.344593)); list.add(new GeoPosition(39.981869,116.344464)); list.add(new GeoPosition(39.9818,116.344405)); list.add(new GeoPosition(39.981801,116.344354)); list.add(new GeoPosition(39.981911,116.344191)); list.add(new GeoPosition(39.981942,116.344102)); list.add(new GeoPosition(39.981964,116.344068)); list.add(new GeoPosition(39.981991,116.344046)); list.add(new GeoPosition(39.98201,116.343949)); list.add(new GeoPosition(39.981931,116.343746)); list.add(new GeoPosition(39.981959,116.343635)); list.add(new GeoPosition(39.981968,116.343566)); list.add(new GeoPosition(39.981886,116.343496)); list.add(new GeoPosition(39.981967,116.343393)); list.add(new GeoPosition(39.981943,116.343232)); list.add(new GeoPosition(39.981984,116.343196)); list.add(new GeoPosition(39.981987,116.343155)); list.add(new GeoPosition(39.982026,116.343165)); list.add(new GeoPosition(39.982102,116.343189)); list.add(new GeoPosition(39.982126,116.343221)); list.add(new GeoPosition(39.982142,116.343138)); list.add(new GeoPosition(39.982195,116.343089)); list.add(new GeoPosition(39.98215,116.342936)); list.add(new GeoPosition(39.982136,116.34291)); list.add(new GeoPosition(39.982159,116.342781)); list.add(new GeoPosition(39.982172,116.342675)); list.add(new GeoPosition(39.982168,116.342515)); list.add(new GeoPosition(39.982139,116.342239)); list.add(new GeoPosition(39.981924,116.341878)); list.add(new GeoPosition(39.981953,116.341702)); list.add(new GeoPosition(39.981905,116.341615)); list.add(new GeoPosition(39.98192,116.341499)); list.add(new GeoPosition(39.981771,116.341375)); list.add(new GeoPosition(39.981815,116.341302)); list.add(new GeoPosition(39.981842,116.341221)); list.add(new GeoPosition(39.981907,116.341181)); list.add(new GeoPosition(39.981876,116.341118)); list.add(new GeoPosition(39.981924,116.34109)); list.add(new GeoPosition(39.98199,116.341044)); list.add(new GeoPosition(39.981984,116.340936)); list.add(new GeoPosition(39.981996,116.340813)); list.add(new GeoPosition(39.982065,116.340664)); list.add(new GeoPosition(39.982193,116.340586)); list.add(new GeoPosition(39.982115,116.340464)); list.add(new GeoPosition(39.982044,116.340434)); list.add(new GeoPosition(39.982003,116.340403)); list.add(new GeoPosition(39.981923,116.340317)); list.add(new GeoPosition(39.981826,116.34022)); list.add(new GeoPosition(39.98161,116.340279)); list.add(new GeoPosition(39.981439,116.340362)); list.add(new GeoPosition(39.981353,116.340293)); list.add(new GeoPosition(39.981299,116.340132)); list.add(new GeoPosition(39.981222,116.34)); list.add(new GeoPosition(39.981184,116.339946)); list.add(new GeoPosition(39.981082,116.339948)); list.add(new GeoPosition(39.981016,116.33984)); list.add(new GeoPosition(39.980955,116.339826)); list.add(new GeoPosition(39.980858,116.339852)); list.add(new GeoPosition(39.980774,116.339844)); list.add(new GeoPosition(39.980748,116.339814)); list.add(new GeoPosition(39.980737,116.339807)); list.add(new GeoPosition(39.980646,116.339822)); list.add(new GeoPosition(39.980553,116.339824)); list.add(new GeoPosition(39.980424,116.339816)); list.add(new GeoPosition(39.980482,116.339665)); list.add(new GeoPosition(39.980491,116.339585)); list.add(new GeoPosition(39.980466,116.339494)); list.add(new GeoPosition(39.980419,116.339406)); list.add(new GeoPosition(39.980398,116.339343)); list.add(new GeoPosition(39.980404,116.339316)); list.add(new GeoPosition(39.980408,116.339301)); list.add(new GeoPosition(39.98041,116.339291)); list.add(new GeoPosition(39.980406,116.339311)); list.add(new GeoPosition(39.980406,116.339311)); list.add(new GeoPosition(39.980407,116.339305)); list.add(new GeoPosition(39.980433,116.339295)); list.add(new GeoPosition(39.980461,116.339289)); list.add(new GeoPosition(39.980474,116.339283)); list.add(new GeoPosition(39.980474,116.339283)); list.add(new GeoPosition(39.980476,116.339276)); list.add(new GeoPosition(39.980479,116.339264)); list.add(new GeoPosition(39.980425,116.339247)); list.add(new GeoPosition(39.9804,116.33929)); list.add(new GeoPosition(39.980413,116.339334)); list.add(new GeoPosition(39.980449,116.339416)); list.add(new GeoPosition(39.980805,116.340038)); list.add(new GeoPosition(39.981583,116.341358)); list.add(new GeoPosition(39.982485,116.342931)); list.add(new GeoPosition(39.983473,116.344615)); list.add(new GeoPosition(39.984591,116.346505)); list.add(new GeoPosition(39.985692,116.348398)); list.add(new GeoPosition(39.986783,116.350271)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772)); list.add(new GeoPosition(39.987717,116.351772));

//        list.add(new GeoPosition(39.9052066,116.5933049)); list.add(new GeoPosition(39.9076999,116.5943299)); list.add(new GeoPosition(39.9077016,116.5943299)); list.add(new GeoPosition(39.9077016,116.5943316)); list.add(new GeoPosition(39.9077099,116.5943333)); list.add(new GeoPosition(39.9077016,116.5943416)); list.add(new GeoPosition(39.9077016,116.5943416)); list.add(new GeoPosition(39.9077033,116.5943449)); list.add(new GeoPosition(39.9076983,116.5943782)); list.add(new GeoPosition(39.9076949,116.5944766)); list.add(new GeoPosition(39.9076916,116.5945983)); list.add(new GeoPosition(39.9076999,116.5947166)); list.add(new GeoPosition(39.9077283,116.5949116)); list.add(new GeoPosition(39.9077383,116.5949983)); list.add(new GeoPosition(39.9077783,116.5950416)); list.add(new GeoPosition(39.9078199,116.5950549)); list.add(new GeoPosition(39.9079233,116.5950433)); list.add(new GeoPosition(39.9079649,116.5950349)); list.add(new GeoPosition(39.9086133,116.5948883)); list.add(new GeoPosition(39.9087433,116.594855)); list.add(new GeoPosition(39.9088716,116.5948049)); list.add(new GeoPosition(39.9090133,116.5947433)); list.add(new GeoPosition(39.909145,116.594645)); list.add(new GeoPosition(39.9092616,116.5945283)); list.add(new GeoPosition(39.9095133,116.5944766)); list.add(new GeoPosition(39.9097599,116.5944283)); list.add(new GeoPosition(39.9100066,116.5943849)); list.add(new GeoPosition(39.9102333,116.5943583)); list.add(new GeoPosition(39.9104316,116.5943766)); list.add(new GeoPosition(39.9106383,116.5944733)); list.add(new GeoPosition(39.9107916,116.5946016)); list.add(new GeoPosition(39.9109566,116.5947633)); list.add(new GeoPosition(39.9110916,116.5949266)); list.add(new GeoPosition(39.9112249,116.595165)); list.add(new GeoPosition(39.911345,116.5954083)); list.add(new GeoPosition(39.9114516,116.5956683)); list.add(new GeoPosition(39.9115016,116.5959733)); list.add(new GeoPosition(39.9115283,116.5962799)); list.add(new GeoPosition(39.9115666,116.5965999)); list.add(new GeoPosition(39.9115983,116.5969233)); list.add(new GeoPosition(39.9116183,116.59725)); list.add(new GeoPosition(39.911615,116.5975899)); list.add(new GeoPosition(39.9116649,116.5979049)); list.add(new GeoPosition(39.9116783,116.5982316)); list.add(new GeoPosition(39.9116899,116.5985366)); list.add(new GeoPosition(39.9116499,116.59885)); list.add(new GeoPosition(39.911625,116.5991483)); list.add(new GeoPosition(39.91163,116.5994449)); list.add(new GeoPosition(39.9116466,116.5997533)); list.add(new GeoPosition(39.911625,116.6000833)); list.add(new GeoPosition(39.911615,116.60042)); list.add(new GeoPosition(39.9115733,116.6007566)); list.add(new GeoPosition(39.9115483,116.6010833)); list.add(new GeoPosition(39.9115766,116.6014099)); list.add(new GeoPosition(39.9115399,116.6017416)); list.add(new GeoPosition(39.9115466,116.602085)); list.add(new GeoPosition(39.9115466,116.6024366)); list.add(new GeoPosition(39.911575,116.6027883)); list.add(new GeoPosition(39.9116016,116.6031533)); list.add(new GeoPosition(39.9115866,116.6035282)); list.add(new GeoPosition(39.9115966,116.6038916)); list.add(new GeoPosition(39.9116216,116.6042349)); list.add(new GeoPosition(39.91167,116.6045733)); list.add(new GeoPosition(39.9116916,116.6049183)); list.add(new GeoPosition(39.9117133,116.6052483)); list.add(new GeoPosition(39.9117366,116.6055616)); list.add(new GeoPosition(39.91178,116.6058566)); list.add(new GeoPosition(39.9117466,116.606165)); list.add(new GeoPosition(39.9117316,116.6064649)); list.add(new GeoPosition(39.9117183,116.6067583)); list.add(new GeoPosition(39.9117299,116.6070333)); list.add(new GeoPosition(39.9117333,116.6073199)); list.add(new GeoPosition(39.9117599,116.6075916)); list.add(new GeoPosition(39.9117866,116.60786)); list.add(new GeoPosition(39.9117783,116.6081382)); list.add(new GeoPosition(39.9117783,116.6084249)); list.add(new GeoPosition(39.9117783,116.6087266)); list.add(new GeoPosition(39.9118049,116.6090366)); list.add(new GeoPosition(39.9118266,116.6093533)); list.add(new GeoPosition(39.911855,116.6096616)); list.add(new GeoPosition(39.9118699,116.6099766)); list.add(new GeoPosition(39.9118983,116.6102983)); list.add(new GeoPosition(39.911925,116.610635)); list.add(new GeoPosition(39.9119566,116.6109682)); list.add(new GeoPosition(39.91199,116.6112983)); list.add(new GeoPosition(39.9120133,116.6116233)); list.add(new GeoPosition(39.9120399,116.6119366)); list.add(new GeoPosition(39.9121032,116.6122416)); list.add(new GeoPosition(39.912135,116.6125382)); list.add(new GeoPosition(39.9121549,116.6128249)); list.add(new GeoPosition(39.9121732,116.6131033)); list.add(new GeoPosition(39.912205,116.6133583)); list.add(new GeoPosition(39.9122249,116.61359)); list.add(new GeoPosition(39.9122516,116.6138)); list.add(new GeoPosition(39.9122799,116.613985)); list.add(new GeoPosition(39.9121649,116.6141349)); list.add(new GeoPosition(39.9123233,116.6142066)); list.add(new GeoPosition(39.9125016,116.6142249)); list.add(new GeoPosition(39.9126666,116.6141966)); list.add(new GeoPosition(39.9127949,116.6141716)); list.add(new GeoPosition(39.9129183,116.6141016)); list.add(new GeoPosition(39.9130333,116.6140116)); list.add(new GeoPosition(39.9131183,116.6138649)); list.add(new GeoPosition(39.9132183,116.6137166)); list.add(new GeoPosition(39.9132633,116.6135716)); list.add(new GeoPosition(39.9133199,116.6134399)); list.add(new GeoPosition(39.9133633,116.6133199)); list.add(new GeoPosition(39.91328,116.6132016)); list.add(new GeoPosition(39.9132449,116.6131266)); list.add(new GeoPosition(39.9132433,116.6130633)); list.add(new GeoPosition(39.9132766,116.6130466)); list.add(new GeoPosition(39.9132266,116.6130416)); list.add(new GeoPosition(39.9131682,116.6130249)); list.add(new GeoPosition(39.9131366,116.6129399)); list.add(new GeoPosition(39.9131,116.612805)); list.add(new GeoPosition(39.9131016,116.6127049)); list.add(new GeoPosition(39.9131049,116.61261)); list.add(new GeoPosition(39.9131066,116.6125233)); list.add(new GeoPosition(39.9131466,116.6124633)); list.add(new GeoPosition(39.9131133,116.6124266)); list.add(new GeoPosition(39.9131033,116.6124366)); list.add(new GeoPosition(39.9130566,116.6124266)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199)); list.add(new GeoPosition(39.9130233,116.6124199));
        
        v.addTrajectory(list);

        v.test();

    }

    private void addPoint(Double a, Double b){
        this.listPoint.put(new GeoPosition(a, b), 20d);
    }

    private void addTrajectory(List<GeoPosition> list){
        list.stream().forEach(this.trajectory::add);
    }
//
//
    private void test(){
        // Create a TileFactoryInfo for Virtual Earth
        TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setThreadPoolSize(8);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        LocalResponseCache.installResponseCache(info.getBaseURL(), cacheDir, false);

        // Setup JXMapViewer

        this.mapViewer.setTileFactory(tileFactory);

        // Set the focus
        this.mapViewer.setZoom(8);
        this.mapViewer.setAddressLocation(new GeoPosition(39.9859649, 116.301));

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(this.mapViewer);
        this.mapViewer.addMouseListener(mia);
        this.mapViewer.addMouseMotionListener(mia);
        this.mapViewer.addMouseListener(new CenterMapListener(this.mapViewer));
        this.mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(this.mapViewer));
        this.mapViewer.addKeyListener(new PanKeyListener(this.mapViewer));



        // Create waypoints from the geo-positions
        Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>();

        List<Color> col = new ArrayList<>();
        col.add(Color.GREEN);
        col.add(Color.CYAN);
        col.add(Color.BLACK);
        col.add(Color.YELLOW);
        col.add(Color.BLUE);

        final Integer[] p = {0};
        this.listPoint.forEach((pos, charge) -> {
            String uniqueID = UUID.randomUUID().toString();
            waypoints.add(new MyWaypoint(uniqueID, Color.BLACK, pos, charge));
            p[0]++;
        });

        // Create a waypoint painter that takes all the waypoints
        this.circleWayPointPainter.setWaypoints(waypoints);
        this.circleWayPointPainter.setRenderer(new CircleWaypointRenderer());

        //this.mapViewer.setOverlayPainter(this.circleWayPointPainter);
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 14, 0));
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 15, 0));
        this.routePainter = new RoutePainter(this.trajectory);


        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
        painters.add(this.routePainter);
        painters.add(this.circleWayPointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        this.mapViewer.setOverlayPainter(painter);



        // Display the viewer in a JFrame
        JFrame frame = new JFrame("Viewer for lgds_idsa");
        frame.getContentPane().add(this.mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
//
//
//    private void addpointtra(){
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 16, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 16, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//
////        this.routePainter = new RoutePainter(this.trajectory);
//        //this.mapViewer.invalidate();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 17, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 17, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//
//
//
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 18, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 18, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 19, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 19, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 20, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 20, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 21, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 21, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 22, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 22, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 23, 0));
//        this.routePainter.addPos(new GeoPosition(50,  4, 0, 8, 23, 0));
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.removeAll();
//        this.mapViewer.setOverlayPainter(painter);
//
//    }




}
