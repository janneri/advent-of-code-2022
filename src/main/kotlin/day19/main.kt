package day19

import util.readInput

data class Robot(
    val oreCost: Int = 0,
    val clayCost: Int = 0,
    val obsidianCost: Int = 0,
)

data class Blueprint(
    val id: Int,
    val oreRobot: Robot,
    val clayRobot: Robot,
    val obsidianRobot: Robot,
    val geodeRobot: Robot,
    // We can use this to prune the amount of states. It makes no sense to build more ore-robots than the max ore cost.
    val maxOreCost: Int = maxOf(oreRobot.oreCost, clayRobot.oreCost, obsidianRobot.oreCost, geodeRobot.oreCost),
    val maxClayCost: Int = maxOf(oreRobot.clayCost, clayRobot.clayCost, obsidianRobot.clayCost, geodeRobot.clayCost),
    val maxObsidianCost: Int = maxOf(oreRobot.obsidianCost, clayRobot.obsidianCost, obsidianRobot.obsidianCost, geodeRobot.obsidianCost),
) {

    companion object {
        private val regex =
            Regex("""Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""")

        fun of(line: String) = regex.matchEntire(line)!!
            .destructured
            .let { (blueprintId, oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, obsidianRobotClayCost,
                       geodeRobotOreCost, geodeRobotObsidianCost) ->
                Blueprint(
                    id = blueprintId.toInt(),
                    oreRobot = Robot(oreCost = oreRobotOreCost.toInt()),
                    clayRobot = Robot(oreCost = clayRobotOreCost.toInt()),
                    obsidianRobot = Robot(oreCost = obsidianRobotOreCost.toInt(), clayCost = obsidianRobotClayCost.toInt()),
                    geodeRobot = Robot(oreCost = geodeRobotOreCost.toInt(), obsidianCost = geodeRobotObsidianCost.toInt())
                )
            }
    }
}

data class GameState(
    val oreRobotCount: Int = 1,
    val clayRobotCount: Int = 0,
    val obsidianRobotCount: Int = 0,
    val geodeRobotCount: Int = 0,

    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0,
): Comparable<GameState> {
    override fun compareTo(other: GameState): Int {
        if (geode != other.geode) return other.geode.compareTo(geode)
        if (geodeRobotCount != other.geodeRobotCount) return other.geodeRobotCount.compareTo(geodeRobotCount)
        if (obsidian != other.obsidian) return other.obsidian.compareTo(obsidian)
        if (obsidianRobotCount != other.obsidianRobotCount) return other.obsidianRobotCount.compareTo(obsidianRobotCount)
        if (clay != other.clay) return other.clay.compareTo(clay)
        if (clayRobotCount != other.clayRobotCount) return other.clayRobotCount.compareTo(clayRobotCount)
        if (ore != other.ore) return other.ore.compareTo(ore)
        if (oreRobotCount != other.oreRobotCount) return other.oreRobotCount.compareTo(oreRobotCount)
        return 0
    }

    fun canAffordOreRobot(blueprint: Blueprint) = ore >= blueprint.oreRobot.oreCost
    fun canAffordClayRobot(blueprint: Blueprint) = ore >= blueprint.clayRobot.oreCost
    fun canAffordObsidianRobot(blueprint: Blueprint) = ore >= blueprint.obsidianRobot.oreCost && clay >= blueprint.obsidianRobot.clayCost
    fun canAffordGeodeRobot(blueprint: Blueprint) = ore >= blueprint.geodeRobot.oreCost && obsidian >= blueprint.geodeRobot.obsidianCost

    fun spendOnOreRobot(blueprint: Blueprint) = copy(ore = ore - blueprint.oreRobot.oreCost)
    fun spendOnClayRobot(blueprint: Blueprint) = copy(ore = ore - blueprint.clayRobot.oreCost)
    fun spendOnObsidianRobot(blueprint: Blueprint) = copy(ore = ore - blueprint.obsidianRobot.oreCost, clay = clay - blueprint.obsidianRobot.clayCost)
    fun spendOnGeodeRobot(blueprint: Blueprint) = copy(ore = ore - blueprint.geodeRobot.oreCost, obsidian =  obsidian - blueprint.geodeRobot.obsidianCost)

    fun collect(): GameState = copy(
        ore = ore + oreRobotCount,
        clay = clay + clayRobotCount,
        obsidian = obsidian + obsidianRobotCount,
        geode = geode + geodeRobotCount,
    )
}

fun getNextStates(state: GameState, blueprint: Blueprint): Set<GameState> {
    val nextStates = mutableSetOf<GameState>()

    // Build ore robot?
    if (state.oreRobotCount < blueprint.maxOreCost && state.canAffordOreRobot(blueprint)) {
        nextStates += state
            .spendOnOreRobot(blueprint)
            .collect()
            .copy(oreRobotCount = state.oreRobotCount + 1)
    }

    // Build clay robot?
    if (state.clayRobotCount < blueprint.maxClayCost && state.canAffordClayRobot(blueprint)) {
        nextStates += state
            .spendOnClayRobot(blueprint)
            .collect()
            .copy(clayRobotCount = state.clayRobotCount + 1)
    }

    // Build obsidian robot?
    if (state.obsidianRobotCount < blueprint.maxObsidianCost && state.canAffordObsidianRobot(blueprint)) {
        nextStates += state
            .spendOnObsidianRobot(blueprint)
            .collect()
            .copy(obsidianRobotCount = state.obsidianRobotCount + 1)
    }

    // Build geode robot?
    if (state.canAffordGeodeRobot(blueprint)) {
        nextStates += state
            .spendOnGeodeRobot(blueprint)
            .collect()
            .copy(geodeRobotCount = state.geodeRobotCount + 1)
    }

    // Or just wait and collect geodes ...
    nextStates += state.collect()

    return nextStates
}

fun play(states: Set<GameState>, blueprint: Blueprint): Set<GameState> =
    states
        // calculate all possible states from this state
        .flatMap { state -> getNextStates(state, blueprint) }
        // take the best states to keep the state-list smaller
        .toSortedSet()
        .take(10000)
        .toSet()

fun main() {
    val bluePrints = readInput("day19").map { Blueprint.of(it) }
    val debugPrint = false

    // part 1 (24 minutes)
    val rounds = 24
    val qualityLevels = bluePrints.map {bluePrint ->
        var states = setOf(GameState(oreRobotCount = 1))
        repeat(rounds) { minute ->
            if (debugPrint) println("Minute ${minute + 1} (states ${states.size})")
            states = play(states, bluePrint)
            if (debugPrint) println(states.take(5).joinToString("\n"))
        }
        val qualityLevel = states.first().geode * bluePrint.id
        println("Blueprint ${bluePrint.id}: $qualityLevel")
        qualityLevel
    }
    println("Overall quality: ${qualityLevels.sum()}")

    // part 2 (32 minutes, but only 3 blueprints)
    val part2Rounds = 32
    val geodeCounts = bluePrints.take(3).map {bluePrint ->
        var states = setOf(GameState(oreRobotCount = 1))
        repeat(part2Rounds) { minute ->
            if (debugPrint) println("Minute ${minute + 1} (states ${states.size})")
            states = play(states, bluePrint)
            if (debugPrint) println(states.take(5).joinToString("\n"))
        }
        val geodeCount = states.first().geode
        println("Blueprint ${bluePrint.id}: $geodeCount")
        geodeCount
    }

    println("Overall quality part2: ${geodeCounts.reduce {acc, i -> acc * i}}")

}