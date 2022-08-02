package cz.lastaapps.base.data.agama

import cz.lastaapps.base.*
import cz.lastaapps.base.data.agama.model.AgamaInterpretDto
import cz.lastaapps.base.data.agama.model.AgamaPersonDto
import cz.lastaapps.base.data.agama.model.AgamaSongDetailDto
import cz.lastaapps.base.domain.model.Author
import cz.lastaapps.base.domain.model.Song
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.OnlineSearchResult
import cz.lastaapps.base.domain.model.search.OnlineSource
import cz.lastaapps.base.domain.model.search.SearchType
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.domain.sources.AgamaDataSource
import cz.lastaapps.base.util.joinLines
import cz.lastaapps.base.util.removeAccents
import cz.lastaapps.base.util.runCatchingKtor
import cz.lastaapps.base.util.trimLines
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.lighthousegames.logging.logging

class AgamaDataSourceImpl(
    private val client: HttpClient,
) : AgamaDataSource {

    companion object {
        private val log = logging()
        private fun agamaLink(id: String) = "http://www.agama2000.com/${id.encodeURLParameter()}"
    }

    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> {
        val dtoList = doSearchByEverything(query).getIfSuccess { return it }

        val parts = query.removeAccents().split(" ").filter { it.isNotBlank() }
        // get only authors that contain query part in name
        val authors = dtoList.asSequence().filter {
            it.name.removeAccents().let { parts.any { part -> it.contains(part) } }
        }.map { Author(it.id, it.name, null, agamaLink(it.id)) }

        val songs = persistentListOf<SearchedSong>().mutate { list ->
            coroutineScope {
                authors.map {
                    async {
                        val res = loadSongsForAuthor(it)
                        if (res.isError()) return@async res
                        list.addAll(res.asSuccess().data.results)
                        Unit.toResult()
                    }
                }.toList().awaitAll()
            }.onEach { res -> res.getIfSuccess { return it } }
        }

        return OnlineSearchResult(OnlineSource.Agama, SearchType.AUTHOR, songs).toResult()
    }

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> {
        val dtoList = doSearchByEverything(query)
        if (dtoList.isError()) return dtoList.casted()

        val parts = query.removeAccents().split(" ").filter { it.isNotBlank() }

        // get only authors that contain query part in name
        val songs = dtoList.asSuccess().data.asSequence().map { author ->
            author.songs.map { song ->
                SearchedSong(song.id, song.name, author.name, SongType.CHORDS, agamaLink(song.id))
            }
        }.flatten().filter {
            it.name.removeAccents().let { parts.any { part -> it.contains(part) } }
        }.toImmutableList()

        return OnlineSearchResult(OnlineSource.Agama, SearchType.NAME, songs).toResult()
    }

    override suspend fun searchByText(query: String): Result<OnlineSearchResult> {
        val dtoList = doSearchByEverything(query)
        if (dtoList.isError()) return dtoList.casted()

        // get only authors that contain query part in name
        val songs = dtoList.asSuccess().data.asSequence().map { author ->
            author.songs.map { song ->
                SearchedSong(song.id, song.name, author.name, SongType.CHORDS, agamaLink(song.id))
            }
        }.flatten().toImmutableList()

        return OnlineSearchResult(OnlineSource.Agama, SearchType.NAME, songs).toResult()
    }

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> {
        val dtoList = doSearchByEverything(query)
        if (dtoList.isError()) return dtoList.casted()

        val parts = query.removeAccents().split(" ").filter { it.isNotBlank() }
        // get only authors that contain query part in name
        return dtoList.asSuccess().data.asSequence().filter {
            it.name.removeAccents().let { parts.any { part -> it.contains(part) } }
        }
            .map { with(it) { Author(id, name, null, agamaLink(id)) } }.toImmutableList().toResult()
    }

    private suspend fun doSearchByEverything(query: String): Result<List<AgamaInterpretDto>> {
        val joined = doSearchByEverythingRequest(query).getIfSuccess { return it }.toMutableList()
        joined.sortBy { it.id }

        // if one author occurred in both searches, join its songs
        return mutableListOf<AgamaInterpretDto>().also { filtered ->
            joined.forEach { item ->
                if (item.id == filtered.lastOrNull()?.id) {
                    val last = filtered.removeLast()
                    val songs = listOf(item.songs, last.songs).flatten().distinct()
                    filtered.add(item.copy(songs = songs))
                } else {
                    filtered.add(item)
                }
            }
        }.toResult()
    }

    private suspend fun doSearchByEverythingRequest(query: String): Result<List<AgamaInterpretDto>> = coroutineScope {
        runCatchingKtor {
            listOfNotNull(query, query.removeAccents().takeIf { it != query }).map {
                async {
                    client.get {
                        url("http://www.agama2000.com/api/findByText")
                        parameter("text", it)
                    }.also { log.i { "Requesting ${it.request.url}" } }.body<List<AgamaInterpretDto>>()
                }
            }.awaitAll().flatten().toResult()
        }
    }

    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> {
        val songs = loadSongForAuthorRequest(author.id).getIfSuccess { return it }.songs.map { song ->
            SearchedSong(song.id, song.name, author.name, SongType.CHORDS, agamaLink(song.id))
        }.toImmutableList()

        return OnlineSearchResult(OnlineSource.Agama, SearchType.AUTHOR, songs).toResult()
    }

    private suspend fun loadSongForAuthorRequest(authorId: String): Result<AgamaPersonDto> = runCatchingKtor {
        client.get {
            url("http://www.agama2000.com/api/loadSongs")
            parameter("personId", authorId)
        }.also { log.i { "Requesting ${it.request.url}" } }.body<AgamaPersonDto>().toResult()
    }


    private val youtubeUrlMatcher = """(https://www\.youtube\.com/watch\?v=.+)$""".toRegex()

    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val fetched = loadSongRequest(song.id).getIfSuccess { return it }
        val ytb = youtubeUrlMatcher.find(fetched.text.lineSequence().lastOrNull() ?: "")?.groupValues?.getOrNull(1)
        val text = (if (ytb == null) fetched.text else fetched.text.replace(ytb, "")).lines().trimLines().joinLines()

        return with(song) {
            Song(id, name, song.author, text, OnlineSource.Agama, link, ytb)
        }.toResult()
    }

    private suspend fun loadSongRequest(songId: String): Result<AgamaSongDetailDto> = runCatchingKtor {
        client.get {
            url("http://agama2000.com/api/loadDocument")
            parameter("docId", songId)
        }.also { log.i { "Requesting ${it.request.url}" } }.also { log.i { "Headers ${it.headers}" } }
            .body<AgamaSongDetailDto>().toResult()
    }
}