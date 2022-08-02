package cz.lastaapps.base.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.base.data.brnkni.BrnkniDataSourceImpl
import cz.lastaapps.base.data.brnkni.BrnkniDataSourceImpl.Companion.brnkniLink
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

class BrnkniDataSourceTest : StringSpec({
    val source = BrnkniDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Když").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author} - ${it.type}") }
    }
    "searchByNameNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data.results
        res.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author} - ${it.type}") }
        res.take(5).forEach { source.loadSong(it).asSuccess().data.text.shouldNotBeBlank() }
    }
    "searchByAuthorNonExisting" {
        val res = source.searchSongsByAuthor("asdfmovie").asSuccess().data.results
        res.shouldBeEmpty()
    }

    "loadSong" {
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, brnkniLink("/b/beethoven/oda-na-radost/")))
            .asSuccess().data.apply { text.shouldNotBeBlank(); println(text) }
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, brnkniLink("/t/traktor/letokruhy/")))
            .asSuccess().data.apply { text.shouldNotBeBlank(); println(text) }
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, brnkniLink("/m/mirai/andel/")))
            .asSuccess().data.apply { text.shouldNotBeBlank(); println(text) }
    }
})