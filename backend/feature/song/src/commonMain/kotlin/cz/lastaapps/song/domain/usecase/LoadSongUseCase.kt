package cz.lastaapps.song.domain.usecase

import cz.lastaapps.base.Result
import cz.lastaapps.base.usecase.UCParam
import cz.lastaapps.base.usecase.UseCaseResult
import cz.lastaapps.base.usecase.UseCaseResultImpl
import cz.lastaapps.song.domain.SongRepository
import cz.lastaapps.song.domain.model.Song
import cz.lastaapps.song.domain.model.search.OnlineSource

interface LoadSongUseCase : UseCaseResult<LoadSongUseCase.Params, Song> {
    data class Params(val source: OnlineSource, val remoteId: String) : UCParam
}

internal class LoadSongUseCaseImpl(
    private val repo: SongRepository
) : UseCaseResultImpl<LoadSongUseCase.Params, Song>(), LoadSongUseCase {
    override suspend fun doWork(params: LoadSongUseCase.Params): Result<Song> {
        return repo.load(params.source, params.remoteId)
    }
}


