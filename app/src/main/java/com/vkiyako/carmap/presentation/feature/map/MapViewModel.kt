package com.vkiyako.carmap.presentation.feature.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vkiyako.carmap.domain.entity.Car
import com.vkiyako.carmap.domain.entity.Position
import com.vkiyako.carmap.domain.interactor.SimpleCarPositionInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MapViewModel : ViewModel() {
    private val carPositionInteractor = SimpleCarPositionInteractor() // TODO use DI

    val carLiveData by lazy { MutableLiveData<Car>() }
    val destinationLiveData by lazy { MutableLiveData<Position>() }

    private var destination: Position? = null
    private var carPositionSubscription: Disposable? = null
        set(value) {
            field?.dispose()
            field = value
        }

    init {
        carLiveData.postValue(createDefaultCar())
    }

    fun onMapTouch(x: Float, y: Float) {
        val destination = Position(x, y)
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
            position = Position(200f, 200f),
            speed = 0.0,
            stateTimestamp = System.currentTimeMillis(),
            angle = 0.0
        )
    }

    override fun onCleared() {
        super.onCleared()
        carPositionSubscription?.dispose()
    }
}
