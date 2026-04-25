import rules.ClassicRule
import rules.IGameRule
import rules.NoBonusRule
import rules.PenaltyRule
import shapes.IFieldShape
import shapes.RectangularShape
import shapes.CustomShape
import logic.Box

class GameFactory(val typeRule: String, val typeShape: String) {
    fun createRule(typeRule: String): IGameRule {
        return when (typeRule) {
            "ClassicRule" -> ClassicRule()
            "NoBonusRule" -> NoBonusRule()
            "PenaltyRule" -> PenaltyRule()
            else -> throw IllegalCallerException("Unknown type\n")
        } as IGameRule
    }

    fun createShape(typeShape: String, fieldSize: List<Int>, boxMask: List<Box>): IFieldShape {
        return when (typeShape) {
            "RectangularShape" -> RectangularShape(fieldSize)
            "CustomShape" -> CustomShape(boxMask)
            else -> throw IllegalCallerException("Unknown type\n")
        }
    }
}