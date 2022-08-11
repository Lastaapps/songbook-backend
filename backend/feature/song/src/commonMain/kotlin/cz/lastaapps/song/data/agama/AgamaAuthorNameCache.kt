package cz.lastaapps.song.data.agama

import cz.lastaapps.base.Result
import cz.lastaapps.base.casted
import cz.lastaapps.base.error.SongErrors
import cz.lastaapps.base.toResult
import cz.lastaapps.song.util.runCatchingKtor
import cz.lastaapps.song.util.runCatchingParse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.lighthousegames.logging.logging
import kotlin.time.Duration.Companion.days

interface AgamaAuthorNameCache {
    suspend fun getAuthorNameForId(id: String): Result<String>
}

internal class AgamaAuthorNameCacheImpl(
    private val client: HttpClient,
) : AgamaAuthorNameCache {
    private val lock = Mutex()
    private var map: Map<String, String> = persistentMapOf()
    private var nextReload: Instant = Instant.DISTANT_PAST

    companion object {
        private val log = logging()
    }

    private val itemMatcher = """"id":"([^"]+)"[^}]+"name":"([^"]+)","used":true"""
        .toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))

    override suspend fun getAuthorNameForId(id: String): Result<String> = lock.withLock {
        val now = Clock.System.now()
        if (now > nextReload) {
            map = when (val res = load()) {
                is Result.Error -> map.ifEmpty { return@withLock res.casted() }
                is Result.Success -> res.data
            }
            nextReload = now + 1.days
        }

        map.getOrElse(id) {
            return@withLock SongErrors.InvalidAuthorId(id).toResult()
        }.toResult()
    }

    private suspend fun load(): Result<Map<String, String>> = runCatchingKtor {
        val html = client.get("http://agama2000.com").also { log.i { "Requesting ${it.request.url}" } }.bodyAsText()

        runCatchingParse {
            itemMatcher.findAll(html).map { match ->
                val (id, name) = match.destructured
                id to name
            }.toMap().toResult()
        }
    }
}
