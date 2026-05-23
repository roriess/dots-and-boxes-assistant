package logic

import rules.IGameRule
import shapes.IFieldShape


class GameLogic(val board: Board, private val rule: IGameRule, val shape: IFieldShape, players: List<Player>) {
    fun validation(edge: Edge): Boolean {
        return !board.isEdge(edge) && shape.isValidEdge(edge)
    }

    fun squaresSearch(edge: Edge): List<Box> {
        val adjacent = shape.getAdjacentBoxes(edge)

        val completed = mutableListOf<Box>()

        for (box in adjacent) {
            val top = Edge(box.x, box.y, true)
            val bottom = Edge(box.x, box.y + 1, true)
            val left = Edge(box.x, box.y, false)
            val right = Edge(box.x + 1, box.y, false)

            if (board.isEdge(top) && board.isEdge(bottom) && board.isEdge(left) && board.isEdge(right)) {
                completed.add(box)
            }
        }
        return completed
    }

    fun scoring(boxes: List<Box>): Int {
        return rule.calculateScore(boxes.size)
    }

    fun gameEndCheck(): Boolean {
        return shape.getAllBoxes().size == board.getCapturedBoxesCount()
    }
}