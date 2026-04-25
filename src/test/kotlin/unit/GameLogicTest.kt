package logic
import rules.IGameRule
import shapes.IFieldShape

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GameLogicTest {

    private class StubBoard : Board() {
        private val drawnEdges = mutableSetOf<Edge>()
        private val capturedBoxes = mutableSetOf<Box>()

        override fun isEdge(edge: Edge): Boolean = edge in drawnEdges
        override fun addEdge(edge: Edge): Boolean = drawnEdges.add(edge)
        override fun captureBox(box: Box, playerId: Int) { capturedBoxes.add(box) }
        override fun getCapturedBoxesCount(): Int = capturedBoxes.size
    }

    private class StubRule(private val scoreFactor: Int = 1) : IGameRule {
        override fun calculateScore(closedBoxesCount: Int): Int = closedBoxesCount * scoreFactor
        override fun isBonusTurn(): Boolean = true
    }

    private class StubShape : IFieldShape {
        var validEdgeResult: Boolean = true
        var adjacentBoxesResult: List<Box> = emptyList()
        var allBoxesResult: List<Box> = emptyList()

        override fun isValidEdge(edge: Edge): Boolean = validEdgeResult
        override fun getAdjacentBoxes(edge: Edge): List<Box> = adjacentBoxesResult
        override fun getAllBoxes(): List<Box> = allBoxesResult
    }


    @Test
    fun `validation returns true when edge is free and shape valid`() {
        val board = StubBoard()
        val shape = StubShape().apply { validEdgeResult = true }
        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)

        val edge = Edge(0, 0, true)
        assertTrue(gameLogic.validation(edge))
    }

    @Test
    fun `validation returns false when edge already drawn`() {
        val board = StubBoard()
        val edge = Edge(0, 0, true)
        board.addEdge(edge)
        val shape = StubShape().apply { validEdgeResult = true }
        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)

        assertFalse(gameLogic.validation(edge))
    }

    @Test
    fun `validation returns false when edge is invalid for shape`() {
        val board = StubBoard()
        val shape = StubShape().apply { validEdgeResult = false }
        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)

        assertFalse(gameLogic.validation(Edge(0, 0, true)))
    }

    @Test
    fun `squaresSearch returns empty list when no adjacent boxes`() {
        val board = StubBoard()
        val shape = StubShape().apply { adjacentBoxesResult = emptyList() }
        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)

        val result = gameLogic.squaresSearch(Edge(0, 0, true))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `squaresSearch returns boxes with all four sides drawn`() {
        val board = StubBoard()
        val box = Box(0, 0)
        board.addEdge(Edge(0, 0, true))
        board.addEdge(Edge(0, 1, true))
        board.addEdge(Edge(0, 0, false))
        board.addEdge(Edge(1, 0, false))

        val shape = StubShape().apply { adjacentBoxesResult = listOf(box) }
        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)

        val result = gameLogic.squaresSearch(Edge(0, 0, true))
        assertEquals(1, result.size)
        assertEquals(box, result[0])
    }

    @Test
    fun `squaresSearch excludes boxes with missing side`() {
        val board = StubBoard()
        val box = Box(0, 0)
        board.addEdge(Edge(0, 0, true))
        board.addEdge(Edge(0, 1, true))
        board.addEdge(Edge(0, 0, false))

        val shape = StubShape().apply { adjacentBoxesResult = listOf(box) }
        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)

        val result = gameLogic.squaresSearch(Edge(0, 0, true))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `scoring delegates to rule with correct argument`() {
        val rule = StubRule(scoreFactor = 5) // каждый квадрат даёт 5 очков
        val gameLogic = GameLogic(StubBoard(), emptyList(), rule, StubShape())

        val boxes = listOf(Box(0, 0), Box(1, 1)) // два квадрата
        assertEquals(10, gameLogic.scoring(boxes))
    }

    @Test
    fun `scoring returns zero for empty list`() {
        val rule = StubRule()
        val gameLogic = GameLogic(StubBoard(), emptyList(), rule, StubShape())

        assertEquals(0, gameLogic.scoring(emptyList()))
    }

    @Test
    fun `gameEndCheck returns true when all boxes captured`() {
        val board = StubBoard()
        val shape = StubShape().apply {
            allBoxesResult = listOf(Box(0, 0), Box(0, 1))
        }
        board.captureBox(Box(0, 0), 1)
        board.captureBox(Box(0, 1), 1)

        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)
        assertTrue(gameLogic.gameEndCheck())
    }

    @Test
    fun `gameEndCheck returns false when not all boxes captured`() {
        val board = StubBoard()
        val shape = StubShape().apply {
            allBoxesResult = listOf(Box(0, 0), Box(0, 1))
        }
        board.captureBox(Box(0, 0), 1)

        val gameLogic = GameLogic(board, emptyList(), StubRule(), shape)
        assertFalse(gameLogic.gameEndCheck())
    }
}