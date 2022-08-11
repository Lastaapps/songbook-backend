package cz.lastaapps.song.domain.usecase

import cz.lastaapps.base.Result
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.base.usecase.UCParam
import cz.lastaapps.base.usecase.UseCaseResult
import cz.lastaapps.base.usecase.UseCaseResultImpl
import cz.lastaapps.base.util.safeSubList
import cz.lastaapps.song.domain.SongRepository
import cz.lastaapps.song.domain.model.search.SearchType
import cz.lastaapps.song.domain.model.search.SearchedSong
import cz.lastaapps.song.util.SearchedSongComparator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

interface SearchSongUseCase : UseCaseResult<SearchSongUseCase.Params, ImmutableList<SearchedSong>> {
    data class Params(
        val query: String, val name: Boolean, val text: Boolean, val author: Boolean, val page: Int?
    ) : UCParam
}

internal class SearchSongUseCaseImpl(
    private val repo: SongRepository,
    private val comparator: SearchedSongComparator,
) : SearchSongUseCase, UseCaseResultImpl<SearchSongUseCase.Params, ImmutableList<SearchedSong>>() {
    companion object {
        const val pageSize: Int = 8
    }

    override suspend fun doWork(params: SearchSongUseCase.Params): Result<ImmutableList<SearchedSong>> {
        val type = when {
            params.name -> SearchType.NAME
            params.text -> SearchType.TEXT
            params.author -> SearchType.AUTHOR
            else -> error("At least one search type must be specified")
        }
        val songs = repo.search(params.query, type).getIfSuccess { return it }

        return songs.sortedWith(comparator).toPersistentList().run {
            params.page?.let {
                safeSubList(params.page * pageSize, (params.page + 1) * pageSize)
            } ?: this
        }.toResult()
    }
}
