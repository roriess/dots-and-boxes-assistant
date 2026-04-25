package shapes

import logic.Box
import logic.Edge

interface IFieldShape {
    // все возможные квадраты поля
    fun getAllBoxes(): List<Box>
    // проверка, что ребро в пределах формы
    fun isValidEdge(edge: Edge): Boolean
    // квадраты, прилегающие к ребру
    fun getAdjacentBoxes(edge: Edge): List<Box>
}