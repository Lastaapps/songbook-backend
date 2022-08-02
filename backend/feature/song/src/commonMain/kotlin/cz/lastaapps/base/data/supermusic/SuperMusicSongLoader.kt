package cz.lastaapps.base.data.supermusic

import cz.lastaapps.base.Result
import cz.lastaapps.base.domain.LoadSongDataSource
import cz.lastaapps.base.domain.SongErrors
import cz.lastaapps.base.domain.model.Song
import cz.lastaapps.base.domain.model.search.OnlineSource
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.getIfSuccess
import cz.lastaapps.base.toResult
import cz.lastaapps.base.util.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.lighthousegames.logging.logging

internal class SuperMusicSongLoader(
    private val client: HttpClient,
) : LoadSongDataSource {

    companion object {
        private val log = logging()
    }

    private val regexOption = setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE)
    private val songChordsPattern =
        """<font color=black><script LANGUAGE="JavaScript">(?>(?!</script>).)*</script>((?>(?!<script).)*)<script"""
            .toRegex(regexOption)
    private val songTabPattern =
        """<font color=black><pre><pre>((?>(?!</pre).)*)</pre></pre></font>""".toRegex(regexOption)
    private val songMelodyAndTextPattern =
        """<font color=black>((?>(?!</font).)*)</font""".toRegex(regexOption)
    private val youtubePattern =
        """(https://www\.youtube\.com/embed/\w+)""".toRegex()

    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val html = loadSongRequest(song.link).getIfSuccess { return it }

        val youtube = youtubePattern.find(html)?.groupValues?.getOrNull(1)?.replace("embed/", "")

        return (null
            ?: songChordsPattern.find(html)?.groupValues?.get(0 + 1)
            ?: songTabPattern.find(html)?.groupValues?.get(0 + 1)
            ?: songMelodyAndTextPattern.find(html)?.groupValues?.get(0 + 1)
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
                with(song) {
                    Song(id, name, author, text, OnlineSource.SuperMusic, link, youtube)
                }
            }?.toResult() ?: SongErrors.ParseError.FailedToMatchSongText().toResult()
    }

    private suspend fun loadSongRequest(link: String): Result<String> = runCatchingKtor {
        client.get(link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText().toResult()
    }
}