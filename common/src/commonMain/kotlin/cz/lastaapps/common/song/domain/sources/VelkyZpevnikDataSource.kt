package cz.lastaapps.common.song.domain.sources

import cz.lastaapps.common.song.domain.*

internal interface VelkyZpevnikDataSource :
    SearchAuthorDataSource, SearchSongByAuthorDataSource, SearchSongByNameDataSource, SearchSongByTextDataSource,
    LoadSongDataSource