package day18

import util.readInput

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

fun main() {
    val coords = readInput("day18").map { Coord3d.parse(it) }
    println(part1(coords))
}