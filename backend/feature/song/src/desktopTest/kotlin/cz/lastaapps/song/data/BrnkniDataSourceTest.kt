package cz.lastaapps.song.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.song.data.brnkni.BrnkniDataSourceImpl
import cz.lastaapps.song.domain.loadSong
import cz.lastaapps.song.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

class BrnkniDataSourceTest : StringSpec({
    val source = BrnkniDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Když").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author} - ${it.type}") }
    }
    "searchByNameNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author} - ${it.type}") }
        res.take(5).forEach { source.loadSong(it).asSuccess().data.text.shouldNotBeBlank() }
    }
    "searchByAuthorNonExisting" {
        val res = source.searchSongsByAuthor("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }

    "loadSong" {
        source.loadSong("/b/beethoven/oda-na-radost/").asSuccess().data.apply { text.shouldNotBeBlank(); println(text) }
        source.loadSong("/t/traktor/letokruhy/").asSuccess().data.apply { text.shouldNotBeBlank(); println(text) }
        source.loadSong("/m/mirai/andel/").asSuccess().data.apply { text.shouldNotBeBlank(); println(text) }
    }
})