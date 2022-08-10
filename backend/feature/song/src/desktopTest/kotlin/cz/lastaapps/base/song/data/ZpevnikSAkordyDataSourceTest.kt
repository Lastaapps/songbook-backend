package cz.lastaapps.base.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.base.data.zpevniksakordy.ZpevnikSAkordyByNameDataSourceImpl
import cz.lastaapps.base.domain.loadSong
import cz.lastaapps.base.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeBlank

class ZpevnikSAkordyDataSourceTest : StringSpec({
    val source = ZpevnikSAkordyByNameDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author}")
        }
    }
    "searchByNameNotExisting" {
        source.searchByName("asdfmovie").asSuccess().data.shouldBeEmpty()
    }

//    "searchByText" {
//        val res = source.searchByText("V mládí jsem se učil").asSuccess().data
//        res.shouldNotBeEmpty()
//        res.forEach {
//            println("${it.name} - ${it.author}")
//        }
//    }
//    "searchByTextNotExisting" {
//        source.searchByText("asdfmovie").asSuccess().data.shouldBeEmpty()
//    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author}")
        }
        res.take(5).forEach {
            println(source.loadSong(it).asSuccess().data.text)
        }
    }
    "searchByAuthorNotExisting" {
        source.searchSongsByAuthor("asdfmovie").asSuccess().data.shouldBeEmpty()
    }

    "loadSongs" {
        source.loadSong("3151").asSuccess().data.text.shouldNotBeBlank()
        source.loadSong("132586").asSuccess().data.text.shouldNotBeBlank()
    }

    "video" {
        source.loadSong("11757").asSuccess().data.videoLink.shouldNotBeNull()
        source.loadSong("132586").asSuccess().data.videoLink.shouldBeNull()
    }

    "emptyAuthorName" {
        source.loadSong("132586").asSuccess().data.author.shouldBeNull()
    }
})