package rules

interface IGameRule {
    fun calculateScore(closedBoxesCount: Int): Int
    fun isBonusTurn(): Boolean
}