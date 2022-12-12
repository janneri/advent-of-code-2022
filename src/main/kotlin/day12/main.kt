package day12

import util.readTestInput

enum class Direction(val dx: Int, val dy: Int) {
    LEFT(-1, 0), RIGHT(1, 0), UP(0, -1), DOWN(0, 1)
}

data class Position(val x: Int, val y: Int) {
    fun move(direction: Direction, amount: Int = 1) =
        Position(x + amount * direction.dx, y + amount * direction.dy)

    override fun toString(): String = "($x, $y)"
}

typealias Path = List<Position>

class HeighMap(private val rows: List<String>) {
    private val width = rows[0].length
    private val height = rows.size
    private val startPos = findPositionOfChar('S')
    private val endPos = findPositionOfChar('E')

    private fun findPositionOfChar(searchedChar: Char): Position =
        findPositionsOfChar(searchedChar).first()

    fun findPositionsOfChar(searchedChar: Char): List<Position> {
        val positions = mutableListOf<Position>()
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (rows[y][x] == searchedChar) positions.add(Position(x, y))
            }
        }
        return positions
    }

    private fun heightAt(pos: Position): Char = when (rows[pos.y][pos.x]) {
        'S' -> 'a'
        'E' -> 'z'
        else -> rows[pos.y][pos.x]
    }

    private fun isValidPos(position: Position): Boolean =
        position.y in 0 until height && position.x in 0 until width

    private fun availablePositions(fromPos: Position): List<Position> {
        return Direction.values().map { fromPos.move(it) }
            .filter { position -> isValidPos(position) }
            .filter { position -> heightAt(position) - heightAt(fromPos) <= 1 }
    }

    // For visualization:
    fun draw(positions: Collection<Position>) {
        for (y in 0 until height) {
            for (x in 0 until width) {
                when {
                    positions.contains(Position(x, y)) -> print('X')
                    else -> print(heightAt(Position(x, y)))
                }
            }
            println("n")
        }
    }

    private fun nextPaths(currentPaths: List<Path>, visited: MutableSet<Position>): List<Path> {
        val newPaths = currentPaths.map { path ->
            val nextAvailable = availablePositions(path.last()).filter { !visited.contains(it) }
            if (nextAvailable.isEmpty()) emptyList<Path>()
            visited.addAll(nextAvailable)
            nextAvailable.map { path + it }
        }.filter { it.isNotEmpty() }.flatten()

        return newPaths;
    }

    fun findStepsToEnd(fromPos: Position = startPos): Int? {
        var currentPaths = listOf(listOf(fromPos))
        val visited = mutableSetOf(fromPos)

        for (steps in 1 until 1000) {
            val nextPaths = nextPaths(currentPaths, visited)
            val newPositions = nextPaths.map { it.last() }
            if (newPositions.contains(endPos)) {
                return steps
            }
            visited.addAll(newPositions)
            currentPaths = nextPaths
        }
        return null
    }

}

fun part1(inputLines: List<String>): Int? {
    val heighMap = HeighMap(inputLines)
    return heighMap.findStepsToEnd()
}

fun part2(inputLines: List<String>): Int? {
    val heighMap = HeighMap(inputLines)
    val startingPoints = heighMap.findPositionsOfChar('a')

    return startingPoints
        .map { startPos -> heighMap.findStepsToEnd(startPos) }
        .filterNotNull()
        .min()
}

fun main() {
    val inputLines = readTestInput("day12")
    println(part1(inputLines))
    println(part2(inputLines))
}