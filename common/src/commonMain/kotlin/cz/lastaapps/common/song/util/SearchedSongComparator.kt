package cz.lastaapps.common.song.util

import cz.lastaapps.common.song.domain.model.search.SearchedSong

class SearchedSongComparator(
    private val comparator: Comparator<String>,
) : Comparator<SearchedSong> {
    companion object {
        private val stringComparator = Comparator<String> { p0, p1 -> p0?.compareTo(p1 ?: "") ?: -1 }
        val default get() = SearchedSongComparator(stringComparator)
    }

    override fun compare(p0: SearchedSong, p1: SearchedSong): Int =
        comparator.compare(p0.name, p1.name).takeUnless { it == 0 }
            ?: comparator.compare(p0.author, p1.author)
}