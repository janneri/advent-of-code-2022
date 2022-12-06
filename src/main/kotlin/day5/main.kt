package day5

import util.readTestInput

data class Crate(val symbol: Char, val stackNumber: Int)
data class Move(val count: Int, val fromStack: Int, val toStack: Int)

// String is for instance "move 1 from 2 to 1"
private val moveRegex = Regex("""move (\d+) from (\d+) to (\d+)""")
fun parseMove(line: String): Move =
    moveRegex.matchEntire(line)!!
        .destructured
        .let { (count, fromStack, toStack) ->
            Move(count.toInt(), fromStack.toInt(), toStack.toInt())
        }

// Parses a line such as "[Z] [M] [P]"
fun parseCrates(line: String, numberOfStacks: Int): List<Crate> {
    val crates = mutableListOf<Crate>()
    var currentCharIndex = 1 // first letter is at pos 1
    for (stackNumber in 1 .. numberOfStacks) {
        if (currentCharIndex < line.length && line[currentCharIndex].toString().isNotBlank()) {
            crates.add(Crate(line[currentCharIndex], stackNumber))
        }
        currentCharIndex += 4 // there's a gap of 4 between each letter
    }
    return crates
}

fun playMoves(crateLines: List<String>, moveLines: List<String>, oneAtATimeMode: Boolean, stackCount: Int): String {
    val moves = moveLines.map { parseMove(it) }
    var stacks = crateLines.map { parseCrates(it, stackCount) }.flatten().groupBy { it.stackNumber }.toMutableMap()

    moves.forEach {move ->
        val cratesToMove = stacks[move.fromStack]!!.take(move.count)
        stacks[move.fromStack] = stacks[move.fromStack]!!.drop(move.count)
        val cratesToInsert = if (oneAtATimeMode) cratesToMove.reversed() else cratesToMove
        stacks[move.toStack] = cratesToInsert + stacks[move.toStack]!!
    }

    return stacks.keys.sorted().fold("") {
            acc, stackNumber -> acc + stacks[stackNumber]?.first()?.symbol.toString()
    }
}

fun part1(crateLines: List<String>, moveLines: List<String>, stackCount: Int): String {
    return playMoves(crateLines, moveLines, true, stackCount)
}

fun part2(crateLines: List<String>, moveLines: List<String>, stackCount: Int): String {
    return playMoves(crateLines, moveLines, false, stackCount)
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/5
    // Downloaded the input from https://adventofcode.com/2022/day/5/input

    val inputLines = readTestInput("day5")

    val linesBeforeBlank = inputLines.takeWhile { it.isNotBlank() }
    val stackNumberLine = linesBeforeBlank.last()
    val stackCount = stackNumberLine.trim().last().digitToInt()
    val crateLines = linesBeforeBlank.dropLast(1)
    val moveLines = inputLines.takeLastWhile { it.isNotBlank() }

    println(part1(crateLines, moveLines, stackCount))
    println(part2(crateLines, moveLines, stackCount))
}