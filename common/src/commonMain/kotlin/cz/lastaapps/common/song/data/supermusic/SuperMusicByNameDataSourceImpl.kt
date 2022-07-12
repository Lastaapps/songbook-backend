package cz.lastaapps.common.song.data.supermusic

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.song.data.AuthorSearchCombine
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.sources.SuperMusicByNameDataSource
import io.ktor.client.*

internal class SuperMusicByNameDataSourceImpl(
    client: HttpClient, comparator: Comparator<SearchedSong>,
) : SuperMusicByNameDataSource {

    companion object {
        const val minQueryLength = 3
    }

    private val songSearch = SuperMusicSongSearchByName(client, comparator)
    private val authorSearch = SuperMusicAuthorSearch(client, comparator)
    private val songLoader = SuperMusicSongLoader(client)
    private val searchByAuthor = AuthorSearchCombine(authorSearch)

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> = songSearch.searchByName(query)

    override suspend fun searchByText(query: String): Result<OnlineSearchResult> = songSearch.searchByText(query)

    override suspend fun searchAuthors(query: String): Result<List<Author>> = authorSearch.searchAuthors(query)
    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> =
        authorSearch.loadSongsForAuthor(author)

    override suspend fun loadSong(song: SearchedSong): Result<Song> = songLoader.loadSong(song)
    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> =
        searchByAuthor.searchSongsByAuthor(query)
}