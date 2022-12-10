package day10

import util.readInput

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
    return commands;
}

class VideoSystem(var cycle: Int = 1, var x: Int = 1, val commands: List<Command>, var activeCommand: Command = commands.first()) {
    fun endingCommands(): List<Command> = commands.filter { it.endCycle == cycle }
    fun lastCycle(): Int = commands.map { it.endCycle }.max()
    val signalStrenths = mutableListOf<Int>()
    val signalStrenghtCycles = listOf(20, 60, 100, 140, 180, 220)

    fun isSpriteVisible(): Boolean {
        // the sprite is 3 pixels wide, and the X register sets the horizontal position of the middle of that sprite
        val drawPosition = (if (cycle % 40 == 0) 40 else cycle % 40) - 1
        return drawPosition - 1 <= x && x <= drawPosition + 1
    }

    fun drawPixel() {
        val pixel = if (isSpriteVisible()) '#' else '.'
        print(pixel)
    }

    fun drawPrintlnIfRequired() {
        if (cycle % 40 == 0) println()
    }

    fun runCommands(commands: List<Command>) {
        commands.forEach {
            if (it is Add && it.endCycle == cycle) {
                x += it.amount
            }
        }
    }

    fun playCycle() {
        if (cycle in signalStrenghtCycles) {
            signalStrenths.add(cycle * x)
        }
        drawPixel()
        drawPrintlnIfRequired()
        runCommands(endingCommands())
        cycle += 1
    }

    fun playCycles() {
        for (cycle in 1..lastCycle()) {
            playCycle()
        }
    }
}

fun main() {
    // Solution for https://adventofcode.com/2022/day/10
    // Downloaded the input from https://adventofcode.com/2022/day/10/input
    
    val inputLines = readInput("day10")
    val commands = parseCommands(inputLines)

    val videoSystem = VideoSystem(commands = commands)
    videoSystem.playCycles()

    println("the sum of ${videoSystem.signalStrenths} is ${videoSystem.signalStrenths.sum()}")
}