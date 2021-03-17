package assets.extensions

import kotlin.math.pow
import kotlin.math.round
import kotlin.math.truncate

fun Double.withNDigitsAfterFloatingPoint(n: Int): Double =
    truncate(this*10.0.pow(n))
        .toInt()
        .toString()
        .plus("0".repeat(n))
        .let { it.insertAt(it.length-(n+1), ".") }
        .toDouble()

fun Double.roundedWithNDigitsAfterFloatingPoint(n: Int): Double =
    round(this*10.0.pow(n))
        .toInt()
        .toString()
        .plus("0".repeat(n))
        .let { it.insertAt(it.length-(n+1), ".") }
        .toDouble()
