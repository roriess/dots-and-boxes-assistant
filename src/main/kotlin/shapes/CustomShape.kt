package shapes

import logic.Box
import logic.Edge

class CustomShape(private val boxMask: List<Box>) : IFieldShape {
    override fun getAllBoxes(): List<Box> = boxMask

    override fun isValidEdge(edge: Edge): Boolean {
        val (x, y) = edge.x to edge.y
        return if (edge.isHorizontal) {
            (Box(x, y) in boxMask) || (Box(x, y - 1) in boxMask)
        } else {
            (Box(x, y) in boxMask) || (Box(x - 1, y) in boxMask)
        }
    }

    override fun getAdjacentBoxes(edge: Edge): List<Box> {
        val boxes = mutableListOf<Box>()
        val (x, y) = edge.x to edge.y
        if (edge.isHorizontal) {
            val below = Box(x, y)
            val above = Box(x, y - 1)
            if (below in boxMask) boxes.add(below)
            if (above in boxMask) boxes.add(above)
        } else {
            val right = Box(x, y)
            val left = Box(x - 1, y)
            if (right in boxMask) boxes.add(right)
            if (left in boxMask) boxes.add(left)
        }
        return boxes
    }
}