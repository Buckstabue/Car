package com.vkiyako.carmap.presentation.feature.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.vkiyako.carmap.R

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)


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
