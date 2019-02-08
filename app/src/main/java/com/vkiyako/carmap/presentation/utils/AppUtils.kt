package com.vkiyako.carmap.presentation.utils

inline fun <T> whenNotNull(value: T?, action: (T) -> Unit) {
    value?.let(action)
}
