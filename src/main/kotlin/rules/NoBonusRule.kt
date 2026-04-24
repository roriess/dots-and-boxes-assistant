package rules

class NoBonusRule(): IGameRule {
    override fun validateMove(isEdge: Boolean): Boolean {
        return !isEdge
    }

    override fun calculateScore(closedBoxesCount: Int): Int {
        return closedBoxesCount
    }

    override fun isBonusTurn(): Boolean {
        return false
    }
}