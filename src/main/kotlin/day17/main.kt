import util.readInput
import util.readTestInput

private enum class Direction(val dx: Int, val dy: Int) {
    LEFT( -1, 0), RIGHT( 1, 0), UP( 0, -1), DOWN( 0, 1)
}

private data class Coord(val x: Int, val y: Int) {
    fun move(direction: Direction) =
        Coord(x + direction.dx, y + direction.dy)
}

private typealias Shape = List<Coord>
private typealias JetStream = List<Direction>
private fun stringToJetStream(string: String): JetStream = string.toCharArray()
    .map { c -> if (c == '<') Direction.LEFT else Direction.RIGHT }

private val shapes = listOf(
    listOf(Coord(0, 0), Coord(1, 0), Coord(2, 0), Coord(3, 0)), // horizontal line
    listOf(Coord(1, 0), Coord(0, -1), Coord(1, -1), Coord(2, -1), Coord(1, -2)), // cross
    listOf(Coord(2, -2), Coord(2, -1), Coord(2, 0), Coord(1, 0), Coord(0, 0)), // J-shape
    listOf(Coord(0, 0), Coord(0, -1), Coord(0, -2), Coord(0, -3)), // vertical line
    listOf(Coord(0, 0), Coord(0, -1), Coord(1, 0), Coord(1, -1)) // box
)

private class Cave(private val jets: List<Direction>, val drawEnabled: Boolean = false) {
    private val width = 7
    private var jetIndex: Int = 0
    private var shapeIndex: Int = 0
    private val floor: Shape = IntRange(0, width - 1).map {x -> Coord(x, 1)}

    // initialize the cave with the floor
    private var cave: MutableSet<Coord> = floor.toMutableSet()

    private fun minY(coords: Collection<Coord>) = coords.minBy { coord -> coord.y }.y
    private fun maxY(coords: Collection<Coord>) = coords.maxBy { coord -> coord.y }.y
    fun nextShape(): Shape = shapes[shapeIndex++ % shapes.size]
    fun nextJetDirection(): Direction = jets[jetIndex++ % jets.size]

    fun moveToStart(shape: Shape): Shape {
        val yDiff = minY(cave) - 4 // gap of 3 between
        return shape.map { c -> Coord(c.x + 2, c.y + yDiff) }
    }

    private fun collides(shape: Shape) = shape.any { coord -> cave.contains(coord) || coord.x < 0 || coord.x >= width }
    private fun moveShape(shape: Shape, direction: Direction): Shape = shape.map { c -> c.move(direction) }

    fun height() = Math.abs(minY(cave)) + 1

    fun play() {
        var movingShape = moveToStart(nextShape())
        draw("A new rock begins falling:", movingShape)
        var shapeIsMoving = true
        while (shapeIsMoving) {
            val jetDirection = nextJetDirection()
            val jettedShape = moveShape(movingShape, jetDirection)
            if (!collides(jettedShape)) {
                movingShape = jettedShape
                draw("Jet of gas pushes rock $jetDirection:", movingShape)
            }
            else {
                draw("Jet of gas pushes rock $jetDirection, but nothing happens:", movingShape)
            }

            val movedShape = moveShape(movingShape, Direction.DOWN)
            if (!collides(movedShape)) {
                movingShape = movedShape
                draw("Rock falls 1 unit:", movingShape)
            }
            else {
                cave.addAll(movingShape)
                draw("Rock falls 1 unit, causing it to come to rest:", movingShape)
                shapeIsMoving = false
            }
        }
    }

    fun draw(msg: String, movingShape: Shape) {
        if (!drawEnabled) return
        println(msg)
        val minY = Math.min(minY(movingShape), minY(cave))
        val maxY = Math.max(maxY(movingShape), maxY(cave))

        for (y in minY until maxY + 1) {
            for (x in -1 until width + 1) {
                when {
                    x == -1 || x == width -> print('|')
                    cave.contains(Coord(x, y)) -> print('#')
                    movingShape.contains(Coord(x, y)) -> print('@')
                    else -> print('.')
                }
            }
            println()
        }
        println()
    }
}

fun main() {
    val jetString: String = readInput("day17").first()
    val cave = Cave(
        jets = stringToJetStream(jetString),
        drawEnabled = false
    )
    repeat(2022) {
        cave.play()
    }
    println(cave.height())
}