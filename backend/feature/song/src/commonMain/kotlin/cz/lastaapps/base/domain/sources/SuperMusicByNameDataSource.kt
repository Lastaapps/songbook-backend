package cz.lastaapps.base.domain.sources

import cz.lastaapps.base.domain.*

internal interface SuperMusicByNameDataSource
    : SearchSongByNameDataSource, SearchSongByTextDataSource, SearchAuthorDataSource, LoadSongDataSource,
    SearchSongByAuthorDataSource

