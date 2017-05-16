package lgds.eval_osm;

import info.pavie.basicosmparser.controller.OSMParser;
import info.pavie.basicosmparser.model.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by alessandrozonta on 13/04/2017.
 */

public class EvalOsm {

    /**
     * Read the osm file
     */
    public void readOsm(){
        OSMParser p = new OSMParser();	//Initialization of the parser
        File osmFile = new File("beijing_china.osm.pbf");	//Create a file object for your OSM XML file
        try {

            Map<String,Element> result = p.parse(osmFile);	//Parse OSM data, and put result in a Map object

        } catch (IOException | SAXException e) {
            e.printStackTrace(); //Input/output errors management
        }

    }
}
