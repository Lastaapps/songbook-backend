package cz.lastaapps.common.song.data.agama.model

import kotlinx.serialization.Serializable

@Serializable
data class AgamaSongDetailDto(
    val name: String,
//    val xid: String,
    val id: String,
    val text: String,
//    val type: String,
    val interprets: List<String>,
//    val created: String,
//    val isLastBoolean: Boolean,
)
