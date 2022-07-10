package cz.lastaapps.common.song.data.supermusic

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.song.data.AuthorSearchCombine
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.sources.SuperMusicDataSource
import io.ktor.client.*
import io.ktor.client.plugins.compression.*

internal class SuperMusicDataSourceImpl(
    private val client: HttpClient,
    private val stringComparator: Comparator<String>,
) : SuperMusicDataSource {

    companion object {
        fun createHttpClient() = HttpClient {
            install(ContentEncoding)
        }

        const val minQueryLength = 3
    }

    private val songComparator = Comparator<SearchedSong> { p0, p1 ->
        stringComparator.compare(p0.name, p1.name).takeUnless { it == 0 }
            ?: stringComparator.compare(p0.author, p1.author)
    }

    private val songSearch = SuperMusicSongSearch(client, songComparator)
    private val authorSearch = SuperMusicAuthorSearch(client, songComparator)
    private val songLoader = SuperMusicSongLoader(client)
    private val searchByAuthor = AuthorSearchCombine(authorSearch)

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> = songSearch.searchByName(query)

    override suspend fun searchByText(query: String): Result<OnlineSearchResult> = songSearch.searchByText(query)

    override suspend fun searchAuthors(query: String): Result<List<Author>> = authorSearch.searchAuthors(query)
    override suspend fun loadSongsForAuthor(author: Author): Result<List<SearchedSong>> =
        authorSearch.loadSongsForAuthor(author)

    override suspend fun loadSong(song: SearchedSong): Result<Song> = songLoader.loadSong(song)
    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> =
        searchByAuthor.searchSongsByAuthor(query)
}