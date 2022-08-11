package cz.lastaapps.song.data.supermusic

import cz.lastaapps.base.Result
import cz.lastaapps.base.error.SongErrors
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.song.domain.LoadSongDataSource
import cz.lastaapps.song.domain.model.Song
import cz.lastaapps.song.domain.model.search.OnlineSource
import cz.lastaapps.song.util.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.lighthousegames.logging.logging

internal class SuperMusicSongLoader(
    private val client: HttpClient,
) : LoadSongDataSource {

    companion object {
        private val log = logging()
        private fun supermusicLink(id: String) = "https://supermusic.cz/skupina.php?idpiesne=$id"
    }

    private val regexOption = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
    private val songChordsMatcher =
        """<font color=black><script LANGUAGE="JavaScript">(?>(?!</script>).)*</script>((?>(?!<script).)*)<script"""
            .toRegex(regexOption)
    private val songTabMatcher =
        """<font color=black><pre><pre>((?>(?!</pre).)*)</pre></pre></font>""".toRegex(regexOption)
    private val songMelodyAndTextMatcher =
        """<font color=black>((?>(?!</font).)*)</font""".toRegex(regexOption)
    private val youtubeMatcher =
        """(https://www\.youtube\.com/embed/\w+)""".toRegex(regexOption)
    private val songNameMatcher =
        """<font class="test3">([^-]*)-([^<]*)</font>""".toRegex(regexOption)

    override suspend fun loadSong(id: String): Result<Song> {
        val link = supermusicLink(id)
        val html = loadSongRequest(link).getIfSuccess { return it }

        return runCatchingParse {
            val youtube = youtubeMatcher.find(html)?.groupValues?.getOrNull(1)?.replace("embed/", "")
            val (author, name) = songNameMatcher.find(html)?.destructured
                ?: return SongErrors.ParseError.FailedToMatchSongNameOrAuthor().toResult()

            (null
                ?: songChordsMatcher.find(html)?.groupValues?.get(1)
                ?: songTabMatcher.find(html)?.groupValues?.get(1)
                ?: songMelodyAndTextMatcher.find(html)?.groupValues?.get(1)
                    )
                ?.let {
                    val text = it
                        .replace("<sup>", "")
                        .replace("</sup>", "")
                        .replace("<pre>", "")
                        .replace("</pre>", "")
                        .replace("""<a[^<>]*>""".toRegex(), "[")
                        .replace("""</a>""".toRegex(), "]")
                        .replace("""<[^b]*br[^<>]*>""".toRegex(), "\n")
                        .lines()
                        .trimLines()
                        .dropToMuchLines()
                        .joinLines()

                    Song(id, name.trim(), author.trim(), text, OnlineSource.SuperMusic, link, youtube)
                }?.toResult() ?: SongErrors.ParseError.FailedToMatchSongText().toResult()
        }
    }

    private suspend fun loadSongRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText().toResult()
    }
}