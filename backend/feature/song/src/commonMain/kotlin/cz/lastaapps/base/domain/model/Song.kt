package cz.lastaapps.base.domain.model

import cz.lastaapps.base.domain.model.search.OnlineSource

data class Song(
    val id: String,
    val name: String,
    val author: String?,
    val text: String,
    val origin: OnlineSource,
    val originLink: String?,
    val videoLink: String?,
)
