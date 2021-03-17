package assets.extensions

import assets.utils.both
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.cast
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.memberProperties

fun <T> T.ofWhich(predicate: T.() -> Boolean): T {
    require(this.predicate())
    return this
}

fun <T> T.producedFromLambda(): () -> T = { this }

inline fun <T, R, reified E : Throwable> T.iterateThroughUntilException(
        selector: T.(Int) -> R
): List<R> {
    var i = 0
    val result = mutableListOf<R>()
    while (true) {
        try {
            result.add(selector(i++))
        } catch (e: Throwable) {
            if (e is E) break
            else throw e
        }
    }
    return result
}

fun <T, R> T.iterateThroughUntilNull(selector: T.(Int) -> R?): List<R> {
    var i = 0
    val result = mutableListOf<R>()
    var selected = selector(i++)
    while (selected != null) {
        result.add(selected)
        selected = selector(i++)
    }
    return result
}

inline fun <reified T : Any> T.fullyEqualsTo(other: Any?): Boolean {
    if (other == null) return false
    if (!(both(this::class, other::class) == T::class)) return false

    @Suppress("UNCHECKED_CAST")
    val members = (this::class as KClass<T>).memberProperties
    return members.all { it.get(this) == it.get(other as T) }
}

infix fun <T> Comparable<T>.comparedTo(other: T): Int = compareTo(other)