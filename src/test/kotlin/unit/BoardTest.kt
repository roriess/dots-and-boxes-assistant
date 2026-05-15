import logic.Board
import logic.Edge
import logic.Box
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardTest {
    @Test
    fun `addEdge and isEdge work correctly`() {
        val board = Board()
        val edge = Edge(0, 0, true)
        assertTrue(board.addEdge(edge))
        assertTrue(board.isEdge(edge))
        assertFalse(board.addEdge(edge))
    }

    @Test
    fun `captureBox and isBoxCaptured work`() {
        val board = Board()
        val box = Box(0, 0)
        assertFalse(board.isBoxCaptured(box))
        board.captureBox(box, 1)
        assertTrue(board.isBoxCaptured(box))
        assertEquals(1, board.getBoxOwner(box))
        assertEquals(1, board.getCapturedBoxesCount())
    }
}