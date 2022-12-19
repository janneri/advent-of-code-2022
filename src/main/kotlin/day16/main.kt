package day16

import util.readTestInput
import kotlin.math.min

private typealias ValveId = String

private data class Valve(val valveId: ValveId) {
    var flowrate: Int = 0
    var neighbors: List<String> = listOf()

    private constructor(valveId: ValveId, flowrate: Int, neighbors: List<ValveId>): this(valveId) {
        this.flowrate = flowrate
        this.neighbors = neighbors
    }

    override fun toString(): String = valveId

    companion object {
        // Parses input lines, such as:
        // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        // Valve JJ has flow rate=21; tunnel leads to valve II
        fun parse(str: String): Valve {
            val valveId = str.substring(6, 8)
            val neighbors = when {
                str.contains("valves") -> str.substringAfter("to valves ").split(", ")
                else -> str.substringAfter("to valve ").split(", ")
            }
            val flowrate = str.substringAfter("flow rate=").takeWhile { it != ';' }.toInt()
            return Valve(valveId, flowrate, neighbors)
        }
    }

}

private data class GameState(var totalPressureReleased : Int = 0, val currentValve: Valve,
                             val currentElephantValve: Valve?, val openValves: Set<Valve>) {
    fun withNewTotalPressure(): GameState {
        totalPressureReleased += openValves.sumOf { it.flowrate }
        return this
    }

    fun currentValveOpenable(): Boolean =
        !openValves.contains(currentValve) && currentValve.flowrate > 0
    fun currentElephantValveOpenable(): Boolean =
        !openValves.contains(currentElephantValve) && currentElephantValve!!.flowrate > 0

    fun currentPressure(): Int = openValves.sumOf { it.flowrate }

}

private class Tunnels(valves: List<Valve>, initialValveId: ValveId, elephantValveId: ValveId?) {
    val valvesById: Map<ValveId, Valve> = valves.fold(mutableMapOf()) { acc, valve ->
        acc[valve.valveId] = valve
        acc
    }
    val openableValves = valves.filter { it.flowrate > 0 }
    val openableValveCount = openableValves.size
    // Distances from all nodes to any other node. In the sample, distances["AA"]["BB"] would be 2
    val distances: Map<ValveId, Map<ValveId, Int>> = valves.associate { it.valveId to getDistances(it) }

    val initialState = GameState(
        currentValve = valvesById[initialValveId]!!,
        currentElephantValve = valvesById[elephantValveId],
        openValves = mutableSetOf()
    )

    fun getDistances(fromValve: Valve): Map<ValveId, Int> {
        val visited = mutableSetOf<Valve>()
        val distances = mutableMapOf(fromValve.valveId to 0)
        val queue = mutableListOf(fromValve)
        while (queue.isNotEmpty()) {
            val current = queue.removeAt(0)
            if (!visited.contains(current)) {
                visited.add(current)
                current.neighbors.forEach{neighbor ->
                    val newDistance = distances[current.valveId]!! + 1
                    if (distances[neighbor] == null) distances[neighbor] = newDistance
                    queue.add(valvesById[neighbor]!!)
                }
            }
        }
        return distances
    }

    fun theoreticalMax(gameState: GameState, timeLeft: Int): Int {
        var closedValveSum = 0
        if (timeLeft > 1) {
            closedValveSum = openableValves.filter { !gameState.openValves.contains(it) }
                .fold(0) { acc, valve ->
                    val moveTime = distances[gameState.currentValve.valveId]?.get(valve.valveId)!!
                    val minMoveTime = when (gameState.currentElephantValve) {
                        null -> moveTime
                        else -> min(moveTime, distances[gameState.currentElephantValve.valveId]?.get(valve.valveId)!!)
                    }
                    acc + (timeLeft - minMoveTime - 1) * valve.flowrate
                }
        }

        val openValveSum = timeLeft * gameState.currentPressure()
        return gameState.totalPressureReleased + closedValveSum + openValveSum
    }

    fun nextStatesFromState(state: GameState): Set<GameState> {
        val newStates = mutableSetOf<GameState>()

        if (state.currentElephantValve == null) {
            // open current
            if (state.currentValveOpenable()) {
                val newState = state.copy(
                    totalPressureReleased = state.totalPressureReleased + state.currentPressure(),
                    openValves = state.openValves + state.currentValve
                )
                newStates.add(newState)
            }
            // move to neighbors
            newStates.addAll(state.currentValve.neighbors.map {
                val nextValve = valvesById[it]!!
                val newState = state.copy(currentValve = nextValve).withNewTotalPressure()
                newState
            })
        }
        else {
            // I move, the elephant moves
            newStates.addAll(state.currentValve.neighbors.flatMap {
                val nextValve = valvesById[it]!!
                state.currentElephantValve.neighbors
                    .map { ev -> valvesById[ev]!! }
                    .map { eValve ->
                        state.copy(currentValve = nextValve,
                            currentElephantValve = eValve,
                            totalPressureReleased = state.totalPressureReleased + state.currentPressure())
                    }.toSet()
            })

            // I move, the elephant opens
            if (state.currentElephantValveOpenable()) {
                newStates.addAll(state.currentValve.neighbors.map {
                    val nextValve = valvesById[it]!!
                    state.copy(currentValve = nextValve,
                        openValves = state.openValves + state.currentElephantValve,
                        totalPressureReleased = state.totalPressureReleased + state.currentPressure())
                })
            }

            // I open, the elephant moves
            if (state.currentValveOpenable()) {
                newStates.addAll(state.currentElephantValve.neighbors.map {
                    val nextValve = valvesById[it]!!
                    state.copy(currentElephantValve = nextValve,
                        openValves = state.openValves + state.currentValve,
                        totalPressureReleased = state.totalPressureReleased + state.currentPressure())
                })
            }

            // I open, the elephant opens
            if (state.currentValveOpenable() && state.currentElephantValveOpenable()) {
                val newState = state.copy(
                    totalPressureReleased = state.totalPressureReleased + state.currentPressure(),
                    openValves = state.openValves + state.currentValve + state.currentElephantValve
                )
                newStates.add(newState)
            }
        }

        return newStates
    }

    fun nextStates(fromStates: Set<GameState>, timeLeft: Int): Set<GameState> {
        val nextStates = fromStates.flatMap { state ->
            val newStates = mutableSetOf<GameState>()

            // just wait because all valves are open
            if (state.openValves.size == openableValveCount) {
                val newState = state.copy().withNewTotalPressure()
                newStates.add(newState)
            }
            else {
                // add all possible states, where we open valves or move
                newStates.addAll(nextStatesFromState(state))
            }

            newStates
        }

        return nextStates
            .sortedBy { state -> theoreticalMax(state, timeLeft) }
            .reversed()
            .take(1000)
            .toSet()
    }
}

fun playRounds(inputLines: List<String>, minutes: Int, elephantValveId: ValveId?): Int {
    val tunnels = Tunnels(inputLines.map { Valve.parse(it) }, "AA", elephantValveId)

    var currentStates = setOf(tunnels.initialState)
    for (i in 1 .. minutes) {
        println("== Minute $i (${currentStates.size} states)==")
        currentStates = tunnels.nextStates(currentStates, minutes - i)
    }

    return currentStates.maxBy { it.totalPressureReleased }.totalPressureReleased
}

fun main() {
    val inputLines = readTestInput("day16")
    println(playRounds(inputLines, 30, null))
    println(playRounds(inputLines, 26, "AA"))
}
