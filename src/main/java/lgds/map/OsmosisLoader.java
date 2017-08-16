package lgds.map;

import Connections.DatabaseEntity;
import crosby.binary.osmosis.OsmosisReader;
import lgds.config.ConfigFile;
import lgds.trajectories.Point;
import org.json.simple.parser.ParseException;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alessandrozonta on 09/08/2017.
 *
 * Class used to readFileAndCreateDB osmosis libary potentiality in loading building
 */
public class OsmosisLoader {
    private DatabaseEntity db;


    /**
     * Constructor zero parameters
     * Loads the db
     */
    public OsmosisLoader() throws FileNotFoundException {
        this.db = new DatabaseEntity();
    }

    /**
     * Load the pbf file
     * Find the list of the top location in the area
     * save them into a db
     * @throws IOException
     * @throws ParseException
     */
    public void readFileAndCreateDB() throws IOException, ParseException {
        // load config file
        ConfigFile conf = new ConfigFile();
        conf.loadFile();
        File f = new File(conf.getGraphHopperPath(), conf.getGraphHopperName());
        OsmosisReader reader = new OsmosisReader(new FileInputStream(f));

        Sink sink = new CustomSink();
        reader.setSink(sink);
        reader.run();



        System.out.println(((CustomSink)sink).getSinglename().toString());
        ((CustomSink)sink).getSinglename().forEach((s, integer) -> {
            System.out.println(s + "=" + integer.toString());
        });

        List<String> names = ((CustomSink)sink).getSinglename().keySet().stream().collect(Collectors.toList());

        reader = new OsmosisReader(new FileInputStream(f));
        sink = new ReadSink(names, this.db);

        reader.setSink(sink);
        reader.run();
    }


    /**
     * Obtain list of values saved on the db
     * @return lsit of string
     */
    public List<String> obtainValues(){
        return this.db.obtainValues();
    }


    /**
     * Open the database
     * @throws SQLException if something wrong happens
     */
    public void openDB() throws SQLException {
        this.db.setConnection();
    }

    /**
     * Return what there is in the position passed as parameter
     * @param lat latitude of the position
     * @param lon longitude of the position
     * @return string contianing the result
     */
    public String findIfThereIsSomethingInPositionBis(Double lat, Double lon) throws SQLException {
        return this.db.readDataBis(lat,lon);
    }


    /**
     * Close the database
     * @throws SQLException if something wrong happens
     */
    public void closeDB() throws SQLException {
        this.db.closeConnection();
    }

    /**
     * Return what there is in the position passed as parameter
     * @param lat latitude of the position
     * @param lon longitude of the position
     * @return string contianing the result
     */
    public String findIfThereIsSomethingInPosition(Double lat, Double lon){
        return this.db.readData(lat,lon);
    }


    /**
     * obtain coordinates
     * @return list of points
     */
    public List<Point> obtainCoordinates(){
        List<DatabaseEntity.Point> p = this.db.obtainCoordinates();
        List<Point> points = new ArrayList<>();
        p.forEach(point -> points.add(new Point(point.getLat(), point.getLon())));
        return points;
    }
}
