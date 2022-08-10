package cz.lastaapps.song.domain.sources

import cz.lastaapps.song.domain.*

internal interface SuperMusicByNameDataSource
    : SearchSongByNameDataSource, SearchSongByTextDataSource, SearchAuthorDataSource, LoadSongDataSource,
    SearchSongByAuthorDataSource

