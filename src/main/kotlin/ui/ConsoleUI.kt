import java.util.*
import rules.ClassicRule
import rules.IGameRule
import rules.NoBonusRule
import rules.PenaltyRule
import shapes.RectangularShape
import logic.Edge

class ConsoleUI {
    private val scanner = Scanner(System.`in`)
    private lateinit var gameControl: GameControl
    private lateinit var repository: Repository

    fun start() {
        repository = Repository()

        println("Dots and Boxes Assistant")

        println("Enter player names separated by commas (например: Alice,Bob): ")
        val namesInput = scanner.nextLine()
        val playerNames = namesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (playerNames.size < 2) {
            println("There must be at least 2 players. Completion.")
            return
        }

        println("Enter the field size (width and height in squares, for example: 3 3): ")
        val sizeInput = scanner.nextLine().split(" ")
        if (sizeInput.size != 2) {
            println("Need two numbers. Completion.")
            return
        }
        val width = sizeInput[0].toIntOrNull() ?: 3
        val height = sizeInput[1].toIntOrNull() ?: 3
        val fieldSize = listOf<Int>(height, width)

        println("Select rules mode: 1 - Classic, 2 - NoBonus, 3 - Penalty")
        val ruleChoice = scanner.nextLine().toIntOrNull() ?: 1
        val rule = when (ruleChoice) {
            1 -> ClassicRule()
            2 -> NoBonusRule()
            3 -> PenaltyRule()
            else -> ClassicRule()
        }

        println("Field shape: 1 - Rectangular (others not yet implemented)")
        val shapeChoice = scanner.nextLine().toIntOrNull() ?: 1
        val shape = when (shapeChoice) {
            1 -> RectangularShape(fieldSize)
            else -> RectangularShape(fieldSize)
        }

        val configuration = Configuration(rule as IGameRule, shape, listOf(width, height), playerNames)

        gameControl = GameControl(repository)
        gameControl.startGame(configuration)

        while (true) {
            printGameState()
            val currentPlayer = gameControl.getCurrentPlayer()
            println("Player's turn: ${currentPlayer.name} (score: ${currentPlayer.score})")

            println("Enter the edge in the format: x y (coordinates of the left/top point) and direction (h - horizontal, v - vertical)")
            println("Example: 0 0 h (horizontal edge from (0,0) to (1,0))")
            val input = scanner.nextLine().split(" ")
            if (input.size != 3) {
                println("Invalid format. Try again.")
                continue
            }
            val x = input[0].toIntOrNull()
            val y = input[1].toIntOrNull()
            val dir = input[2]
            if (x == null || y == null || (dir != "h" && dir != "v")) {
                println("Invalid format. Try again.\n")
                continue
            }
            val isHorizontal = dir == "h"
            val edge = Edge(x, y, isHorizontal)

            try {
                gameControl.makeMove(edge)
                if (gameControl.isGameFinished()) {
                    printGameState()
                    val winner = gameControl.getWinner()
                    println("Game over! Winner: ${winner.name} with score ${winner.score}")
                    repository.saveGame(winner.name, configuration.playersNames)
                    break
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        repository.close()
    }

    private fun printGameState() {
        println("Score:")
        gameControl.getPlayers().forEach { player ->
            println("${player.name}: ${player.score} points")
        }
        println("Current player: ${gameControl.getCurrentPlayer().name}")
    }
}
