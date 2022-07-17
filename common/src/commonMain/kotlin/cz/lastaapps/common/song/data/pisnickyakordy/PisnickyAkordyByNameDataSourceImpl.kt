package cz.lastaapps.common.song.data.pisnickyakordy

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.base.toResult
import cz.lastaapps.common.base.util.joinLines
import cz.lastaapps.common.base.util.removeAccents
import cz.lastaapps.common.base.util.trimLines
import cz.lastaapps.common.song.data.AuthorSearchCombine
import cz.lastaapps.common.song.data.pisnickyakordy.model.PisnickyAkordySearchedItemDto
import cz.lastaapps.common.song.domain.SongErrors
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.SongType
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.OnlineSource
import cz.lastaapps.common.song.domain.model.search.SearchType
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.sources.PisnickyAkordyByNameDataSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.lighthousegames.logging.logging

class PisnickyAkordyByNameDataSourceImpl(
    private val client: HttpClient,
) : PisnickyAkordyByNameDataSource {

    companion object {
        private val log = logging()

        fun linkForId(id: String) = "https://pisnicky-akordy.cz$id"
    }

    private val regexOptions = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> {
        val noAccent = client.get { setupUrl(query, true, name = true) }
            .also { log.i { "Requesting ${it.request.url}" } }
        val accent = client.get { setupUrl(query, false, name = true) }
            .also { log.i { "Requesting ${it.request.url}" } }

        val songs = listOf(noAccent, accent).map { response ->
            try {
                response.body<List<PisnickyAkordySearchedItemDto>>()
            } catch (e: Exception) {
                log.e(e) { "Failed to deserialize searched items" }
                return SongErrors.ParseError.FailedToMatchSongList(e).toResult()
            }.map { with(it) { SearchedSong(id, name, author, SongType.UNKNOWN, linkForId(link)) } }
        }.flatten().toSet().toImmutableList()

        return OnlineSearchResult(OnlineSource.PisnickyAkordy, SearchType.NAME, songs).toResult()
    }

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> {
        val noAccent = client.get { setupUrl(query, true, author = true) }
        val accent = client.get { setupUrl(query, false, author = true) }

        return listOf(noAccent, accent).map { response ->
            try {
                response.body<List<PisnickyAkordySearchedItemDto>>()
            } catch (e: Exception) {
                return SongErrors.ParseError.FailedToMatchSongList(e).toResult()
            }.map { with(it) { Author(id, name, null, linkForId(link)) } }
        }.flatten().toSet().toImmutableList().toResult()
    }

    private val authorSongListMatcher =
        """<div[^<>]*id="az"[^<>]*>(?>(?!<ul).)*<ul[^<>]*>((?>(?!</ul>).)*)</ul>((?>(?!</div>).)*)</div>"""
            .toRegex(regexOptions)
    private val authorEachSongMather =
        """<li[^<>]*>(?>(?!<a).)*<a[^<>]*href="((?>(?!").)+)"[^<>]*>(?>(?!<i).)*<i[^<>]*class='((?>(?!').)+)'[^<>]*>(?>(?!<span).)*<span[^<>]*>((?>(?!</span).)*)</span>"""
            .toRegex(regexOptions)

    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> {
        val html = client.get(author.link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText()
        val match = authorSongListMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchInterpreterSongList().toResult()

        val songs = authorEachSongMather.findAll(match).map {
            val (link, type, name) = it.destructured
            val foundType = when {
                type.contains("glyphicon-music") -> SongType.CHORDS
                type.contains("glyphicon-") -> return@map null // work in progress songs
                else -> SongType.TEXT
            }
            SearchedSong(link, name.trim(), author.name, foundType, linkForId(link))
        }.filterNotNull().toImmutableList()

        return OnlineSearchResult(OnlineSource.PisnickyAkordy, SearchType.AUTHOR, songs).toResult()
    }

    private fun HttpRequestBuilder.setupUrl(
        query: String, stripAccent: Boolean,
        name: Boolean = false, author: Boolean = false, album: Boolean = false,
    ) {
        url("https://pisnicky-akordy.cz/index.php")
        parameter("option", "com_lyrics")
        parameter("task", "ajax.display")
        parameter("format", "json")
        parameter("tmpl", "component")
        parameter("q", if (stripAccent) query.removeAccents() else query)
        parameter("interpreters", author)
        parameter("songs", name)
        parameter("albums", album)
        parameter("limit", 128)
    }

    private val songTextMatcher =
        """<div[^<>]* id="songtext"[^<>]*>[^<>]*<pre[^<>]*>((?>(?!</pre).)*)</pre>[^<>]*</div>""".toRegex(regexOptions)

    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val html = client.get(song.link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText()
        val songText = (songTextMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchSongList().toResult())
            .replace("<el[^<>]*>".toRegex(), "")
            .replace("</el", "")
            .replace("<span[^<>]*>".toRegex(), "[")
            .replace("</span>".toRegex(), "]")
            .lines().trimLines().joinLines()

        return with(song) { Song(id, name, author, songText, OnlineSource.PisnickyAkordy, link, null) }.toResult()
    }

    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> =
        AuthorSearchCombine(this).searchSongsByAuthor(query)
}