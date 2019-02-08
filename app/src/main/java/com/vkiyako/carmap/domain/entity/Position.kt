package com.vkiyako.carmap.domain.entity

import kotlin.math.sqrt

data class Position(
    val x: Float,
    val y: Float
) {
    fun getDistanceTo(another: Position): Double {
        val deltaX = x - another.x.toDouble()
        val deltaY = y - another.y.toDouble()
        return sqrt(deltaX * deltaX + deltaY * deltaY)
    }
}
