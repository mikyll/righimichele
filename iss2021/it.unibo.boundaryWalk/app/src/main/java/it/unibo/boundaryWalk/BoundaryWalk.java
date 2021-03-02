package it.unibo.boundaryWalk;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;

public class BoundaryWalk {

    public BoundaryWalk() {}

    public void executeBoundaryWalk(int forwardTime)
    {
        MoveVirtualRobot appl = new MoveVirtualRobot();

        boolean moveFailed;
        int timeF = forwardTime, timeL = 300;

        for(int i = 0; i < 4; i++) // "w*lw*lw*lw*l"
        {
            while(!(moveFailed = appl.moveForward(timeF))) // forward until the robot hits the wall (w*)
            {
                System.out.println("MoveVirtualRobot | moveForward : " + timeF + " failed= " + moveFailed);
            }
            moveFailed = appl.moveLeft(timeL); // wall hit, the robot turns left (l)
        }
    }

    // MAIN =======================================
    public static void main(String[] args)
    {
        BoundaryWalk appl = new BoundaryWalk();

        appl.executeBoundaryWalk(100);
    }

}

