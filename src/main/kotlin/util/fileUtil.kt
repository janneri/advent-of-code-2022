package util

import java.io.File

fun readInts(filePath: String): List<Int> {
    return File(filePath)
        .readLines()
        .map { it.toInt() }
}

fun readNullableInts(filePath: String): List<Int?> {
    return File(filePath)
        .readLines()
        .map { if (it.isBlank()) null else it.toInt() }
}

fun readInput(dayDir: String): List<String> {
    return File("src/main/kotlin/$dayDir/input.txt").readLines()
}

fun readTestInput(dayDir: String): List<String> {
    return File("src/main/kotlin/$dayDir/testinput.txt").readLines()
}

fun printLinesOfCode() {
    (1..25).forEach { dayNum ->
        val srcFile = File("src/main/kotlin/day$dayNum/main.kt")
        if (srcFile.isFile) {
            val rowCount = srcFile
                .readLines()
                .filter { it.isNotBlank() }
                .count()

            println("$dayNum: $rowCount")
        }
    }
}

fun main() {
    printLinesOfCode()
}