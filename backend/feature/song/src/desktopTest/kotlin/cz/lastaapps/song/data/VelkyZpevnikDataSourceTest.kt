package cz.lastaapps.song.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.song.data.velkyzpevnik.VelkyZpevnikDataSourceImpl
import cz.lastaapps.song.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

class VelkyZpevnikDataSourceTest : StringSpec({

    val source = VelkyZpevnikDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }
    "searchByNameNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }

    "searchByText" {
        val res = source.searchByText("Hrobař").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }
    "searchByTextNonExisting" {
        val res = source.searchByText("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchByName("Kabát").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }
    "searchByAuthorNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }

    "loadSong" {
        source.loadSong("/premier/hrobar").asSuccess().data.text.apply { shouldNotBeBlank(); println(this) }
        source.loadSong("/kabat/pohoda").asSuccess().data.text.apply { shouldNotBeBlank(); println(this) }
        source.loadSong("/kabat/kabat").asSuccess().data.text.apply { shouldNotBeBlank(); println(this) }
    }
})