import rules.IGameRule
import shapes.IFieldShape

data class Configuration(val selectedRule: IGameRule, val selectedShape: IFieldShape, val fieldSize: List<Int>, val playersNames: List<String>)
