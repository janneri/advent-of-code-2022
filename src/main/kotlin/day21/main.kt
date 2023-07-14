package day21

import util.readInput

sealed class Monkey(open val name: String) {
    abstract fun result(): ULong
    abstract fun resultPart2(humnPath: Set<Monkey>, expectedResult: ULong): ULong

    companion object {
        fun parse(str: String) = when {
            NumberMonkey.matches(str) -> NumberMonkey.of(str)
            else -> MathOperationMonkey.of(str)
        }
    }
}

data class NumberMonkey(override val name: String, val number: ULong): Monkey(name) {
    override fun result(): ULong = number
    override fun resultPart2(humnPath: Set<Monkey>, expectedResult: ULong): ULong =
        if (name == "humn") expectedResult else number

    companion object {
        private val regex = Regex("""([a-z]+): (\d+)""")
        fun matches(str: String) = regex.containsMatchIn(str)
        fun of(str: String) = regex.matchEntire(str)!!
            .destructured
            .let { (name, number) ->
                NumberMonkey(name, number.toULong())
            }
    }
}

data class MathOperationMonkey(override val name: String, val leftName: String, val rightName: String, val operation: String): Monkey(name) {
    lateinit var leftMonkey: Monkey
    lateinit var rightMonkey: Monkey

    override fun result(): ULong = when (operation) {
        "+" -> leftMonkey.result() + rightMonkey.result()
        "-" -> leftMonkey.result() - rightMonkey.result()
        "*" -> leftMonkey.result() * rightMonkey.result()
        else -> leftMonkey.result() / rightMonkey.result()
    }

    /** returns the unknown left value when the right value is known */
    private fun calculateLeftValue(expectedResult: ULong, rightValue: ULong): ULong {
        return when (operation) {
            "+" -> expectedResult - rightValue // ? + rightValue = expectedResult
            "-" -> expectedResult + rightValue // ? - rightValue = expectedResult
            "*" -> expectedResult / rightValue // ? * rightValue = expectedResult
            else -> expectedResult * rightValue // ? / rightValue = expectedResult
        }
    }

    /** returns the unknown right value when the left value is known */
    private fun calculateRightValue(expectedResult: ULong, leftValue: ULong): ULong {
        return when (operation) {
            "+" -> expectedResult - leftValue // leftValue + ? = expectedResult
            "-" -> leftValue - expectedResult // leftValue - ? = expectedResult
            "*" -> expectedResult / leftValue // leftValue * ? = expectedResult
            else -> leftValue / expectedResult // leftValue / ? = expectedResult
        }
    }

    override fun resultPart2(humnPath: Set<Monkey>, expectedResult: ULong): ULong {
        return when (leftMonkey) {
            // When left the unknown value (and right value is known):
            in humnPath -> leftMonkey.resultPart2(humnPath, calculateLeftValue(expectedResult, rightMonkey.result()))
            // When right is the unkown value (and left is fixed):
            else -> rightMonkey.resultPart2(humnPath, calculateRightValue(expectedResult, leftMonkey.result()))
        }
    }

    companion object {
        private val regex = Regex("""([a-z]+): ([a-z]+) (.) ([a-z]+)""")
        fun of(str: String) = regex.matchEntire(str)!!
                .destructured
                .let { (name, leftName, operation, rightName) ->
                    MathOperationMonkey(name, leftName, rightName, operation)
                }
    }
}

fun parseMonkeys(inputLines: List<String>): Map<String, Monkey> {
    val monkeys = inputLines.map { Monkey.parse(it) }
    val monkeysByName = monkeys.associateBy { it.name }
    monkeys
        .filterIsInstance<MathOperationMonkey>()
        .forEach {
            it.leftMonkey = monkeysByName[it.leftName]!!
            it.rightMonkey = monkeysByName[it.rightName]!!
        }
    return monkeysByName
}

fun findHumnPath(rootMonkey: Monkey): Set<Monkey> {
    fun findPath(monkey: Monkey, path: MutableList<Monkey>): Boolean {
        path += monkey
        if (monkey is NumberMonkey) {
            if (monkey.name == "humn") return true
        }
        if (monkey is MathOperationMonkey && (findPath(monkey.leftMonkey, path) || findPath(monkey.rightMonkey, path))) {
            return true
        }

        path.removeLast()
        return false
    }
    val path = mutableListOf<Monkey>()
    findPath(rootMonkey, path)
    return path.toSet()
}

fun part1(inputLines: List<String>): ULong {
    val monkeysByName = parseMonkeys(inputLines)
    return monkeysByName["root"]!!.result()
}

fun part2(inputLines: List<String>): ULong {
    val monkeysByName = parseMonkeys(inputLines)
    val humnPathMonkeys = findHumnPath(monkeysByName["root"]!!)
    val root = monkeysByName["root"]!! as MathOperationMonkey

    if (root.leftMonkey in humnPathMonkeys) {
        return root.leftMonkey.resultPart2(humnPathMonkeys, root.rightMonkey.result())
    }
    else {
        return root.rightMonkey.resultPart2(humnPathMonkeys, root.leftMonkey.result())
    }
}


fun main() {
    val inputLines = readInput("day21")
    println(part1(inputLines))
    println(part2(inputLines))
}