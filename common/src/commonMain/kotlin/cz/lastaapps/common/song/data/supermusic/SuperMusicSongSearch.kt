package cz.lastaapps.common.song.data.supermusic

import cz.lastaapps.common.base.*
import cz.lastaapps.common.base.util.bodyAsSafeText
import cz.lastaapps.common.base.util.removeAccents
import cz.lastaapps.common.song.domain.SearchSongDataSource
import cz.lastaapps.common.song.domain.SongErrors
import cz.lastaapps.common.song.domain.model.search.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.lighthousegames.logging.logging

internal class SuperMusicSongSearch(
    private val client: HttpClient,
    private val songComparator: Comparator<SearchedSong>,
) : SearchSongDataSource {

    companion object {
        private val log = logging()
    }

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> {
        val minQuery = SuperMusicDataSourceImpl.minQueryLength
        if (query.length < minQuery) return SongErrors.ParseError.ToShortQuery(minQuery).toResult()

        val response = client.get {
            searchUrl(query, true)
        }.also { log.i { "Requesting ${it.request.url}" } }

        val data = response.parse()
        return data.get()?.let {
            OnlineSearchResult(OnlineSource.SuperMusicSk, setOf(SearchType.NAME), it).toSuccess()
        } ?: data.casted()
    }

    override suspend fun searchByText(query: String): Result<OnlineSearchResult> {
        val minQuery = SuperMusicDataSourceImpl.minQueryLength
        if (query.length < minQuery) return SongErrors.ParseError.ToShortQuery(minQuery).toResult()

        val response = client.get {
            searchUrl(query, false)
        }.also { log.i { "Requesting ${it.request.url}" } }

        val data = response.parse()
        return data.get()?.let {
            OnlineSearchResult(OnlineSource.SuperMusicSk, setOf(SearchType.TEXT), it).toSuccess()
        } ?: data.casted()
    }

    private fun HttpRequestBuilder.searchUrl(query: String, isName: Boolean) {
        url("https://supermusic.cz/najdi.php")
        parameter("fraza", "on")
        parameter("hladane", query.removeAccents())
        parameter("typhladania", if (isName) "piesen" else "textpiesen")
    }

    private val regexOption = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
    private val songListRegex =
        """<table[^<>]*>(?>(?!</table>).)*Prebehlo vyhľadávanie slov:((?>(?!</table>).)*)</table>""".toRegex(regexOption)
    private val songFindIndividual =
        """<a[^<>]* href="[^"]*idpiesne=(\d+)[^"]*"[^<>]*><b>([^<>]*)</b></a> - ([^<>]+) \(<a[^<>]*>([^<>]*)</a>\)"""
            .toRegex(regexOption)

    private suspend fun HttpResponse.parse(): Result<List<SearchedSong>> {
        log.i { headers }

        val text = bodyAsSafeText()
        val selectionOnly = songListRegex.find(text)?.groupValues?.getOrNull(0)
            ?: return SongErrors.ParseError.FailedToMatchSongList(Throwable()).toResult()

        return songFindIndividual.findAll(selectionOnly).map { result ->
            val (id, name, styles, author) = result.destructured

            val styleList = listOf(
                "text" to SongType.TEXT, "akordy" to SongType.CHORDS,
                "taby" to SongType.TAB, "melodia" to SongType.NOTES,
                "preklad" to SongType.TRANSLATION,
            ).asSequence().filter { styles.contains(it.first) }.map { it.second }.toSet()

            SearchedSong(id, name, author, styleList, "https://supermusic.cz/skupina.php?idpiesne=$id")
        }.toList().sortedWith(songComparator).toSuccess()
    }
}