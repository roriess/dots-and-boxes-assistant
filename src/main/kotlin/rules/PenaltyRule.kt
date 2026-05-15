package rules

class PenaltyRule(): IGameRule {
    override fun calculateScore(closedBoxesCount: Int): Int {
        return -closedBoxesCount
    }

    override fun isBonusTurn(): Boolean {
        return false
    }
}