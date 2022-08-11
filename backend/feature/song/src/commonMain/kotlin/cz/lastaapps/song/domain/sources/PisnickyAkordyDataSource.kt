package cz.lastaapps.song.domain.sources

import cz.lastaapps.song.domain.LoadSongDataSource
import cz.lastaapps.song.domain.SearchAuthorDataSource
import cz.lastaapps.song.domain.SearchSongByAuthorDataSource
import cz.lastaapps.song.domain.SearchSongByNameDataSource

internal interface PisnickyAkordyDataSource
    : SearchAuthorDataSource, SearchSongByAuthorDataSource, SearchSongByNameDataSource, LoadSongDataSource