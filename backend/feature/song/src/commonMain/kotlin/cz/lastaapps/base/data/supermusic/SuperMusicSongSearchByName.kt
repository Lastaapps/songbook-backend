package cz.lastaapps.base.data.supermusic

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.SearchSongByNameDataSource
import cz.lastaapps.base.domain.SearchSongByTextDataSource
import cz.lastaapps.base.domain.SongErrors
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.OnlineSearchResult
import cz.lastaapps.base.domain.model.search.OnlineSource
import cz.lastaapps.base.domain.model.search.SearchType
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
import kotlinx.collections.immutable.toPersistentList
import org.lighthousegames.logging.logging

internal class SuperMusicSongSearchByName(
    private val client: HttpClient,
) : SearchSongByNameDataSource, SearchSongByTextDataSource {

    companion object {
        private val log = logging()
    }

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> {
        val minQuery = SuperMusicByNameDataSourceImpl.minQueryLength
        if (query.length < minQuery) return SongErrors.ToShortQuery(minQuery).toResult()

        val response = commonRequest(query, true).getIfSuccess { return it }

        val data = response.parse().getIfSuccess { return it }
        return OnlineSearchResult(OnlineSource.SuperMusic, SearchType.NAME, data).toResult()
    }

    override suspend fun searchByText(query: String): Result<OnlineSearchResult> {
        val minQuery = SuperMusicByNameDataSourceImpl.minQueryLength
        if (query.length < minQuery) return SongErrors.ToShortQuery(minQuery).toResult()

        val response = commonRequest(query, false).getIfSuccess { return it }

        val data = response.parse().getIfSuccess { return it }
        return OnlineSearchResult(OnlineSource.SuperMusic, SearchType.TEXT, data).toResult()
    }

    private suspend fun commonRequest(query: String, isName: Boolean): Result<HttpResponse> = runCatchingKtor {
        client.get {
            url("https://supermusic.cz/najdi.php")
            parameter("fraza", "on")
            parameter("hladane", query.removeAccents())
            parameter("typhladania", if (isName) "piesen" else "textpiesen")
        }.also { log.i { "Requesting ${it.request.url}" } }.toResult()
    }

    private val regexOption = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
    private val songListRegex =
        """<table[^<>]*>(?>(?!</table>).)*Prebehlo vyhľadávanie slov:((?>(?!</table>).)*)</table>""".toRegex(regexOption)
    private val songFindIndividual =
        """<a[^<>]* href="[^"]*idpiesne=(\d+)[^"]*"[^<>]*><b>([^<>]*)</b></a> - ([^<>]+) \(<a[^<>]*>([^<>]*)</a>\)"""
            .toRegex(regexOption)

    private suspend fun HttpResponse.parse(): Result<ImmutableList<SearchedSong>> {
        log.i { headers }

        val text = bodyAsSafeText()
        val selectionOnly = songListRegex.find(text)?.groupValues?.getOrNull(0)
            ?: return SongErrors.ParseError.FailedToMatchSongList(Throwable()).toResult()

        return songFindIndividual.findAll(selectionOnly).map { result ->
            val (id, name, type, author) = result.destructured

            val mainStyle = when {
                type.contains("akordy") -> SongType.CHORDS
                type.contains("texty") -> SongType.TEXT
                type.contains("taby") -> SongType.TAB
                type.contains("melodie") -> SongType.NOTES
                type.contains("preklady") -> SongType.TRANSLATION
                else -> SongType.UNKNOWN
            }

            SearchedSong(id, name, author, mainStyle, "https://supermusic.cz/skupina.php?idpiesne=$id")
        }.toPersistentList().toResult()
    }
}