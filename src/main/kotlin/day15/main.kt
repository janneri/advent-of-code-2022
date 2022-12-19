package day15

import util.readTestInput
import kotlin.math.abs
import kotlin.math.max

private data class Coord(val x: Int, val y: Int) {
    fun distanceTo(coord: Coord) = abs(x - coord.x) + abs(y - coord.y)
}

private data class Sensor(val sensorCoord: Coord, val beaconCoord: Coord) {
    val range = sensorCoord.distanceTo(beaconCoord)
    fun rangeAt(y: Int) = max(0, range - abs(sensorCoord.y - y))
    fun isInRange(y: Int) = range >= abs(sensorCoord.y - y)
    fun minXAtY(y: Int) = sensorCoord.x - rangeAt(y)
    fun maxXAtY(y: Int) = sensorCoord.x + rangeAt(y)
}

private fun limit(num: Int, min: Int, max: Int): Int {
    return when {
        num < min -> min
        num > max -> max
        else -> num
    }
}

// 1-3 a: abcde
private val regex = Regex("""Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")
private fun parseSensor(line: String): Sensor =
    regex.matchEntire(line)!!
        .destructured
        .let { (sensorX, sensorY, beaconX, beaconY) ->
            Sensor(Coord(sensorX.toInt(), sensorY.toInt()), Coord(beaconX.toInt(), beaconY.toInt()))
        }

fun part1(inputLines: List<String>, y: Int): Int {
    val sensors = inputLines.map { parseSensor(it) }
    val beaconXCoordsAtY = sensors.filter { it.beaconCoord.y == y }.map { it.beaconCoord.x }.toSet()

    val coveredXCoords = sensors.fold(mutableSetOf<Int>()) { acc, sensor ->
        if (sensor.isInRange(y)) {
            acc.addAll(sensor.minXAtY(y) .. sensor.maxXAtY(y))
        }
        acc
    }

    return coveredXCoords.filter { !beaconXCoordsAtY.contains(it) }.size
}

private fun findCoveredXRanges(y: Int, sensors: List<Sensor>, maxXAndY: Int): Set<IntRange> =
    sensors.fold(mutableSetOf()) { acc, sensor ->
        if (sensor.isInRange(y)) {
            val minX = limit(sensor.minXAtY(y), 0, maxXAndY)
            val maxX = limit(sensor.maxXAtY(y), 0, maxXAndY)
            acc.add(IntRange(minX, maxX))
        }
        acc
    }

fun merge(range1: IntRange, range2: IntRange): IntRange? {
    return when {
        range1.first >= range2.first && range1.last <= range2.last -> range2 // ...2...1...1...2..
        range2.first >= range1.first && range2.last <= range1.last -> range1 // ...1...2...2...1..
        range1.first < range2.first && range1.last >= range2.first -> IntRange(range1.first, max(range1.last, range2.last)) // ...1...2...1...2..
        range2.first < range1.first && range2.last >= range1.first -> IntRange(range2.first, max(range1.last, range2.last)) // ...2...1...2...1..
        else -> null // range1 is fully before or fully after range2
    }
}

fun part2(inputLines: List<String>, maxXAndY: Int): ULong {
    val sensors = inputLines.map { parseSensor(it) }

    fun findUncoveredCoord(): Coord? {
        for (currentY in 0 until maxXAndY) {
            val coveredRanges = findCoveredXRanges(currentY, sensors, maxXAndY).sortedBy { it.first }

            var currentRange = coveredRanges.first()
            for (range in coveredRanges) {
                val mergedRange = merge(currentRange, range)

                // merge returns null when ranges are not overlapping, which means we have found the gap
                if (mergedRange == null) {
                    return Coord(currentRange.last + 1, currentY)
                }

                currentRange = mergedRange
            }
        }
        return null
    }

    val distressBeaconCoord = findUncoveredCoord()
    return distressBeaconCoord!!.x.toULong() * 4000000.toULong() + distressBeaconCoord.y.toULong()
}

fun main() {
    val inputLines = readTestInput("day15")
    println(part1(inputLines, 10))
    println(part2(inputLines, 4000000))
}