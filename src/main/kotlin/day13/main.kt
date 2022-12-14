package day13

import util.*

data class PacketPair(val left: Packet, val right: Packet)

open class Packet
data class IntValue(val value: Int): Packet() {
    override fun toString(): String = value.toString()
}
data class ListValue(val values: List<Packet>): Packet() {
    constructor(value: Packet): this(listOf(value))
    override fun toString(): String = "[${values.joinToString(";")}]"
}

fun isEmptyList(packet: Packet): Boolean = packet is ListValue && packet.values.isEmpty()

fun readListItems(str: String): List<String> {
    var level = 1
    val items = mutableListOf<String>()
    val buffer = StringBuilder()
    for (char in str.substring(1, str.length - 1)) {
        if (char == '[') {
            level += 1
        }
        else if (char == ']') {
            level -= 1
        }
        else if (char == ',' && level == 1) {
            items.add(buffer.toString())
            buffer.clear()
            continue
        }

        buffer.append(char)
    }
    if (buffer.isNotEmpty()) items.add(buffer.toString())

    return items
}

fun parsePacket(str: String): Packet {
    return when {
        str.isBlank() -> throw IllegalArgumentException("Got blank")
        str == "[]" -> ListValue(emptyList())
        str.startsWith("[") -> {
            val values = readListItems(str).map { parsePacket(it) }
            if (values.isEmpty()) ListValue(emptyList()) else ListValue(values)
        }
        else -> IntValue(str.toInt())
    }
}

fun compareTo(packetPair: PacketPair, level: Int = 0): Int {
    debug("compare $packetPair", level)

    if (packetPair.left is ListValue && packetPair.right is ListValue) {
        for (i in 0 until packetPair.left.values.size) {
            val leftValue = packetPair.left.values.get(i)
            val righValue = packetPair.right.values.getOrNull(i)
            if (leftValue == righValue) {
                continue
            }
            if (isEmptyList(leftValue)) {
                debug("left side ran out of items, returning true", 2)
                return 1
            }
            if (righValue == null || isEmptyList(righValue)) {
                debug("right side ran out of items, returning false", 2)
                return -1
            }
            val compareToResult = compareTo(PacketPair(leftValue, righValue), level + 1)
            if (compareToResult == 0) {
                // value is same, so we need to compare next values
                continue
            }
            return compareToResult
        }
        if (level == 0) {
            debug("left side ran out of items, returning true", 2)
            return 1
        }
    }
    else if (packetPair.left is IntValue && packetPair.right is IntValue) {
        return packetPair.right.value - packetPair.left.value
    }
    else if (packetPair.left is IntValue && packetPair.right is ListValue) {
        debug("mixed types, convert to list", level + 1)
        return compareTo(PacketPair(ListValue(packetPair.left), packetPair.right), level + 1)
    }
    else if (packetPair.left is ListValue && packetPair.right is IntValue) {
        debug("mixed types, convert to list", level + 1)
        return compareTo(PacketPair(packetPair.left, ListValue(packetPair.right)), level + 1)
    }

    // Comparison of equal items at level deeper than 1. Returning 0 leads to comparing next values.
    return 0
}

fun parsePacketPair(inputLines: List<String>): PacketPair =
    PacketPair(parsePacket(inputLines[0]), parsePacket(inputLines[1]))

fun part1(): Int {
    val packetPairs = readInput("day13")
        .chunked(3)
        .map { parsePacketPair(it) }

    val correctIndexes = packetPairs.foldIndexed(mutableListOf<Int>()) { index, acc, pair ->
        if (compareTo(pair) >= 1) acc.add(index + 1)
        acc
    }

    println(correctIndexes)
    println(correctIndexes.sum())
    return correctIndexes.sum()
}

fun part2(): Int {
    var packets = readTestInput("day13")
        .filter { it.isNotBlank() }
        .map { parsePacket(it) }

    val divider1 = parsePacket("[[2]]")
    val divider2 = parsePacket("[[6]]")
    packets = packets + listOf(divider1, divider2)

    val sortedPackets = packets.sortedWith { left, right -> compareTo(PacketPair(left, right)) }.reversed()

    val index1 = sortedPackets.indexOf(divider1) + 1
    val index2 = sortedPackets.indexOf(divider2) + 1

    return index1 * index2
}

fun main() {
    equals(13, part1())
    equals(140, part2())
}