package com.vkiyako.carmap.domain.entity

import kotlin.math.roundToInt

data class Car(
    val position: Position,
    val angle: Double,
    val speed: Double,
    val stateTimestamp: Long
) {
    /**
     * @return offset to the current car's angle, this offset shows how direct is the car to the destination.
     * The more offset is, the greater angle the car must turn
     */
    fun calculateAngleToDestinationOffset(destination: Position): Int {
        val idealAngleTgx: Double = (destination.y - this.position.y.toDouble()) / (destination.x - this.position.x)
        var idealAngle: Double = Math.toDegrees(Math.atan(idealAngleTgx))
        if (destination.x <= this.position.x) {
            idealAngle += 180 // if destination is behind of the car, we should turn 180 degrees
        }
        idealAngle %= 360

        val angleOffset = (idealAngle - this.angle).roundToInt()
        return if (angleOffset > 180) {
            angleOffset - 360 // fixing direction of turning to make it faster, otherwise we would always turn the same direction
        } else {
            angleOffset
        }
    }
}
