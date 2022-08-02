package cz.lastaapps.base.data.supermusic

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.model.Author
import cz.lastaapps.base.domain.model.Song
import cz.lastaapps.base.domain.model.search.OnlineSearchResult
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.domain.sources.SuperMusicByNameDataSource
import io.ktor.client.*
import kotlinx.collections.immutable.ImmutableList

internal class SuperMusicByNameDataSourceImpl(
    client: HttpClient,
) : SuperMusicByNameDataSource {

    companion object {
        const val minQueryLength = 3
    }

    private val songSearch = SuperMusicSongSearchByName(client)
    private val authorSearch = SuperMusicAuthorSearch(client)
    private val songLoader = SuperMusicSongLoader(client)
    private val searchByAuthor = cz.lastaapps.base.data.AuthorSearchCombine(authorSearch)

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> = songSearch.searchByName(query)

    override suspend fun searchByText(query: String): Result<OnlineSearchResult> = songSearch.searchByText(query)

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> = authorSearch.searchAuthors(query)
    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> =
        authorSearch.loadSongsForAuthor(author)

    override suspend fun loadSong(song: SearchedSong): Result<Song> = songLoader.loadSong(song)
    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> =
        searchByAuthor.searchSongsByAuthor(query)
}