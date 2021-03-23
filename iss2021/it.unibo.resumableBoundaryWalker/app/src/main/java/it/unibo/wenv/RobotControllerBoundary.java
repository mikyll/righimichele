/*
===============================================================
RobotControllerBoundary.java
implements the business logic by handling messages received on the cmdsocket-8091

===============================================================
*/
package it.unibo.wenv;
import it.unibo.supports.IssCommSupport;
import it.unibo.interaction.IssObserver;
import it.unibo.interaction.MsgRobotUtil;
import org.json.JSONObject;

public class RobotControllerBoundary implements IssObserver {
private int stepNum              = 1;
private String journey           = "";
private boolean boundaryWalkDone = false ;
private IssCommSupport rs ;
private boolean usearil  = false;
private int moveInterval = 1000;

    //public enum robotLang {cril, aril}    //todo

    public RobotControllerBoundary(IssCommSupport support, boolean usearil){
        rs = support;
        this.usearil = usearil;
    }

    //used by the main program
    public synchronized String doBoundary(){
        System.out.println("RobotControllerBoundary | doBoundary rs=" + rs + " usearil=" + usearil);
        rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg  );
        /*The reply to the request is sent by WEnv after the wtime defined in issRobotConfig.txt */
        delay(moveInterval ); //to reduce the robot move rate
        while( ! boundaryWalkDone ) {
            try {
                wait();
                //System.out.println("RobotControllerBoundary | RESUMES - final journey=" + journey);
                rs.close();
                //return journey;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return journey;
    }

    @Override
    public void handleInfo(String infoJson) {
        handleInfo( new JSONObject(infoJson) );
    }

    /*
Handler of the messages sent by WENv over the cmdsocket-8091 to notify:
- the answer to a robot-command move {"endmove":"RESULT", "move":MOVE}
- the information emitted by a sonar { "sonarName": "sonarName", "distance": 1, "axis": "x" }
- a collision between the robot and an obstacle { "collision" : "false", "move": "moveForward"}
     */
    @Override
    public void handleInfo(JSONObject infoJson) {
        if( infoJson.has("endmove") )        handleEndMove(infoJson);
        else if( infoJson.has("sonarName") ) handleSonar(infoJson);
        else if( infoJson.has("collision") ) handleCollision(infoJson);
    }

    protected void handleSonar( JSONObject sonarinfo ){
        String sonarname = (String)  sonarinfo.get("sonarName");
        int distance     = (Integer) sonarinfo.get("distance");
        System.out.println("RobotControllerBoundary | handleSonar:" + sonarname + " distance=" + distance);
    }
    protected void handleCollision( JSONObject collisioninfo ){
        //we should handle a collision  when there are moving obstacles
        //in this case we could have a collision even if the robot does not move
        //String move   = (String) collisioninfo.get("move");
        //System.out.println("RobotControllerBoundary | handleCollision move=" + move  );
    }
    protected void handleEndMove(JSONObject endmove ){
        String answer = (String) endmove.get("endmove");
        String move   = (String) endmove.get("move");
        //System.out.println("RobotControllerBoundary | handleEndMove:" + move + " answer=" + answer);
        switch( answer ){
            case "true"       : boundary( move, false );break;
            case "false"      : boundary( move, true  );break;
            case "halted"     : System.out.println("RobotControllerBoundary | handleEndMove to do halt" );break;
            case "notallowed" : System.out.println("RobotControllerBoundary | handleEndMove to do notallowed" );break;
            default           : System.out.println("RobotControllerBoundary | handleEndMove IMPOSSIBLE answer for move=" + move);
        }
    }
//Business logic in RobotControllerBoundary
    protected synchronized void boundary( String move, boolean obstacle ){
         if (stepNum <= 4) {
            if( move.equals("turnLeft") ){
                journey = journey + "l";
                if (stepNum == 4) {
                    boundaryWalkDone=true;
                    notify(); //to resume the main
                    return;
                }
                stepNum++;
                rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg );
                delay(moveInterval ); //to reduce the robot move rate
                return;
            }
            //the move is moveForward
            if( obstacle ){
                rs.request( usearil ? MsgRobotUtil.lMsg : MsgRobotUtil.turnLeftMsg   );
            }
            if( ! obstacle ){
                journey = journey + "w";
                rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg );
            }
            delay(moveInterval ); //to reduce the robot move rate
        }else{ //stepNum > 4
            System.out.println("RobotControllerBoundary | boundary journey:" + journey);
        }
    }

    protected void delay( int dt ){
        try { Thread.sleep(dt); } catch (InterruptedException e) { e.printStackTrace(); }
    }

}
