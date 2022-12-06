package day5

import util.readTestInput

data class Crate(val symbol: Char, val stackNumber: Int)
data class Move(val count: Int, val fromStack: Int, val toStack: Int)

data class GameInput(val moves: List<Move>, val crates: List<Crate>, val stackCount: Int) {
    companion object {
        /*
            Input looks something like this:
                [D]
            [N] [C]
            [Z] [M] [P]
             1   2   3

            move 1 from 2 to 1
            move 3 from 1 to 3
         */
        fun parse(inputLines: List<String>): GameInput {
            val moves = inputLines.takeLastWhile { it.isNotBlank() }.map { parseMove(it) }
            val linesBeforeBlank = inputLines.takeWhile { it.isNotBlank() }
            val stackCount = linesBeforeBlank.last().trim().last().digitToInt()
            val crates = linesBeforeBlank.dropLast(1).map { parseCrates(it, stackCount) }.flatten()

            return GameInput(moves, crates, stackCount)
        }

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
    }
}

fun playMoves(gameInput: GameInput, oneAtATimeMode: Boolean): String {
    val stacks = gameInput.crates.groupBy { it.stackNumber }.toMutableMap()

    gameInput.moves.forEach {move ->
        val cratesToMove = stacks[move.fromStack]!!.take(move.count)
        stacks[move.fromStack] = stacks[move.fromStack]!!.drop(move.count)
        val cratesToInsert = if (oneAtATimeMode) cratesToMove.reversed() else cratesToMove
        stacks[move.toStack] = cratesToInsert + stacks[move.toStack]!!
    }

    return stacks.keys.sorted().fold("") {
            acc, stackNumber -> acc + stacks[stackNumber]?.first()?.symbol.toString()
    }
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/5
    // Downloaded the input from https://adventofcode.com/2022/day/5/input

    val gameInput = GameInput.parse(readTestInput("day5"))

    println(playMoves(gameInput, true)) // part1
    println(playMoves(gameInput, false)) // part2
}