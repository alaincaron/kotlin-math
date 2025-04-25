package org.alc.math

data class Point2d<out T: Number>(val x: T, val y: T)
internal fun fix0(x: Double) = if (x == 0.0) 0.0 else x
