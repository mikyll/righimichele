package it.unibo.wenv;

import mapRoomKotlin.mapUtil;

public class RobotMovesInfo {
    private boolean  doMap = false;
    private String journey = "";

    public RobotMovesInfo(boolean doMap){
        this.doMap = doMap;

    }
    public void showRobotMovesRepresentation(  ){
        if( doMap ) mapUtil.showMap();
        else System.out.println( "journey=" + journey );
    }

    public String getMovesRepresentationAndClean(  ){
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


}
