package cz.lastaapps.common.song.data.agama.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AgamaPersonDto(
    val loaded: Boolean,
    val time: Long,
    val personId: String,
    val songs: List<AgamaSongGeneralDto>,
)