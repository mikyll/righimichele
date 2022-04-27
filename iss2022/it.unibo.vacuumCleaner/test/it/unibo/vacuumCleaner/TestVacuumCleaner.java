package it.unibo.vacuumCleaner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unibo.actor22comm.http.HttpConnection;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

import java.util.ArrayList;

public class TestVacuumCleaner {
    private final static int DOWN = 0, RIGHT = 1, UP = 2, LEFT = 3;
    private final String localHostName = "localhost";
    private final int port = 8090;
    private final String HttpURL = "http://" + localHostName + ":" + port + "/api/move";

    private Interaction2021 conn;
    private String cleaned;
    private int curX, curY, heading;

    @Before
    public void init() {
        // setup string
        cleaned = "";

        // set robot position to DEN
        curX = 0;
        curY = 0;
        heading = DOWN;

        // enstablish connection
        conn = HttpConnection.create(localHostName + ":" + port);
    }

    @Test
    public void testCleaning() throws Exception {
        boolean obstacle = false;
        String answer = "";
        while(true)
        {
            while(!obstacle)
            {
                answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":500}");
                obstacle = answer.contains("collision");
                cleaned += obstacle ? "f" : "";
                CommUtils.delay(500);
            }
            obstacle = false;
            if(heading == DOWN)
            {
                // rotate left by 90°
                conn.request( "{\"robotmove\":\"turnLeft\", \"time\":300}");
                cleaned += "l";
                CommUtils.delay(300);

                // step forward
                answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":500}");
                if(answer.contains("collision"))
                    break;
                else cleaned += "f";
                CommUtils.delay(500);

                // rotate right by 90°
                conn.request( "{\"robotmove\":\"turnLeft\", \"time\":300}");
                cleaned += "l";
                CommUtils.delay(300);

                heading = UP;
                continue;
            }
            if(heading == UP)
            {
                // rotate right by 90°
                conn.request( "{\"robotmove\":\"turnRight\", \"time\":300}");
                cleaned += "r";
                CommUtils.delay(300);

                // step forward
                answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":500}");
                if(answer.contains("collision"))
                    break;
                else cleaned += "f";
                CommUtils.delay(500);

                // rotate left by 90°
                conn.request( "{\"robotmove\":\"turnRight\", \"time\":300}");
                cleaned += "r";
                CommUtils.delay(300);

                heading = DOWN;
                continue;
            }
        }
        while(!obstacle)
        {
            if (heading == DOWN)
            {
                // rotate left by 90°
                conn.request( "{\"robotmove\":\"turnLeft\", \"time\":300}");
                CommUtils.delay(300);

                // rotate left by 90°
                conn.request( "{\"robotmove\":\"turnLeft\", \"time\":300}");
                CommUtils.delay(300);
            }
            heading = UP;

            answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":500}");
            obstacle = answer.contains("collision");
            CommUtils.delay(500);
        }
        while(!obstacle)
        {
            answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":500}");
            obstacle = answer.contains("collision");
            CommUtils.delay(500);
        }
        // rotate left by 90°
        conn.request( "{\"robotmove\":\"turnLeft\", \"time\":300}");
        CommUtils.delay(300);
    }

    @After
    public void printResults() {
        System.out.println(cleaned);
    }
}
