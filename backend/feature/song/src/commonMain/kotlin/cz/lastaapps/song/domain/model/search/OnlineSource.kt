package cz.lastaapps.song.domain.model.search

sealed interface OnlineSource {
    object Agama : OnlineSource
    object Brnkni : OnlineSource
    object PisnickyAkordy : OnlineSource
    object SuperMusic : OnlineSource
    object VelkyZpevnik : OnlineSource
    object ZpevnikSAkordy : OnlineSource
}
