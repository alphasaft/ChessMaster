package assets.extensions

import game.pawns.BasePawn
import game.pawns.Color
import kotlinx.coroutines.Job

fun Iterable<BasePawn>.only(color: Color) = filter { it.owner == color }

fun Collection<Job>.waitForCompletion() {
    while (true) {
        if (this.all { it.isCompleted }) break
    }
}
