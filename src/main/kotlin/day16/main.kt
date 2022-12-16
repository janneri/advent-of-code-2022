package day16

import util.readInput
import util.readTestInput
import java.lang.IllegalArgumentException

private typealias Path = List<Valve>

private data class Valve(val symbol: String, val flowrate: Int, val neighbors: List<String>) {
    override fun toString(): String = symbol

    companion object {
        fun parse(str: String): Valve {
            // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
            // Valve JJ has flow rate=21; tunnel leads to valve II
            val symbol = str.substring(6, 8)
            val neighbors = when {
                str.contains("valves") -> str.substringAfter("to valves ").split(", ")
                else -> str.substringAfter("to valve ").split(", ")
            }
            val flowrate = str.substringAfter("flow rate=").takeWhile { it != ';' }.toInt()
            return Valve(symbol, flowrate, neighbors)
        }
    }
}

private data class GameState(var totalPressureReleased : Int = 0, val currentValve: Valve, val openValves: Set<Valve>) {
    fun withNewTotalPressure(): GameState {
        totalPressureReleased += openValves.map { it.flowrate }.sum()
        return this
    }
}

/*
override fun hashCode() = EssentialData(this).hashCode()
        override fun toString() : String {
            return if(isLeaf()) leaf.toString()
            else "[" + left?.toString() + "," + right?.toString() + "]"
        }
    }

private data class EssentialData(val currentValve: Valve, val openValves: Set<Valve>) {
    constructor(state: GameState) : this(leaf = number.leaf)
}

 */

private class Cave(val valves: List<Valve>, val minutes: Int = 30, var totalPressureReleased: Int = 0) {
    val openValves = mutableSetOf<Valve>()
    val valvesBySymbol = valves.groupBy { it.symbol }
    fun getValve(symbol: String): Valve = valvesBySymbol[symbol]?.first()!!
    var currentValve = getValve("AA")
    fun currentPressure() = openValves.map { it.flowrate }.sum()

    val initialState = GameState(currentValve = getValve("AA"), openValves = mutableSetOf())

    fun nextStates(fromStates: Set<GameState>): Set<GameState> {
        val nextStates = fromStates.flatMap { state ->
            val newStates = mutableSetOf<GameState>()
            // avaaminen
            if (!state.openValves.contains(state.currentValve) && state.currentValve.flowrate > 0) {
//                val newState = state.copy(openValves = state.openValves + state.currentValve).withNewTotalPressure()
                val newState = state.copy().withNewTotalPressure().copy(openValves = state.openValves + state.currentValve)
                newStates.add(newState)
            }
            // odottaminen, koska kaikki on avattu
            if (state.openValves.size == valves.size) {
                val newState = state.copy().withNewTotalPressure()
                newStates.add(newState)
            }
            // liikkuminen
            newStates.addAll(state.currentValve.neighbors.map {
                val nextValve = getValve(it)
                val newState = state.copy(currentValve = nextValve).withNewTotalPressure()
                newState
            })
            newStates
        }.toSet()

        // prune if currentvalve and open valves are the same

        return nextStates
    }

    private fun nextPaths(currentPaths: List<Path>, visited: MutableSet<Valve>): List<Path> {
        val newPaths = currentPaths.map { path ->
            val nextAvailable = path.last().neighbors.map { getValve(it) }.filter { !visited.contains(it) }
            if (nextAvailable.isEmpty()) emptyList<Path>()
            visited.addAll(nextAvailable)
            nextAvailable.map { path + it }
        }.filter { it.isNotEmpty() }.flatten()

        return newPaths;
    }

    fun getShortestPathTo(fromValve: Valve, toValve: Valve): Path {
        if (fromValve == toValve) return emptyList()

        var currentPaths = listOf(listOf(fromValve))
        val visited = mutableSetOf(fromValve)

        for (steps in 1 until 1000) {
            val nextPaths = nextPaths(currentPaths, visited)
            val winningPaths = nextPaths.filter { toValve == it.last() }
            if (winningPaths.isNotEmpty()) {
                return winningPaths.first()
            }
            visited.addAll(nextPaths.map { it.last() })
            currentPaths = nextPaths
        }
        throw IllegalArgumentException("No path from $fromValve to $toValve")
    }

