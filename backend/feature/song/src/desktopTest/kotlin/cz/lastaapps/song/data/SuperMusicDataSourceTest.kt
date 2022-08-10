package cz.lastaapps.song.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.song.data.supermusic.SuperMusicByNameDataSourceImpl
import cz.lastaapps.song.domain.model.Author
import cz.lastaapps.song.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeBlank

internal class SuperMusicDataSourceTest : StringSpec({

    val source = SuperMusicByNameDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach {
            println(it.name + " - " + it.author + " - " + it.type)
        }
    }
    "searchByNameNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }

    "searchByText" {
        val res = source.searchByText("Hrobař").asSuccess().data
        res.shouldNotBeEmpty()
        res.forEach {
            println(it.name + " - " + it.author + " - " + it.type)
        }
    }
    "searchByTextNonExisting" {
        val res = source.searchByText("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Filip").asSuccess().data
        res.shouldNotBeEmpty()
        res.map { it.author }.toSet().shouldHaveAtLeastSize(2)
        res.forEach {
            println(it.name + " - " + it.author + " - " + it.type)
        }
    }
    "searchByAuthorNonExisting" {
        val res = source.searchAuthors("asdfmovie").asSuccess().data
        res.shouldBeEmpty()
    }
    "searchByAuthorNoSongs" {
        val author = Author("", "", null, "https://supermusic.cz/skupina.php?idskupiny=1825273")
        val res = source.loadSongsForAuthor(author).asSuccess().data
        res.shouldBeEmpty()
    }

    "loadSong" {
        // melody
        source.loadSong("49271").asSuccess().data.text.shouldNotBeBlank()
        // text
        source.loadSong("156329").asSuccess().data.text.shouldNotBeBlank()
        // text
        source.loadSong("57232").asSuccess().data.text.shouldNotBeBlank()
        // tab
        source.loadSong("344750").asSuccess().data.text.shouldNotBeBlank()
        // video link
        source.loadSong("1327001").asSuccess().data.videoLink.shouldNotBeNull()
    }

//    "just many things to load" {
//        val res = source.searchSongsByAuthor("Michal").asSuccess().data
//        res.forEach {
//            source.loadSong(it).asSuccess()
//        }
//    }
})
