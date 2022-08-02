package cz.lastaapps.base.data.zpevniksakordy

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.SongErrors
import cz.lastaapps.base.domain.model.Song
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.OnlineSearchResult
import cz.lastaapps.base.domain.model.search.OnlineSource
import cz.lastaapps.base.domain.model.search.SearchType
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.domain.sources.ZpevnikSAkordyByNameDataSource
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.base.util.joinLines
import cz.lastaapps.base.util.runCatchingKtor
import cz.lastaapps.base.util.trimLines
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.lighthousegames.logging.logging

class ZpevnikSAkordyByNameDataSourceImpl(
    private val client: HttpClient,
) : ZpevnikSAkordyByNameDataSource {

    companion object {
        private val log = logging()
    }

    override suspend fun searchByName(query: String): Result<OnlineSearchResult> {
        val songs = commonRequest(name = query).getIfSuccess { return it }.parseSongList().getIfSuccess { return it }
        return OnlineSearchResult(OnlineSource.ZpevnikSAkordy, SearchType.NAME, songs).toResult()
    }

//    override suspend fun searchByText(query: String): Result<OnlineSearchResult> {
//        val songs = commonRequest(text = query).getIfSuccess { return it }.parseSongList().getIfSuccess { return it }
//        return OnlineSearchResult(OnlineSource.ZpevnikSAkordy, SearchType.TEXT, songs).toResult()
//    }

    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> {
        val songs = commonRequest(author = query).getIfSuccess { return it }.parseSongList().getIfSuccess { return it }
        return OnlineSearchResult(OnlineSource.ZpevnikSAkordy, SearchType.AUTHOR, songs).toResult()
    }

    private suspend fun commonRequest(
        name: String? = null,
        text: String? = null,
        author: String? = null
    ): Result<String> = runCatchingKtor {
        client.get {
            url("http://www.zpevnik.wz.cz/index.php")
            name?.let { parameter("nazev", it) }
            text?.let { parameter("slova", it) }
            author?.let { parameter("autor", it) }
            parameter("pg", "vysledky_vyhledavani")
        }.also { log.i { "Retrieving ${it.request.url}" } }.bodyAsText().toResult()
    }

    private val regexOption = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
    private val mainFilter = """<div[^<>]* class="songy"[^<>]*>(.*)</div>""".toRegex(regexOption)
    private val itemsFilter =
        """<li[^<>]*><a[^<>]* href="\?id=(\d+)"[^<>]*>([^<>]+)</a>[^<>]*\(<a[^<>]* href="\?id=(\d+)"[^<>]*>([^<]*)</a>\)</li>"""
            .toRegex(regexOption)

    private fun String.parseSongList(): Result<ImmutableList<SearchedSong>> {
        val main = mainFilter.find(this)?.groupValues?.getOrNull(1)
            ?: Unit.takeIf { this.contains(songEmptySearch) }
                ?.let { return persistentListOf<SearchedSong>().toResult() }
            ?: return SongErrors.ParseError.FailedToMatchSongList().toResult()

        return itemsFilter.findAll(main).map { match ->
            val (songId, songName, _, authorName) = match.destructured
            SearchedSong(
                songId,
                songName,
                authorName,
                SongType.UNKNOWN,
                "http://zpevnik.wz.cz/index.php?id=$songId"
            )
        }.toImmutableList().toResult()
    }


    private val songTextMatch = """<div[^<>]* class="song"[^<>]*>((?>(?!</div>).)*)</div>""".toRegex(regexOption)
    private val songEmptySearch = """Nebyla nalezena žádná písnička"""
    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val html = loadSongRequest(song.link).getIfSuccess { return it }

        val text = songTextMatch.find(html)?.groupValues?.get(1)
            ?.lines()?.trimLines()?.joinLines()
            ?: return SongErrors.ParseError.FailedToMatchSongText().toResult()

        return with(song) { Song(id, name, author, text, OnlineSource.ZpevnikSAkordy, link, null) }.toResult()
    }

    private suspend fun loadSongRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Retrieving ${it.request.url}" } }.bodyAsText().toResult()
    }
}