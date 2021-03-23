package mapRoomKotlin
lateinit var targetMap  : RoomMap
val nr = 4
val nc = 5

 	fun buildRefTestMap(){
 			for( i in 1..nr ) mapUtil.doMove("w")
			mapUtil.doMove("l")
		    for( i in 1..nc ) mapUtil.doMove("w")
			mapUtil.doMove("l")
			for( i in 1..nr ) mapUtil.doMove("w")
			mapUtil.doMove("l")
		    for( i in 1..nc ) mapUtil.doMove("w")
			mapUtil.doMove("l")
		
			targetMap = mapUtil.map
//			println( "buildRefTestMap DONE" )
  	}
