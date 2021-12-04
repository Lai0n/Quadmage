package dev.sharpwave.quadmage.util

fun concatToPath(vararg fragments: String): String {
    val iterator = fragments.iterator()
    var result = iterator.next()

    while (iterator.hasNext()) {
        result += "/" + iterator.next()
    }

    return result
}