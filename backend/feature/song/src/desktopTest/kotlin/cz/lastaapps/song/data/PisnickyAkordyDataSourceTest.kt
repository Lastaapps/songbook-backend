package cz.lastaapps.song.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.song.data.pisnickyakordy.PisnickyAkordyDataSourceImpl
import cz.lastaapps.song.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

class PisnickyAkordyDataSourceTest : StringSpec({
    val source = PisnickyAkordyDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author} - ${it.type}")
        }
    }
    "searchByNameNonExisting" {
        source.searchByName("asdfmovie").asSuccess().data.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author} - ${it.type}") }
    }
    "searchByAuthorNonExisting" {
        source.searchSongsByAuthor("asdfmovie").asSuccess().data.shouldBeEmpty()
    }

    "loadSongs" {
        source.loadSong("/taborove-pisne/okor").asSuccess().data.text.shouldNotBeBlank()
        source.loadSong("/kabat/alkohol").asSuccess().data.text.shouldNotBeBlank()
        source.loadSong("/kabat/nic-vic").asSuccess().data.text.shouldBeEmpty()
    }

//    "largeData" {
//        val res = source.searchSongsByAuthor("Kabát").asSuccess().data
//        res.shouldNotBeEmpty()
//        res.forEach {
//            println("${it.name} - ${it.author} - ${it.type}")
//            println(source.loadSong(it).asSuccess().data.text)
//        }
//    }
})