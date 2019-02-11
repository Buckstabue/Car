package com.vkiyako.carmap.domain.entity

class Angle private constructor(
    /**
     * Always within (-360, 360)
     */
    val degrees: Double
) {
    companion object {
        fun fromRadians(radians: Double) = Angle.fromDegrees(Math.toDegrees(radians))
        fun fromDegrees(degrees: Double) = Angle(degrees = optimizeAngle(degrees))

        // set angle within [-360, 360)
        private fun optimizeAngle(degrees: Double): Double {
            return degrees % 360
        }
    }

    /**
     * Always within (-2p, 2p)
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
