package cz.lastaapps.base.song.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.base.data.agama.AgamaAuthorNameCacheImpl
import cz.lastaapps.base.data.agama.AgamaDataSourceImpl
import cz.lastaapps.base.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank

class AgamaDataSourceTest : StringSpec({
    val client = songBookHttpClient
    val source = AgamaDataSourceImpl(client, AgamaAuthorNameCacheImpl(client))

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }

    "searchByText" {
        val res = source.searchByText("V mládí jsem se").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }

    "getAuthors" {
        val res = source.searchAuthors("Petr").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println(it.name) }
    }

    "loadSong" {
        source.loadSong("r6xrzzstjgp3resxjxbcefjklmp")
            .asSuccess().data.also {
                it.text.shouldNotBeBlank()
                it.videoLink.shouldNotBeBlank()
                println(it.name)
                println(it.text)
                println(it.videoLink)
            }
        source.loadSong("nrpowrprspk5jcjuyxabghilnq")
            .asSuccess().data.also {
                it.text.shouldNotBeBlank()
                it.videoLink.shouldBeNull()
                println(it.name)
                println(it.text)
                println(it.videoLink)
            }
    }

    "linkTest" {
        source.loadSong("rgzr3xrgkk3qqd52kxbcdegkmopq")
            .asSuccess().data.also {
                it.originLink shouldBe "http://www.agama2000.com/nhoxqx9phiismxitbxadeghilnp/Ale%C5%A1%20Ulm/rgzr3xrgkk3qqd52kxbcdegkmopq/Pozdravuj"
            }
    }
})