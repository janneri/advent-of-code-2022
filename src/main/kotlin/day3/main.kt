package day3

import util.readTestInput

data class Rucksack(val part1: CharArray, val part2: CharArray) {
    fun getDuplicate(): Char {
       return part1.find { part2.contains(it) }!!
    }

    companion object {
        fun parse(str: String): Rucksack {
            val partLength = str.length / 2
            return Rucksack(str.take(partLength).toCharArray(), str.substring(partLength).toCharArray())
        }
    }
}

fun getCharPrio(char: Char): Int {
    return if (char.isUpperCase()) char.code - 38 else char.code - 96
}

fun part1(inputLines: List<String>): Int {
    return inputLines
        .map { Rucksack.parse(it) }
        .map { getCharPrio(it.getDuplicate()) }
        .sum()
}

/** The group has always 3 items. Returns a char found in all three groups. */
fun findCommonChar(group: List<String>): Char {
    val charArrays = group.map { it.toCharArray() }
    return charArrays[0].find { group[1].contains(it) && group[2].contains(it) }!!
}

fun part2(inputLines: List<String>): Int {
    return inputLines.chunked(3)
        .map { findCommonChar(it) }
        .map { getCharPrio(it) }
        .sum()
}

fun main() {
    val inputLines = readTestInput("day3")
    println(part1(inputLines))
    println(part2(inputLines))
}