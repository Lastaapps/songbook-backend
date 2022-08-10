package cz.lastaapps.song.domain.sources

import cz.lastaapps.song.domain.*

internal interface AgamaDataSource
    : SearchSongByAuthorDataSource, SearchSongByNameDataSource, SearchSongByTextDataSource, SearchAuthorDataSource,
    LoadSongDataSource
