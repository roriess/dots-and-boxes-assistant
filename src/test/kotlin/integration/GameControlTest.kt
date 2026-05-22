package integration
import GameControl
import Configuration
import logic.*
import rules.ClassicRule
import shapes.RectangularShape
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameControlTest {
    private lateinit var gameControl: GameControl

    @BeforeEach
    fun setup() {
        gameControl = GameControl()
        val playersNames = listOf("Alice", "Bob")
        val rule = ClassicRule()
        val shape = RectangularShape(listOf<Int>(1, 1))
        val fieldSize = listOf(1, 1)
        val config = Configuration(rule, shape, fieldSize, playersNames)
        gameControl.startGame(config)
    }

    @Test
    fun `closing a box gives points and bonus turn`() {
        val moves = listOf(
            Edge(0, 0, true),
            Edge(0, 0, false),
            Edge(1, 0, false),
            Edge(0, 1, true)
        )
        for (edge in moves) {
            gameControl.makeMove(edge)
        }

        assertTrue(gameControl.isGameFinished())

        val winner = gameControl.getWinner()
        assertEquals("Bob", winner.name)
        assertEquals(1, winner.score)
    }
}