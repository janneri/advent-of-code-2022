package util

data class Coord(val x: Int, val y: Int)

class GameMap(val field: List<String>) {
    fun getCoord(coord: Coord): Char {
        return field[coord.y][coord.x]
    }

    fun width(): Int {
        return field[0].length
    }

    fun height(): Int {
        return field.size
    }

    fun isValid(coord: Coord): Boolean {
        return coord.y >= 0 && coord.y < field.size && coord.x >= 0 && coord.x < field[0].length
    }

}

fun move (coord: Coord, dx: Int, dy: Int): Coord {
    return Coord(coord.x + dx, coord.y + dy)
}

