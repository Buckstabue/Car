package com.vkiyako.carmap.presentation.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

@Suppress("NOTHING_TO_INLINE")
inline fun Context.dpToPx(dp: Int): Int {
    return Math.round(this.resources.displayMetrics.density * dp)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Context.getCompatColor(@ColorRes colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}
