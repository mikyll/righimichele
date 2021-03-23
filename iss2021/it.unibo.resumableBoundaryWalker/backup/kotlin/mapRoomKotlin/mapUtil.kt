package mapRoomKotlin

object mapUtil{
 	private var state    = RobotState(0,0,Direction.DOWN)	 
	var map              = RoomMap.getRoomMap()
	var refMapForTesting = buildRefTestMap()
	
	
	
 	fun buildRefTestMap() : String{
		val nr = 4
		val nc = 4
 			for( i in 1..nr ) mapUtil.doMove("w")
			mapUtil.doMove("l")
		    for( i in 1..nc ) mapUtil.doMove("w")
			mapUtil.doMove("l")
			for( i in 1..nr ) mapUtil.doMove("w")
			mapUtil.doMove("l")
		    for( i in 1..nc ) mapUtil.doMove("w")
			mapUtil.doMove("l")
			val res = mapUtil.map.toString()
			showMap()
			RoomMap.resetRoomMap()
 			//println( "buildRefTestMap DONE $res" )
			return res
  	}
	
	
	fun setObstacle( move : String ="w"){	//we could move w or s
		var x   = state.x
		var y   = state.y
		var dir = state.direction
		//println("mapUtil | setobstacleeeeeeeeeeeeeeee ${x},${y}  move=$move"  )
		if( move == "w" ) { when( dir){
			Direction.DOWN  -> y = y + 1
			Direction.UP    -> if( y > 0 ) y = y - 1
			Direction.LEFT  -> if( x > 0 ) x = x - 1
			Direction.RIGHT -> x = x + 1
		} }
		if( move == "s"   ) { when( dir){
			Direction.DOWN  -> if( y > 0 ) y = y - 1
			Direction.UP    -> y = y + 1
			Direction.LEFT  -> x = x + 1
			Direction.RIGHT -> if( x > 0 ) x = x - 1
		} }
		map.put( x,  y, Box(true, false, false))
	}
	
    fun doMove(move: String ) {
       val x = state.x
       val y = state.y
       //println("doMove move=$move  dir=${state.direction} x=$x y=$y dimMapX=$map.dimX{} dimMapY=${map.dimY}")
       try {
            when (move) {
                "moveForward","w" -> {
                     map.put(x, y, Box(false, false, false)) //clean the cell
					 state = state.forward();
                     map.put(state.x, state.y, Box(false, false, true))
                }
				"moveBackward","s" -> {
					 map.put(x, y, Box(false, false, false)) //clean the cell
	                 state = state.backward();
                     map.put(state.x, state.y, Box(false, false, true))
                }
				"a"  -> {
                     map.put(state.x, state.y, Box(false, false, true))
                }
				"turnLeft","l" -> {
					  state = state.turnLeft();
                      map.put(state.x, state.y, Box(false, false, true))
                }
				"d" -> {
                     map.put(state.x, state.y, Box(false, false, true))
                }
				"turnRight","r" -> {
 					state = state.turnRight();
                    map.put(state.x, state.y, Box(false, false, true))
                }
				/*
				"obstacle" -> {
					println("mapUtil | obstacleeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee "  )
					map.put(state.x, state.y, Box(true, false, true))
				}
*/
		   }//switch
		   
		   //println( "$map"  )
        } catch (e: Exception) {
            println("mapUtil | doMove: ERROR:" + e.message)
		    println("doMove move=$move  dir=${state.direction} x=$x y=$y dimMapX=$map.dimX{} dimMapY=${map.dimY}")
        }
	}
	
	fun showMap(){
		println( "$map direction=${state.direction}"  )
	}



}