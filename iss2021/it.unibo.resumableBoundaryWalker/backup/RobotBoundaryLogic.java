/*
===============================================================
RobotBoundaryLogic.java
implements the business logic  

===============================================================
*/
package it.unibo.wenv;
import it.unibo.interaction.MsgRobotUtil;
import it.unibo.supports.IssCommSupport;
import mapRoomKotlin.mapUtil;

public class RobotBoundaryLogic {
    private IssCommSupport rs ;

private int stepNum              = 1;
//private String journey           = "";
private boolean boundaryWalkDone = false ;
private boolean usearil          = false;
private int moveInterval         = 1000;
//private boolean  doMap           = false;
private RobotMovesInfo robotInfo;
    //public enum robotLang {cril, aril}    //todo

    public RobotBoundaryLogic(IssCommSupport support, boolean usearil, boolean doMap){
        rs           = support;
        this.usearil = usearil;
        robotInfo    = new RobotMovesInfo(doMap);
        //this.doMap   = doMap;
        robotInfo.showRobotMovesRepresentation();
    }

    public void doBoundaryGoon(){
        rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg  );
        delay(moveInterval ); //to reduce the robot move rate
    }

    public synchronized String doBoundaryInit(){
        System.out.println("RobotBoundaryLogic | doBoundary rs=" + rs + " usearil=" + usearil);
        //rs.request( usearil ? MsgRobotUtil.wMsg : MsgRobotUtil.forwardMsg  );
        //The reply to the request is sent by WEnv after the wtime defined in issRobotConfig.txt  
        //delay(moveInterval ); //to reduce the robot move rate
        System.out.println( mapUtil.getMapRep() );
        while( ! boundaryWalkDone ) {
            try {
                wait();
                //System.out.println("RobotBoundaryLogic | RESUMES " );
                rs.close();
             } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return robotInfo.getMovesRepresentationAndClean();
    }
/*
    protected String getMovesRepresentationAndClean(  ){
        if( doMap ) return mapUtil.getMapAndClean();
        else {
            String answer = journey;
            journey       = "";
            return answer;
        }
    }

    public String getMovesRepresentation(  ){
        if( doMap ) return mapUtil.getMapRep();
        else return journey;
    }

    public void updateRobotMovesRepresentation(String move ){
        if( doMap )  mapUtil.doMove( move );
        else journey = journey + move;
    }
    public void showRobotMovesRepresentation(  ){
        if( doMap ) mapUtil.showMap();
        else System.out.println( "journey=" + journey );
    }
*/

    public void updateMovesRep (String move ){
        robotInfo.updateRobotMovesRepresentation(move);
    }

 //Business logic in RobotBoundaryLogic
    protected synchronized void boundaryStep( String move, boolean obstacle ){
         if (stepNum <= 4) {
            if( move.equals("turnLeft") ){
                updateMovesRep("l");
                //showRobotMovesRepresentation();
                if (stepNum == 4) {
                    boundaryWalkDone=true;
                    notify(); //to resume the main
                    robotInfo.getMovesRepresentationAndClean();
                    return;
                }
                stepNum++;
                doBoundaryGoon();
                return;
            }
            //the move is moveForward
            if( obstacle ){
                rs.request( usearil ? MsgRobotUtil.lMsg : MsgRobotUtil.turnLeftMsg   );
                delay(moveInterval ); //to reduce the robot move rate
            }
            if( ! obstacle ){
                updateMovesRep("w");
                doBoundaryGoon();
            }
            robotInfo.showRobotMovesRepresentation();
        }else{ //stepNum > 4
            System.out.println("RobotBoundaryLogic | boundary ENDS"  );
        }
    }

    protected void delay( int dt ){
        try { Thread.sleep(dt); } catch (InterruptedException e) { e.printStackTrace(); }
    }

}
