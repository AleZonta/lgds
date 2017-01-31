package lgds.viewer;

import lgds.trajectories.*;
import lgds.trajectories.Point;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.VirtualEarthTileFactoryInfo;
import org.jxmapviewer.input.*;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.util.*;


/**
 * Created by alessandrozonta on 31/01/2017.
 * should load the GUI for the map
 */
public class View {
    private Map<GeoPosition, Double> listPoint; //list of point to update
    private GeoPosition mapFocus; //focus of the map
    private JXMapViewer mapViewer; //map

    public View(){
        this.listPoint = new HashMap<>();
        this.mapFocus = new GeoPosition(50,  7, 0, 8, 41, 0);
        this.listPoint.put(new GeoPosition(50,  5, 0, 8, 14, 0), 10d);
        this.listPoint.put(new GeoPosition(50,  0, 0, 8, 16, 0), 15d);
        this.listPoint.put(new GeoPosition(49, 52, 0, 8, 39, 0), 18d);
        this.listPoint.put(new GeoPosition(50,  6, 0, 8, 46, 0), 20d);
        this.mapViewer = new JXMapViewer();

    }

    /**
     * visualise the map of everything
     */
    public void showMap(){
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
        WaypointPainter<MyWaypoint> waypointPainter = new WaypointPainter<MyWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        waypointPainter.setRenderer(new FancyWaypointRenderer());

        this.mapViewer.setOverlayPainter(waypointPainter);

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("Viewer for lgds_idsa");
        frame.getContentPane().add(this.mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);


        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waypointPainter.getWaypoints().stream().forEach(myWaypoint -> myWaypoint.setDiameter(myWaypoint.getDiameter() + 15));
        this.mapViewer.repaint();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waypointPainter.getWaypoints().stream().forEach(myWaypoint -> myWaypoint.setDiameter(myWaypoint.getDiameter() + 35));
        this.mapViewer.repaint();
    }



    public static void main(String[] args){
        View v = new View();
        v.showMap();

    }




}
