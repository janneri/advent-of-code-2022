package day24

import util.Coord
import util.Direction
import util.readTestInput
import java.lang.IllegalArgumentException


data class Blizzard(val coord: Coord, val direction: Direction) {
    fun move(lastFreeX: Int, lastFreeY: Int): Blizzard {
        var newCoord = this.coord.move(this.direction)
        when {
            newCoord.x == 0 -> newCoord = Coord(lastFreeX, coord.y)
            newCoord.x > lastFreeX -> newCoord = Coord(1, coord.y)
            newCoord.y == 0 -> newCoord = Coord(coord.x, lastFreeY)
            newCoord.y > lastFreeY -> newCoord = Coord(coord.x, 1)
        }
        return this.copy(coord = newCoord)
    }
}

data class Path(val steps: Int, val playerPos: Coord)

private fun parseBlizzards(inputLines: List<String>) = inputLines.flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, char ->
        when (char) {
            '<' -> Blizzard(Coord(x, y), Direction.LEFT)
            '>' -> Blizzard(Coord(x, y), Direction.RIGHT)
            'v' -> Blizzard(Coord(x, y), Direction.DOWN)
            '^' -> Blizzard(Coord(x, y), Direction.UP)
            else -> null
        }
    }
}.toSet()

data class Map(val blizzards: Set<Blizzard>, val lastFreeX: Int, val lastFreeY: Int) {
    private val blizzardCoords = blizzards.map { it.coord }.toSet()

    fun moveBlizzards(): Map {
        return this.copy(blizzards = blizzards.map {
            it.move(this.lastFreeX, this.lastFreeY)
        }.toSet())
    }

    private fun isInBounds(coord: Coord) = coord.x in 1..lastFreeX && coord.y in 1..lastFreeY
    fun hasBlizzard(coord: Coord) = coord in blizzardCoords
    fun isMovable(coord: Coord) = isInBounds(coord) && coord !in blizzardCoords
}

fun searchPath(
    startPos: Coord,
    endPos: Coord,
    map: Map
): Pair<Path, Map> {
    val mapStates = mutableMapOf(0 to map) // steps to map
    val queue = mutableListOf(Path(0, startPos))
    val seen = mutableSetOf<Path>()

    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()

        if (path.playerPos == endPos) {
            return Pair(path, mapStates[path.steps]!!)
        }

        if (path !in seen) {
            seen += path
            // the map constantly changes because of blizzards
            var nextMap = mapStates[path.steps + 1]
            if (nextMap == null) {
                nextMap = mapStates[path.steps]!!.moveBlizzards()
                mapStates[path.steps + 1] = nextMap
            }

            //draw(mapStates[path.steps]!!, path, queue)

            // wait, if still possible and not occupied by a blizzard:
            if (!nextMap.hasBlizzard(path.playerPos)) {
                queue.add(Path(path.steps + 1, path.playerPos))
            }

            // move:
            path.playerPos.neighbors()
                .filter { it == startPos || it == endPos || nextMap.isMovable(it) }
                .forEach { neighbor ->
                    queue.add(Path(path.steps + 1, neighbor))
                }

        }
    }
    throw IllegalArgumentException("No route to end")
}

// Debug print (the same way as in the assignment):
fun draw(theMap: Map, path: Path, queue: MutableList<Path>) {
    println("queue $queue")
    val coords = setOf(Coord(0,0), Coord(theMap.lastFreeX + 1, theMap.lastFreeY + 1))
    println("Minute ${path.steps}")
    util.drawGrid(coords) {
        val blizzards = theMap.blizzards.filter { b -> b.coord == it }
        when {
            path.playerPos == it -> 'E'
            blizzards.size > 1 -> blizzards.size.digitToChar()
            blizzards.size == 1 -> blizzards.first().direction.symbol
            theMap.hasBlizzard(it) -> 'o'
            else -> '.'
        }
    }
}

fun part1(startPos: Coord, endPos: Coord, map: Map): Path {
    val (path, _) = searchPath(startPos, endPos, map)
    return path
}

fun part2(startPos: Coord, endPos: Coord, initialMap: Map): Int {
    val (pathToEnd, mapAtEnd) = searchPath(startPos, endPos, initialMap)
    val (pathToStart, mapAtStart) = searchPath(endPos, startPos, mapAtEnd)
    val (backToEnd, _) = searchPath(startPos, endPos, mapAtStart)
    return pathToEnd.steps + pathToStart.steps + backToEnd.steps
}


fun main() {
    val inputLines = readTestInput("day24")
    val blizzards = parseBlizzards(inputLines)
    val startPos = Coord(inputLines.first().indexOfFirst { it == '.' }, 0)
    val endPos = Coord(inputLines.last().indexOfFirst { it == '.' }, inputLines.lastIndex)
    val lastFreeX = inputLines.first().length - 2
    val lastFreeY = inputLines.size - 2
    val initialMap = Map(blizzards, lastFreeX, lastFreeY)

    println(part1(startPos, endPos, initialMap))
    println(part2(startPos, endPos, initialMap))
}