    fun getReleaseSum(round: Int, distanceTo: Int, valve: Valve): Int {
        if (valve.flowrate == 0) return 0
        val minutesLeft = minutes - round
        return (minutesLeft - distanceTo) * valve.flowrate
    }

    fun resolveNextValve(round: Int): Pair<Valve, Valve> {
        // optimal valve releases the most pressure?
        // What is the sum of pressure to end minus steps to move?
        val nextValves = valves
            .filter { !openValves.contains(it) && it.flowrate > 0 }
            .map { valve -> Pair(valve, getShortestPathTo(currentValve, valve)) }
            .map { (valve, path) -> Triple(valve, path, getReleaseSum(round, path.size - 1, valve)) }

//            .map { valve -> Pair(valve, getReleaseSum(round, getShortestPathTo(currentValve, valve).size - 1, valve)) }

        if (nextValves.isEmpty()) {
            // no more valves to open
            return Pair(currentValve, currentValve)
        }

        val (nextValve, path, releaseSum) = nextValves.maxBy { (_, _, releaseSum) -> releaseSum }

        println("$nextValve, $path, $releaseSum")
        return Pair(nextValve, if (currentValve == nextValve) currentValve else path[1])
    }

    fun playRound(round: Int) {
        println("== Minute $round ==, you are at ${currentValve.symbol}")
        if (openValves.isEmpty()) {
            println("No valves are open.")
        }
        else {
            val currentPressure = currentPressure()
            totalPressureReleased += currentPressure
            println("Valves ${openValves.map {it.symbol}.joinToString(",") } are open, releasing $currentPressure pressure.")
        }

        val (targetValve, nextValve) = resolveNextValve(round)
        if (currentValve == targetValve) {
            if (!openValves.contains(currentValve)) {
                println("You open valve ${currentValve.symbol}")
                openValves.add(currentValve)
            }
        }
        else {
            println("You move to valve to ${nextValve.symbol} from ${currentValve.symbol} ")
        }

        currentValve = nextValve
    }
}

fun part1(inputLines: List<String>): Int {
    val cave = Cave(inputLines.map { Valve.parse(it) })

//    for (i in 1 .. 30) {
//        cave.playRound(i)
//    }

    val expectedPath = listOf<String>(
        "AA", "DD", "DD", "CC", "BB", "BB", "AA", "II", "JJ", "JJ", "II",
        "AA", "DD", "EE", "FF", "GG", "HH", "HH", "GG", "FF", "EE", "EE",
        "DD", "CC", "CC",
        "CC", "CC", "CC", "CC", "CC", "CC",

    )
    var currentStates = setOf(cave.initialState)
    for (i in 1 .. 30) {
        val move = if (expectedPath[i-1] == expectedPath[i]) "open/stay ${expectedPath[i-1]}" else "move ${expectedPath[i-1]}->${expectedPath[i]}"
        println("== Minute $i (${currentStates.size} states)==")
//        println(currentStates.sortedBy { it.currentValve.symbol }.joinToString("\n"))
        println(move)
        currentStates = cave.nextStates(currentStates)
//        println(currentStates.sortedBy { it.currentValve.symbol }.joinToString("\n"))
    }


    /*
    == Minute 30 (138 957 states)==
open/stay CC
     */
//    println(currentStates.sortedBy { it.currentValve.symbol }.joinToString("\n"))
    return currentStates.maxBy { it.totalPressureReleased }.totalPressureReleased
}

fun part2(inputLines: List<String>): Int {
    return 0
}

fun main() {
    val inputLines = readTestInput("day16")
    println(part1(inputLines))
    println(part2(inputLines))
}