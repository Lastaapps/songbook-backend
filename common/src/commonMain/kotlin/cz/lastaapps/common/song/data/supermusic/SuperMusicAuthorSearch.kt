package cz.lastaapps.common.song.data.supermusic

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.base.toResult
import cz.lastaapps.common.base.util.bodyAsSafeText
import cz.lastaapps.common.base.util.removeAccents
import cz.lastaapps.common.song.domain.SearchAuthorDataSource
import cz.lastaapps.common.song.domain.SongErrors
import cz.lastaapps.common.song.domain.model.Author
import cz.lastaapps.common.song.domain.model.SongType
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.OnlineSource
import cz.lastaapps.common.song.domain.model.search.SearchType
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.lighthousegames.logging.logging

internal class SuperMusicAuthorSearch(
    private val client: HttpClient,
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

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> {
        val minQuery = SuperMusicByNameDataSourceImpl.minQueryLength
        if (query.length < minQuery) return SongErrors.ToShortQuery(minQuery).toResult()

        val html = client.get {
            searchUrl(query)
        }.also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText()

        val mainPart = mainFilter.find(html)?.groupValues?.getOrNull(1)
            ?: notFoundRegex.find(html)?.let { return persistentListOf<Author>().toResult() }
            ?: return SongErrors.ParseError.FailedToMatchInterpreterList().toResult()

        return eachInterpreter.findAll(mainPart).map {
            val group = it.groupValues[1]
            val (id, name, songs) = interpreterDetail.find(group)!!.destructured
            Author(id, name, songs.toInt(), "https://supermusic.cz/skupina.php?idskupiny=$id")
        }.toImmutableList().toResult()
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

    override suspend fun loadSongsForAuthor(author: Author): Result<OnlineSearchResult> {
        val html = client.get(author.link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText()

        val main = matchSongList.find(html)?.groupValues?.getOrNull(1)
            ?: Unit.takeIf { html.contains(matchNoSongs) }?.let {
                return OnlineSearchResult(
                    OnlineSource.PisnickyAkordy, SearchType.AUTHOR, persistentListOf(),
                ).toResult()
            }
            ?: return SongErrors.ParseError.FailedToMatchInterpreterSongList().toResult()

        val songs = matchSongDetail.findAll(main).map {
            val (type, id, name) = it.destructured

            // Order is important, song with "akordy a texty" should be matched as CHORDS, but would also match TEXT
            val mainStyle = when {
                type.contains("akordy") -> SongType.CHORDS
                type.contains("texty") -> SongType.TEXT
                type.contains("taby") -> SongType.TAB
                type.contains("melodie") -> SongType.NOTES
                type.contains("preklady") -> SongType.TRANSLATION
                else -> SongType.UNKNOWN
            }

            SearchedSong(id, name, author.name, mainStyle, "https://supermusic.cz/skupina.php?idpiesne=$id")
        }.toImmutableList()

        return OnlineSearchResult(OnlineSource.PisnickyAkordy, SearchType.AUTHOR, songs).toResult()
    }
}