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
                answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":50}");
                obstacle = answer.contains("collision");
                cleaned += obstacle ? "f" : "";
            }
            obstacle = false;
            if(heading == DOWN)
            {
                // rotate left by 90°
                conn.request( "{\"robotmove\":\"turnLeft\", \"time\":300}");
                cleaned += "l";

                // step forward
                answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":50}");
                if(answer.contains("collision"))
                    break;
                else cleaned += "f";

                // rotate right by 90°
                conn.request( "{\"robotmove\":\"turnRight\", \"time\":300}");
                cleaned += "r";

                continue;
            }
            if(heading == UP)
            {
                // rotate right by 90°
                conn.request( "{\"robotmove\":\"turnRight\", \"time\":300}");
                cleaned += "r";

                // step forward
                answer = conn.request( "{\"robotmove\":\"moveForward\", \"time\":50}");
                if(answer.contains("collision"))
                    break;
                else cleaned += "f";

                // rotate left by 90°
                conn.request( "{\"robotmove\":\"turnLeft\", \"time\":300}");
                cleaned += "l";

                continue;
            }

        }

    }

    @After
    public void printResults() {
        System.out.println(cleaned);
    }
}
