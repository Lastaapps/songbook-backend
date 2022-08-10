package cz.lastaapps.song.presentation.model.payload

import cz.lastaapps.song.domain.model.SongType
import cz.lastaapps.song.domain.model.search.SearchedSong
import cz.lastaapps.song.presentation.model.Source
import cz.lastaapps.song.presentation.model.toPresentation
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchPayload(
    val source: Source,
    val remoteId: String,
    val name: String,
    val author: String?,
    val type: SongType,
)

internal fun SearchedSong.toPayload(): SearchPayload = SearchPayload(
    source.toPresentation(), id, name, author, type
)