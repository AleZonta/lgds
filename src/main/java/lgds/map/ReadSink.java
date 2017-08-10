package lgds.map;

import Connections.DatabaseEntity;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.List;
import java.util.Map;

/**
 * Created by alessandrozonta on 09/08/2017.
 */
public class ReadSink implements Sink {
    private List<String> nameTosave;
    private DatabaseEntity db;


    /**
     * Constructor one parameter
     * It loads the database
     * @param nameTosave list of places to save
     * @param db database to read/write
     */
    public ReadSink(List<String> nameTosave, DatabaseEntity db){
        this.nameTosave = nameTosave;
        this.db = db;
    }


    /**
     * Process the entity
     * if the entity tag is contained in the allowed word list I save it in the database
     * if is not a node I drop it
     * @param entityContainer entity under analyses
     */
    @Override
    public void process(EntityContainer entityContainer) {
        Entity entity = entityContainer.getEntity();
        if (entity.getTags().size() > 0) {

            if(entity.getTags().stream().filter(tag -> (tag.getKey().equals("amenity") || tag.getKey().equals("building")) && this.nameTosave.contains(tag.getValue())).findAny().isPresent()){
                //this.entitySaved.add(entity);
                Tag t = entity.getTags().stream().filter(tag -> (tag.getKey().equals("amenity") || tag.getKey().equals("building")) && this.nameTosave.contains(tag.getValue())).findAny().get();

                Double lat = 0d;
                Double lon = 0d;
                try {
                    lat = ((Node) entity).getLatitude();
                    lon = ((Node) entity).getLongitude();
                    this.db.insertData(lat, lon, (double) entity.getId(), t.getKey(), t.getValue());
                }catch (Exception e){

                }

                //
            }

        }
    }

    @Override
    public void initialize(Map<String, Object> map) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void release() {

    }

}
