package com.vkiyako.carmap.domain.interactor

import com.vkiyako.carmap.domain.entity.Car
import com.vkiyako.carmap.domain.entity.Position
import io.reactivex.Flowable

interface CarPositionInteractor {
    fun driveCarToDestination(car: Car, destination: Position): Flowable<Car>
}
