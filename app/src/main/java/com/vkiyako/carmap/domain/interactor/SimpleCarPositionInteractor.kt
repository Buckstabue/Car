package com.vkiyako.carmap.domain.interactor

import com.vkiyako.carmap.domain.entity.Car
import com.vkiyako.carmap.domain.entity.Position
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToLong
import kotlin.math.sign
import kotlin.math.sqrt
import kotlin.math.tan

class SimpleCarPositionInteractor {
    companion object {
        private const val RATE = 100 // approximate number of updates per second
        private val SLEEP_MS = (1000.0 / RATE).roundToLong()
        private const val MAX_SPEED = 10.0
        private const val TURN_SPEED = 10.0 // degrees per second
    }

    fun driveCarToDestination(car: Car, destination: Position): Flowable<Car> {
        return Flowable.create(
            {
                try {
                    var nextCar = car.copy(stateTimestamp = System.currentTimeMillis())
                    while (nextCar.position != destination) {
                        nextCar = calculateNextState(nextCar, destination)
                        it.onNext(nextCar)
                        Thread.sleep(SLEEP_MS)
                    }
                    it.onComplete()
                } catch (e: Throwable) {
                    it.onError(e)
                }
            },
            BackpressureStrategy.LATEST
        )
    }

    private fun calculateNextState(car: Car, destination: Position): Car {
        val newTimestamp: Long = System.currentTimeMillis()
        val timeDelta: Long = newTimestamp - car.stateTimestamp
        // angle offset that car might be impossible to turn within timeDelta
        val desiredAngleOffset: Int = car.calculateAngleToDestinationOffset(destination)
        // how much time from timeDelta will be spent on turning car
        val turnTime: Long = min(calculateTimeMsToTurn(desiredAngleOffset), timeDelta)
        // how much time from timeDelta will be spent on moving car
        val movementTime: Long = timeDelta - turnTime
        // offset applied to the current car angle within the current time delta
        val realAngleOffset: Double = sign(desiredAngleOffset.toDouble()) * (turnTime * TURN_SPEED) / 1000.0
        val newAngle: Double = (car.angle + realAngleOffset) % 360
        val newPosition: Position = calculateNextPosition(car.position, newAngle, movementTime, destination)
        return Car(
            position = newPosition,
            angle = newAngle,
            speed = MAX_SPEED,
            stateTimestamp = newTimestamp
        )
    }

    private fun calculateTimeMsToTurn(angle: Int): Long {
        if (angle == 0) {
            return 0
        }
        return ((TURN_SPEED / abs(angle)) * 1000).roundToLong()
    }

    private fun calculateNextPosition(currentPosition: Position, angle: Double, timeDelta: Long, destination: Position): Position {
        if (timeDelta <= 0) {
            return currentPosition
        }
        // calculate new position with vector algebra using vector multiplication
        val k: Double = tan(Math.toRadians(angle))
        val b: Double = currentPosition.y - k * currentPosition.x // calculate X-offset of the car direction vector

        val oldCarVectorX: Double = currentPosition.x.toDouble() - b
        val oldCarVectorY: Float = currentPosition.y

        val maxDistance: Double = (timeDelta * MAX_SPEED) / 1000.0
        val distanceToDestination: Double = currentPosition.getDistanceTo(destination)
        val realDistance: Double = min(maxDistance, distanceToDestination)
        val oldCarVectorLength = sqrt(oldCarVectorX * oldCarVectorX + oldCarVectorY * oldCarVectorY) // TODO consider the case with 0 length
        val newCarVectorLength = oldCarVectorLength + realDistance
        val vectorMultiplier = newCarVectorLength / oldCarVectorLength

        val newCarVectorX: Double = oldCarVectorX * vectorMultiplier
        val newCarVectorY: Double = oldCarVectorY * vectorMultiplier

        return Position(
            x = (newCarVectorX + b).toFloat(),
            y = newCarVectorY.toFloat()
        )
    }
}
