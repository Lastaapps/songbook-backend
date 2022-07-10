package cz.lastaapps.common.song.domain.model

data class Song(
    val id: String,
    val name: String,
    val author: String?,
    val text: String,
    val originLink: String?,
    val videoLink: String?,
)
