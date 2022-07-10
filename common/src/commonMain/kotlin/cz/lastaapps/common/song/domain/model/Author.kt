package cz.lastaapps.common.song.domain.model

data class Author(
    val id: String,
    val name: String,
    val songNumber: Int?,
    val link: String,
)