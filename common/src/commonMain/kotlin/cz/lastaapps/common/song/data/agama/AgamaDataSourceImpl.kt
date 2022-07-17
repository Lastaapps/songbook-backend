package cz.lastaapps.common.song.data.agama

import cz.lastaapps.common.base.*
import cz.lastaapps.common.base.util.joinLines
import cz.lastaapps.common.base.util.removeAccents
import cz.lastaapps.common.base.util.trimLines
import cz.lastaapps.common.song.data.agama.model.AgamaInterpretDto
import cz.lastaapps.common.song.data.agama.model.AgamaPersonDto
import cz.lastaapps.common.song.data.agama.model.AgamaSongDetailDto
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.SongType
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.OnlineSource
import cz.lastaapps.common.song.domain.model.search.SearchType
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.sources.AgamaDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.lighthousegames.logging.logging

class AgamaDataSourceImpl(
    private val client: HttpClient,
) : AgamaDataSource {

    companion object {
        private val log = logging()
        private fun agamaLink(id: String) = "http://www.agama2000.com/${id.encodeURLParameter()}"
    }

    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> {
        val dtoList = doSearchByEverything(query)
        if (dtoList.isError()) return dtoList.casted()

        val parts = query.removeAccents().split(" ").filter { it.isNotBlank() }
        // get only authors that contain query part in name
        val authors = dtoList.asSuccess().data.asSequence().filter {
            it.name.removeAccents().let { parts.any { part -> it.contains(part) } }
        }.map { Author(it.id, it.name, null, agamaLink(it.id)) }

        val songs = persistentListOf<SearchedSong>().mutate { list ->
            authors.forEach {
                val res = loadSongsForAuthor(it)
                if (res.isError()) return res.casted()
                list.addAll(res.asSuccess().data.results)
            }
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
        val list1 = client.get {
            url("http://www.agama2000.com/api/findByText")
            parameter("text", query)
        }.also { log.i { "Requesting ${it.request.url}" } }.body<List<AgamaInterpretDto>>()
        val list2 = client.get {
            url("http://www.agama2000.com/api/findByText")
            parameter("text", query.removeAccents())
        }.also { log.i { "Requesting ${it.request.url}" } }.body<List<AgamaInterpretDto>>()

        val joined = list1.toMutableList().also { it.addAll(list2) }
        joined.sortBy { it.id }

        // if one author occurred in both searches, join its songs
        val filtered = mutableListOf<AgamaInterpretDto>()
        joined.forEach { item ->
            if (item.id == filtered.lastOrNull()?.id) {
                val last = filtered.removeLast()
                val songs = listOf(item.songs, last.songs).flatten().distinct()
                filtered.add(item.copy(songs = songs))
            } else {
                filtered.add(item)
            }
        }
        return filtered.toResult()
    }

    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> {
        val person = client.get {
            url("http://www.agama2000.com/api/loadSongs")
            parameter("personId", author.id)
        }.also { log.i { "Requesting ${it.request.url}" } }.body<AgamaPersonDto>()

        val songs = person.songs.map { song ->
            SearchedSong(song.id, song.name, author.name, SongType.CHORDS, agamaLink(song.id))
        }.toImmutableList()

        return OnlineSearchResult(OnlineSource.Agama, SearchType.AUTHOR, songs).toResult()
    }

    private val youtubeUrlMatcher = """(https://www\.youtube\.com/watch\?v=.+)$""".toRegex()

    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val fetched = client.get {
            url("http://agama2000.com/api/loadDocument")
            parameter("docId", song.id)
        }.also { log.i { "Requesting ${it.request.url}" } }.body<AgamaSongDetailDto>()

        val ytb = youtubeUrlMatcher.find(fetched.text.lineSequence().lastOrNull() ?: "")?.groupValues?.getOrNull(1)
        val text = (if (ytb == null) fetched.text else fetched.text.replace(ytb, "")).lines().trimLines().joinLines()

        return with(song) {
            Song(id, name, song.author, text, OnlineSource.Agama, link, ytb)
        }.toResult()
    }
}