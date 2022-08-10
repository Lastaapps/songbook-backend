package cz.lastaapps.base.data.supermusic

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.SearchAuthorDataSource
import cz.lastaapps.base.domain.SongErrors
import cz.lastaapps.base.domain.model.Author
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.base.util.bodyAsSafeText
import cz.lastaapps.base.util.removeAccents
import cz.lastaapps.base.util.runCatchingKtor
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

        val html = searchAuthorsRequest(query).getIfSuccess { return it }

        val mainPart = mainFilter.find(html)?.groupValues?.getOrNull(1)
            ?: notFoundRegex.find(html)?.let { return persistentListOf<Author>().toResult() }
            ?: return SongErrors.ParseError.FailedToMatchInterpreterList().toResult()

        return eachInterpreter.findAll(mainPart).map {
            val group = it.groupValues[1]
            val (id, name, songs) = interpreterDetail.find(group)!!.destructured
            Author(id, name, songs.toInt(), "https://supermusic.cz/skupina.php?idskupiny=$id")
        }.toImmutableList().toResult()
    }

    private suspend fun searchAuthorsRequest(query: String): Result<String> = runCatchingKtor {
        client.get {
            url("https://supermusic.cz/najdi.php")
            parameter("fraza", "on")
            parameter("hladane", query.removeAccents())
            parameter("typhladania", "skupina")
        }.also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText().toResult()
    }

    private val matchSongList = """Pridať novú pesničku((?>(?!</table>).)*)</table>""".toRegex(regexOption)
    private val matchNoSongs = """Neboli nájdené žiadne piesne"""
    private val matchSongDetail =
        """<img[^<>]*src="images/(\S+)\.gif"(?>(?!<a).)*<a[^<>]*href="skupina\.php\?idpiesne=(\d+)[^"]*"[^<>]*>([^<]+)<(?>(?!</a>).)*</a>"""
            .toRegex(regexOption)

    override suspend fun loadSongsForAuthor(author: Author): Result<ImmutableList<SearchedSong>> {
        val html = loadSongsForAuthorRequest(author.link).getIfSuccess { return it }
        val main = matchSongList.find(html)?.groupValues?.getOrNull(1)
            ?: Unit.takeIf { html.contains(matchNoSongs) }?.let {
                return persistentListOf<SearchedSong>().toResult()
            }
            ?: return SongErrors.ParseError.FailedToMatchInterpreterSongList().toResult()

        return matchSongDetail.findAll(main).map {
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

            SearchedSong(id, name, author.name, mainStyle)
        }.toImmutableList().toResult()
    }

    private suspend fun loadSongsForAuthorRequest(link: String) = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText().toResult()
    }
}