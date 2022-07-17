package cz.lastaapps.common.song.data.zpevniksakordy

import cz.lastaapps.common.base.*
import cz.lastaapps.common.base.util.joinLines
import cz.lastaapps.common.base.util.trimLines
import cz.lastaapps.common.song.domain.SongErrors
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.SongType
import cz.lastaapps.common.song.domain.model.search.OnlineSearchResult
import cz.lastaapps.common.song.domain.model.search.OnlineSource
import cz.lastaapps.common.song.domain.model.search.SearchType
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.sources.ZpevnikSAkordyByNameDataSource
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
        val songs = client.get {
            setupUrl(name = query)
        }.also { log.i { "Retrieving ${it.request.url}" } }
            .bodyAsText().parseSongList()
        if (songs.isError()) return songs.casted()
        return OnlineSearchResult(OnlineSource.ZpevnikSAkordy, SearchType.NAME, songs.asSuccess().data).toResult()
    }

    override suspend fun searchByText(query: String): Result<OnlineSearchResult> {
        val songs = client.get {
            setupUrl(text = query)
        }.also { log.i { "Retrieving ${it.request.url}" } }
            .bodyAsText().parseSongList()
        if (songs.isError()) return songs.casted()
        return OnlineSearchResult(OnlineSource.ZpevnikSAkordy, SearchType.TEXT, songs.asSuccess().data).toResult()
    }

    override suspend fun searchSongsByAuthor(query: String): Result<OnlineSearchResult> {
        val songs = client.get {
            setupUrl(author = query)
        }.also { log.i { "Retrieving ${it.request.url}" } }
            .bodyAsText().parseSongList()
        if (songs.isError()) return songs.casted()
        return OnlineSearchResult(OnlineSource.ZpevnikSAkordy, SearchType.AUTHOR, songs.asSuccess().data).toResult()
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
                authorName.takeIf { it.isNotBlank() },
                SongType.UNKNOWN,
                "http://zpevnik.wz.cz/index.php?id=$songId"
            )
        }.toImmutableList().toResult()
    }

    private fun HttpRequestBuilder.setupUrl(name: String? = null, text: String? = null, author: String? = null) {
        url("http://www.zpevnik.wz.cz/index.php")
        name?.let { parameter("nazev", it) }
        text?.let { parameter("slova", it) }
        author?.let { parameter("autor", it) }
        parameter("pg", "vysledky_vyhledavani")
    }

    private val songTextMatch = """<div[^<>]* class="song"[^<>]*>((?>(?!</div>).)*)</div>""".toRegex(regexOption)
    private val songEmptySearch = """Nebyla nalezena žádná písnička"""
    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val html = client.get(song.link).also { log.i { "Retrieving ${it.request.url}" } }.bodyAsText()
        val text = songTextMatch.find(html)?.groupValues?.get(1)
            ?.lines()?.trimLines()?.joinLines()
            ?: return SongErrors.ParseError.FailedToMatchSongText().toResult()
        return with(song) { Song(id, name, author, text, OnlineSource.ZpevnikSAkordy, link, null) }.toResult()
    }
}