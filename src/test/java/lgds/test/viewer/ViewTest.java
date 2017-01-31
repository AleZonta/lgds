package lgds.test.viewer;

import lgds.viewer.View;
import org.junit.Test;

/**
 * Created by alessandrozonta on 31/01/2017.
 */
public class ViewTest {
    @Test
    /**
     * test if the method run works
     */
    public void showMap() throws Exception {
        View v = new View();
        v.showMap();
    }
}
