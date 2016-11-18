package lgds.test.config;

import lgds.config.ConfigFile;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by alessandrozonta on 01/11/2016.
 */
public class ConfigFileTest {
    @Test
    public void loadFile() throws Exception {
        ConfigFile file = new ConfigFile();
        file.loadFile();
        assertEquals("/Users/alessandrozonta/PycharmProjects/createTraces/traces.txt", file.getIDSATraces());
        assertEquals("/Users/alessandrozonta/Downloads/Geolife trajectories 1.3/Data", file.getGeoLifeTrace());
        assertEquals("/Users/alessandrozonta/Documents/lgds/", file.getGraphHopperPath());
        assertEquals("AIzaSyDL8Wc8VXGYPTiFMSTAtj7mKQZukTRawac", file.getGoogleAPIKey());
        assertEquals("china-latest.osm.pbf", file.getGraphHopperName());
    }

}