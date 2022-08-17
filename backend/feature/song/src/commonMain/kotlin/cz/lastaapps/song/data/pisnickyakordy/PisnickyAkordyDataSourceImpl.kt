package cz.lastaapps.song.data.pisnickyakordy

import cz.lastaapps.base.Result
import cz.lastaapps.base.error.SongErrors
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.base.util.removeAccents
import cz.lastaapps.song.data.pisnickyakordy.model.PisnickyAkordySearchedItemDto
import cz.lastaapps.song.domain.model.Author
import cz.lastaapps.song.domain.model.Song
import cz.lastaapps.song.domain.model.SongType
import cz.lastaapps.song.domain.model.search.OnlineSource
import cz.lastaapps.song.domain.model.search.SearchedSong
import cz.lastaapps.song.domain.sources.PisnickyAkordyDataSource
import cz.lastaapps.song.util.joinLines
import cz.lastaapps.song.util.runCatchingKtor
import cz.lastaapps.song.util.runCatchingParse
import cz.lastaapps.song.util.trimLines
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.lighthousegames.logging.logging

class PisnickyAkordyDataSourceImpl(
    private val client: HttpClient,
) : PisnickyAkordyDataSource {

    companion object {
        private val log = logging()

        fun pisnickyAkordyId(id: String) = "https://pisnicky-akordy.cz$id"
    }

    private val regexOptions = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)

    override suspend fun searchByName(query: String): Result<ImmutableList<SearchedSong>> {
        return makeSearchRequest(query, name = true)
            .getIfSuccess { return it }.let { list ->
                coroutineScope {
                    list.map {
                        async {
                            it.body<List<PisnickyAkordySearchedItemDto>>().map {
                                with(it) {
                                    SearchedSong(
                                        id,
                                        name,
                                        author!!,
                                        SongType.UNKNOWN,
                                        OnlineSource.PisnickyAkordy
                                    )
                                }
                            }
                        }
                    }.awaitAll()
                }
            }.flatten().toSet().toImmutableList().toResult()
    }

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> =
        makeSearchRequest(query, author = true)
            .getIfSuccess { return it }
            .map { response ->
                runCatching { response.body<List<PisnickyAkordySearchedItemDto>>() }.getOrElse {
                    return SongErrors.ParseError.FailedToMatchSongList(it).toResult()
                }.map { with(it) { Author(id, name, null, pisnickyAkordyId(link)) } }
            }.flatten().toSet().toImmutableList().toResult()

    private suspend fun makeSearchRequest(
        query: String, name: Boolean = false, author: Boolean = false, album: Boolean = false,
    ): Result<List<HttpResponse>> = runCatchingKtor {
        coroutineScope {
            listOfNotNull(query, query.removeAccents().takeIf { it != query }).map {
                async {
                    client.get { setupUrl(it, name, author, album) }
                        .also { log.i { "Requesting ${it.request.url}" } }
                }
            }.awaitAll().toResult()
        }
    }

    private fun HttpRequestBuilder.setupUrl(
        query: String, name: Boolean = false, author: Boolean = false, album: Boolean = false,
    ) {
        url("https://pisnicky-akordy.cz/index.php")
        parameter("option", "com_lyrics")
        parameter("task", "ajax.display")
        parameter("format", "json")
        parameter("tmpl", "component")
        parameter("q", query)
        parameter("interpreters", author)
        parameter("songs", name)
        parameter("albums", album)
        parameter("limit", 128)
    }

    private val authorSongListMatcher =
        """<div[^<>]*id="az"[^<>]*>(?>(?!<ul).)*<ul[^<>]*>((?>(?!</ul>).)*)</ul>((?>(?!</div>).)*)</div>"""
            .toRegex(regexOptions)
    private val authorEachSongMather =
        """<li[^<>]*>(?>(?!<a).)*<a[^<>]*href="((?>(?!").)+)"[^<>]*>(?>(?!<i).)*<i[^<>]*class='((?>(?!').)+)'[^<>]*>(?>(?!<span).)*<span[^<>]*>((?>(?!</span).)*)</span>"""
            .toRegex(regexOptions)

    override suspend fun loadSongsForAuthor(author: Author): Result<ImmutableList<SearchedSong>> {
        val html = loadSongsForAuthorRequest(author.link).getIfSuccess { return it }
        val match = authorSongListMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchInterpreterSongList().toResult()

        return runCatchingParse {
            authorEachSongMather.findAll(match).map {
                val (link, type, name) = it.destructured
                val foundType = when {
                    type.contains("glyphicon-music") -> SongType.CHORDS
                    type.contains("glyphicon-") -> return@map null // work in progress songs
                    else -> SongType.TEXT
                }
                SearchedSong(link, name.trim(), author.name, foundType, OnlineSource.PisnickyAkordy)
            }.filterNotNull().toImmutableList().toResult()
        }
    }

    private suspend fun loadSongsForAuthorRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

    private val songTextMatcher =
        """<div[^<>]* id="songtext"[^<>]*>[^<>]*<pre[^<>]*>((?>(?!</pre).)*)</pre>[^<>]*</div>""".toRegex(regexOptions)
    private val songNameMatcher =
        """<div[^<>]*id="songheader"[^<>]*>[^<>]*<h1>[^<>]*<a[^<>]*>([^<>]*)</a>[^<>]*</h1>(?>(?!<a).)*<a[^<>]*>([^<>]*)</a>"""
            .toRegex(regexOptions)

    override suspend fun loadSong(id: String): Result<Song> {
        val link = pisnickyAkordyId(id)
        val html = loadSongRequest(link).getIfSuccess { return it }

        val songText = (songTextMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchSongList().toResult())
            .replace("<el[^<>]*>".toRegex(), "")
            .replace("</el", "")
            .replace("<span[^<>]*>".toRegex(), "[")
            .replace("</span>".toRegex(), "]")
            .lines().trimLines().joinLines()

        return runCatchingParse {
            val (name, author) = songNameMatcher.find(html)?.destructured
                ?: return SongErrors.ParseError.FailedToMatchSongNameOrAuthor().toResult()

            Song(id, name, author, songText, OnlineSource.PisnickyAkordy, link, null).toResult()
        }
    }

    private suspend fun loadSongRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

    override suspend fun searchSongsByAuthor(query: String): Result<ImmutableList<SearchedSong>> =
        cz.lastaapps.song.data.AuthorSearchCombine(this).searchSongsByAuthor(query)
}