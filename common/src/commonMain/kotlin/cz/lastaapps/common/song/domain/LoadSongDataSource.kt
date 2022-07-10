package cz.lastaapps.common.song.domain

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.search.SearchedSong


internal interface LoadSongDataSource {
    suspend fun loadSong(song: SearchedSong): Result<Song>
}
