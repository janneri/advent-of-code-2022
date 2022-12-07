package day7

import util.readTestInput

open class Command
class ListCurrentDir: Command()
class ChangeDir(val arg: String): Command()
class ChangeDirOut: Command()

open class Result
data class MyFile(val size: Int, val name: String): Result()
data class MyDir(val name: String): Result()

fun parseCommand(str: String): Command? {
    return when {
        str.startsWith("$ ls") -> ListCurrentDir()
        str.startsWith("$ cd ..") -> ChangeDirOut()
        str.startsWith("$ cd") -> ChangeDir(str.substringAfterLast(" "))
        else -> null
    }
}

fun parseResult(str: String): Result? {
    return when {
        // "123 abc" means a file with size 123
        str.first().isDigit() -> str.split(" ").let {(size, name) -> MyFile(size.toInt(), name)}
        // "dir xyz" meas a dir named xyz
        str.startsWith("dir") -> MyDir(str.substringAfterLast(" "))
        else -> null
    }
}

val ROOT_DIR = "."
fun parseDirectorySizes(inputLines: List<String>): MutableMap<String, Int> {
    var currentPath = ROOT_DIR
    val directorySizes = mutableMapOf<String, Int>()

    for (line in inputLines) {
        when (val command = parseCommand(line)) {
            is ChangeDir -> {currentPath += "/${command.arg}"}
            is ChangeDirOut -> {currentPath = currentPath.substringBeforeLast("/")}
        }

        when (val result = parseResult(line)) {
            is MyFile -> {
                // increase current dir size
                val currentDirSize = directorySizes.getOrPut(currentPath) { 0 }
                directorySizes[currentPath] = currentDirSize + result.size

                // update parent directory sizes (unless inside the root dir)
                if (currentPath != ROOT_DIR) {
                    var dirPath = currentPath
                    do {
                        dirPath = dirPath.substringBeforeLast("/")
                        directorySizes[dirPath] = directorySizes.getOrPut(dirPath) { 0 } + result.size
                    } while (dirPath != ROOT_DIR)
                }
            }
        }
    }
    return directorySizes
}

fun part1(inputLines: List<String>): Int {
    val directorySizes = parseDirectorySizes(inputLines)
    directorySizes.remove(".")
    return directorySizes.values.filter { it <= 100000 }.sum()
}

fun part2(inputLines: List<String>): Int {
    val directorySizes = parseDirectorySizes(inputLines)

    val usedSize = directorySizes["."]!!
    val unusedSize = 70000000 - usedSize
    val spaceNeeded = 30000000

    val shouldFreeSize = spaceNeeded - unusedSize

    return directorySizes.filterValues { it >= shouldFreeSize }.values.min()
}


fun main() {
    // Solution for https://adventofcode.com/2022/day/7
    // Downloaded the input from https://adventofcode.com/2022/day/7/input

    // first command is cd / and ls, we can safely skip them
    val inputLines = readTestInput("day7").drop(2)
    println(part1(inputLines)) // for testinput 95 437
    println(part2(inputLines)) // for testinput 24 933 642
}