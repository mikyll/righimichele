package mapRoomKotlin

object mapUtil{
 	private var state = RobotState(0,0,Direction.DOWN)	 
	var map   = RoomMap.getRoomMap()

    @JvmStatic fun getMapAndClean() : String{ //(fName : String="storedMap.txt")
		val outS = map.toString()
		RoomMap.resetRoomMap()
		return outS
	}

    @JvmStatic fun getMapRep() : String{
        return map.toString()
    }

    @JvmStatic fun setObstacle(){
		map.put( state.x,  state.y, Box(true, false, false))
	}

    @JvmStatic fun doMove(move: String ) {
       val x = state.x
       val y = state.y
//       println("doMove move=$move  dir=${state.direction} x=$x y=$y dimMapX=$map.dimX{} dimMapY=${map.dimY}")
       try {
            when (move) {
                "w" -> {
                     map.put(x, y, Box(false, false, false)) //clean the cell
					 state = state.forward();
                     map.put(state.x, state.y, Box(false, false, true))
                }
                "s" -> {
	                 state = state.backward();
                     map.put(state.x, state.y, Box(false, false, true))
                }
                "a"  -> {
                     map.put(state.x, state.y, Box(false, false, true))
                }
                "l" -> {
					  state = state.turnLeft();
                      map.put(state.x, state.y, Box(false, false, true))
                }
                "d" -> {
                     map.put(state.x, state.y, Box(false, false, true))
                }
                "r" -> {
 					state = state.turnRight();
                    map.put(state.x, state.y, Box(false, false, true))
                }
 
		   }//switch
		   
//		   println( "$map"  )
        } catch (e: Exception) {
            println("doMove: ERROR:" + e.message)
        }
	}

    @JvmStatic fun showMap(){
		println( "$map"  )
	}
	
}