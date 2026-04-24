package rules

interface IGameRule {
    fun validateMove(isEdge: Boolean): Boolean
    fun calculateScore(closedBoxesCount: Int): Int
    fun isBonusTurn(): Boolean
}