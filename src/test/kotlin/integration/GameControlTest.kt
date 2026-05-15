package integration
import GameControl
import Repository
import Configuration
import logic.*
import rules.ClassicRule
import shapes.RectangularShape
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameControlTest {

    private lateinit var gameControl: GameControl
    private lateinit var repositorySpy: RepositorySpy

    class RepositorySpy : Repository() {
        var savedWinner: String? = null
        var savedPlayers: List<String>? = null
        var saveCalled = false

        override fun saveGame(winnerName: String, playersNames: List<String>) {
            savedWinner = winnerName
            savedPlayers = playersNames
            saveCalled = true
        }
    }

    @BeforeEach
    fun setup() {
        repositorySpy = RepositorySpy()
        gameControl = GameControl(repositorySpy)
        val playersNames = listOf("Alice", "Bob")
        val rule = ClassicRule()
        val shape = RectangularShape(listOf<Int>(1, 1))
        val fieldSize = listOf(1, 1)
        val config = Configuration(rule, shape, fieldSize, playersNames)
        gameControl.startGame(config)
    }

    @Test
    fun `full game on 1x1 board ends with winner and saves result`() {
        val moves = listOf(
            Edge(0, 0, true),
            Edge(0, 0, false),
            Edge(1, 0, false),
            Edge(0, 1, true)
        )
        for ((index, edge) in moves.withIndex()) {
            if (gameControl.isGameFinished()) break
            gameControl.makeMove(edge)
            if (index == moves.lastIndex) {
                assertTrue(gameControl.isGameFinished())
            }
        }

        val winner = gameControl.getWinner()
        assertEquals("Bob", winner.name)
        assertEquals(1, winner.score)

        assertTrue(repositorySpy.saveCalled)
        assertEquals("Bob", repositorySpy.savedWinner)
        assertEquals(listOf("Alice", "Bob"), repositorySpy.savedPlayers)
    }

    @Test
    fun `game does not end before all moves`() {
        val moves = listOf(
            Edge(0, 0, true),
            Edge(0, 0, false),
            Edge(1, 0, false)
        )
        for (edge in moves) {
            gameControl.makeMove(edge)
            assertFalse(gameControl.isGameFinished())
        }
    }
}