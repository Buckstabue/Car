package com.vkiyako.carmap.presentation.feature.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.vkiyako.carmap.R
import com.vkiyako.carmap.domain.entity.Car
import com.vkiyako.carmap.domain.entity.Position
import com.vkiyako.carmap.presentation.utils.dpToPx
import com.vkiyako.carmap.presentation.utils.getCompatColor
import com.vkiyako.carmap.presentation.utils.whenNotNull

class MapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var car: Car? = null
        set(value) {
            field = value
            invalidate()
        }

    var destination: Position? = null
        set(value) {
            field = value
            invalidate()
        }

    private val carWidth = context.dpToPx(30).toFloat()
    private val carHeight = carWidth / 0.45f
    private val destinationRadius = context.dpToPx(10).toFloat()
    private val carDirectionRadius = context.dpToPx(5).toFloat()

    private val circlePaint = Paint().apply {
        color = context.getCompatColor(R.color.green)
    }

    private val carPaint = Paint().apply {
        color = context.getCompatColor(R.color.light_orange)
    }

    private val carDirectionPaint = Paint().apply {
        color = context.getCompatColor(R.color.dark_purple)
    }

    override fun onDraw(canvas: Canvas) {
        whenNotNull(car) {
            drawCar(it, canvas)
        }
        whenNotNull(destination) {
            drawDestination(it, canvas)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun drawCar(car: Car, canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.rotate(car.angle.toFloat(), car.position.x, car.position.y)
        val topLeftX = car.position.x + carWidth / 2
        val topLeftY = car.position.y
        val bottomRightX = topLeftX - carWidth
        val bottomRightY = car.position.y - carHeight

        canvas.drawRect(topLeftX, topLeftY, bottomRightX, bottomRightY, carPaint)

        canvas.drawCircle(car.position.x, car.position.y - carDirectionRadius, carDirectionRadius, carDirectionPaint)

        canvas.restoreToCount(saveCount)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun drawDestination(destination: Position, canvas: Canvas) {
        canvas.drawCircle(destination.x, destination.y, destinationRadius, circlePaint)
    }
}


