import util.Coord
import util.Direction
import util.readTestInput
import kotlin.math.abs

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

private fun minY(coords: Collection<Coord>) = coords.minBy { coord -> coord.y }.y
private fun maxY(coords: Collection<Coord>) = coords.maxBy { coord -> coord.y }.y

private class Cave(val jets: List<Direction>, val drawEnabled: Boolean = false) {
    val width = 7
    var jetIndex: Int = 0
    var shapeIndex: Int = 0
    private val floor: Shape = IntRange(0, width - 1).map {x -> Coord(x, 1)}

    // initialize the cave with the floor
    var rockCoords: MutableSet<Coord> = floor.toMutableSet()

    fun nextShape(): Shape = shapes[shapeIndex++ % shapes.size]
    fun nextJetDirection(): Direction = jets[jetIndex++ % jets.size]

    fun moveToStart(shape: Shape): Shape {
        val yDiff = minY(rockCoords) - 4 // gap of 3 between
        return shape.map { c -> Coord(c.x + 2, c.y + yDiff) }
    }

    private fun collides(shape: Shape) = shape.any { coord -> rockCoords.contains(coord) || coord.x < 0 || coord.x >= width }
    private fun moveShape(shape: Shape, direction: Direction): Shape = shape.map { c -> c.move(direction) }

    fun height() = abs(minY(rockCoords)) + 1

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
                rockCoords.addAll(movingShape)
                draw("Rock falls 1 unit, causing it to come to rest:", movingShape)
                shapeIsMoving = false
            }
        }
    }

    fun draw(msg: String, movingShape: Shape) {
        if (!drawEnabled) return
        println(msg)
        val minY = Math.min(minY(movingShape), minY(rockCoords))
        val maxY = Math.max(maxY(movingShape), maxY(rockCoords))

        for (y in minY until maxY + 1) {
            for (x in -1 until width + 1) {
                when {
                    x == -1 || x == width -> print('|')
                    rockCoords.contains(Coord(x, y)) -> print('#')
                    movingShape.contains(Coord(x, y)) -> print('@')
                    else -> print('.')
                }
            }
            println()
        }
        println()
    }
}

private fun part1(jetString: String, rounds: Int) {
    val cave = Cave(
        jets = stringToJetStream(jetString),
        drawEnabled = false
    )

    repeat(rounds) {
        cave.play()
    }
    println("part 1 cave height: ${cave.height()}")
}


private object LoopDetector {
    data class State(val caveTop: List<Int>, val shapeIndex: Int, val jetIndex: Int) {
        var height: Int = 0
        var round: Long = 0
    }

    val states = mutableSetOf<State>()

    fun addState(cave: Cave) {
        states += convertToState(cave)
    }

    fun isRepeatedState(cave: Cave) = convertToState(cave) in states

    fun findState(cave: Cave): State? = states.find { it == convertToState(cave) }

    fun findStateByRound(round: Long): State? = states.find { it.round == round }

    fun convertToState(cave: Cave): State {
        val state = State(
            normalizedCaveTop(cave),
            cave.shapeIndex % shapes.size,
            cave.jetIndex % cave.jets.size,
        )
        state.height = cave.height()
        state.round = cave.shapeIndex.toLong()
        return state
    }

    private fun normalizedCaveTop(cave: Cave): List<Int> {
        val top = IntRange(0, cave.width - 1).map {x -> minY(cave.rockCoords.filter { it.x == x })}
        val normal = kotlin.math.abs(top.min())
        return top.map { it + normal }
    }
}

private fun part2(jetString: String, rounds: Long) {
    val cave = Cave(
        jets = stringToJetStream(jetString),
        drawEnabled = false
    )

    while (true) {
        LoopDetector.addState(cave)
        cave.play()
        if (LoopDetector.isRepeatedState(cave)) {
            val loopStartState = LoopDetector.findState(cave)!!
            val loopEndState = LoopDetector.convertToState(cave)

            val loopSize = loopEndState.round - loopStartState.round
            val loopCount = (rounds - loopStartState.round) / loopSize
            val loopHeight = loopEndState.height - loopStartState.height

            val loopEndRound = loopStartState.round + loopCount * loopSize
            val roundsAfterLoopEnd = rounds - loopEndRound
            val heightGainedAfterLoopEnd = LoopDetector.findStateByRound(loopStartState.round + roundsAfterLoopEnd)!!.height - loopStartState.height

            println("height ${loopStartState.height + loopHeight * loopCount + heightGainedAfterLoopEnd}")
            return
        }
    }
}

fun main() {
    val jetString: String = readTestInput("day17").first()

    part1(jetString, 2022)

    part2(jetString, 1000000000000)
}