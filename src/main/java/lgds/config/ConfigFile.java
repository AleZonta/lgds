package lgds.config;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Paths;


/**
 * Created by alessandrozonta on 24/08/16.
 */
public class ConfigFile {
    private final String currentPath; //current path of the program
    private String IDSATraces;
    private String GeoLifeTrace;
    private String GraphHopperPath;
    private String GoogleAPIKey;
    private String GraphHopperName;
    private String source;
    private Integer DBSCANratio;
    private Double maxLength; //if length is set to 999999.0 it means no limit
    private Boolean translate; //Do i have to translate the original coordinates (only for IDSA)
    private Boolean avoid;
    private Boolean smoother;


    //constructor
    public ConfigFile(){
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/settings.json";
        this.IDSATraces = null;
        this.GeoLifeTrace = null;
        this.GraphHopperPath = null;
        this.GoogleAPIKey = null;
        this.GraphHopperName = null;
        this.DBSCANratio = null;
        this.maxLength = null;
        this.translate = null;
        this.source = null;
        this.avoid = Boolean.FALSE;
        this.smoother = null;
    }

    //method that reads the configfile
    //This method load the field from the JSON file.
    //Tested
    public void loadFile() throws Exception {
        FileReader reader = new FileReader(this.currentPath);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(reader);

        this.IDSATraces = (String) jsonObject.get("IDSATraces");
        this.GeoLifeTrace = (String) jsonObject.get("GeoLifeTrace");
        this.GraphHopperPath = (String) jsonObject.get("GraphHopperPath");
        this.GoogleAPIKey = (String) jsonObject.get("GoogleAPIKey");
        this.GraphHopperName  = (String) jsonObject.get("GraphHopperName");
        this.DBSCANratio = ((Long) jsonObject.get("Radio")).intValue();
        this.maxLength = (Double) jsonObject.get("MaxLength");
        this.source = (String) jsonObject.get("SourceTrajectories");
        if(((Long) jsonObject.get("Smoother")).intValue()==0){
            this.smoother = Boolean.FALSE;
        }else{
            this.smoother = Boolean.TRUE;
        }
        if(((Long) jsonObject.get("translate")).intValue()==0){
            this.translate = Boolean.FALSE;
        }else{
            if(((Long) jsonObject.get("translate")).intValue()==1){
                this.translate = Boolean.TRUE;
            }else{
                if(((Long) jsonObject.get("translate")).intValue()==3){
                    this.translate = Boolean.TRUE;
                    this.avoid = Boolean.TRUE;
                }else{
                    throw new Exception("translate has a value not supported");
                }
            }
        }
    }

    //getter
    public String getIDSATraces() {
        return IDSATraces;
    }

    public String getGeoLifeTrace() {
        return GeoLifeTrace;
    }

    public String getGraphHopperPath() {
        return GraphHopperPath;
    }

    public String getGoogleAPIKey() {
        return GoogleAPIKey;
    }

    public String getGraphHopperName() {
        return GraphHopperName;
    }

    public int getDBSCANradio() { return this.DBSCANratio; }

    public double getMaxLength() { return this.maxLength; }

    public boolean getTranslate() {
        return this.translate;
    }

    public boolean getAvoid() {
        return this.avoid;
    }

    @Override
    public String toString() {
        return "ConfigFile{" + "\n" +
                "currentPath='" + currentPath + '\'' + "\n" +
                ", IDSATraces='" + IDSATraces + '\'' + "\n" +
                ", GeoLifeTrace='" + GeoLifeTrace + '\'' + "\n" +
                ", GraphHopperPath='" + GraphHopperPath + '\'' + "\n" +
                ", GoogleAPIKey='" + GoogleAPIKey + '\'' + "\n" +
                ", GraphHopperName='" + GraphHopperName + '\'' + "\n" +
                ", DBSCANratio=" + DBSCANratio + "\n" +
                ", maxLength=" + maxLength + "\n" +
                ", translate=" + translate + "\n" +
                ", avoid=" + avoid + "\n" +
                ", source=" + source + "\n" +
                ", smoother=" + smoother + "\n" +
                '}';
    }

    public String getSource() {
        return source;
    }

    public Boolean getSmoother() {
        return smoother;
    }
}
