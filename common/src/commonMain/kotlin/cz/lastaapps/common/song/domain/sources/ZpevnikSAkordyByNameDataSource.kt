package cz.lastaapps.common.song.domain.sources

import cz.lastaapps.common.song.domain.LoadSongDataSource
import cz.lastaapps.common.song.domain.SearchSongByAuthorDataSource
import cz.lastaapps.common.song.domain.SearchSongByNameDataSource

internal interface ZpevnikSAkordyByNameDataSource
// Text search is to slow
    : SearchSongByNameDataSource, SearchSongByAuthorDataSource, LoadSongDataSource
//: SearchSongByNameDataSource, SearchSongByTextDataSource, SearchSongByAuthorDataSource, LoadSongDataSource
