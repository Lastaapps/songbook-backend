package cz.lastaapps.song.domain.sources

import cz.lastaapps.song.domain.*

internal interface VelkyZpevnikDataSource :
    SearchAuthorDataSource, SearchSongByAuthorDataSource, SearchSongByNameDataSource, SearchSongByTextDataSource,
    LoadSongDataSource