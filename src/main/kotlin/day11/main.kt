package day11

import util.readTestInput
import java.lang.IllegalArgumentException

data class Operation(val leftOperand: String, val symbol: String, val rightOperand: String) {
    companion object {
        fun parse(str: String) = str.split(" ").let{Operation(it[0], it[1], it[2])}
    }
}

data class Monkey(val monkeyNumber: Int, var inspectCount: Long = 0, val items: MutableList<ULong>,
                  val operation: Operation, val testDivisor: ULong, val trueMonkeyNum: Int, val falseMonkeyNum: Int)

fun parseMonkey(lines: List<String>) =
    Monkey(
        monkeyNumber = lines[0].substringAfter("Monkey ")[0].digitToInt(),
        items = lines[1].substringAfter("items: ").split(", ").map { it.trim().toULong() }.toMutableList(),
        operation = Operation.parse(lines[2].substringAfter("Operation: new = ")),
        testDivisor = lines[3].substringAfter("Test: divisible by ").toULong(),
        trueMonkeyNum = lines[4].substringAfter("If true: throw to monkey ").toInt(),
        falseMonkeyNum = lines[5].substringAfter("If false: throw to monkey ").toInt(),
    )

fun runOperation(operation: Operation, item: ULong): ULong {
    fun convertToNumber(operand: String) = if (operand == "old") item else operand.toULong()
    val leftOperand = convertToNumber(operation.leftOperand)
    val rightOperand = convertToNumber(operation.rightOperand)

    return when (operation.symbol) {
        "+" -> leftOperand + rightOperand
        "*" -> leftOperand * rightOperand
        else -> throw IllegalArgumentException("Unknown operand")
    }
}

fun playRound(monkeys: List<Monkey>, divideBy3Mode: Boolean) {
    val commonDivisor = monkeys.map { it.testDivisor }.reduce { acc, divisor -> acc * divisor }

    monkeys.forEach {monkey ->
        monkey.items.forEach {item ->
            var worryLevel = runOperation(monkey.operation, item)

            if (divideBy3Mode) {
                // In part 1 the worry level is decreased by dividing it with constant 3
                worryLevel /= 3.toULong()
            }
            else {
                // In part 2, the keep worry level is decreased by dividing it with a common test divisor
                worryLevel %= commonDivisor
            }

            val targetMonkey = if (worryLevel % monkey.testDivisor == 0.toULong()) monkey.trueMonkeyNum else monkey.falseMonkeyNum

            monkeys[targetMonkey].items.add(worryLevel)
            monkey.inspectCount += 1
        }
        monkey.items.clear()
    }
}

fun playRounds(inputLines: List<String>, roundCount: Int, divideBy3Mode: Boolean): Long {
    val monkeys = inputLines.chunked(7).map { parseMonkey(it) }

    for (roundNumber in 1 ..roundCount) {
        playRound(monkeys, divideBy3Mode)
    }

    println(monkeys.joinToString("\n"))
    val mostActiveMonkeys = monkeys.sortedBy { it.inspectCount }.reversed().take(2)
    return mostActiveMonkeys[0].inspectCount * mostActiveMonkeys[1].inspectCount
}

fun main() {
    val inputLines = readTestInput("day11")

    println(playRounds(inputLines, 20, true))
    println(playRounds(inputLines, 10000, false))
}