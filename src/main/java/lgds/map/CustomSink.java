package lgds.map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by alessandrozonta on 09/08/2017.
 *
 * custom osmosis sink
 */
public class CustomSink implements Sink {
    //    private final List<Entity> amenityList;
//    private final List<Entity> building;
    private final Map<String,Integer> singlename;

    /**
     * constructor with zero parameters
     */
    public CustomSink(){
        this.singlename = new HashMap<>();
//        this.amenityList = new ArrayList<>();
//        this.building = new ArrayList<>();
    }

    /**
     * Override of the process method
     * it processes the entity adding it to the correct list
     * @param entityContainer entity
     */
    @Override
    public void process(EntityContainer entityContainer) {
        Entity entity = entityContainer.getEntity();
        if (entity.getTags().size() > 0) {


            if(entity.getTags().stream().anyMatch(tag -> tag.getKey().equals("amenity"))){
//                this.amenityList.add(entity);
                entity.getTags().stream().filter(tag -> tag.getKey().equals("amenity")).findAny().ifPresent(tag -> {
                    if(!this.singlename.containsKey(tag.getValue())){
                        this.singlename.put(tag.getValue(), 1);
                    }else{
                        this.singlename.put(tag.getValue(), this.singlename.get(tag.getValue()) + 1);
                    }
                });
            }

            if(entity.getTags().stream().anyMatch(tag -> tag.getKey().equals("building"))){
//                this.building.add(entity);
                entity.getTags().stream().filter(tag -> tag.getKey().equals("building")).findAny().ifPresent(tag -> {
                    if(!this.singlename.containsKey(tag.getValue())){
                        this.singlename.put(tag.getValue(), 1);
                    }else{
                        this.singlename.put(tag.getValue(), this.singlename.get(tag.getValue()) + 1);
                    }
                });
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

    /**
     * getter for the map of single name
     * the map contains the name and how many occurrences are in the area
     * @return map ordered from the more present to the less one. Only the first 100 location are taken into consideration
     */
    public Map<String,Integer> getSinglename() {
        Map<String, Integer> result = this.singlename.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return result.entrySet().stream().limit(100).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }



}
