package logic

class Board {
    val adjacencyTable = mutableSetOf<Edge>()

    // значение - ID игрока (или null, если квадрат свободен)
    private val capturedBoxes = mutableMapOf<Box, Int>()

    fun addEdge(edge: Edge): Boolean {
        return adjacencyTable.add(edge)
    }

    fun isEdge(edge: Edge): Boolean {
        return edge in adjacencyTable
    }

    fun captureBox(box: Box, playerId: Int) {
        capturedBoxes[box] = playerId
    }

    fun isBoxCaptured(box: Box): Boolean {
        return capturedBoxes.containsKey(box)
    }

    fun getBoxOwner(box: Box): Int? {
        return capturedBoxes[box]
    }

    fun getCapturedBoxesCount(): Int {
        return capturedBoxes.size
    }
}