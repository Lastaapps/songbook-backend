package cz.lastaapps.common.song.data.brnkni

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.base.asSuccess
import cz.lastaapps.common.base.getIfSuccess
import cz.lastaapps.common.base.toResult
import cz.lastaapps.common.base.util.joinLines
import cz.lastaapps.common.base.util.removeAccents
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
        val html1 = commonRequest(query, stripAccents = false, searchForSongs = true)
        val html2 =
            if (query.removeAccents() != query) commonRequest(
                query,
                stripAccents = true,
                searchForSongs = true
            ) else null

        val songs = listOfNotNull(html1, html2).map { html ->
            val main = songListMatcher.find(html.getIfSuccess { return it })?.groupValues?.getOrNull(1)
                ?: Unit.takeIf { html.asSuccess().data.contains(songListEmpty) }?.let { return@map emptyList() }
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

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> {
        val html1 = commonRequest(query, stripAccents = false, searchForSongs = false)
        val html2 =
            if (query.removeAccents() != query) commonRequest(
                query,
                stripAccents = true,
                searchForSongs = false
            ) else null

        return listOfNotNull(html1, html2).map { html ->
            val main = authorListMatcher.find(html.getIfSuccess { return it })?.groupValues?.getOrNull(1)
                ?: Unit.takeIf { html.asSuccess().data.contains(authorListEmpty) }?.let { return@map emptyList() }
                ?: return SongErrors.ParseError.FailedToMatchInterpreterList().toResult()

            authorItemMatcher.findAll(main).map { match ->
                val (authorLink, authorName, songNumber) = match.destructured
                Author(authorLink, authorName, songNumber.toIntOrNull(), brnkniLink(authorLink))
            }.toPersistentList()
        }.flatten().toPersistentList().toResult()
    }

    private suspend fun commonRequest(query: String, stripAccents: Boolean, searchForSongs: Boolean): Result<String> {
        return client.get {
            url("https://www.brnkni.cz/hledat-pisen")
            parameter("q", if (stripAccents) query.removeAccents() else query)
            parameter("w", if (searchForSongs) "pisen" else "interpret")
            parameter("do", "searchForm-submit")
        }.also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

    private val authorSongAreaMatcher =
        """<ul[^<>]*class="songs"[^<>]*>((?>(?!</ul>).)*)</ul>""".toRegex(regexOption)
    private val authorSongItemMatcher =
        """<li[^<>]*>[^<>]*<div[^<>]*class="about"[^<>]*>[^<>]*<h3><a[^<>]*href="([^"]*)"[^<>]*>([^<>]*)</a>[^<>]*</h3>[^<>]*<p[^<>]*class="text"[^<>]*>[^<>]*</p>[^<>]*</div>[^<>]*<div[^<>]*>[^<>]*<p>([^<>]*)</p>"""
            .toRegex(regexOption)

    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> {
        val html = client.get(author.link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText()
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

    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> =
        AuthorSearchCombine(this).searchSongsByAuthor(query)

    private val songTextMatcher =
        """<div[^<>]*class="[^"]*text[^"]*"[^<>]*>((?>(?!</div>).)*)</div>""".toRegex(regexOption)

    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val html = client.get(song.link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText()
        val text = (songTextMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchSongText().toResult())
            .replace("""<span[^<>]*>""".toRegex(), "[")
            .replace("</span>", "]")
            .replace("""^<p[^<>]*>""".toRegex(), "")
            .replace("""</p>$""".toRegex(), "")
            .lines().trimLines().joinLines()

        return with(song) { Song(id, name, author, text, OnlineSource.Brnkni, link, null) }.toResult()
    }
}