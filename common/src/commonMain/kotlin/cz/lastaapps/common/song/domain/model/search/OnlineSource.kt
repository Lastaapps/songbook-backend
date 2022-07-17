package cz.lastaapps.common.song.domain.model.search

sealed interface OnlineSource {
    object Agama : OnlineSource
    object PisnickyAkordy : OnlineSource
    object SuperMusicSk : OnlineSource
    object ZpevnikSAkordy : OnlineSource
}
