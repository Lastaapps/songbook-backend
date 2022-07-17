package cz.lastaapps.common.base.util

/**
 * Removes starting and ending blank lines
 */
fun List<String>.trimLines(): List<String> =
    dropWhile { it.isBlank() }.dropLastWhile { it.isBlank() }

/**
 * Joins lines back to String
 */
fun List<String>.joinLines(): String = joinToString(separator = "\n")

/**
 * In some songs every other line is empty - this function filters them out
 */
fun List<String>.dropToMuchLines(): List<String> {
    val even = filterIndexed { index, _ -> index % 2 == 0 }
    val odd = filterIndexed { index, _ -> index % 2 == 1 }

    return if (even.all { it.isBlank() }) odd
    else if (odd.all { it.isBlank() }) even
    else this
}
