import logic.Edge
import logic.GameLogic
import logic.Player
import logic.Board

class GameControl (private val repository: Repository) {
    private lateinit var gameLogic: GameLogic
    private lateinit var configuration: Configuration
    private lateinit var players: List<Player>
    private lateinit var board: Board
    private var currentPlayerIndex: Int = 0
    private var isGameActive: Boolean = false

    fun getCurrentPlayer(): Player = players[currentPlayerIndex]
    fun getPlayers(): List<Player> = players
    fun isGameFinished(): Boolean = gameLogic.gameEndCheck()
    fun getWinner(): Player = players.maxByOrNull { it.score } ?: players[0]

    fun startGame(configuration: Configuration) {
        this.configuration = configuration
        this.players = configuration.playersNames.mapIndexed { idx, name ->
            Player(idx + 1, name, 0)
        }

        this.board = Board()
        this.gameLogic = GameLogic(
            board = this.board,
            players = this.players,
            rule = configuration.selectedRule,
            shape = configuration.selectedShape
        )
        this.currentPlayerIndex = 0
        this.isGameActive = true
    }

    fun makeMove(edge: Edge) {
        if (!gameLogic.validation(edge)) {
            throw Exception("Incorrect edge.\n")
        }
        if (!isGameActive) {
            throw Exception("Game is not active.\n")
        }

        board.addEdge(edge)
        val squares = gameLogic.squaresSearch(edge)
        if (squares.isNotEmpty()) {
            val currentPlayer = players[currentPlayerIndex]
            currentPlayer.score += squares.size
            squares.forEach { box -> board.captureBox(box, currentPlayer.id) }
            if (!configuration.selectedRule.isBonusTurn()) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size
            }
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        }
        if (gameLogic.gameEndCheck()) {
            endGame()
        }
    }

    fun endGame() {
        isGameActive = false
        val winner = players.maxByOrNull { it.score } ?: players[0]
        repository.saveGame(winner.name, players.map { it.name })
    }

    fun resetGame() {
        if (::board.isInitialized) {
            board = Board()
            players.forEach { it.score = 0 }
            gameLogic = GameLogic(board, players, configuration.selectedRule, configuration.selectedShape)
            currentPlayerIndex = 0
            isGameActive = true

        }
    }
}