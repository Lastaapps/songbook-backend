package cz.lastaapps.common.song.domain.model

import cz.lastaapps.common.song.domain.model.search.OnlineSource

data class Song(
    val id: String,
    val name: String,
    val author: String?,
    val text: String,
    val origin: OnlineSource,
    val originLink: String?,
    val videoLink: String?,
)
