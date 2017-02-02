package lgds.viewer;

import lgds.POI.POI;
import lgds.trajectories.Point;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.painter.*;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;


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
    private final List<GeoPosition> trajectory; //Point for the trajectory
    private final List<GeoPosition> wayPoints; //Visualise the wayPoints -> If empty no way points to visualise
    private RoutePainter routePainter; //Painter fro visualise the route

    /**
     * constructor for test
     */
    public View(){
        this.listPoint = new HashMap<>();
//        this.mapFocus = new GeoPosition(50,  7, 0, 8, 41, 0);
//        this.listPoint.put(new GeoPosition(50,  5, 0, 8, 14, 0), 1d);
//        this.listPoint.put(new GeoPosition(50,  0, 0, 8, 16, 0), 1.5d);
//        this.listPoint.put(new GeoPosition(49, 52, 0, 8, 39, 0), 1.8d);
//        this.listPoint.put(new GeoPosition(50,  6, 0, 8, 46, 0), 2d);
        //this.mapFocus = null;
        this.mapViewer = new JXMapViewer();
        this.circleWayPointPainter = new WaypointPainter<>();
        this.trajectory = new ArrayList<>();
        this.wayPoints = new ArrayList<>();
        this.routePainter = null;
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
        this.trajectory = new ArrayList<>();
        this.wayPoints = new ArrayList<>();
        this.routePainter = null;
    }


    /**
     * Contructor with the first list of POI
     * @param poiList list of POI to show
     * @param root root of the world
     */
    public View(List<POI> poiList, Point root, Point firstPoint){
        this.listPoint = new HashMap<>();
        this.mapFocus = new GeoPosition(root.getLatitude(), root.getLongitude());
        poiList.stream().forEach(poi -> this.listPoint.put(new GeoPosition(poi.getLocation().getLatitude(), poi.getLocation().getLongitude()), poi.getCharge()));
        this.mapViewer = new JXMapViewer();
        this.circleWayPointPainter = new WaypointPainter<>();
        this.trajectory = new ArrayList<>();
        this.trajectory.add(new GeoPosition(firstPoint.getLatitude(), firstPoint.getLongitude()));
        this.wayPoints = new ArrayList<>();
        this.routePainter = null;
    }


    /**
     * Set the charge of the poi to visualise
     * I am changing the diametre of the point
     * @param poiList list of the new poi with the new charges
     */
    public void setNewPOI(List<POI> poiList){

        this.circleWayPointPainter.getWaypoints().stream().forEach(myWaypoint -> {
            myWaypoint.setDiameter(poiList.stream().filter(poi -> poi.getLocation().equals(myWaypoint.getPointPosition())).findFirst().get().getCharge());
        });
        this.mapViewer.repaint();
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
        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
        painters.add(this.routePainter);
        painters.add(this.circleWayPointPainter);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
        this.mapViewer.removeAll();
        this.mapViewer.setOverlayPainter(painter);
        this.mapViewer.repaint();
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
        this.mapViewer.setZoom(10);
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
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



//    public static void main(String[] args){
//        View v = new View();
//        v.test();
//        v.addpointtra();
//    }
//
//
//    private void test(){
//        // Create a TileFactoryInfo for Virtual Earth
//        TileFactoryInfo info = new VirtualEarthTileFactoryInfo(VirtualEarthTileFactoryInfo.MAP);
//        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
//        tileFactory.setThreadPoolSize(8);
//
//        // Setup local file cache
//        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
//        LocalResponseCache.installResponseCache(info.getBaseURL(), cacheDir, false);
//
//        // Setup JXMapViewer
//
//        this.mapViewer.setTileFactory(tileFactory);
//
//
//
//        // Set the focus
//        this.mapViewer.setZoom(10);
//        this.mapViewer.setAddressLocation(this.mapFocus);
//
//        // Add interactions
//        MouseInputListener mia = new PanMouseInputListener(this.mapViewer);
//        this.mapViewer.addMouseListener(mia);
//        this.mapViewer.addMouseMotionListener(mia);
//        this.mapViewer.addMouseListener(new CenterMapListener(this.mapViewer));
//        this.mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCenter(this.mapViewer));
//        this.mapViewer.addKeyListener(new PanKeyListener(this.mapViewer));
//
//
//
//        // Create waypoints from the geo-positions
//        Set<MyWaypoint> waypoints = new HashSet<MyWaypoint>();
//        this.listPoint.forEach((pos, charge) -> {
//            String uniqueID = UUID.randomUUID().toString();
//            waypoints.add(new MyWaypoint(uniqueID, Color.BLACK, pos, charge));
//        });
//
//        // Create a waypoint painter that takes all the waypoints
//        this.circleWayPointPainter.setWaypoints(waypoints);
//        this.circleWayPointPainter.setRenderer(new CircleWaypointRenderer());
//
//        //this.mapViewer.setOverlayPainter(this.circleWayPointPainter);
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 14, 0));
//        this.trajectory.add(new GeoPosition(50,  4, 0, 8, 15, 0));
//        this.routePainter = new RoutePainter(this.trajectory);
//
//
//        // Create a compound painter that uses both the route-painter and the waypoint-painter
//        List<org.jxmapviewer.painter.Painter<JXMapViewer>> painters = new ArrayList<org.jxmapviewer.painter.Painter<JXMapViewer>>();
//        painters.add(this.routePainter);
//        painters.add(this.circleWayPointPainter);
//
//        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
//        this.mapViewer.setOverlayPainter(painter);
//
//
//
//        // Display the viewer in a JFrame
//        JFrame frame = new JFrame("Viewer for lgds_idsa");
//        frame.getContentPane().add(this.mapViewer);
//        frame.setSize(800, 600);
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
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
//
//
//
//
}
