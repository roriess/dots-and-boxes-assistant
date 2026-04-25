package system

import Repository
import GameControl
import Configuration
import logic.*
import rules.ClassicRule
import shapes.RectangularShape
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.DriverManager

class FullGameSystemTest {

    @Test
    fun `complete game on 2x2 board`() {
        val memoryDbUrl = "jdbc:sqlite::memory:"
        val connection = DriverManager.getConnection(memoryDbUrl)
        val repository = Repository(connection)

        val gameControl = GameControl(repository)
        val playersNames = listOf("1", "2")
        val rule = ClassicRule()
        val shape = RectangularShape(listOf(1, 1))
        val config = Configuration(rule, shape, listOf(2, 2), playersNames)
        gameControl.startGame(config)

        val allEdges = listOf(
            Edge(0,0,true),
            Edge(0,1,true),
            Edge(0,0,false),
            Edge(1,0,false),
        )

        for (edge in allEdges) {
            if (gameControl.isGameFinished()) break
            try {
                gameControl.makeMove(edge)
            } catch (_: Exception) {
            }
        }

        assertTrue(gameControl.isGameFinished())
        val winner = gameControl.getWinner()
        val totalScore = gameControl.getPlayers().sumOf { it.score }
        assertEquals(1, totalScore)

        val history = repository.readHistory()
        assertTrue(history.isNotEmpty())
        val (_, savedWinner) = history.first()
        assertEquals(winner.name, savedWinner)
    }
}