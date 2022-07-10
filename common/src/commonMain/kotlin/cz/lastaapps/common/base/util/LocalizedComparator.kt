package cz.lastaapps.common.base.util

import io.fluidsonic.locale.Locale

interface LocalizedComparatorProvider {
    fun createDefault(): Comparator<String>
    fun create(locale: Locale): Comparator<String>
}