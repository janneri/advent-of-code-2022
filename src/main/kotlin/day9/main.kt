package day9

import util.readTestInput
import kotlin.math.abs

enum class Direction(val symbol: String, val dx: Int, val dy: Int) {
    LEFT("L", -1, 0), RIGHT("R", 1, 0),
    UP("U", 0, -1), DOWN("D", 0, 1)
}

data class Move(val direction: Direction, val amount: Int = 1) {
    companion object {
        fun parse(str: String): Move {
            val (directionStr, amountStr) = str.split(" ")
            val direction = Direction.values().find {it.symbol == directionStr }!!
            return Move(direction, amountStr.toInt())
        }
    }
}

data class Coord(val x: Int, val y: Int) {
    fun moveXTowards(coord: Coord) =
        Coord(x + if (coord.x > x) 1 else -1, y)

    fun moveYTowards(coord: Coord) =
        Coord(x, y + if (coord.y > y) 1 else -1)

    fun move(direction: Direction) =
        Coord(x + direction.dx, y + direction.dy)

    fun move(move: Move) =
        Coord(x + move.amount * move.direction.dx, y + move.amount * move.direction.dy)

    fun collectCoords(move: Move): List<Coord> =
        (1 .. move.amount).map { amount -> this.move(Move(move.direction, amount)) }

    fun isAdjacent(coord: Coord) = abs(x - coord.x) <= 1 && abs(y - coord.y) <= 1
}

// For rope visualization
fun drawPath(path: List<Coord>, width: Int = 6, height: Int = 5) {
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (path.contains(Coord(x, y))) print('X') else print('.')
        }
        println()
    }
}

fun nextTailPos(headPos: Coord, tailPos: Coord): Coord? {
    return when {
        headPos.isAdjacent(tailPos) -> null                          // don't move
        headPos.x == tailPos.x -> tailPos.moveYTowards(headPos)      // move up or down
        headPos.y == tailPos.y -> tailPos.moveXTowards(headPos)      // move left or righ
        else -> tailPos.moveXTowards(headPos).moveYTowards(headPos)  // move diagonally towards head
    }
}

fun headPath(moves: List<Move>, startingPos: Coord): List<Coord> =
    moves.fold(listOf(startingPos)) {
        path, move -> path + path.last().collectCoords(move)
    }


fun knotPath(followPath: List<Coord>): List<Coord> {
    return followPath.fold(listOf(followPath.first())) { path, coord ->
        val nextTailPos = nextTailPos(coord, path.last())
        if (nextTailPos != null) path + nextTailPos else path
    }
}

fun part1(moves: List<Move>, startingPos: Coord): Int {
    val headPath = headPath(moves, startingPos)
    val tailPath = knotPath(headPath)
    return tailPath.toSet().size
}

fun part2(moves: List<Move>, startingPos: Coord): Int {
    var currentPath = headPath(moves, startingPos)

    for (i in 1..9) {
        currentPath = knotPath(currentPath)
    }

    return currentPath.toSet().size
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/9
    // Downloaded the input from https://adventofcode.com/2022/day/9/input
    val moves = readTestInput("day9").map { Move.parse(it) }
    val startingPos = Coord(0, 4)
    println(part1(moves, startingPos))     // 13
    println(part2(moves, startingPos))     // 1
}