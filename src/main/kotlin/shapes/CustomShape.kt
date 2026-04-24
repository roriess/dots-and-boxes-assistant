package shapes

import logic.Box
import logic.Edge

class CustomShape(private val boxMask: List<Box>): IFieldShape {
    override fun getAllBoxes(): List<Box> {
        return boxMask
    }

    override fun isValidEdge(edge: Edge): Boolean {
        val x = edge.x
        val y = edge.y
        return if (edge.isHorizontal) {
            (Box(x, y) in boxMask) || (Box(x, y - 1) in boxMask)
        } else {
            (Box(x, y) in boxMask) || (Box(x - 1, y) in boxMask)
        }
    }

    override fun getAdjacentBoxes(edge: Edge): List<Box> {
        val boxes = mutableListOf<Box>()
        val x = edge.x
        val y = edge.y
        if (edge.isHorizontal) {
            val box1 = Box(x, y)
            val box2 = Box(x - 1, y)
            if (box1 in boxMask) {
                boxes.add(box1)
            }
            if (box2 in boxMask) {
                boxes.add(box2)
            }
        } else {
            val box1 = Box(x, y)
            val box2 = Box(x, y - 1)
            if (box1 in boxMask) {
                boxes.add(box1)
            }
            if (box2 in boxMask) {
                boxes.add(box2)
            }
        }
        return boxes
    }
}