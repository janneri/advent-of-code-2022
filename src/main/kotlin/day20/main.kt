package day20

import util.readTestInput

// Wrapper class to capture the original index of each number.
// The original index can be used to find the current index of number to be shifted
private data class ShiftingNumber(val originalIndex: Int, val value: Long)

// Decryption key is used in part 2. In part 1 it's 1.
private fun parseNumbers(inputLines: List<String>, decryptionKey: Long) =
    inputLines
        .mapIndexed { index, valueStr -> ShiftingNumber(index, decryptionKey * valueStr.toLong()) }
        .toMutableList()

private fun shift(index: Int, numberList: MutableList<ShiftingNumber>) {
    // Numbers should be moved in the order they originally appear.
    // Find the current position based on the original position:
    val currentIndex = numberList.indexOfFirst { it.originalIndex == index }

    // Remove the number
    val shiftingNumber = numberList.removeAt(currentIndex)

    // Resolve the new index (wrapping around when necessary):
    // Note: Unlike %, the mod-function always returns a positivive number.
    // This was the hardest part of the puzzle.
    val newIndex = (currentIndex + shiftingNumber.value).mod(numberList.size)

    // Add the number back
    numberList.add(newIndex, shiftingNumber)
}

private fun shiftAll(numberList: MutableList<ShiftingNumber>) {
    for (index in 0 until numberList.size) {
        shift(index, numberList)
    }
}

private fun getGroveCoordinates(numberList: List<ShiftingNumber>): List<Long> {
    val indexOfZero = numberList.indexOfFirst { it.value == 0L }
    return listOf(1000, 2000, 3000).map {
        numberList[(indexOfZero + it) % numberList.size].value
    }
}

fun part1(inputLines: List<String>): Long {
    val numberList = parseNumbers(inputLines, 1)
    shiftAll(numberList)
    return getGroveCoordinates(numberList).sum()
}

fun part2(inputLines: List<String>): Long {
    val numberList = parseNumbers(inputLines, 811589153)
    repeat(10) {
        shiftAll(numberList)
    }
    return getGroveCoordinates(numberList).sum()
}

fun main() {
    val inputLines = readTestInput("day20")
    println(part1(inputLines))
    println(part2(inputLines))
}