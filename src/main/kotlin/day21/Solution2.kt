package day21

import day21.MonkeyMath.calculateLeftValue
import day21.MonkeyMath.calculateRightValue
import util.readInput

data class MonkeyValue(
    var name: String,
    var leftName: String? = null,
    var rightName: String? = null,
    var mathOperation: String? = null,
    var number: ULong? = null
)

data class TreeNode(var monkeyValue: MonkeyValue) {
    var left: TreeNode? = null
    var right: TreeNode? = null
    fun isLeafNode() = left == null && right == null
}

object MonkeyParser {
    private val numberMonkeyRegex = Regex("""([a-z]+): (\d+)""")
    private val mathOperationMonkeyRegex = Regex("""([a-z]+): ([a-z]+) (.) ([a-z]+)""")

    fun parseMonkeys(inputLines: List<String>): Map<String, TreeNode> {
        val nodes = inputLines.map { inputLine ->
            if (numberMonkeyRegex.containsMatchIn(inputLine)) {
                numberMonkeyRegex.matchEntire(inputLine)!!.destructured.let {
                    (name, number) -> TreeNode(MonkeyValue(name, number = number.toULong())) }
            }
            else {
                val groups = mathOperationMonkeyRegex.matchEntire(inputLine)!!.groupValues
                TreeNode(MonkeyValue(name = groups[1], leftName = groups[2], mathOperation = groups[3], rightName = groups[4]))
            }
        }
        val nodesByMonkeyName = nodes.associateBy { it.monkeyValue.name }
        nodes
            .filter { it.monkeyValue.leftName != null }
            .forEach {
                it.left = nodesByMonkeyName[it.monkeyValue.leftName]
                it.right = nodesByMonkeyName[it.monkeyValue.rightName]
            }
        return nodesByMonkeyName
    }
}

object MonkeyMath {
    fun calculate(operation: String, leftValue: ULong, rightValue: ULong): ULong =
        when (operation) {
            "+" -> leftValue + rightValue
            "-" -> leftValue - rightValue
            "*" -> leftValue * rightValue
            else -> leftValue / rightValue
        }

    /** returns the unknown left value when the right value is known */
    fun calculateLeftValue(operation: String, expectedResult: ULong, rightValue: ULong) =
        when (operation) {
            "+" -> expectedResult - rightValue // ? + rightValue = expectedResult
            "-" -> expectedResult + rightValue // ? - rightValue = expectedResult
            "*" -> expectedResult / rightValue // ? * rightValue = expectedResult
            else -> expectedResult * rightValue // ? / rightValue = expectedResult
        }

    /** returns the unknown right value when the left value is known */
    fun calculateRightValue(operation: String, expectedResult: ULong, leftValue: ULong) =
        when (operation) {
            "+" -> expectedResult - leftValue // leftValue + ? = expectedResult
            "-" -> leftValue - expectedResult // leftValue - ? = expectedResult
            "*" -> expectedResult / leftValue // leftValue * ? = expectedResult
            else -> leftValue / expectedResult // leftValue / ? = expectedResult
        }
}

class MonkeyTree(val root: TreeNode) {
    // Calculate values of all the nodes down the tree
    fun calculateValue(node: TreeNode): ULong {
        if (node.isLeafNode()) {
            return node.monkeyValue.number!!
        }

        return MonkeyMath.calculate(node.monkeyValue.mathOperation!!, calculateValue(node.left!!), calculateValue(node.right!!))
    }

    // Find the humn-node and pass down the expected value of it
    fun calculateValuePart2(node: TreeNode, humnPath: Set<TreeNode>, expectedResult: ULong): ULong {
        if (node.isLeafNode()) {
            return if (node.monkeyValue.name == "humn") expectedResult else node.monkeyValue.number!!
        }

        return if (node.left in humnPath) {
            calculateValuePart2(node.left!!, humnPath, calculateLeftValue(
                node.monkeyValue.mathOperation!!, expectedResult, calculateValue(node.right!!)
            ))
        }
        else {
            calculateValuePart2(node.right!!, humnPath, calculateRightValue(
                node.monkeyValue.mathOperation!!, expectedResult, calculateValue(node.left!!)
            ))
        }
    }

    fun findPathToNode(predicate: (TreeNode) -> Boolean): Set<TreeNode> {
        fun findPath(node: TreeNode?, path: MutableList<TreeNode>): Boolean {
            if (node == null) return false

            path += node
            if (predicate(node) || findPath(node.left, path) || findPath(node.right, path)) {
                return true
            }

            path.removeLast()
            return false
        }
        val path = mutableListOf<TreeNode>()
        findPath(root, path)
        return path.toSet()
    }
}


fun main() {
    val nodesByMonkeyName: Map<String, TreeNode> = MonkeyParser.parseMonkeys(readInput("day21"))
    val monkeyTree = MonkeyTree(nodesByMonkeyName["root"]!!)

    // part 1
    println("part1: ${monkeyTree.calculateValue(monkeyTree.root)}")

    // part 2
    val humnPath = monkeyTree.findPathToNode { it.monkeyValue.name == "humn" }
    val part2Result = if (monkeyTree.root.left in humnPath) {
        monkeyTree.calculateValuePart2(monkeyTree.root.left!!, humnPath, monkeyTree.calculateValue(monkeyTree.root.right!!))
    } else {
        monkeyTree.calculateValuePart2(monkeyTree.root.right!!, humnPath, monkeyTree.calculateValue(monkeyTree.root.left!!))
    }

    println("part2: $part2Result")
}