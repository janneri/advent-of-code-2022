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


fun readLines(filePath: String): List<String> {
    return File(filePath)
        .readLines()
}


// 1-3 a: abcde
private val regex = Regex("""(\d+)-(\d+) ([a-z]): ([a-z]+)""")
fun parseUsingRegex(line: String): RegexData =
    regex.matchEntire(line)!!
        .destructured
        .let { (start, end, letter, password) ->
            RegexData(password, start.toInt(), end.toInt(), letter.single())
        }

data class RegexData(val str: String, val start: Int, val end: Int, val letter: Char)