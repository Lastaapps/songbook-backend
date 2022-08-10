package cz.lastaapps.song.presentation.model

import cz.lastaapps.song.domain.model.search.OnlineSource
import kotlinx.serialization.Serializable

// wrapper for OnlineSource
@Serializable
internal enum class Source {
    AGAMA, BRNKNI, PISNICKYAKORDY, SUPERMUSIC, VELKYZPEVNIK, ZPEVNIKSAKORDY, ;

    companion object {
        fun from(name: String): Source? {
            val lower = name.lowercase()
            return values().firstOrNull { it.name.lowercase() == lower }
        }
    }
}

internal fun OnlineSource.toPresentation(): Source = when (this) {
    OnlineSource.Agama -> Source.AGAMA
    OnlineSource.Brnkni -> Source.BRNKNI
    OnlineSource.PisnickyAkordy -> Source.PISNICKYAKORDY
    OnlineSource.SuperMusic -> Source.SUPERMUSIC
    OnlineSource.VelkyZpevnik -> Source.VELKYZPEVNIK
    OnlineSource.ZpevnikSAkordy -> Source.ZPEVNIKSAKORDY
}

internal fun Source.toDomain(): OnlineSource = when (this) {
    Source.AGAMA -> OnlineSource.Agama
    Source.BRNKNI -> OnlineSource.Brnkni
    Source.PISNICKYAKORDY -> OnlineSource.PisnickyAkordy
    Source.SUPERMUSIC -> OnlineSource.SuperMusic
    Source.VELKYZPEVNIK -> OnlineSource.VelkyZpevnik
    Source.ZPEVNIKSAKORDY -> OnlineSource.ZpevnikSAkordy
}
