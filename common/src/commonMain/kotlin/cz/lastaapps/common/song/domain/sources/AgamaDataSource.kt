package cz.lastaapps.common.song.domain.sources

import cz.lastaapps.common.song.domain.*

internal interface AgamaDataSource
    : SearchSongByAuthorDataSource, SearchSongByNameDataSource, SearchSongByTextDataSource, SearchAuthorDataSource,
    LoadSongDataSource
