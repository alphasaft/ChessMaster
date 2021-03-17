package assets.utils

import assets.extensions.pop

class PausedIterableProcessing<T, R>(
        _toProcess: Iterable<T>,
        private val action: (T, timeout: Int) -> R
) {
    private val toProcess: MutableList<T> = _toProcess.toMutableList()
    private val processed: MutableList<R> = mutableListOf()

    val isReady get() = toProcess.isEmpty()
    val isNotReady get() = toProcess.isNotEmpty()

    fun processValuesWithinGivenMillis(timeout: Int) {
        val startTime = System.currentTimeMillis()

        while (isNotReady && System.currentTimeMillis() - startTime < timeout) {
            processed.add(action(toProcess.pop(), (startTime+timeout-System.currentTimeMillis()).toInt()))
        }
    }

    fun processAllValues() {
        while (isNotReady) {
            processed.add(action(toProcess.pop(), Int.MAX_VALUE))
        }
    }

    fun getAllProcessedValuesOrNull(): List<R>? =
            if (isReady) processed else null
}

fun <T, R> Iterable<T>.processValuesWithinGivenMillis(
        timeout: Int,
        action: (T, timeout: Int) -> R,
) = this.initPausedProcessing(action).apply { processValuesWithinGivenMillis(timeout) }

fun <T, R> Iterable<T>.initPausedProcessing(action: (T, timeout: Int) -> R) =
        PausedIterableProcessing(this, action)
