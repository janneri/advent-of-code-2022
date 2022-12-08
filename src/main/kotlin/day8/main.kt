package day8

import day8.Direction.*
import util.readTestInput
import java.lang.Integer.max

enum class Direction(val dx: Int, val dy: Int) {
    LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1)
}

data class Coord(val x: Int, val y: Int) {
    fun move(direction: Direction, amount: Int = 1) =
        Coord(x + amount * direction.dx, y + amount * direction.dy)
}

class Grid(val rows: List<String>) {
    val width = rows[0].length
    val height = rows.size

    fun valueAt(coord: Coord): Int = rows[coord.y][coord.x].digitToInt()

    fun isSmaller(coord: Coord, coord2: Coord): Boolean = valueAt(coord) < valueAt(coord2)
    fun isEqual(coord: Coord, coord2: Coord): Boolean = valueAt(coord) == valueAt(coord2)
    fun isBigger(coord: Coord, coord2: Coord): Boolean = valueAt(coord) > valueAt(coord2)

    fun isVisibleToAnyDirection(coord: Coord): Boolean {
        fun isVisibleTowards(dir: Direction): Boolean =
            IntRange(1, max(width, height))
                .map { amount -> coord.move(dir, amount) }
                .takeWhile { isValidCoordinate(it) }
                .all { valueAt(it) < valueAt(coord) }

        return Direction.values().any { isVisibleTowards(it) }
    }

    fun countUntilBiggerOrEqual(coord: Coord, direction: Direction): Int {
        var count = 0
        var currentCoord = coord.move(direction)
        while (isValidCoordinate(currentCoord)) {
            if (isSmaller(currentCoord, coord)) {
                count += 1
            }
            else if (isEqual(currentCoord, coord) || isBigger(currentCoord, coord)) {
                count +=1 // the equal size (or bigger) tree is counted, but we need to stop counting
                return count
            }
            currentCoord = currentCoord.move(direction)
        }
        return count
    }

    fun scenicScore(coord: Coord): Int {
        val toLeft = countUntilBiggerOrEqual(coord, LEFT)
        val toRight = countUntilBiggerOrEqual(coord, RIGHT)
        val up = countUntilBiggerOrEqual(coord, UP)
        val down = countUntilBiggerOrEqual(coord, DOWN)

        return toLeft * toRight * up * down
    }

    fun innerCoords(): List<Coord> {
        val coords = mutableListOf<Coord>()
        for (y in 1 .. height - 2) {
            for (x in 1 .. width - 2) {
                coords.add(Coord(x, y))
            }
        }
        return coords
    }

    fun isValidCoordinate(coord: Coord): Boolean {
        return coord.y in 0 until height && coord.x in 0 until width
    }
}

fun part1(grid: Grid): Int {
    val outerCoordCount = grid.width * 2 + (grid.height - 2) * 2
    return outerCoordCount + grid.innerCoords().count { grid.isVisibleToAnyDirection(it) }
}

fun part2(grid: Grid): Int {
    return grid.innerCoords().map { grid.scenicScore(it) }.max()
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/8
    // Downloaded the input from https://adventofcode.com/2022/day/8/input
    
    val grid = Grid(readTestInput("day8"))

    println(part1(grid))
    println(part2(grid))
}