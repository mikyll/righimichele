package mapRoomKotlin
import java.io.Serializable
import java.util.*

class RoomMap private constructor()  { //: Serializable
    private var roomMap : MutableList<ArrayList<Box>> = ArrayList<ArrayList<Box>>()

	companion object {
//      private const val serialVersionUID = 1L
        private var singletonRoomMap: RoomMap? = null
        fun getRoomMap(): RoomMap {
            if (singletonRoomMap == null) singletonRoomMap = RoomMap()
            return singletonRoomMap!!
        }

        fun setRoomMap(map: RoomMap?) {
            singletonRoomMap = map
        }
		
        fun resetRoomMap( ) {
			if (singletonRoomMap == null) return
			singletonRoomMap!!.roomMap = ArrayList<ArrayList<Box>>()
            singletonRoomMap =  null
        }
		
    }

    init {
        put(0, 0, Box(false, false, true))  //robot in 0,0
    }

	fun put(x: Int, y: Int, box: Box) {
        try {
            roomMap[y]
        } catch (e: IndexOutOfBoundsException) {
            var i = roomMap.size
			//println("put 1 i=$i")
            while (i <= y) {
                roomMap.add( ArrayList<Box>() )  //add a row
                i++
            }
			//println( toString() )
        }
        try {
            roomMap[y][x]
            roomMap[y].removeAt(x)
            roomMap[y].add(x, box)
			//println("put 2")
        } catch (e: IndexOutOfBoundsException) {
           var j = roomMap[y].size
		   //println("put 3 j=$j")
           while (j < x) {
                roomMap[y].add (Box() )	//add a notExplored box => false, true, false
                j++
            }
            roomMap[y].add(x, box)  //ad the given box
			//println( toString() )
        }
    }

    fun isObstacle(x: Int, y: Int): Boolean {
		return roomMap[y][x].isObstacle
    }

    fun isNotExplored(x: Int, y: Int): Boolean {
		return roomMap[y][x].notExplored
    }

    fun canMove(x: Int, y: Int, direction: Direction): Boolean {
        return when (direction) {
            Direction.UP -> canMoveUp(x, y)
            Direction.RIGHT -> canMoveRight(x, y)
            Direction.DOWN -> canMoveDown(x, y)
            Direction.LEFT -> canMoveLeft(x, y)
        }
    }

    fun canMoveUp(x: Int, y: Int): Boolean {
		return ! roomMap[y - 1][x].isObstacle
    }

    fun canMoveRight(x: Int, y: Int): Boolean {
		return ! roomMap[y][x+1].isObstacle
    }

    fun canMoveDown(x: Int, y: Int): Boolean {
		return ! roomMap[y+1][x].isObstacle
    }

    fun canMoveLeft(x: Int, y: Int): Boolean {
		return ! roomMap[y][x-1].isObstacle
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (a in roomMap) {  //row ArrayList
            builder.append("|")
            for (b in a) {  //column
                if (b.isRobot ) builder.append("r, ")                //r => robot
				else if (b.isObstacle) builder.append("X, ")		 //X => obstacle
				    else if ( b.notExplored ) builder.append("0, ")  //0 => not explored
				         else builder.append("1, ")
            }
            builder.append("\n")
        }
        return builder.toString()
    }

    val dimX: Int
        get() {
            var result = 0
            for (i in roomMap.indices) {
                if (result < roomMap[i].size) result = roomMap[i].size
            }
            return result
        }

    val dimY: Int
        get() = roomMap.size

    val isClean: Boolean
        get() {
            for (row in roomMap) {
                for (b in row) if (b.notExplored) return false
            }
            return true
        }

    fun setObstacles() {
        for (row in roomMap) {
            for (b in row) {
                if (! b.isObstacle && b.notExplored ) {
                    b.notExplored = false
 					b.isObstacle = true
                }
            }
        }
    }

    fun setNotExplored() {
        for (row in roomMap) {
            for (b in row) {
                if (!b.isObstacle && !b.notExplored && ! b.isRobot) 
                    b.notExplored = true
            }
        }
    }

}