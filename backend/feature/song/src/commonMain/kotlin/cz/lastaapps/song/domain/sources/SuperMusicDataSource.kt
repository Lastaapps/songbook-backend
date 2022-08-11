package cz.lastaapps.song.domain.sources

import cz.lastaapps.song.domain.*

internal interface SuperMusicDataSource
    : SearchSongByNameDataSource, SearchSongByTextDataSource, SearchAuthorDataSource, LoadSongDataSource,
    SearchSongByAuthorDataSource

