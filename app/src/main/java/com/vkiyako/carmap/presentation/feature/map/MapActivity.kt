package com.vkiyako.carmap.presentation.feature.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.vkiyako.carmap.R
import com.vkiyako.carmap.domain.interactor.SimpleCarPositionInteractor

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapViewModel: MapViewModel
    private val viewModelFactory = MapViewModelFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapViewModel = ViewModelProviders.of(this, viewModelFactory).get(MapViewModel::class.java)

        mapView.setOnTouchListener { _, motionEvent ->
            mapViewModel.onMapTouch(motionEvent.x, motionEvent.y, mapView.height)
            true
        }

        mapViewModel.destinationLiveData.observe(this, Observer {
            mapView.destination = it
        })
        mapViewModel.carLiveData.observe(this, Observer {
            mapView.car = it
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.setOnTouchListener(null)
    }
}

private class MapViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(SimpleCarPositionInteractor()) as T
        }
        throw IllegalStateException("View model for ${modelClass::class.java.name} not found")
    }
}
