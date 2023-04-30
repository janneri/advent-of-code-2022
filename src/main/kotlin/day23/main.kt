package day23

import util.Coord
import util.IDirection
import util.IDirection.*
import util.drawGrid
import java.lang.IllegalArgumentException

private val mainToSubdirections = listOf(
    NORTH to listOf(NORTH, NORTHEAST, NORTHWEST),
    SOUTH to listOf(SOUTH, SOUTHEAST, SOUTHWEST),
    WEST to listOf(WEST, NORTHWEST, SOUTHWEST),
    EAST to listOf(EAST, NORTHEAST, SOUTHEAST),
)

private data class DirectionResolver(val firstDirectionIndex: Int) {
    val directionsOfThisRound = listOf(
        mainToSubdirections[firstDirectionIndex % 4],
        mainToSubdirections[(firstDirectionIndex + 1) % 4],
        mainToSubdirections[(firstDirectionIndex + 2) % 4],
        mainToSubdirections[(firstDirectionIndex + 3) % 4]
    )

    fun proposeDirection(coord: Coord, neighbors: Set<Coord>): IDirection? {
        for ((mainDirection, subDirections) in directionsOfThisRound) {
            // Check that no-one is in that direction
            val isDirectionClear = subDirections.all { dir -> !neighbors.contains(coord.move(dir)) }
            if (isDirectionClear) {
                return mainDirection
            }
        }

        return null
        //throw IllegalArgumentException("Direction not found for coord $coord with neighbors $neighbors")
    }
}

data class Elf(val coord: Coord, var proposedCoord: Coord)
data class RoundResult(val elfCoords: Set<Coord>, val elfsMoved: Boolean)

fun playRound(elfCoords: Set<Coord>, firstDirectionIndex: Int): RoundResult {
    val directionResolver = DirectionResolver(firstDirectionIndex)

    // first half: proposed moves
    val elfs: List<Elf> = elfCoords.map { coord ->
        val neighbors = elfCoords.filter { it in coord.diagonalNeighbors() }.toSet()

        // If no other Elves are in one of the eight positions, the Elf does not do anything during this round.
        if (neighbors.isEmpty()) {
            //println("no neighbors for coord $coord")
            Elf(coord, coord)
        }
        else {
            //  Elf looks in each of four directions and proposes moving one step in the first valid direction:
            val proposedDirection = directionResolver.proposeDirection(coord, neighbors)
            //println("proposing $proposedDirection for coord $coord with neighbors $neighbors")
            Elf(coord, if (proposedDirection != null) coord.move(proposedDirection) else coord)
        }
    }

    // Second half:
    // Each Elf moves to their proposed destination tile if they were the only Elf to propose moving to that position.
    val proposedCoordCounts = elfs.groupingBy { it.proposedCoord }.eachCount()
    var elfsMoved = false

    val newElfCoords = elfs.map { elf ->
        val proposedCoord = elf.proposedCoord
        val proposedCoordCount = proposedCoordCounts[proposedCoord]!!
        // If two or more Elves propose moving to the same position, none of those Elves move.
        if (proposedCoordCount > 1 || elf.coord == elf.proposedCoord) {
            elf.coord
        }
        else {
            elfsMoved = true
            elf.proposedCoord
        }
    }.toSet()

    return RoundResult(newElfCoords, elfsMoved)
}

fun draw(coords: Set<Coord>) {
    drawGrid(coords) { if (coords.contains(it)) '#' else '.' }
}

fun freeCoordCount(coords: Set<Coord>): Int {
    val yRange = coords.minBy { it.y }.y .. coords.maxBy { it.y }.y
    val xRange = coords.minBy { it.x }.x .. coords.maxBy { it.x }.x
    val squareSize = xRange.count() * yRange.count()
    return squareSize - coords.size
}
fun part1(elfCoords: Set<Coord>): Int {
    var result = playRound(elfCoords, 0)
    draw(result.elfCoords)

    for (i in 1 .. 9) {
        result = playRound(result.elfCoords, i)
        draw(result.elfCoords)
        if (!result.elfsMoved) {
            return freeCoordCount(result.elfCoords)
        }
    }
    return freeCoordCount(result.elfCoords)
}

fun main() {
    val elfCoords = util.readInput("day23")
        .flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, char -> if (char == '#') Coord(x, y) else null }
        }.toSet()


    println(part1(elfCoords))
}