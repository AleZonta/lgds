package lgds.test.simulator;
import lgds.simulator.Simulator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by alessandrozonta on 26/09/16.
 */
public class SimulatorTest {
    @Test
    /**
     * test if the method run works
     */
    public void run() throws Exception {
        Simulator sim = new Simulator();
        sim.init(1);
        sim.run();

        assertEquals(0, sim.getParticipant().stream().filter(agent -> !agent.getDead()).toArray().length);
    }

    @Test
    /**
     * Test if it initialise correctly the class simulator
     */
    public void init() throws Exception{
        Simulator sim = new Simulator();
        sim.init(400);
        assertEquals(400,sim.getParticipant().size());



    }


    /**
     * Test if graphhopper it works with our data
     */
    @Test
    public void work() throws Exception{
        Simulator sim = new Simulator();
        sim.init(5);
        sim.work();
    }
}
