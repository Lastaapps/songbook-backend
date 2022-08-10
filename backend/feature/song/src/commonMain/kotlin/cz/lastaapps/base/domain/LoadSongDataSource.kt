package cz.lastaapps.base.domain

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.Song
import cz.lastaapps.base.domain.model.search.SearchedSong


internal interface LoadSongDataSource {
    suspend fun loadSong(id: String): Result<Song>
}

internal suspend fun LoadSongDataSource.loadSong(song: SearchedSong) =
    loadSong(song.id)
