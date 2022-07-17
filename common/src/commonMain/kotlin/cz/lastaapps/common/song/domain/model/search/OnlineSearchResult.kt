package cz.lastaapps.common.song.domain.model.search

import kotlinx.collections.immutable.ImmutableList

data class OnlineSearchResult(
    val source: OnlineSource,
    val type: SearchType,
    val results: ImmutableList<SearchedSong>
)
