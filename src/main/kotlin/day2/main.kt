package day2

import day2.Hand.*
import day2.RoundResult.*
import util.readTestInput

enum class Hand(val points: Int, val symbols: List<String>) {
    ROCK(1, listOf("X", "A")),
    PAPER(2, listOf("Y", "B")),
    SCISSORS(3, listOf("Z", "C"));
    companion object {
        fun ofSymbol(symbol: String): Hand {
            return values().findLast { hand ->  hand.symbols.contains(symbol) }!!
        }
    }
}
data class Round(val opponentHand: Hand, val myHand: Hand)
enum class RoundResult(val points: Int, val symbol: String) {
    LOSE(0, "X"),
    DRAW(3, "Y"),
    WIN(6, "Z")
}

val possibleRoundResults = mapOf(
    mapOf(ROCK to PAPER) to LOSE,
    mapOf(ROCK to SCISSORS) to WIN,
    mapOf(PAPER to ROCK) to WIN,
    mapOf(PAPER to SCISSORS) to LOSE,
    mapOf(SCISSORS to ROCK) to LOSE,
    mapOf(SCISSORS to PAPER) to WIN
)

fun calculatePoints(round: Round): Int {
    val result = possibleRoundResults.getOrDefault(mapOf(round.myHand to round.opponentHand), DRAW)
    return round.myHand.points + result.points
}

fun part1(inputLines: List<String>): Int {
    return inputLines
        .map { it.split(" ").let { (h1, h2) -> Round(Hand.ofSymbol(h1), Hand.ofSymbol(h2)) } }
        .map { round -> calculatePoints(round) }
        .sum()
}

fun resolveExpectedHand(opponentSymbol: String, expectedResultSymbol: String): Round {
    val opponentHand = Hand.ofSymbol(opponentSymbol)
    val expectedResult = RoundResult.values().findLast { r -> r.symbol == expectedResultSymbol }

    val expectedHand = when {
        (expectedResult == WIN && opponentHand == ROCK) -> PAPER
        (expectedResult == WIN && opponentHand == PAPER) -> SCISSORS
        (expectedResult == WIN && opponentHand == SCISSORS) -> ROCK
        (expectedResult == LOSE && opponentHand == ROCK) -> SCISSORS
        (expectedResult == LOSE && opponentHand == PAPER) -> ROCK
        (expectedResult == LOSE && opponentHand == SCISSORS) -> PAPER
        else -> opponentHand
    }

    return Round(opponentHand, expectedHand)
}

fun part2(inputLines: List<String>): Int {
    return inputLines
        .map { it.split(" ").let {
                (opponentSymbol, expectedResultSymbol) -> resolveExpectedHand(opponentSymbol, expectedResultSymbol) }
        }
        .map { round -> calculatePoints(round) }
        .sum()
}

fun main() {
    val inputLines = readTestInput("day2")
    println(part1(inputLines))
    println(part2(inputLines))
}