package com.vkiyako.carmap.domain.entity

data class Car(
    val position: Position,
    val angle: Angle,
    val speed: Double,
    val stateTimestamp: Long
) {
    /**
     * @return offset to the current car's angle, this offset shows how direct is the car to the destination.
     * The more offset is, the greater angle the car must turn
     */
    fun calculateAngleToDestinationOffset(destination: Position): Angle {
        val idealAngleTgx: Double = (destination.y - this.position.y.toDouble()) / (destination.x - this.position.x)
        var idealAngle = Angle.fromRadians(Math.atan(idealAngleTgx))
        if (destination.x <= this.position.x) {
            // if destination is behind the car, we should turn 180 degrees
            idealAngle += Angle.fromDegrees(180.0)
        }

        val angleOffset = idealAngle - this.angle
        return if (angleOffset.degrees > 180) {
            // fixing direction of turning to make it faster, otherwise we would always turn the same direction
            // 181 degrees -> -179 degrees
            angleOffset - Angle.fromDegrees(360.0)
        } else {
            angleOffset
        }
    }
}
