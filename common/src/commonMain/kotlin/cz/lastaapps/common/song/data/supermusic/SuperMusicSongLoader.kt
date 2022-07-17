package cz.lastaapps.common.song.data.supermusic

import cz.lastaapps.common.base.Result
import cz.lastaapps.common.base.toResult
import cz.lastaapps.common.base.util.bodyAsSafeText
import cz.lastaapps.common.base.util.dropToMuchLines
import cz.lastaapps.common.base.util.joinLines
import cz.lastaapps.common.base.util.trimLines
import cz.lastaapps.common.song.domain.LoadSongDataSource
import cz.lastaapps.common.song.domain.SongErrors
import cz.lastaapps.common.song.domain.model.Song
import cz.lastaapps.common.song.domain.model.search.OnlineSource
import cz.lastaapps.common.song.domain.model.search.SearchedSong
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
    private val songMelodyPattern =
        """<font color=black>((?>(?!</font).)*)</font""".toRegex(regexOption)

    override suspend fun loadSong(song: SearchedSong): Result<Song> {
        val html = client.get(song.link).also { log.i { "Requesting ${it.request.url}" } }.bodyAsSafeText()

        return (null
            ?: songChordsPattern.find(html)?.groupValues?.get(0 + 1)
            ?: songTabPattern.find(html)?.groupValues?.get(0 + 1)
            ?: songMelodyPattern.find(html)?.groupValues?.get(0 + 1)
                )?.let {
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
                    Song(id, name, author, text, OnlineSource.SuperMusic, link, null)
                }
            }?.toResult() ?: SongErrors.ParseError.FailedToMatchSongText().toResult()
    }
}