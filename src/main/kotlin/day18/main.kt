package day18

import util.readTestInput

private data class Coord3d(val x: Int, val y: Int, val z: Int) {
    fun neighbors(): List<Coord3d> = listOf(
        copy(x = x + 1),
        copy(x = x - 1),
        copy(y = y + 1),
        copy(y = y - 1),
        copy(z = z + 1),
        copy(z = z - 1)
    )

    companion object {
        fun parse(str: String): Coord3d = str.split(",")
            .let{ Coord3d(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
    }
}

private const val MAX_SIDES_EXPOSED: Int = 6

private fun part1(coords: List<Coord3d>): Int =
    coords.sumOf { coord ->
        val neighborCount: Int = coord.neighbors().count { coords.contains(it) }
        MAX_SIDES_EXPOSED - neighborCount
    }

private fun part2(coords: Set<Coord3d>): Int {
    fun getRange(func: (Coord3d) -> Int): IntRange {
        val values = coords.map {func(it) }
        return values.min() - 1 .. values.max() + 1
    }
    // Search space is a cube around the lava:
    val xRange = getRange { it.x }
    val yRange = getRange { it.y }
    val zRange = getRange { it.z }

    // Create a queue and start from a corner
    val queue = ArrayDeque<Coord3d>()
    queue.add(Coord3d(xRange.first, yRange.first, zRange.first))

    // Search for all the points of air that touch lava
    var sideCount = 0
    val seen = mutableSetOf<Coord3d>()
    while (queue.isNotEmpty()) {
        val nextCoord = queue.removeFirst()
        if (nextCoord !in seen) {
            seen += nextCoord
            nextCoord.neighbors()
                // Stay inside the search space
                .filter { it.x in xRange && it.y in yRange && it.z in zRange }
                .forEach { neighbor ->
                    when (neighbor) {
                        in coords -> sideCount += 1
                        else -> queue.add(neighbor)
                    }
                }
        }
    }

    return sideCount
}

fun main() {
    val lavaCoords = readTestInput("day18").map { Coord3d.parse(it) }
    println(part1(lavaCoords))
    println(part2(lavaCoords.toSet()))
}