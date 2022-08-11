package cz.lastaapps.song.domain.sources

import cz.lastaapps.song.domain.LoadSongDataSource
import cz.lastaapps.song.domain.SearchSongByAuthorDataSource
import cz.lastaapps.song.domain.SearchSongByNameDataSource

internal interface ZpevnikSAkordyDataSource
// Text search is to slow
    : SearchSongByNameDataSource, SearchSongByAuthorDataSource, LoadSongDataSource
//: SearchSongByNameDataSource, SearchSongByTextDataSource, SearchSongByAuthorDataSource, LoadSongDataSource
