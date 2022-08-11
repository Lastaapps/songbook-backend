package cz.lastaapps.base.util

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> {
    val from = fromIndex.coerceAtLeast(0)
    val to = toIndex.coerceIn(from, size)
    if (from > lastIndex || to < 0) return emptyList()
    return this.subList(from, to)
}

fun <T> ImmutableList<T>.safeSubList(fromIndex: Int, toIndex: Int): ImmutableList<T> {
    val from = fromIndex.coerceAtLeast(0)
    val to = toIndex.coerceIn(from, size)
    if (from > lastIndex || to < 0) return persistentListOf()
    return this.subList(from, to)
}
