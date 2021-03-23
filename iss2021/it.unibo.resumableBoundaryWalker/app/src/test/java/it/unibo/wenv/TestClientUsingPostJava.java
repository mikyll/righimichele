package it.unibo.wenv;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TestClientUsingPostJava {
    private ClientNaiveUsingPost appl;

    @Before
    public void systemSetUp() {
        System.out.println("TestClientUsingPost | setUp: robot should be at HOME-DOWN ");
        //TODO: put the robot at home or design each test properly
        appl = new ClientNaiveUsingPost();
    }

    @After
    public void  terminate() {
        System.out.println("%%%  TestClientUsingPost |  terminates ");
    }

    @Test
    public void testMoveLeftRight() {
      }

    @Test
    public void testMoveForwardNoHit() {
     }

    @Test
    public void testMoveForwardHit() {
      }

}
