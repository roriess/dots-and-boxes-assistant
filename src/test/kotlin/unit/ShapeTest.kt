import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import shapes.RectangularShape
import logic.Box
import logic.Edge

class ShapeTest {
    private val shape = RectangularShape(listOf(2, 2))
    @Test
    fun `getAllBoxes correct work`() {
        assertEquals(listOf<Box>(Box(x=0, y=0), Box(x=0, y=1), Box(x=1, y=0), Box(x=1, y=1)), shape.getAllBoxes())
    }

    @Test
    fun `isValidEdge correct work`() {
        assertTrue(shape.isValidEdge(Edge(1, 0, true)))
        assertFalse(shape.isValidEdge(Edge(10, 0, true)))
    }

    @Test
    fun `getAdjacentBoxes correct work`() {
        assertEquals(listOf<Box>(Box(x=0, y=0)), shape.getAdjacentBoxes(Edge(0, 0, true)))
    }
}