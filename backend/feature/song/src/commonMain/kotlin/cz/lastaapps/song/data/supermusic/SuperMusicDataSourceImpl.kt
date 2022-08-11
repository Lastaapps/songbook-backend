package cz.lastaapps.song.data.supermusic

import cz.lastaapps.base.Result
import cz.lastaapps.song.domain.model.Author
import cz.lastaapps.song.domain.model.Song
import cz.lastaapps.song.domain.model.search.SearchedSong
import cz.lastaapps.song.domain.sources.SuperMusicDataSource
import io.ktor.client.*
import kotlinx.collections.immutable.ImmutableList

internal class SuperMusicDataSourceImpl(
    client: HttpClient,
) : SuperMusicDataSource {

    companion object {
        const val minQueryLength = 3
    }

    private val songSearch = SuperMusicSongSearchByName(client)
    private val authorSearch = SuperMusicAuthorSearch(client)
    private val songLoader = SuperMusicSongLoader(client)
    private val searchByAuthor = cz.lastaapps.song.data.AuthorSearchCombine(authorSearch)

    override suspend fun searchByName(query: String): Result<ImmutableList<SearchedSong>> =
        songSearch.searchByName(query)

    override suspend fun searchByText(query: String): Result<ImmutableList<SearchedSong>> =
        songSearch.searchByText(query)

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> = authorSearch.searchAuthors(query)

    override suspend fun loadSongsForAuthor(author: Author): Result<ImmutableList<SearchedSong>> =
        authorSearch.loadSongsForAuthor(author)

    override suspend fun loadSong(id: String): Result<Song> = songLoader.loadSong(id)

    override suspend fun searchSongsByAuthor(query: String): Result<ImmutableList<SearchedSong>> =
        searchByAuthor.searchSongsByAuthor(query)
}