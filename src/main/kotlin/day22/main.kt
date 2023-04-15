package day22

import day22.Direction.*
import day22.Grid.TileType.*
import util.readTestInput
import java.lang.IllegalArgumentException

private enum class Direction(val dx: Int, val dy: Int, val symbol: Char, val facingPoints: Int) {
    UP( 0, -1, '^', 3) {
        override fun turnLeft() = LEFT
        override fun turnRight() = RIGHT
    },
    RIGHT( 1, 0, '>', 0) {
        override fun turnLeft() = UP
        override fun turnRight() = DOWN
    },
    DOWN( 0, 1, 'v', 1) {
        override fun turnLeft() = RIGHT
        override fun turnRight() = LEFT
    },
    LEFT( -1, 0, '<', 2) {
        override fun turnLeft() = DOWN
        override fun turnRight() = UP
    };

    abstract fun turnLeft(): Direction
    abstract fun turnRight(): Direction
}

private data class Coord(val x: Int, val y: Int) {
    fun move(direction: Direction, amount: Int = 1) =
        Coord(x + amount * direction.dx, y + amount * direction.dy)

    fun moveUntil(direction: Direction, predicate: (Coord) -> Boolean): Coord {
        val maxMoves = 1000
        var newCoord = this

        for (n in 0 until maxMoves) {
            val potentialNewCoord = newCoord.move(direction)
            if (predicate(potentialNewCoord)) {
                return newCoord
            }
            newCoord = potentialNewCoord
        }

        throw IllegalArgumentException("too many moves")
    }

}

private interface Instruction
private data class Move(val amount: Int): Instruction
private object TurnLeft: Instruction
private object TurnRight: Instruction

private data class Player(val coord: Coord, val direction: Direction)

private data class Grid(val inputLines: List<String>) {
    private enum class TileType(val symbol: String) {
        BLANK(" "), FLOOR("."), WALL("#");
        companion object {
            fun of(symbol: String) = values().first { it.symbol == symbol }
        }
    }
    private data class Tile(val tileType: TileType, val coord: Coord) {
        override fun toString() = tileType.symbol
    }

    private val tiles: List<List<Tile>> =
        inputLines.mapIndexed { y , line ->
            line.mapIndexed { x, symbol -> Tile(TileType.of(symbol.toString()), Coord(x, y))}
        }

    private val maxX = tiles.fold(0) { acc, tiles -> acc + tiles.maxBy {it.coord.x}.coord.x }
    private val maxY = tiles.size - 1

    fun isOutOfGrid(coord: Coord) =
        coord.x < 0 || coord.y < 0 || coord.x > maxX || coord.y > maxY

    private fun tileAt(coord: Coord): Tile =
        when {
            isOutOfGrid(coord) -> Tile(BLANK, coord)
            coord.x >= tiles[coord.y].size -> Tile(BLANK, coord)
            else -> tiles[coord.y][coord.x]
        }
    fun isBlocked(coord: Coord): Boolean = tileAt(coord).tileType != FLOOR
    fun jumpsToCoord(coord: Coord, direction: Direction): Coord? {
        if (tileAt(coord).tileType != BLANK) {
            return null
        }

        val oppositeDirection = direction.turnLeft().turnLeft()
        val jumpCoord = coord.moveUntil(oppositeDirection) { isOutOfGrid(it) || tileAt(it).tileType == BLANK }
        return if (tileAt(jumpCoord).tileType == WALL) null else jumpCoord
    }

    fun leftMostFloorCoord(): Coord = tiles[0]
        .filter { it.tileType == FLOOR }
        .minBy { tile -> tile.coord.x }.coord

    fun draw(customDraw: (Coord) -> Char?  = {_:Coord -> null}) {
        val height = tiles.size
        val width = maxX

        for (y in 0 until height) {
            for (x in 0 until width) {
                val coord = Coord(x, y)
                val customChar = customDraw(coord)
                if (customChar != null) print(customChar) else print(tileAt(coord))
            }
            println()
        }
    }
}


private fun parseInstructions(str: String): List<Instruction> {
    val parts = """\d+|[LR]""".toRegex().findAll(str).map {it.value}
    return parts.map { part ->
        when (part) {
            "L" -> TurnLeft
            "R" -> TurnRight
            else -> Move(part.toInt())
        }
    }.toList()
}

private data class GameState(val grid: Grid, val instructions: List<Instruction>) {
    var player = Player(grid.leftMostFloorCoord(), RIGHT)

    // Keep the player states in memory, so that we can visualize the path
    val playerStates = mutableListOf(player)
    private fun updatePlayerState(newPlayer: Player) {
        player = newPlayer
        playerStates.add(player)
    }

    fun movePlayer(instruction: Instruction) {
        when (instruction) {
            is Move -> {
                repeat(instruction.amount) {
                    val newCoord = player.coord.move(player.direction)
                    val jumpsToCoord = grid.jumpsToCoord(newCoord, player.direction)

                    if (jumpsToCoord != null) {
                        updatePlayerState(player.copy(coord = jumpsToCoord))
                    }
                    else if (!grid.isBlocked(newCoord)) {
                        updatePlayerState(player.copy(coord = newCoord))
                    }
                }
            }
            is TurnLeft -> updatePlayerState(player.copy(direction = player.direction.turnLeft()))
            is TurnRight -> updatePlayerState(player.copy(direction = player.direction.turnRight()))
        }
    }

    fun calculatePassword() =
        1000 * (player.coord.y + 1) + 4 * (player.coord.x + 1) + player.direction.facingPoints

    fun draw() {
        grid.draw { coord ->
            playerStates.findLast { it.coord == coord }?.direction?.symbol
        }
    }
}

fun part1(inputLines: List<String>): Int {
    val gridLines = inputLines.takeWhile{ it.isNotBlank() }
    val instructionLine = inputLines.takeLastWhile { it.isNotBlank() }.first()
    val instructions = parseInstructions(instructionLine)

    val grid = Grid(gridLines)

    val gameState = GameState(grid, instructions)
    instructions.forEach { gameState.movePlayer(it) }

    gameState.draw()
    return gameState.calculatePassword()
}

fun main() {
    val inputLines = readTestInput("day22")

    println(part1(inputLines))
}