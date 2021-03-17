package assets.extensions

fun <K, V> Map<K, V>.random(): Map.Entry<K, V> {
    if (isEmpty()) throw NoSuchElementException()
    val picked = toList().random()
    for (entry in this) if (entry.toPair() == picked) return entry
    throw InternalError("EmptyList.random() didn't throw")
}

fun <K> Map<K, Double>.withValuesSummedWithThoseOf(other: Map<K, Double>): Map<K, Double> {
    val summed = mutableMapOf<K, Double>()
    for ((key, value) in this) {
        summed[key] = value + (other[key] ?: 0.0)
    }
    return summed
}

/** [equalityDefiner] is assumed to be :
 * * reflexive : equalityDefiner(x, x) == true
 * * transitive : if equalityDefiner(x, y) and equalityDefiner(y, z), equalityDefiner(x, z) == true
 * * symmetric : if equalityDefiner(x, y), equalityDefiner(y, x) == true */
fun <T> List<T>.sortAccordingToEquality(equalityDefiner: (T, T) -> Boolean): List<List<T>> {
    val result = mutableListOf<List<T>>()
    val iterator = iterator()
    if (!iterator.hasNext()) return result

    var equivalenceClass = mutableListOf(iterator.next())
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (equalityDefiner(equivalenceClass.last(), next)) equivalenceClass.add(next)
        else {
            result.add(equivalenceClass)
            equivalenceClass = mutableListOf()
        }
    }
    result.add(equivalenceClass)

    return result
}
