package cz.lastaapps.base.util

import io.fluidsonic.locale.Locale
import io.fluidsonic.locale.toCommon
import io.fluidsonic.locale.toPlatform
import java.text.Collator

class LocalizedComparatorProviderImpl : LocalizedComparatorProvider {

    override fun createDefault(): Comparator<String> = create(java.util.Locale.getDefault().toCommon())
    override fun create(locale: Locale): Comparator<String> = object : Comparator<String> {
        private val collator: Collator = Collator.getInstance(locale.toPlatform())

        init {
            Locale.root
            collator.decomposition = Collator.FULL_DECOMPOSITION
            collator.strength = Collator.SECONDARY // ignores lower/upper case
        }

        override fun compare(p0: String?, p1: String?): Int =
            collator.compare(p0, p1)
    }
}