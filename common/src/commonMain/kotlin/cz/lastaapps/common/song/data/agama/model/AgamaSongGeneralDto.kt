package cz.lastaapps.common.song.data.agama.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AgamaSongGeneralDto(
    val id: String,
    val name: String,
    val interpretId: List<String>,
)