package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.Song
import cz.lastaapps.base.domain.model.search.SearchedSong


internal interface LoadSongDataSource {
    suspend fun loadSong(song: SearchedSong): Result<Song>
}
