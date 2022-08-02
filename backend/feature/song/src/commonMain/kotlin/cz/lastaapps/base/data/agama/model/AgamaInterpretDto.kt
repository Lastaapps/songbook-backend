package cz.lastaapps.base.data.agama.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AgamaInterpretDto(
    val id: String,
    val name: String,
    val songs: List<AgamaSongGeneralDto>,
)