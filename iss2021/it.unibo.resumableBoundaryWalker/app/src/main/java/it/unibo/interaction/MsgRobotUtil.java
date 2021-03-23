package it.unibo.interaction;

public class MsgRobotUtil {
    //movetimes (used by IssAnnotationUtil.fillMap)
    public static final int wtime    = 400;
    public static final int stime    = wtime;
    public static final int ltime    = 300;
    public static final int rtime    = ltime;
    public static final int htime    = 100;

    //cril
    public static final String forwardMsg   = "{\"robotmove\":\"moveForward\", \"time\": 400}";
    public static final String backwardMsg  = "{\"robotmove\":\"moveBackward\", \"time\": 400}";
    public static final String turnLeftMsg  = "{\"robotmove\":\"turnLeft\", \"time\": 300}";
    public static final String turnRightMsg = "{\"robotmove\":\"turnRight\", \"time\": 300}";
    public static final String haltMsg      = "{\"robotmove\":\"alarm\", \"time\": 100}";


    //aril
    public static final String wMsg  = "w";
    public static final String lMsg  = "l";
    public static final String rMsg  = "r";
    public static final String sMsg  = "s";
    public static final String hMsg  = "h";

    //msg( MSGID,  MSGTYPE,  SENDER,  RECEIVER,  CONTENT, SEQNUM )
    //AppMsg with cril payload //TODO
    /*
    public static final AppMsg forwardApp   = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ forwardMsg + ")");
    public static final AppMsg backwardApp  = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ backwardMsg + ")");
    public static final AppMsg turnLeftApp  = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ turnLeftMsg + "))");
    public static final AppMsg turnRightApp = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+turnRightMsg+")");
    public static final AppMsg haltMsgApp   = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ haltMsg +")");
    */

    //AppMsg with aril payload
    // msg( MSGID,  MSGTYPE,  SENDER,  RECEIVER,  CONTENT, SEQNUM )
    /*
    public static final AppMsg ahead = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv," + wMsg +" )");
    public static final AppMsg left  = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ lMsg +")");
    public static final AppMsg right = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ rMsg +")");
    public static final AppMsg back  = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ sMsg +")");
    public static final AppMsg halt  = AppMsg.create( "msg(robotcmd,dispatch,appl,wenv,"+ hMsg +")");
    */
}
