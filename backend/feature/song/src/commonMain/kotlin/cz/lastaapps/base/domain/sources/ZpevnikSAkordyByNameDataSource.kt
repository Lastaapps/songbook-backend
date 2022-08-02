package cz.lastaapps.base.domain.sources

import cz.lastaapps.base.domain.LoadSongDataSource
import cz.lastaapps.base.domain.SearchSongByAuthorDataSource
import cz.lastaapps.base.domain.SearchSongByNameDataSource

internal interface ZpevnikSAkordyByNameDataSource
// Text search is to slow
    : SearchSongByNameDataSource, SearchSongByAuthorDataSource, LoadSongDataSource
//: SearchSongByNameDataSource, SearchSongByTextDataSource, SearchSongByAuthorDataSource, LoadSongDataSource
