package day4

import util.readTestInput

data class SectionPair(val first: IntRange, val second: IntRange)

// Input etc 2-4,6-8
private val inputRegex = Regex("""(\d+)-(\d+),(\d+)-(\d+)""")
fun parseInputToSectionPairs(line: String): SectionPair =
    inputRegex.matchEntire(line)!!
        .destructured
        .let { (start, end, start2, end2) ->
            SectionPair(IntRange(start.toInt(), end.toInt()), IntRange(start2.toInt(), end2.toInt()))
        }

fun part1(inputLines: List<String>): Int {
    return inputLines.map { parseInputToSectionPairs(it) }
        .count { pair -> pair.first.all { pair.second.contains(it) } || pair.second.all { pair.first.contains(it) } }
}

fun part2(inputLines: List<String>): Int {
    return inputLines.map { parseInputToSectionPairs(it) }
        .count { pair -> pair.first.any { pair.second.contains(it) } || pair.second.any { pair.first.contains(it) } }
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/4
    // Downloaded the input from https://adventofcode.com/2022/day/4/input
    val inputLines = readTestInput("day4")

    println(part1(inputLines))
    println(part2(inputLines))
}