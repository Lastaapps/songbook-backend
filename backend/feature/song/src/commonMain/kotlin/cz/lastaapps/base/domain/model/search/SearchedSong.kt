package cz.lastaapps.base.domain.model.search

import cz.lastaapps.base.domain.model.SongType

data class SearchedSong(
    val id: String,
    val name: String,
    val author: String,
    val type: SongType,
    val link: String,
) : Comparable<SearchedSong> {
    override fun compareTo(other: SearchedSong): Int {
        return name.compareTo(other.name).takeUnless { it == 0 }
            ?: author.compareTo(other.author)
    }

}
