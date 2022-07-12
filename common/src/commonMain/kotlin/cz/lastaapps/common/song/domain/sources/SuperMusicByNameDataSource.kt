package cz.lastaapps.common.song.domain.sources

import cz.lastaapps.common.song.domain.*

internal interface SuperMusicByNameDataSource
    : SearchSongByNameDataSource, SearchSongByTextDataSource, SearchAuthorDataSource, LoadSongDataSource,
    SearchSongByAuthorDataSource

