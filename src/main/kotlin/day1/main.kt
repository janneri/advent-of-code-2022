package day1

import util.readNullableInts
import java.io.File

data class Reindeer(val sums: List<Int>)

// The imperative version of parsing:
fun parseReindeers(lines: List<String>): List<Reindeer> {
    var nums = mutableListOf<Int>()
    val reindeers = mutableListOf<Reindeer>()

    lines.forEach {
        if (it.isBlank()) {
            reindeers.add(Reindeer(nums))
            nums = mutableListOf()
        } else {
            nums.add(it.toInt())
        }
    }

    if (nums.isNotEmpty()) reindeers.add(Reindeer(nums))

    return reindeers
}

// The functional version of parsing:
fun parseReindeersFold(ints: List<Int?>): MutableList<Int> {
    return ints.fold(mutableListOf(0)) { acc, num ->
        if (num != null) {
            acc[acc.lastIndex] = acc.last() + num
        } else {
            acc.add(0)
        }
        acc
    }
}

fun part1(lines: List<String>): Int {
    val reindeers = parseReindeers(lines)
    val maxCaloryReindeer = reindeers.maxWith(Comparator.comparingInt { it.sums.sum() })
    return maxCaloryReindeer.sums.sum()
}

fun part2(lines: List<String>): Int {
    val reindeers = parseReindeers(lines)
    val maxCaloryReindeers = reindeers.sortedWith(Comparator.comparingInt { it.sums.sum() }).reversed().take(3)
    val calorySum = maxCaloryReindeers.map({it.sums.sum()}).sum()
    println(maxCaloryReindeers)
    return calorySum
}

fun part1Fold(ints: List<Int?>): Int {
    return parseReindeersFold(ints).max()
}

fun part2Fold(ints: List<Int?>): Int {
    return parseReindeersFold(ints).sorted().reversed().take(3).sum()
}

fun main() {
    val inputfile = "src/main/kotlin/day1/testinput.txt"
    println(part1Fold(readNullableInts(inputfile)))
    println(part2Fold(readNullableInts(inputfile)))

    val input = File(inputfile).readLines()
    println(part1(input))
    println(part2(input))
}