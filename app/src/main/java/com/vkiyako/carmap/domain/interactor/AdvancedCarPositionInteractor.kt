package com.vkiyako.carmap.domain.interactor

/**
 * Tracers path of the given car to the destination
 */
//class AdvancedCarPositionInteractor {
//    companion object {
//        private val RATE = 100 // approximate number of updates per second
//        private val SLEEP_MS = (1000.0 / RATE).roundToLong()
//        private val MAX_SPEED = 60.0
//    }
//
//    fun driveCarToDestination(car: Car, destination: Position): Flowable<Car> {
//        return Flowable.create(
//            {
//                try {
//                    var nextCar = car.copy(stateTimestamp = System.currentTimeMillis())
//                    while (nextCar.position != destination) {
//                        nextCar = calculateNextState(nextCar, destination)
//                        it.onNext(nextCar)
//                        Thread.sleep(SLEEP_MS)
//                    }
//                    it.onComplete()
//                } catch (e: Throwable) {
//                    it.onError(e)
//                }
//            },
//            BackpressureStrategy.LATEST
//        )
//    }
//
//    /**
//     * Calculates the next state of the given car with respect of time.
//     * The returned state will get the car closer to the destination
//     */
//    private fun calculateNextState(car: Car, destination: Position): Car {
//        val newTimestamp = System.currentTimeMillis()
//        val angleOffset = car.calculateAngleToDestinationOffset(destination)
//        val maxTurnAngle = getMaxTurnAngleForSpeed(car.speed)
//        val turnAngle = min(angleOffset, maxTurnAngle)
//
//        val newCarSpeed = car.speed + getSpeedDeltaForNextState(car, destination, newTimestamp)
//        val newCarPosition = car.position
//
//        return Car(
//            position = newCarPosition,
//            angle = turnAngle,
//            speed = newCarSpeed,
//            stateTimestamp = newTimestamp
//        )
//    }
//
//    private fun getSpeedDeltaForNextState(car: Car, destination: Position, newTimestamp: Long): Double {
//        // FIXME calculate real value
//        return min(MAX_SPEED, car.speed + calculateMaxAcceleration(car, newTimestamp))
//    }
//
//    private fun calculateMaxAcceleration(car: Car, newTimestamp: Long): Double {
//        // FIXME do read calculations
//        return 20.0
//    }
//
//    /**
//     * @return max angle in degrees that a car can turn
//     */
//    private fun getMaxTurnAngleForSpeed(speed: Double): Int {
//        if (speed == 0.0) {
//            return 0
//        }
//        val maxPossibleAngle = 90
//        // 36 000 was taken to make it possible to turn 90 degrees at 20 kmh speed
//        val actualMaxAngle = 36000 / (speed * speed)
//        if (actualMaxAngle > maxPossibleAngle) {
//            return maxPossibleAngle
//        }
//        return actualMaxAngle.roundToInt()
//    }
//}
