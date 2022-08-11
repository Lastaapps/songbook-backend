package cz.lastaapps.song.data.zpevniksakordy

import cz.lastaapps.base.Result
import cz.lastaapps.base.error.SongErrors
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.song.domain.model.Song
import cz.lastaapps.song.domain.model.SongType
import cz.lastaapps.song.domain.model.search.OnlineSource
import cz.lastaapps.song.domain.model.search.SearchedSong
import cz.lastaapps.song.domain.sources.ZpevnikSAkordyDataSource
import cz.lastaapps.song.util.joinLines
import cz.lastaapps.song.util.runCatchingKtor
import cz.lastaapps.song.util.runCatchingParse
import cz.lastaapps.song.util.trimLines
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.lighthousegames.logging.logging

class ZpevnikSAkordyDataSourceImpl(
    private val client: HttpClient,
) : ZpevnikSAkordyDataSource {

    companion object {
        private val log = logging()

        private fun zpevnikSAkordyLink(id: String) = "http://zpevnik.wz.cz/index.php?id=$id"
    }

    override suspend fun searchByName(query: String): Result<ImmutableList<SearchedSong>> =
        commonRequest(name = query).getIfSuccess { return it }.parseSongList().getIfSuccess { return it }.toResult()

//    override suspend fun searchByText(query: String): Result<ImmutableList<SearchedSong>> =
//        commonRequest(text = query).getIfSuccess { return it }.parseSongList().getIfSuccess { return it }.toResult()

    override suspend fun searchSongsByAuthor(query: String): Result<ImmutableList<SearchedSong>> =
        commonRequest(author = query).getIfSuccess { return it }.parseSongList().getIfSuccess { return it }.toResult()

    private suspend fun commonRequest(
        name: String? = null, text: String? = null, author: String? = null
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
    private val mainMatcher = """<div[^<>]* class="songy"[^<>]*>(.*)</div>""".toRegex(regexOption)
    private val songEmptyMatcher = """Nebyla nalezena žádná písnička"""
    private val itemsMatcher =
        """<li[^<>]*><a[^<>]* href="\?id=(\d+)"[^<>]*>([^<>]+)</a>[^<>]*\(<a[^<>]* href="\?id=(\d+)"[^<>]*>([^<]*)</a>\)</li>"""
            .toRegex(regexOption)

    private fun String.parseSongList(): Result<ImmutableList<SearchedSong>> {
        val main = mainMatcher.find(this)?.groupValues?.getOrNull(1)
            ?: Unit.takeIf { this.contains(songEmptyMatcher) }
                ?.let { return persistentListOf<SearchedSong>().toResult() }
            ?: return SongErrors.ParseError.FailedToMatchSongList().toResult()

        return runCatchingParse {
            itemsMatcher.findAll(main).map { match ->
                val (songId, songName, _, authorName) = match.destructured
                SearchedSong(
                    songId, songName, authorName.trimAuthorDash(), SongType.UNKNOWN, OnlineSource.ZpevnikSAkordy,
                )
            }.toImmutableList().toResult()
        }
    }


    private val songTextMatcher = """<div[^<>]* class="song"[^<>]*>((?>(?!</div>).)*)</div>""".toRegex(regexOption)
    private val songNameMatcher =
        """<h1>([^()]*)\(<a[^<>]*>([^<>]*)</a>[^<>]*\)[^<>]*</h1>[^<>]*<div[^<>]*class="song"[^<>]*>"""
            .toRegex(regexOption)
    private val youtubeMatcher =
        """www\.youtube\.com/embed/([a-zA-Z0-9-_]+)""".toRegex(regexOption)

    override suspend fun loadSong(id: String): Result<Song> {
        val link = zpevnikSAkordyLink(id)
        val html = loadSongRequest(link).getIfSuccess { return it }

        return runCatchingParse {
            val text = songTextMatcher.find(html)?.groupValues?.get(1)
                ?.lines()?.trimLines()?.joinLines()
                ?: return SongErrors.ParseError.FailedToMatchSongText().toResult()

            val (name, author) = songNameMatcher.find(html)?.destructured
                ?: return SongErrors.ParseError.FailedToMatchSongNameOrAuthor().toResult()

            val video =
                youtubeMatcher.find(html)?.groupValues?.getOrNull(1)?.let { "https://www.youtube.com/watch?v=$it" }

            Song(
                id, name, author.takeIf { it.isNotBlank() }?.trimAuthorDash(),
                text, OnlineSource.ZpevnikSAkordy, link, video,
            ).toResult()
        }
    }

    private suspend fun loadSongRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Retrieving ${it.request.url}" } }.bodyAsText().toResult()
    }

    private fun String.trimAuthorDash() = trim().removePrefix("-").trim()
}