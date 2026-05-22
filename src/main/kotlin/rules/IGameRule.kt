package rules

interface IGameRule {
    // вычисление количества закрытых квадратов
    fun calculateScore(closedBoxesCount: Int): Int
    // есть ли в данных правилах бонусный ход (дополнительный ход после закрытия квадрата)
    fun isBonusTurn(): Boolean
}