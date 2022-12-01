package util

import java.nio.file.Files
import java.nio.file.Path


fun createDay(dayNum: Int) {
    val dir = Path.of("src", "main", "kotlin", "day$dayNum")
    Files.createDirectories(dir)
    dir.resolve("input.txt").toFile().createNewFile()
    dir.resolve("testinput.txt").toFile().createNewFile()
    val mainFile = dir.resolve("main.kt").toFile()
    Files.writeString(mainFile.toPath(), """
        package day$dayNum
        
        import util.readLines
        
        fun part1(input: List<String>): String {
            return "todo"
        }
        
        fun part2(input: List<String>): String {
            return "todo"
        }
        
        fun main() {
            val inputLines = readLines("src/main/kotlin/day$dayNum/testinput.txt")
            println(part1(inputLines))
            println(part2(inputLines))
        }
        """.trimIndent())

}

fun main() {
    createDay(2)
}