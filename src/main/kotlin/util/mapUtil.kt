package util

import java.lang.IllegalArgumentException

enum class Direction(val dx: Int, val dy: Int, val symbol: Char) {
    UP( 0, -1, '^') {
        override fun turnLeft() = LEFT
        override fun turnRight() = RIGHT
    },
    RIGHT( 1, 0, '>') {
        override fun turnLeft() = UP
        override fun turnRight() = DOWN
    },
    DOWN( 0, 1, 'v') {
        override fun turnLeft() = RIGHT
        override fun turnRight() = LEFT
    },
    LEFT( -1, 0, '<') {
        override fun turnLeft() = DOWN
        override fun turnRight() = UP
    };

    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
    fun turnOpposite() = turnLeft().turnLeft()
}

enum class IDirection(val dx: Int, val dy: Int) {
    NORTH( 0, -1),
    NORTHEAST( 1, -1),
    EAST( 1, 0),
    SOUTHEAST( 1, 1),
    SOUTH( 0, 1),
    SOUTHWEST( -1, 1),
    WEST( -1, 0),
    NORTHWEST( -1, -1)
}

data class Coord(val x: Int, val y: Int) {
    fun neighbors(): List<Coord> = Direction.values().map { this.move(it) }
    fun diagonalNeighbors(): List<Coord> = IDirection.values().map { this.move(it) }
    fun move(direction: IDirection, amount: Int = 1) =
        Coord(x + amount * direction.dx, y + amount * direction.dy)
    fun move(direction: Direction, amount: Int = 1) =
        Coord(x + amount * direction.dx, y + amount * direction.dy)

    fun moveUntil(direction: Direction, predicate: (Coord) -> Boolean): Coord {
        val maxMoves = 10000
        var newCoord = this

        for (n in 0 until maxMoves) {
            val potentialNewCoord = newCoord.move(direction)
            if (predicate(potentialNewCoord)) {
                return newCoord
            }
            newCoord = potentialNewCoord
        }

        throw IllegalArgumentException("Too many (> $maxMoves) moves!")
    }

    override fun toString() = "($x, $y)"
}

fun drawGrid(coords: Set<Coord>, tileSymbolAt: (Coord) -> Char) {
    val yRange = coords.minBy { it.y }.y .. coords.maxBy { it.y }.y
    val xRange = coords.minBy { it.x }.x .. coords.maxBy { it.x }.x

    for (y in yRange) {
        for (x in xRange) {
            val coord = Coord(x, y)
            print(tileSymbolAt(coord))
        }
        println()
    }
}

fun main() {
    println(Coord(1,4).moveUntil(Direction.RIGHT) { it.x == 4 })
    println(Coord(1,1).diagonalNeighbors())
}