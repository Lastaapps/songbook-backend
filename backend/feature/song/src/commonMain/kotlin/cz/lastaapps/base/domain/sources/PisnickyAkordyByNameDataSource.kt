package cz.lastaapps.base.domain.sources

import cz.lastaapps.base.domain.LoadSongDataSource
import cz.lastaapps.base.domain.SearchAuthorDataSource
import cz.lastaapps.base.domain.SearchSongByAuthorDataSource
import cz.lastaapps.base.domain.SearchSongByNameDataSource

internal interface PisnickyAkordyByNameDataSource
    : SearchAuthorDataSource, SearchSongByAuthorDataSource, SearchSongByNameDataSource, LoadSongDataSource