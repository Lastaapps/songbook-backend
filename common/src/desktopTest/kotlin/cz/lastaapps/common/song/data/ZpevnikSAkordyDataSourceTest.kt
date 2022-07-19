package cz.lastaapps.common.song.data

import cz.lastaapps.common.base.asSuccess
import cz.lastaapps.common.base.util.songBookHttpClient
import cz.lastaapps.common.song.data.zpevniksakordy.ZpevnikSAkordyByNameDataSourceImpl
import cz.lastaapps.common.song.domain.model.SongType
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

class ZpevnikSAkordyDataSourceTest : StringSpec({
    val source = ZpevnikSAkordyByNameDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author}")
            println(source.loadSong(it).asSuccess().data.text)
        }
    }
    "searchByNameNotExisting" {
        source.searchByName("asdfmovie").asSuccess().data.results.shouldBeEmpty()
    }

    "searchByText" {
        val res = source.searchByText("V mládí jsem se učil").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author}")
        }
        res.take(5).forEach {
            println(source.loadSong(it).asSuccess().data.text)
        }
    }
    "searchByTextNotExisting" {
        source.searchByText("asdfmovie").asSuccess().data.results.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author}")
        }
        res.take(5).forEach {
            println(source.loadSong(it).asSuccess().data.text)
        }
    }
    "searchByAuthorNotExisting" {
        source.searchSongsByAuthor("asdfmovie").asSuccess().data.results.shouldBeEmpty()
    }

    "loadSongs" {
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, "http://zpevnik.wz.cz/index.php?id=3151"))
            .asSuccess().data.text.shouldNotBeBlank()
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, "http://zpevnik.wz.cz/index.php?id=132586"))
            .asSuccess().data.text.shouldNotBeBlank()
    }
})