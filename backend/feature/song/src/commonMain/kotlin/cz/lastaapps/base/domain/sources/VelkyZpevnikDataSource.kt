package cz.lastaapps.base.domain.sources

import cz.lastaapps.base.domain.*

internal interface VelkyZpevnikDataSource :
    SearchAuthorDataSource, SearchSongByAuthorDataSource, SearchSongByNameDataSource, SearchSongByTextDataSource,
    LoadSongDataSource