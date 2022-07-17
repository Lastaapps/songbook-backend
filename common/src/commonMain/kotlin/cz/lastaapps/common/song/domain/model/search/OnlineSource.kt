package cz.lastaapps.common.song.domain.model.search

sealed interface OnlineSource {
    object Agama : OnlineSource
    object PisnickyAkordy : OnlineSource
    object SuperMusic : OnlineSource
    object VelkyZpevnik : OnlineSource
    object ZpevnikSAkordy : OnlineSource
}
