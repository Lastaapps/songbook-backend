package cz.lastaapps.base.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.base.data.agama.AgamaDataSourceImpl
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.string.shouldNotBeBlank

class AgamaDataSourceTest : StringSpec({
    val source = AgamaDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }

    "searchByText" {
        val res = source.searchByText("V mládí jsem se").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }

    "getAuthors" {
        val res = source.searchAuthors("Petr").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println(it.name) }
    }

    "loadSong" {
        source.loadSong(SearchedSong("r6xrzzstjgp3resxjxbcefjklmp", "", "", SongType.UNKNOWN, ""))
            .asSuccess().data.also {
                it.text.shouldNotBeBlank()
                it.videoLink.shouldNotBeBlank()
                println(it.name)
                println(it.text)
                println(it.videoLink)
            }
        source.loadSong(SearchedSong("nrpowrprspk5jcjuyxabghilnq", "", "", SongType.UNKNOWN, ""))
            .asSuccess().data.also {
                it.text.shouldNotBeBlank()
                it.videoLink.shouldBeNull()
                println(it.name)
                println(it.text)
                println(it.videoLink)
            }
    }
})