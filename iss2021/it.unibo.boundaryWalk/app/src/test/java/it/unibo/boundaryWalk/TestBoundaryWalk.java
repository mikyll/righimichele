package it.unibo.boundaryWalk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class TestBoundaryWalk
{
    private MoveVirtualRobot appl;

    @Before
    public void systemSetUp()
    {
        System.out.println("TestMoveVirtualRobot | setUp: robot should be at HOME-DOWN ");
        appl = new MoveVirtualRobot();
    }

    @Test
    public void TestRobotForwardMove()
    {
        System.out.println("Test: forward move 100ms");
        boolean moveFailed;
        moveFailed = appl.moveForward(100);
        assertTrue(!moveFailed);
        moveFailed = appl.moveBackward(100);
        assertTrue(!moveFailed);
        System.out.println("Test: forward move 100ms OK");
    }
    @Test
    public void TestRobotLeftMove()
    {
        System.out.println("Test: left move");
        boolean moveFailed;
        moveFailed = appl.moveLeft(300);
        assertTrue(!moveFailed);
        moveFailed = appl.moveRight(300);
        assertTrue(!moveFailed);
        System.out.println("Test: left move OK");
    }
    @Test
    public void TestCollision()
    {
        System.out.println("Test: wall hits detection.");
        boolean moveFailed;
        moveFailed = appl.moveForward(2000);
        assertTrue(moveFailed);
        moveFailed = appl.moveBackward(2000);
        assertTrue(moveFailed);
        System.out.println("Test: wall hits detection OK");
    }

    @After
    public void testRobotEndHeading()
    {
        System.out.println("Test: terminated correctly.");
    }
}
