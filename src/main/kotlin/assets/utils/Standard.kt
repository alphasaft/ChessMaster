package assets.utils

import kotlin.contracts.*
import kotlin.test.currentStackTrace

fun input(prompt: String): String {
    print(prompt)
    return readLine()!!
}

inline fun doUntilTrue(block: () -> Boolean) {
    do {
        val done = block()
    } while (!done)
}

// Version without inference of the buildMap from the stdlib
fun <K, V> buildMap(builder: MutableMap<K, V>.() -> Unit) =
    mutableMapOf<K, V>().apply(builder)

fun <T> both(a: T, b: T) = object {
        override fun equals(other: Any?): Boolean {
            return a == other && b == other
        }
}

fun callSite() = currentStackTrace().getOrNull(2) ?: throw IllegalStateException("Function ${currentStackTrace()[1].methodName} doesn't have any call site")
fun mainFileName() = currentStackTrace().last().fileName!!
