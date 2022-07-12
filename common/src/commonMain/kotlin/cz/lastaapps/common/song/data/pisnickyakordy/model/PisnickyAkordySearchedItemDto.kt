package cz.lastaapps.common.song.data.pisnickyakordy.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PisnickyAkordySearchedItemDto(
    val id: String,
    val name: String,
    @SerialName("typ")
    val type: SearchType,
    @SerialName("interpret")
    val author: String? = null,
    val link: String,
) {
    @Serializable
    enum class SearchType {
        @SerialName("interpret")
        AUTHOR,

        @SerialName("album")
        ALBUM,

        @SerialName("písnička")
        SONG,
    }
}
