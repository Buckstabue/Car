package com.vkiyako.carmap.presentation.feature.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vkiyako.carmap.domain.entity.Angle
import com.vkiyako.carmap.domain.entity.Car
import com.vkiyako.carmap.domain.entity.Position
import com.vkiyako.carmap.domain.interactor.CarPositionInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

class MapViewModel(
    private val carPositionInteractor: CarPositionInteractor
) : ViewModel() {
    val carLiveData by lazy { MutableLiveData<Car>() }
    val destinationLiveData by lazy { MutableLiveData<Position>() }

    private var destination: Position? = null
    private var carPositionSubscription: Disposable = Disposables.disposed()
        set(value) {
            field.dispose()
            field = value
        }

    init {
        carLiveData.postValue(createDefaultCar())
    }

    fun onMapTouch(x: Float, y: Float, mapViewHeight: Int) {
        val isCarMoving = !carPositionSubscription.isDisposed
        if (isCarMoving) {
            return // ignore user input while car is moving
        }

        val realX = x
        val realY = mapViewHeight - y // translate Y coordinate to traditional math Y, where Y starts on bottom left
        val destination = Position(realX, realY)

        this.destination = destination
        destinationLiveData.postValue(destination)
        val newCar = carLiveData.value ?: createDefaultCar()
        carPositionSubscription = carPositionInteractor.driveCarToDestination(newCar, destination)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                carLiveData.postValue(it)
            }, { Log.e("TEST", "Fatal error: ", it) })
    }

    private fun createDefaultCar(): Car {
        return Car(
            position = Position(250f, 500f),
            speed = 0.0,
            stateTimestamp = System.currentTimeMillis(),
            angle = Angle.fromDegrees(160.0)
        )
    }

    override fun onCleared() {
        super.onCleared()
        carPositionSubscription.dispose()
    }
}
