package mapRoomKotlin

class RobotState( x: Int, y: Int, direction: Direction) {
    var x: Int private set
    var y: Int private set
    var direction: Direction private set

    init {
        require(!(x < 0 || y < 0 ||
				direction != Direction.UP &&
				direction != Direction.RIGHT &&
				direction != Direction.DOWN &&
				direction != Direction.LEFT))
        this.x = x
        this.y = y
        this.direction = direction
//        println("RobotState CREATED x=$x y=$y direction=$direction" )
    }
	
    override fun hashCode(): Int {
        var result = x + 31 * y
        result = when (direction) {
            Direction.UP    -> result + 31 * 31 * 0
            Direction.RIGHT -> result + 31 * 31 * 1
            Direction.DOWN  -> result + 31 * 31 * 2
            Direction.LEFT  -> result + 31 * 31 * 3
        }
        return result
    }

    fun equals(o: RobotState): Boolean {
        return x == o.x && y == o.y && direction == o.direction
    }

    val backwardDirection: Direction
        get() = when (direction) {
            Direction.UP -> Direction.DOWN
            Direction.RIGHT -> Direction.LEFT
            Direction.DOWN -> Direction.UP
            Direction.LEFT -> Direction.RIGHT
         }

    fun turnRight(): RobotState {
        val result = RobotState(x, y, direction)
        when (result.direction) {
            Direction.UP -> result.direction = Direction.RIGHT
            Direction.RIGHT -> result.direction = Direction.DOWN
            Direction.DOWN -> result.direction = Direction.LEFT
            Direction.LEFT -> result.direction = Direction.UP
         }
        return result
    }

    fun turnLeft(): RobotState {
        val result = RobotState(x, y, direction)
        when (result.direction) {
            Direction.UP    -> result.direction = Direction.LEFT
            Direction.RIGHT -> result.direction = Direction.UP
            Direction.DOWN  -> result.direction = Direction.RIGHT
            Direction.LEFT  -> result.direction = Direction.DOWN
         }
        return result
    }

    fun forward(): RobotState {
        val result = RobotState(x, y, direction)
        when (result.direction) {
            Direction.UP    -> result.y--
            Direction.DOWN  -> result.y++
            Direction.LEFT  -> result.x--
            Direction.RIGHT -> result.x++
         }
        return result
    }

    fun backward(): RobotState {
        val result = RobotState(x, y, direction)
        when (result.direction) {
            Direction.UP    -> result.y++
            Direction.DOWN  -> result.y--
            Direction.LEFT  -> result.x++
            Direction.RIGHT -> result.x--
        }
        return result
    }

}