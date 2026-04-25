package rules

class ClassicRule(): IGameRule {
    override fun calculateScore(closedBoxesCount: Int): Int {
        return closedBoxesCount
    }

    override fun isBonusTurn(): Boolean {
        return true
    }
}