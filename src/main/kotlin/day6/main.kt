package day6

import util.readInput
import util.readTestInput

fun findMarker(data: String, count: Int): Int {
    val buffer = StringBuilder()
    data.toCharArray().mapIndexed { index, c ->
        // keep it at size 4
        if (buffer.length > count) buffer.deleteCharAt(0)

        // reset the buffer to start from the next letter after the duplicate
        if (buffer.contains(c)) {
            buffer.deleteRange(0, buffer.indexOf(c) + 1)
        }

        buffer.append(c)

        if (buffer.length == count) {
            return index + 1
        }
    }
    return -1
}

fun test() {
    check(findMarker("mjqjpqmgbljsphdztnvjfqwrcgsmlb", 4) == 7)

    check(findMarker("bvwbjplbgvbhsrlpgdmjqwftvncz", 14) == 23)
    check(findMarker("nppdvjthqldpwncqszvftbrmjlhg", 14) == 23)
    check(findMarker("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", 14) == 29)
    check(findMarker("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", 14) == 26)
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/6
    // Downloaded the input from https://adventofcode.com/2022/day/6/input
    test()

    val input = readTestInput("day6").first()
    println(findMarker(input, 4))
    println(findMarker(input, 14))
}