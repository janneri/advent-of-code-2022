package day4

import util.readTestInput


// Input etc 2-4,6-8 leads to Pair(IntRange(2, 4), IntRange(6,8))
fun parseSectionRangePairs(inputLines: List<String>): List<Pair<IntRange, IntRange>> {
    // Converts for example "6-8" to a an IntRange
    fun strToRange(str: String): IntRange {
        return str.split("-").let {(start, end) -> IntRange(start.toInt(), end.toInt())}
    }

    return inputLines
        .map { it.split(",").let { (range1, range2) -> Pair(strToRange(range1), strToRange(range2)) } }
}

fun part1(inputLines: List<String>): Int {
    return parseSectionRangePairs(inputLines)
        .count { pair -> pair.first.all { pair.second.contains(it) } || pair.second.all { pair.first.contains(it) } }
}

fun part2(inputLines: List<String>): Int {
    return parseSectionRangePairs(inputLines)
        .count { pair -> pair.first.any { pair.second.contains(it) } || pair.second.any { pair.first.contains(it) } }
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/4
    // Downloaded the input from https://adventofcode.com/2022/day/4/input
    val inputLines = readTestInput("day4")

    println(part1(inputLines))
    println(part2(inputLines))
}