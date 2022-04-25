package unibo.wenvUsage22.wshttp;

import unibo.actor22comm.http.HttpConnection;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.ApplData;

import java.util.Observable;

public class BoundaryWalkerWs implements IObserver {
    private  final String localHostName    = "localhost";
    private  final int port                = 8091;
    private  final String WsURL          = "ws://"+localHostName+":"+port+"/api/move";

    private Interaction2021 conn;

    private boolean obstacle = false;

    protected void doJob() throws Exception {
        conn = WsConnection.create(localHostName + ":" + port);
        ((WsConnection)conn).addObserver(this);

        for( int i=1; i<=4; i++ ) {
            while( ! obstacle ) {
                conn.forward( ApplData.moveForward(500));
                CommUtils.delay(600); // 100 more to see the steps
            }
            obstacle = false;
            conn.forward( ApplData.turnLeft(300));
            CommUtils.delay(400); // 100 more to see the steps
        }
    }

    public static void main(String[] args) throws Exception   {
        CommUtils.aboutThreads("Before start - ");
        new BoundaryWalkerWs().doJob();
        CommUtils.aboutThreads("At end - ");
    }

    @Override
    public void update(Observable source, Object data) {
        ColorsOut.out("ClientUsingWs update/2 receives:" + data);
        obstacle = data.toString().contains("collision");
    }
    @Override
    public void update(String data) {
        ColorsOut.out("ClientUsingWs update receives:" + data);
    }
}
