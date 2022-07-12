package cz.lastaapps.common.song.data.pisnickyakordy

import cz.lastaapps.common.base.asSuccess
import cz.lastaapps.common.base.util.songBookHttpClient
import cz.lastaapps.common.song.domain.model.search.SearchedSong
import cz.lastaapps.common.song.domain.model.search.SongType
import cz.lastaapps.common.song.util.SearchedSongComparator
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

class PisnickyAkordyDataSourceImplTest : StringSpec({
    val source = PisnickyAkordyByNameDataSourceImpl(songBookHttpClient, SearchedSongComparator.default)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author} - ${it.type}")
            println(source.loadSong(it).asSuccess().data.text)
        }
    }
    "searchByNameNonExisting" {
        source.searchByName("asdfmovie").asSuccess().data.results.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author} - ${it.type}") }
        res.take(5).forEach { println(source.loadSong(it).asSuccess().data.text) }
    }
    "searchByAuthorNonExisting" {
        source.searchSongsByAuthor("asdfmovie").asSuccess().data.results.shouldBeEmpty()
    }

    "loadSongs" {
        fun link(id: String) = PisnickyAkordyByNameDataSourceImpl.linkForId(id)
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, link("/taborove-pisne/okor")))
            .asSuccess().data.text.shouldNotBeBlank()
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, link("/kabat/alkohol")))
            .asSuccess().data.text.shouldNotBeBlank()
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, link("/kabat/nic-vic")))
            .asSuccess().data.text.shouldBeEmpty()
    }

    "largeData" {
        val res = source.searchSongsByAuthor("Kabát").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach {
            println("${it.name} - ${it.author} - ${it.type}")
            println(source.loadSong(it).asSuccess().data.text)
        }
    }
})