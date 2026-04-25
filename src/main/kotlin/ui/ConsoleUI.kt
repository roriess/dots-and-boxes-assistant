import java.util.*
import rules.*
import shapes.RectangularShape
import shapes.CustomShape
import logic.Edge
import logic.Box

class ConsoleUI {
    private val scanner = Scanner(System.`in`)
    private lateinit var gameControl: GameControl
    private lateinit var repository: Repository

    fun start() {
        repository = Repository()

        while (true) {
            println("Dots and Boxes Assistant")
            println("1. Start new game")
            println("2. Show leaderboard")
            println("3. Exit")
            print("Choose option: ")
            when (scanner.nextLine()) {
                "1" -> startNewGame()
                "2" -> showLeaderboard()
                "3" -> {
                    println("Goodbye!")
                    repository.close()
                    return
                }
                else -> println("Invalid choice, try again.")
            }
        }
    }

    private fun startNewGame() {
        println("Enter player names separated by commas (e.g., Alice,Bob): ")
        val namesInput = scanner.nextLine()
        val playerNames = namesInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (playerNames.size < 2) {
            println("There must be at least 2 players. Completion.")
            return
        }

        println("Enter the field size (width and height in squares, for example: 3 3): ")
        val sizeInput = scanner.nextLine().split(" ")
        if (sizeInput.size != 2) {
            println("Need two numbers. Using default 3x3.")
        }
        val width = sizeInput.getOrNull(0)?.toIntOrNull() ?: 3
        val height = sizeInput.getOrNull(1)?.toIntOrNull() ?: 3

        println("Select rules mode: 1 - Classic, 2 - NoBonus, 3 - Penalty")
        val ruleChoice = scanner.nextLine().toIntOrNull() ?: 1
        val rule = when (ruleChoice) {
            1 -> ClassicRule()
            2 -> NoBonusRule()
            3 -> PenaltyRule()
            else -> ClassicRule()
        }

        println("Field shape: 1 - Rectangular, 2 - Custom")
        val shapeChoice = scanner.nextLine().toIntOrNull() ?: 1
        val shape = when (shapeChoice) {
            1 -> RectangularShape(listOf<Int>(height, width))
            2 -> {
                println("Enter a list of squares in the format x y (separated by spaces), with a blank line to end:")
                val boxes = mutableListOf<Box>()
                while (true) {
                    val line = scanner.nextLine()
                    if (line.isBlank()) break
                    val parts = line.split(" ")
                    if (parts.size == 2) {
                        val x = parts[0].toIntOrNull()
                        val y = parts[1].toIntOrNull()
                        if (x != null && y != null) boxes.add(Box(x, y))
                        else println("Invalid format, please try again")
                    } else println("Enter two numbers, for example: 0 0")
                }
                if (boxes.isEmpty()) {
                    println("The list is empty, using the default 2x2 field")
                    CustomShape(listOf(Box(0,0), Box(1,0), Box(0,1), Box(1,1)))
                } else CustomShape(boxes)
            }
            else -> RectangularShape(listOf<Int>(height, width))
        }

        val configuration = Configuration(rule, shape, listOf(width, height), playerNames)
        gameControl = GameControl(repository)
        gameControl.startGame(configuration)

        while (true) {
            printGameState()
            val currentPlayer = gameControl.getCurrentPlayer()
            println("${currentPlayer.name}'s turn (score: ${currentPlayer.score})")
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
    }

    private fun printGameState() {
        println("Score:")
        gameControl.getPlayers().forEach { player ->
            println("${player.name}: ${player.score} points")
        }
        println("Current player: ${gameControl.getCurrentPlayer().name}")
    }

    private fun showLeaderboard() {
        val history = repository.readHistory()
        if (history.isEmpty()) {
            println("No games played yet.")
            return
        }
        println("Leaderboard")
        println("Date\t\t\tWinner")
        for ((date, winner) in history) {
            println("$date\t$winner")
        }
    }
}