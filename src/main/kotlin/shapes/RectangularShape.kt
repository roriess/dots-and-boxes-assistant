package shapes

import logic.Box
import logic.Edge

class RectangularShape(val fieldSize: List<Int>): IFieldShape {
    private val height = fieldSize[0]
    private val width = fieldSize[1]

    override fun getAllBoxes(): List<Box> {
        val boxes = mutableListOf<Box>()
        for (x in 0 until width) {
            for (y in 0 until height) {
                boxes.add(Box(x, y))
            }
        }
        return boxes
    }

    override fun isValidEdge(edge: Edge): Boolean {
        val x = edge.x
        val y = edge.y
        return if (edge.isHorizontal) {
            x in 0..<width && y >= 0 && y <= height
        } else {
            x in 0..width && y >= 0 && y < height
        }
    }

    override fun getAdjacentBoxes(edge: Edge): List<Box> {
        val boxes = mutableListOf<Box>()
        val x = edge.x
        val y = edge.y
        if (edge.isHorizontal) {
            if (x < height) {
                boxes.add(Box(x, y))
            }
            if (y > 0) {
                boxes.add(Box(x, y - 1))
            }
        } else {
            if (x < width) {
                boxes.add(Box(x, y))
            }
            if (x > 0) {
                boxes.add(Box(x - 1, y))
            }
        }
        return boxes
    }
}