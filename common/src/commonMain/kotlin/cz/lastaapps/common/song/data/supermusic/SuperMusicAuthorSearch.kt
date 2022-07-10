package cz.lastaapps.common.song.data.supermusic

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.base.toResult
import cz.lastaapps.common.base.toSuccess
import cz.lastaapps.common.base.util.bodyAsSafeText
import cz.lastaapps.common.base.util.removeAccents
import cz.lastaapps.common.song.domain.SearchAuthorDataSource
import cz.lastaapps.common.song.domain.SongErrors
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.model.search.SongType
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.lighthousegames.logging.logging

internal class SuperMusicAuthorSearch(
    private val client: HttpClient,
    private val songComparator: Comparator<SearchedSong>,
) : SearchAuthorDataSource {

    companion object {
        private val log = logging()
    }

    private val regexOption = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)

    private val mainFilter = """Prebehlo vyhľadávanie slov:.*<table>((?>(?!</table>).)*)</table>""".toRegex(regexOption)
    private val notFoundRegex =
        """Počet nájdených interpretov s '.*' v názve: (?>(?!<[^<>]*br[^<>]*>).)*0(?>(?!<[^<>]*br[^<>]*>).)*<[^<>]*br[^<>]*>"""
            .toRegex(regexOption)
    private val eachInterpreter = """<tr><td[^<>]*>((?>(?!</tr>).)+)</td></tr>""".toRegex(regexOption)
    private val interpreterDetail =
        """</td>(?>(?!</td>).)*<td>(?>(?!<a).)*<a href="skupina\.php\?idskupiny=(\d+)"[^/]*>([^<]*)(?>(?!\().)*\(piesní: (\d+)\)"""
            .toRegex(regexOption)

    override suspend fun searchAuthors(query: String): Result<List<Author>> {
        val minQuery = SuperMusicDataSourceImpl.minQueryLength
        if (query.length < minQuery) return SongErrors.ParseError.ToShortQuery(minQuery).toResult()

        val html = client.get {
            searchUrl(query)
        }.also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText()

        val mainPart = mainFilter.find(html)?.groupValues?.getOrNull(1)
            ?: notFoundRegex.find(html)?.let { return emptyList<Author>().toSuccess() }
            ?: return SongErrors.ParseError.FailedToMatchInterpreterList().toResult()

        return eachInterpreter.findAll(mainPart).map {
            val group = it.groupValues[1]
            val (id, name, songs) = interpreterDetail.find(group)!!.destructured
            Author(id, name, songs.toInt(), "https://supermusic.cz/skupina.php?idskupiny=$id")
        }.toList().toSuccess()
    }

    private fun HttpRequestBuilder.searchUrl(query: String) {
        url("https://supermusic.cz/najdi.php")
        parameter("fraza", "on")
        parameter("hladane", query.removeAccents())
        parameter("typhladania", "skupina")
    }

    private val matchSongList = """Pridať novú pesničku((?>(?!</table>).)*)</table>""".toRegex(regexOption)
    private val matchNoSongs = """Neboli nájdené žiadne piesne"""
    private val matchSongDetail =
        """<img[^<>]*src="images/(\S+)\.gif"(?>(?!<a).)*<a[^<>]*href="skupina\.php\?idpiesne=(\d+)[^"]*"[^<>]*>([^<]+)<(?>(?!</a>).)*</a>"""
            .toRegex(regexOption)

    override suspend fun loadSongsForAuthor(author: Author): Result<List<SearchedSong>> {
        val html = client.get(author.link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText()

        val main = matchSongList.find(html)?.groupValues?.getOrNull(1)
            ?: Unit.takeIf { html.contains(matchNoSongs) }?.let { return emptyList<SearchedSong>().toSuccess() }
            ?: return SongErrors.ParseError.FailedToMatchInterpreterSongList().toResult()

        return matchSongDetail.findAll(main).map {
            val (type, id, name) = it.destructured

            val styleList = listOf(
                "texty" to SongType.TEXT, "akordy" to SongType.CHORDS,
                "taby" to SongType.TAB, "melodie" to SongType.NOTES,
                "preklady" to SongType.TRANSLATION,
            ).asSequence().filter { type.contains(it.first) }.map { it.second }.toSet()

            SearchedSong(id, name, author.name, styleList, "https://supermusic.cz/skupina.php?idpiesne=$id")
        }.sortedWith(songComparator).toList().toSuccess()
    }
}