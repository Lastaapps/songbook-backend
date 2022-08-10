package cz.lastaapps.base.data.velkyzpevnik

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.SongErrors
import cz.lastaapps.base.domain.model.Author
import cz.lastaapps.base.domain.model.Song
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.OnlineSource
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.domain.sources.VelkyZpevnikDataSource
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.base.util.joinLines
import cz.lastaapps.base.util.runCatchingKtor
import cz.lastaapps.base.util.trimLines
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.lighthousegames.logging.logging

class VelkyZpevnikDataSourceImpl(
    private val client: HttpClient
) : VelkyZpevnikDataSource {

    companion object {
        private val log = logging()
        internal fun velkyZpevnikLink(id: String) = "https://www.velkyzpevnik.cz${id.encodeURLPath()}"
    }

    private val regexOption = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    private val sectionNameMatcher =
        """<section[^<>]*id="pisne"[^<>]*>((?>(?!</section>).)*)</section>""".toRegex(regexOption)
    private val sectionNameSongMatcher =
        """class="title"[^<>]*href="([^"]*)"[^<>]*>([^<]*)</a>[^<]*<a[^<>]*class="interpret"[^<>]*href="([^"]*)"[^<>]*>([^<]*)</a>"""
            .toRegex(regexOption)

    override suspend fun searchByName(query: String): Result<ImmutableList<SearchedSong>> {
        val html = loadPageForQuery(query).getIfSuccess { return it }
        val main = sectionNameMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return persistentListOf<SearchedSong>().toResult()

        @Suppress("UNUSED_VARIABLE")
        return sectionNameSongMatcher.findAll(main).map { match ->
            val (songLink, songName, authorLink, authorName) = match.destructured
            SearchedSong(songLink, songName, authorName.trimAuthorName(), SongType.UNKNOWN)
        }.toImmutableList().toResult()
    }

    private val sectionTextMatcher =
        """<section[^<>]*id="pisne-dle-textu"[^<>]*>((?>(?!</section>).)*)</section>""".toRegex(regexOption)
    private val sectionTextSongMatcher =
        """class="title"[^<>]*href="([^"]*)"[^<>]*>([^<]*)</a>[^<]*<a[^<>]*class="interpret"[^<>]*href="([^"]*)"[^<>]*>([^<]*)</a>"""
            .toRegex(regexOption)

    override suspend fun searchByText(query: String): Result<ImmutableList<SearchedSong>> {
        val html = loadPageForQuery(query).getIfSuccess { return it }
        val main = sectionTextMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return persistentListOf<SearchedSong>().toResult()

        @Suppress("UNUSED_VARIABLE")
        return sectionTextSongMatcher.findAll(main).map { match ->
            val (songLink, songName, authorLink, authorName) = match.destructured
            SearchedSong(songLink, songName, authorName.trimAuthorName(), SongType.UNKNOWN)
        }.toImmutableList().toResult()
    }

    private val sectionAuthorMatcher =
        """<section[^<>]*id="interpreti"[^<>]*>((?>(?!</section>).)*)</section>""".toRegex(regexOption)
    private val sectionAuthorInfoMatcher =
        """<a[^<>]*href="([^"]*)"[^<>]*>(?>(?!<p).)*<p class="title">([^<]*)</p>""".toRegex(regexOption)

    override suspend fun searchAuthors(query: String): Result<ImmutableList<Author>> {
        val html = loadPageForQuery(query).getIfSuccess { return it }

        val main = sectionAuthorMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return persistentListOf<Author>().toResult()

        return sectionAuthorInfoMatcher.findAll(main).map { match ->
            val (link, name) = match.destructured
            Author(link, name.trimAuthorName(), null, velkyZpevnikLink(link))
        }.toImmutableList().toResult()
    }

    private suspend fun loadPageForQuery(query: String): Result<String> = runCatchingKtor {
        client.get("https://www.velkyzpevnik.cz/vyhledavani/${query.encodeURLPath()}")
            .also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

    private val artistSongsSectionMatcher =
        """<div[^<>]*class="songs"[^<>]*>((?>(?!</div>).)*)</div>""".toRegex(regexOption)
    private val artistsSongsListMatcher =
        """<a[^<>]*href="([^"]+)"[*<>]*>[^<>]*<p[^<>]*class="song-title"[^<>]*>([^<>]*)</p>""".toRegex(regexOption)

    override suspend fun loadSongsForAuthor(author: Author): Result<ImmutableList<SearchedSong>> {
        val html = loadSongsForAuthorRequest(author.link).getIfSuccess { return it }

        val main = artistSongsSectionMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return persistentListOf<SearchedSong>().toResult()

        return artistsSongsListMatcher.findAll(main).map { match ->
            val (link, name) = match.destructured
            SearchedSong(link, name, author.name, SongType.UNKNOWN)
        }.toImmutableList().toResult()
    }

    private suspend fun loadSongsForAuthorRequest(link: String) = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

    private val combined = cz.lastaapps.base.data.AuthorSearchCombine(this)
    override suspend fun searchSongsByAuthor(query: String): Result<ImmutableList<SearchedSong>> =
        combined.searchSongsByAuthor(query)

    private val songTextMatcher =
        """<div[^<>]*>[^<>]*<pre[^<>]*>(.*)</pre>[^<>]*</div>""".toRegex(regexOption)
    private val songNameMatcher =
        """<article[^<>]*class="song"[^<>]*>[^<>]*<h1[^<>]*>([^<>]*)</h1>[^<>]*<h3>[^<>]*<a[^<>]*title="Detail interpreta"[^<>]*>([^<>]*)</a>"""
            .toRegex(regexOption)

    override suspend fun loadSong(id: String): Result<Song> {
        val link = velkyZpevnikLink(id)
        val html = loadSongRequest(link).getIfSuccess { return it }

        val text = (songTextMatcher.find(html)?.groupValues?.getOrNull(1)
            ?: return SongErrors.ParseError.FailedToMatchSongText().toResult())
            .replace("""<span[^<>]*>""".toRegex(), "[")
            .replace("</span>", "]")
            .lines().trimLines().joinLines()

        val (name, author) = (songNameMatcher.find(html)?.destructured
            ?: return SongErrors.ParseError.FailedToMatchSongNameOrAuthor().toResult())

        return Song(id, name, author.trimAuthorName(), text, OnlineSource.VelkyZpevnik, link, null).toResult()
    }

    private suspend fun loadSongRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsText().toResult()
    }

    // some author names start with '- '
    private fun String.trimAuthorName() = removePrefix("- ")
}