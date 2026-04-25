import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import shapes.RectangularShape
import shapes.CustomShape
import logic.Box
import logic.Edge

class  RectangularShapeTest {
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

class CustomShapeTest {
    private val mask = listOf(Box(0,0), Box(1,0), Box(0,1))
    private val shape = CustomShape(mask)

    @Test
    fun `getAllBoxes returns correct mask`() {
        assertEquals(mask, shape.getAllBoxes())
    }

    @Test
    fun `isValidEdge for horizontal edges`() {
        assertTrue(shape.isValidEdge(Edge(0,0,true)))
        assertTrue(shape.isValidEdge(Edge(1,0,true)))
        assertTrue(shape.isValidEdge(Edge(0,1,true)))
        assertTrue(shape.isValidEdge(Edge(1,1,true)))
        assertFalse(shape.isValidEdge(Edge(2,0,true)))
    }

    @Test
    fun `isValidEdge for vertical edges`() {
        assertTrue(shape.isValidEdge(Edge(0,0,false)))
        assertTrue(shape.isValidEdge(Edge(0,1,false)))
        assertTrue(shape.isValidEdge(Edge(1,0,false)))
        assertTrue(shape.isValidEdge(Edge(1,1,false)))
        assertFalse(shape.isValidEdge(Edge(-1,0,false)))
        assertFalse(shape.isValidEdge(Edge(0,2,false)))
    }

    @Test
    fun `getAdjacentBoxes for horizontal edge`() {
        assertEquals(listOf(Box(0,0)), shape.getAdjacentBoxes(Edge(0,0,true)))
        assertEquals(listOf(Box(1,0)), shape.getAdjacentBoxes(Edge(1,0,true)))
        val mid = shape.getAdjacentBoxes(Edge(0,1,true))
        assertEquals(2, mid.size)
        assertTrue(mid.containsAll(listOf(Box(0,0), Box(0,1))))
    }

    @Test
    fun `getAdjacentBoxes for vertical edge`() {
        assertEquals(listOf(Box(0,0)), shape.getAdjacentBoxes(Edge(0,0,false)))
        assertEquals(listOf(Box(0,1)), shape.getAdjacentBoxes(Edge(0,1,false)))
        val mid = shape.getAdjacentBoxes(Edge(1,0,false))
        assertEquals(2, mid.size)
        assertTrue(mid.containsAll(listOf(Box(0,0), Box(1,0))))
    }
}