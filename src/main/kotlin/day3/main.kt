package day3

import util.readTestInput

data class Rucksack(val part1Chars: CharArray, val part2Chars: CharArray) {
    fun getDuplicateChar(): Char {
       return part1Chars.find { part2Chars.contains(it) }!!
    }

    companion object {
        fun parse(str: String): Rucksack {
            val partLength = str.length / 2
            return Rucksack(str.take(partLength).toCharArray(), str.substring(partLength).toCharArray())
        }
    }
}

fun getPrioOfChar(char: Char): Int {
    return if (char.isUpperCase()) char.code - 38 else char.code - 96
}

fun part1(inputLines: List<String>): Int {
    return inputLines
        .map { Rucksack.parse(it) }
        .map { getPrioOfChar(it.getDuplicateChar()) }
        .sum()
}

/** The group has always 3 items. Returns a char found in all three groups. */
fun findCommonChar(group: List<String>): Char {
    val charArrays = group.map { it.toCharArray() }
    return charArrays[0].find { group[1].contains(it) && group[2].contains(it) }!!
    //     charArrays[0].find { char -> group.drop(1).all { it.contains(char) } }!!
    // The commented versio would be more generic, but I think the not generic version is more readable
}

fun part2(inputLines: List<String>): Int {
    return inputLines.chunked(3)
        .map { findCommonChar(it) }
        .map { getPrioOfChar(it) }
        .sum()
}

fun main() {
    val inputLines = readTestInput("day3")
    println(part1(inputLines))
    println(part2(inputLines))
}