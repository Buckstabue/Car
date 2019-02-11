package com.vkiyako.carmap.domain.entity

class Angle private constructor(
    val degrees: Double
) {
    companion object {
        fun fromRadians(radians: Double) = Angle(degrees = Math.toDegrees(radians))
        fun fromDegrees(degrees: Double) = Angle(degrees = degrees % 360)
    }

    val radians: Double
        get() = Math.toRadians(degrees)
}


operator fun Angle.plus(value: Angle): Angle {
    return Angle.fromDegrees(this.degrees + value.degrees)
}

operator fun Angle.minus(value: Angle): Angle {
    return Angle.fromDegrees(this.degrees - value.degrees)
}
