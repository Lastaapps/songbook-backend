package cz.lastaapps.common.song.domain.model.search

data class OnlineSearchResult(
    val source: OnlineSource,
    val types: Collection<SearchType>,
    val results: Collection<SearchedSong>
)

data class SearchedSong(
    val id: String,
    val name: String,
    val author: String?,
    val type: SongType,
    val link: String,
) : Comparable<SearchedSong> {
    override fun compareTo(other: SearchedSong): Int {
        return name.compareTo(other.name).takeUnless { it == 0 }
            ?: author?.compareTo(other.author ?: "")
            ?: 0
    }

}

sealed interface OnlineSource {
    object PisnickyAkordy : OnlineSource
    object SuperMusicSk : OnlineSource
    object ZpevnikSAkordy : OnlineSource
}

enum class SearchType {
    NAME, AUTHOR, TEXT;
}

enum class SongType {
    TEXT, CHORDS, TAB, NOTES, TRANSLATION, UNKNOWN;
}