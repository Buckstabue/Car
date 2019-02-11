package com.vkiyako.carmap.domain.interactor

import com.vkiyako.carmap.domain.entity.Car
import com.vkiyako.carmap.domain.entity.Position
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.*

class SimpleCarPositionInteractor {
    companion object {
        private const val RATE = 20 // approximate number of updates per second
        private val SLEEP_MS = (1000.0 / RATE).roundToLong()
        private const val MAX_SPEED = 20.0
        private const val TURN_SPEED = 20.0 // degrees per second

        private val ANGLE_FIXED_FLAG = AtomicBoolean()

        private const val DISTANCE_EPS = 8
    }

    fun driveCarToDestination(car: Car, destination: Position): Flowable<Car> {
        return Flowable.create(
            {
                try {
                    ANGLE_FIXED_FLAG.set(false)
                    var nextCar = car.copy(stateTimestamp = System.currentTimeMillis())
                    while (nextCar.position.getDistanceTo(destination) > DISTANCE_EPS) {
                        Thread.sleep(SLEEP_MS)
                        nextCar = calculateNextState(nextCar, destination)
                        it.onNext(nextCar)
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

        val maxDistance: Double = (timeDelta * MAX_SPEED) / 1000.0
        val distanceToDestination: Double = currentPosition.getDistanceTo(destination)
        val realDistance: Double = min(maxDistance, distanceToDestination)

        val radianAngle = Math.toRadians(angle)
        val deltaX = realDistance * cos(radianAngle)
        val deltaY = realDistance * sign(radianAngle)

        val newCarVectorX: Double = currentPosition.x + deltaX
        val newCarVectorY: Double = currentPosition.y + deltaY

        return Position(
            x = (newCarVectorX).toFloat(),
            y = newCarVectorY.toFloat()
        )
    }
}
