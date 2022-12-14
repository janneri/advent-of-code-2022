package day14

import day14.Direction.*
import util.readTestInput
import kotlin.math.max

private enum class Direction(val dx: Int, val dy: Int) {
    LEFT( -1, 0), RIGHT( 1, 0), UP( 0, -1), DOWN( 0, 1)
}

private data class Move(val direction: Direction, val amount: Int = 1)

private data class Vector(val start: Coord, val end: Coord) {
    fun getAllCoords(): List<Coord> {
        val direction = when {
            end.x > start.x -> RIGHT
            end.x < start.x -> LEFT
            end.y < start.y -> UP
            else -> DOWN
        }
        val coords = mutableListOf(start)
        do {
            coords.add(coords.last().move(direction))
        } while (coords.last() != end)
        return coords
    }
}

private data class Coord(val x: Int, val y: Int) {
    fun move(direction: Direction) =
        Coord(x + direction.dx, y + direction.dy)

    fun move(move: Move) =
        Coord(x + move.amount * move.direction.dx, y + move.amount * move.direction.dy)
}

private typealias Path = List<Coord>

private class Cave(rockPaths: List<Path>, private val hasFloor: Boolean = false) {
    val sandPourCoord = Coord(500,0)
    var rockCoords = rockPaths
        .flatMap { path -> path.windowed(2).flatMap {coords -> Vector(coords[0], coords[1]).getAllCoords()} }
    val sandCoords = mutableListOf<Coord>()
    var width = rockCoords.maxOf { it.x } + 1
    val height = rockCoords.maxOf { it.y } + 1 + (if (hasFloor) 2 else 0)

    fun isFLoor(coord: Coord) = coord.y == height - 1

    fun isBlocked(coord: Coord) =
        sandCoords.contains(coord) || rockCoords.contains(coord) || coord == sandPourCoord || (hasFloor && isFLoor(coord))

    fun findNewSandPos(fromPos: Coord = sandPourCoord): Coord? {
        for (i in 1 until height - fromPos.y) {
            val pos = fromPos.move(Move(DOWN, i))
            if (isBlocked(pos)) {
                // when the pos is blocked, we check if we can move down left or right.
                val newPos = when {
                    !isBlocked(pos.move(LEFT)) -> findNewSandPos(pos.move(LEFT))
                    !isBlocked(pos.move(RIGHT)) -> findNewSandPos(pos.move(RIGHT))
                    else -> pos.move(UP)
                }
                return if (newPos == sandPourCoord) null else newPos
            }
        }
        // dropping to void
        return null
    }

    fun play() {
        do {
            val newSandPos = findNewSandPos()
            sandCoords.add(newSandPos!!)
            if (sandCoords.size % 500 == 0) println("Round ${sandCoords.size} dropped to ${sandCoords.last()}")
        } while (findNewSandPos() != null)
    }

    fun drawMap() {
        val dynamicWidth = if (hasFloor) max(width, sandCoords.maxOf { it.x } + 1) else width
        for (y in 0 until height) {
            for (x in 0 until dynamicWidth) {
                if (x < 450) continue
                when {
                    sandPourCoord == Coord(x, y) -> print('+')
                    hasFloor && y == height -1 -> print('#')
                    rockCoords.contains(Coord(x, y)) -> print('#')
                    sandCoords.contains(Coord(x, y)) -> print('o')
                    else -> print('.')
                }
            }
            println()
        }
    }
}


private fun part1(rockPaths: List<Path>): Int {
    val cave = Cave(rockPaths)
    cave.play()
    cave.drawMap()
    return cave.sandCoords.size
}

private fun part2(rockPaths: List<Path>): Int {
    val cave = Cave(rockPaths, hasFloor = true)
    cave.play()
    cave.drawMap()
    return cave.sandCoords.size + 1 // add one because the source of sand is full of sand too
}

private fun parsePath(line: String): Path = line.split(" -> ")
    .map { it.split(",").let { parts -> Coord(parts[0].toInt(), parts[1].toInt()) } }

fun main() {
    val rockPaths = readTestInput("day14").map { parsePath(it) }
    println(part1(rockPaths))
    println(part2(rockPaths))
}