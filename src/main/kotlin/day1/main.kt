package day1

import util.readNullableInts

fun parseReindeers(ints: List<Int?>): MutableList<Int> {
    return ints.fold(mutableListOf(0)) { acc, num ->
        if (num != null) {
            acc[acc.lastIndex] = acc.last() + num
        } else {
            acc.add(0)
        }
        acc
    }
}

fun part1(ints: List<Int?>): Int {
    return parseReindeers(ints).max()
}

fun part2(ints: List<Int?>): Int {
    return parseReindeers(ints).sorted().reversed().take(3).sum()
}

fun main() {
    val input = readNullableInts("src/main/kotlin/day1/testinput.txt")
    println(part1(input))
    println(part2(input))
}