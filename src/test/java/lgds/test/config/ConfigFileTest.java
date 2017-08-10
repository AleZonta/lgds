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
        assertNotEquals("", file.getIDSATraces());
        assertNotEquals("", file.getGeoLifeTrace());
        assertNotEquals("", file.getGraphHopperPath());
        assertNotEquals("", file.getGoogleAPIKey());
        assertNotEquals("", file.getGraphHopperName());
        assertNotNull(file.getIDSATraces());
        assertNotNull(file.getGeoLifeTrace());
        assertNotNull(file.getGraphHopperPath());
        assertNotNull(file.getGoogleAPIKey());
        assertNotNull(file.getGraphHopperName());
        assertTrue(file.getMaxLength() < 10000 && file.getMaxLength() > 0);
        assertTrue(file.getDBSCANradio() < 10000 && file.getDBSCANradio() > 0);
    }

}