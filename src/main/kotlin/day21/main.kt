package day21

import util.readInput
import util.readTestInput

data class Monkey(val name: String, val operation: String,
                  var left: String, var right: String,
                  var leftValue: ULong? = null, var rightValue: ULong? = null, var result: ULong? = null) {
    fun hasValues() = leftValue != null && rightValue != null
}


fun parseMonkey(str: String): Monkey {
    val parts = str.split(":")
    val name = parts[0]
    val (left, right, operation) = when {
        parts[1].contains("+") -> parts[1].split("+").toList() + "+"
        parts[1].contains("-") -> parts[1].split("-").toList() + "-"
        parts[1].contains("*") -> parts[1].split("*").toList() + "*"
        parts[1].contains("/") -> parts[1].split("/").toList() + "/"
        else -> listOf(parts[1], "0", "NOOP")
    }
    val monkey = Monkey(name, operation, left.trim(), right.trim())
    if (operation == "NOOP") monkey.result = left.trim().toULong()
    return monkey
}

fun part1(inputLines: List<String>): ULong {
    val monkeys = inputLines.map { parseMonkey(it) }
    val monkeysByName: Map<String, Monkey> = monkeys.fold(mutableMapOf()) { acc, monkey ->
        acc[monkey.name] = monkey
        acc
    }

    do {
        val monkeysToProcess = monkeys.filter {it.result == null}
        monkeysToProcess.forEach {
            if (it.leftValue == null) it.leftValue = monkeysByName[it.left]!!.result
            if (it.rightValue == null) it.rightValue = monkeysByName[it.right]!!.result
            when {
                it.operation == "+" && it.hasValues() -> it.result = it.leftValue!! + it.rightValue!!
                it.operation == "-" && it.hasValues() -> it.result = it.leftValue!! - it.rightValue!!
                it.operation == "*" && it.hasValues() -> it.result = it.leftValue!! * it.rightValue!!
                it.operation == "/" && it.hasValues() -> it.result = it.leftValue!! / it.rightValue!!
            }
        }
    } while (monkeysToProcess.isNotEmpty())

    monkeys.forEach{ println(it) }

    // 2 147 483 647
    // 1 405 357 608 is too low
    return monkeysByName["root"]!!.result!!
}

fun part2(inputLines: List<String>): Int {
    return 0
}

fun main() {
    val inputLines = readInput("day21")
    println(part1(inputLines))
    println(part2(inputLines))
}