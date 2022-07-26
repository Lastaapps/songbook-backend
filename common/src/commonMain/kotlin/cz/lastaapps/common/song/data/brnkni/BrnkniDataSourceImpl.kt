package cz.lastaapps.common.song.data.brnkni

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.base.getIfSuccess
import cz.lastaapps.common.base.toResult
import cz.lastaapps.common.base.util.joinLines
import cz.lastaapps.common.base.util.removeAccents
import cz.lastaapps.common.base.util.runCatchingKtor
import cz.lastaapps.common.base.util.trimLines
import cz.lastaapps.common.song.data.AuthorSearchCombine
import cz.lastaapps.common.song.domain.SongErrors
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.SongType
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.OnlineSource
import cz.lastaapps.common.song.domain.model.search.SearchType
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.sources.BrnkniDataSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.lighthousegames.logging.logging

internal class BrnkniDataSourceImpl(
    private val client: HttpClient,
) : BrnkniDataSource {

    companion object {
        private val log = logging()
        fun brnkniLink(id: String) = "https://www.brnkni.cz${id.encodeURLPath()}"
    }

    private val regexOption = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
    private val songListMatcher =
        """<ul class="songs">((?>(?!</ul>).)*)</ul>""".toRegex(regexOption)
    private val songItemMatcher =
        """<li[^<>]*>[^<>]*<div[^<>]*class="about"[^<>]*>[^<>]*<h3><a[^<>]*href="([^"]*)"[^<>]*>([^<>]*)</a>[^<>]*</h3>[^<>]*<p[^<>]*class="author"[^<>]*>[^<>]*<a[^<>]*href="[^"]*"[^<>]*>([^<>]*)</a>[^<>]*</p>[^<>]*<p[^<>]*class="text"[^<>]*>[^<>]*</p>[^<>]*</div>[^<>]*<div[^<>]*>[^<>]*<p>([^<>]*)</p>"""
            .toRegex(regexOption)
    private val songListEmpty = """Bohužel, na hledaný výraz nebyla nalezena žádná píseň"""

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> {
        val songs = commonRequest(query, true)
            .getIfSuccess { return it }
            .map { html ->

                val main = songListMatcher.find(html)?.groupValues?.getOrNull(1)
                    ?: Unit.takeIf { html.contains(songListEmpty) }?.let { return@map emptyList() }
                    ?: return SongErrors.ParseError.FailedToMatchSongList().toResult()

                songItemMatcher.findAll(main).map { match ->
                    val (songLink, songName, authorName, type) = match.destructured
                    val songType = when (type) {
                        "Akordy" -> SongType.CHORDS
                        "Taby" -> SongType.TAB
                        "Akordy + taby" -> SongType.CHORDS_AND_TAB
                        else -> SongType.UNKNOWN
                    }
                    SearchedSong(songLink, songName, authorName, songType, brnkniLink(songLink))
                }.toPersistentList()
            }.flatten().toPersistentList()

        return OnlineSearchResult(OnlineSource.Brnkni, SearchType.NAME, songs).toResult()
    }

    private val authorListMatcher =
        """<ul class="list">((?>(?!</ul>).)*)</ul>""".toRegex(regexOption)
    private val authorItemMatcher =
        """<li[^<>]*>[^<>]*<h2>[^<>]*<a[^<>]*href="([^"]*)"[^<>]*>([^<>]*)</a>[^<>]*</h2>[^<>]*<p[^<>]*>(\d+)[^<>]*</p>"""
            .toRegex(regexOption)
    private val authorListEmpty = """Bohužel, na hledaný výraz nebyl nalezen žádný interpret"""

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> =
        commonRequest(query, searchForSongs = false)
            .getIfSuccess { return it }
            .map { html ->
                val main = authorListMatcher.find(html)?.groupValues?.getOrNull(1)
                    ?: Unit.takeIf { html.contains(authorListEmpty) }?.let { return@map emptyList() }
                    ?: return SongErrors.ParseError.FailedToMatchInterpreterList().toResult()

                authorItemMatcher.findAll(main).map { match ->
                    val (authorLink, authorName, songNumber) = match.destructured
                    Author(authorLink, authorName, songNumber.toIntOrNull(), brnkniLink(authorLink))
                }.toPersistentList()
            }.flatten().toPersistentList().toResult()

    private suspend fun commonRequest(query: String, searchForSongs: Boolean): Result<List<String>> = coroutineScope {
        runCatchingKtor {
            listOfNotNull(query, query.removeAccents().takeIf { it != query }).map {
                async {
                    client.get {
                        url("https://www.brnkni.cz/hledat-pisen")
                        parameter("q", query)
                        parameter("w", if (searchForSongs) "pisen" else "interpret")
                        parameter("do", "searchForm-submit")
                    }.also { log.i { "Requesting ${it.request.url}" } }.bodyAsText()
                }
            }.awaitAll().toResult()
        }
    }

    private val authorSongAreaMatcher =
        """<ul[^<>]*class="songs"[^<>]*>((?>(?!</ul>).)*)</ul>""".toRegex(regexOption)
    private val authorSongItemMatcher =
        """<li[^<>]*>[^<>]*<div[^<>]*class="about"[^<>]*>[^<>]*<h3><a[^<>]*href="([^"]*)"[^<>]*>([^<>]*)</a>[^<>]*</h3>[^<>]*<p[^<>]*class="text"[^<>]*>[^<>]*</p>[^<>]*</div>[^<>]*<div[^<>]*>[^<>]*<p>([^<>]*)</p>"""
            .toRegex(regexOption)

    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> {
        val html = loadSongsForAuthorRequest(author.link).getIfSuccess { return it }

        val main = authorSongAreaMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchInterpreterSongList().toResult()

        val songs = authorSongItemMatcher.findAll(main).map { match ->
            val (link, name, type) = match.destructured
            val songType = when (type) {
                "Akordy" -> SongType.CHORDS
                "Taby" -> SongType.TAB
                "Akordy + taby" -> SongType.CHORDS_AND_TAB
                else -> SongType.UNKNOWN
            }
            SearchedSong(link, name, author.name, songType, brnkniLink(link))
        }.toImmutableList()

        return OnlineSearchResult(OnlineSource.Brnkni, SearchType.AUTHOR, songs).toResult()
    }

    private suspend fun loadSongsForAuthorRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> =
        AuthorSearchCombine(this).searchSongsByAuthor(query)

    private val songTextMatcher =
        """<div[^<>]*class="[^"]*text[^"]*"[^<>]*>((?>(?!</div>).)*)</div>""".toRegex(regexOption)

    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val html = loadSongRequest(song.link).getIfSuccess { return it }
        val text = (songTextMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchSongText().toResult())
            .replace("""<span[^<>]*>""".toRegex(), "[")
            .replace("</span>", "]")
            .replace("""^<p[^<>]*>""".toRegex(), "")
            .replace("""</p>$""".toRegex(), "")
            .lines().trimLines().joinLines()

        return with(song) { Song(id, name, author, text, OnlineSource.Brnkni, link, null) }.toResult()
    }

    private suspend fun loadSongRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

}