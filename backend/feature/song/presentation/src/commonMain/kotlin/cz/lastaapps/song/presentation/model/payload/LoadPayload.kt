package cz.lastaapps.song.presentation.model.payload

import cz.lastaapps.song.domain.model.Song
import cz.lastaapps.song.presentation.model.Source
import cz.lastaapps.song.presentation.model.toPresentation
import kotlinx.serialization.Serializable

@Serializable
internal data class LoadPayload(
    val source: Source,
    val remoteId: String,
    val name: String,
    val author: String?,
    val text: String,
    val originLink: String,
    val videoLink: String?,
)

internal fun Song.toPayload(): LoadPayload = LoadPayload(
    origin.toPresentation(), id, name, author, text, originLink, videoLink,
)
