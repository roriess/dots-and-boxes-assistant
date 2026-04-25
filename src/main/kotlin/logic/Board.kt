package logic

open class Board {
    val adjacencyTable = mutableSetOf<Edge>()

    // значение - ID игрока (или null, если квадрат свободен)
    private val capturedBoxes = mutableMapOf<Box, Int>()

    open fun addEdge(edge: Edge): Boolean {
        return adjacencyTable.add(edge)
    }

    open fun isEdge(edge: Edge): Boolean {
        return edge in adjacencyTable
    }

    open fun captureBox(box: Box, playerId: Int) {
        capturedBoxes[box] = playerId
    }

    open fun isBoxCaptured(box: Box): Boolean {
        return capturedBoxes.containsKey(box)
    }

    open fun getBoxOwner(box: Box): Int? {
        return capturedBoxes[box]
    }

    open fun getCapturedBoxesCount(): Int {
        return capturedBoxes.size
    }
}