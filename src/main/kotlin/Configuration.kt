import rules.IGameRule
import shapes.IFieldShape

data class Configuration(var selectedRule: IGameRule, val selectedShape: IFieldShape, val fieldSize: List<Int>, val playersNames: List<String>)
