package com.vkiyako.carmap.domain.interactor

import com.vkiyako.carmap.domain.entity.Angle
import com.vkiyako.carmap.domain.entity.Car
import com.vkiyako.carmap.domain.entity.Position
import com.vkiyako.carmap.domain.entity.plus
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlin.math.*

class SimpleCarPositionInteractor : CarPositionInteractor {
    companion object {
        private const val RATE = 20 // approximate number of updates per second
        private val SLEEP_MS = (1000.0 / RATE).roundToLong()
        private const val MAX_SPEED = 20.0 / 1000.0 // pixels per millisecond
        private const val TURN_SPEED = 20.0 / 1000.0 // degrees per millisecond

        /**
         * indicates min distance in pixels to destination when the car is considered to be on place
         */
        private const val DISTANCE_EPS = 8
    }

    override fun driveCarToDestination(car: Car, destination: Position): Flowable<Car> {
        return Flowable.create(
            {
                try {
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
        val timeDeltaMs: Long = newTimestamp - car.stateTimestamp
        // angle offset that car might be impossible to turn within timeDelta
        val desiredAngleOffset = car.calculateAngleToDestinationOffset(destination)
        // how much time from timeDelta will be spent on turning car
        val turnTimeMs: Long = min(calculateTimeMsToTurn(desiredAngleOffset), timeDeltaMs)
        // how much time from timeDelta will be spent on moving car
        val movementTimeMs: Long = timeDeltaMs - turnTimeMs
        // offset applied to the current car angle within the current time delta
        val realAngleOffset: Double = sign(desiredAngleOffset.degrees) *
                min((turnTimeMs * TURN_SPEED), abs(desiredAngleOffset.degrees))
        val newAngle = car.angle + Angle.fromDegrees(realAngleOffset)
        val newPosition: Position = calculateNextPosition(car.position, newAngle, movementTimeMs, destination)
        return Car(
            position = newPosition,
            angle = newAngle,
            speed = MAX_SPEED,
            stateTimestamp = newTimestamp
        )
    }

    private fun calculateTimeMsToTurn(angle: Angle): Long {
        return (abs(angle.degrees) / TURN_SPEED).roundToLong()
    }

    private fun calculateNextPosition(
        currentPosition: Position,
        angle: Angle,
        timeDelta: Long,
        destination: Position
    ): Position {
        if (timeDelta <= 0) {
            return currentPosition
        }

        val maxDistance: Double = timeDelta * MAX_SPEED
        val distanceToDestination: Double = currentPosition.getDistanceTo(destination)
        val realDistance: Double = min(maxDistance, distanceToDestination)

        val deltaX = realDistance * cos(angle.radians)
        val deltaY = realDistance * sin(angle.radians)

        val newCarVectorX: Double = currentPosition.x + deltaX
        val newCarVectorY: Double = currentPosition.y + deltaY

        return Position(
            x = (newCarVectorX).toFloat(),
            y = newCarVectorY.toFloat()
        )
    }
}
