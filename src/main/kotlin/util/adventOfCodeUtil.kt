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
        
        import util.readTestInput
        
        fun part1(inputLines: List<String>): String {
            return "todo"
        }
        
        fun part2(inputLines: List<String>): String {
            return "todo"
        }
        
        fun main() {
            val inputLines = readTestInput("day$dayNum")
            println(part1(inputLines))
            println(part2(inputLines))
        }
        """.trimIndent())

}

fun main() {
    createDay(4)
}