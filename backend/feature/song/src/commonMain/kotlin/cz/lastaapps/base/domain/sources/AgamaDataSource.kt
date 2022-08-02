package cz.lastaapps.base.domain.sources

import cz.lastaapps.base.domain.*

internal interface AgamaDataSource
    : SearchSongByAuthorDataSource, SearchSongByNameDataSource, SearchSongByTextDataSource, SearchAuthorDataSource,
    LoadSongDataSource
