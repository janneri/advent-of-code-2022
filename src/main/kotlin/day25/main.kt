package day25

import util.equals
import util.readTestInput
import kotlin.math.pow

data class SnafuDigit(val value: Long, val symbol: Char) {
    companion object {
        fun of(symbol: Char): SnafuDigit = when (symbol) {
            '-' -> SnafuDigit(-1, '-')
            '=' -> SnafuDigit(-2, '=')
            else -> SnafuDigit(symbol.digitToInt().toLong(), symbol)
        }

        fun ofNum(value: Long): SnafuDigit = when (value) {
            -1L -> SnafuDigit(-1, '-')
            -2L -> SnafuDigit(-2, '=')
            else -> SnafuDigit(value, value.toInt().digitToChar())
        }
    }

    override fun toString(): String = symbol.toString()
}

data class SnafuNumber(val digits: List<SnafuDigit>) {
    companion object {
        fun of(str: String) = SnafuNumber(str.map { SnafuDigit.of(it) })

        fun of(value: Long): SnafuNumber {
            // We need to divide by the base (5) until quotient becomes 0. For example 13 -> 111:
            // 13 / 5 = quotient 2, rem 3
            //  2 / 5 = quotient 0, rem 2
            // The twist here is that 2 is the max number in SNAFU.
            // If the rem is 3 or 4, we need to add 1 to the next 5 and use the doubleminus or minus
            // 13 / 5 = quotient 2, rem 3 -> quotient 3, rem -2
            //  3 / 5 = quotient 0, rem 3 -> quotient 1, rem -2
            //  1 / 5 = quotient 0, rem 1
            var quotient = value
            var rem: Long
            val result = mutableListOf<Long>()
            while (quotient != 0L) {
                rem = quotient % 5
                quotient /= 5
                if (rem == 3L) {
                    rem = -2L
                    quotient += 1
                }
                else if (rem == 4L) {
                    rem = -1L
                    quotient += 1
                }
                result += rem
            }

            return SnafuNumber(result.reversed().map { SnafuDigit.ofNum(it) })
        }
    }

    fun toLong(): Long {
        return digits.reversed().foldIndexed(0) {index, acc, snafuDigit ->
            val value: Long = 5.toDouble().pow(index.toDouble()).toLong() * snafuDigit.value
            value + acc
        }
    }

    override fun toString(): String = digits.joinToString("")
}

fun testConversions() {
    val testCases = mapOf(
        "1" to 1L,
        "2" to 2L,
        "1=" to 3L,
        "1-" to 4L,
        "10" to 5L,
        "11" to 6L,
        "12" to 7L,
        "2=" to 8L,
        "2-" to 9L,
        "20" to 10L,
        "1=0" to 15L,
        "1-0" to 20L,
        "1=11-2" to 2022L,
        "1-0---0" to 12345L,
        "1121-1110-1=0" to 314159265L,
    )

    testCases.forEach {(snafuStr, longVal) -> equals(longVal, SnafuNumber.of(snafuStr).toLong()) }
    testCases.forEach {(snafuStr, longVal) -> equals(snafuStr, SnafuNumber.of(longVal).toString())}
}

fun main() {
    testConversions()
    val inputLines = readTestInput("day25")
    val sumOfNumbers = inputLines
        .map { SnafuNumber.of(it) }
        .sumOf { it.toLong() }

    println(SnafuNumber.of(sumOfNumbers).toString())
}