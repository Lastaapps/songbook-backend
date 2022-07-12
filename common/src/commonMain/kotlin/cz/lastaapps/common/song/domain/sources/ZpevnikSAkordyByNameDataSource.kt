package cz.lastaapps.common.song.domain.sources

import cz.lastaapps.common.song.domain.LoadSongDataSource
import cz.lastaapps.common.song.domain.SearchSongByAuthorDataSource
import cz.lastaapps.common.song.domain.SearchSongByNameDataSource
import cz.lastaapps.common.song.domain.SearchSongByTextDataSource

internal interface ZpevnikSAkordyByNameDataSource
    : SearchSongByNameDataSource, SearchSongByTextDataSource, SearchSongByAuthorDataSource, LoadSongDataSource