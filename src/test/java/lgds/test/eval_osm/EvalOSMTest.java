package lgds.test.eval_osm;

import lgds.eval_osm.EvalOsm;
import org.junit.Test;

/**
 * Created by alessandrozonta on 13/04/2017.
 */
public class EvalOSMTest {
    @Test
    public void readOsm() throws Exception {
        EvalOsm ev = new EvalOsm();
        ev.readOsm();
    }
}
