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
        val slopeAngleTgx: Double = (destination.y - this.position.y.toDouble()) / (destination.x - this.position.x)
        var slopeAngle = Angle.fromRadians(Math.atan(slopeAngleTgx))
        if (slopeAngle.degrees < 0) {
            slopeAngle = Angle.fromDegrees(180.0) + slopeAngle // = 180 - abs(slopeAngle) since slopeAngle is negative
        }
        val deltaY = position.y - destination.y
        val idealAngle = if (deltaY < 0) {
            slopeAngle
        } else {
            slopeAngle + Angle.fromDegrees(180.0)
        }

        var angleOffset = idealAngle - this.angle
        return angleOffset
    }
}
