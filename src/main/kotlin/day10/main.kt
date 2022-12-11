package day10

import util.readTestInput

open class Command(val endCycle: Int)
data class Add(val amount: Int, val startCycle: Int): Command(startCycle + 1)
data class Noop(val startCycle: Int): Command(startCycle)

fun parseCommands(inputLines: List<String>): List<Command> {
    val commands = mutableListOf<Command>()
    inputLines.forEachIndexed { index, cmdStr ->
        val prevCommand = commands.getOrNull(index - 1)
        val nextStart = if (prevCommand != null) prevCommand.endCycle + 1 else index + 1
        val command = when {
            cmdStr.startsWith("addx") -> Add(cmdStr.substringAfter(" ").toInt(), startCycle = nextStart)
            else -> Noop(startCycle = nextStart)
        }
        commands.add(command)
    }
    return commands
}

class VideoSystem(private var cycle: Int = 1, private var x: Int = 1, val commands: List<Command>) {
    private fun endingCommands(): List<Command> = commands.filter { it.endCycle == cycle }
    private fun lastCycle(): Int = commands.maxOfOrNull { it.endCycle }!!
    private val signalStrenghtCycles = listOf(20, 60, 100, 140, 180, 220)

    private fun isSpriteVisible(): Boolean {
        // the sprite is 3 pixels wide, and the X register sets the horizontal position of the middle of that sprite
        val drawPosition = (cycle - 1) % 40
        return drawPosition - 1 <= x && x <= drawPosition + 1
    }

    private fun drawPixel() {
        val pixel = if (isSpriteVisible()) '#' else '.'
        print(pixel)
        if (cycle % 40 == 0) println()
    }

    private fun runCommands(commands: List<Command>) {
        commands.forEach {
            if (it is Add && it.endCycle == cycle) {
                x += it.amount
            }
        }
    }

    private fun playCycle() {
        if (cycle in signalStrenghtCycles) {
            signalStrenths.add(cycle * x)
        }
        drawPixel()
        runCommands(endingCommands())
        cycle += 1
    }

    val signalStrenths = mutableListOf<Int>()

    fun playCycles() {
        for (cycle in 1..lastCycle()) {
            playCycle()
        }
    }
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/10
    // Downloaded the input from https://adventofcode.com/2022/day/10/input
    
    val inputLines = readTestInput("day10")
    val commands = parseCommands(inputLines)

    val videoSystem = VideoSystem(commands = commands)
    videoSystem.playCycles()

    println("the sum of ${videoSystem.signalStrenths} is ${videoSystem.signalStrenths.sum()}")
}