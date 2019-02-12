package com.vkiyako.carmap.domain.entity

class Angle private constructor(
    /**
     * Always within [-180, 180)
     */
    val degrees: Double
) {
    companion object {
        fun fromRadians(radians: Double) = Angle.fromDegrees(Math.toDegrees(radians))
        fun fromDegrees(degrees: Double) = Angle(degrees = optimizeAngle(degrees))

        // set angle within [-180, 180)
        private fun optimizeAngle(degrees: Double): Double {
            var degrees = degrees % 360
            return when {
                degrees >= 180 -> degrees - 360.0
                degrees < -180 -> degrees + 360
                else -> degrees
            }
        }
    }

    /**
     * Always within [-p, p)
     */
    val radians: Double
        get() = Math.toRadians(degrees)

    override fun toString(): String {
        return "Angle(degrees=$degrees)"
    }
}

operator fun Angle.plus(value: Angle): Angle {
    return Angle.fromDegrees(this.degrees + value.degrees)
}

operator fun Angle.minus(value: Angle): Angle {
    return Angle.fromDegrees(this.degrees - value.degrees)
}
