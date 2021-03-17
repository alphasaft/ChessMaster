package assets.extensions

import kotlin.reflect.KMutableProperty0

fun <T> KMutableProperty0<T>.getThenSetTo(value: T): T {
    val ret = get()
    set(value)
    return ret
}