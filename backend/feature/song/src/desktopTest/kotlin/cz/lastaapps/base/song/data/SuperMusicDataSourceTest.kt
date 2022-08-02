package cz.lastaapps.base.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.base.data.supermusic.SuperMusicByNameDataSourceImpl
import cz.lastaapps.base.domain.model.Author
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeBlank

internal class SuperMusicDataSourceTest : StringSpec({

    val source = SuperMusicByNameDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach {
            println(it.name + " - " + it.author + " - " + it.type)
        }
    }
    "searchByNameNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data.results
        res.shouldBeEmpty()
    }

    "searchByText" {
        val res = source.searchByText("Hrobař").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach {
            println(it.name + " - " + it.author + " - " + it.type)
        }
    }
    "searchByTextNonExisting" {
        val res = source.searchByText("asdfmovie").asSuccess().data.results
        res.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchSongsByAuthor("Filip").asSuccess().data.results
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
        val res = source.loadSongsForAuthor(author).asSuccess().data.results
        res.shouldBeEmpty()
    }

    "loadSong" {
        // melody
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, "https://supermusic.cz/skupina.php?idpiesne=49271"))
            .asSuccess().data.text.shouldNotBeBlank()
        // text
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, "https://supermusic.cz/skupina.php?idpiesne=156329"))
            .asSuccess().data.text.shouldNotBeBlank()
        // text
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, "https://supermusic.cz/skupina.php?idpiesne=57232"))
            .asSuccess().data.text.shouldNotBeBlank()
        // tab
        source.loadSong(SearchedSong("", "", "", SongType.UNKNOWN, "https://supermusic.cz/skupina.php?idpiesne=344750"))
            .asSuccess().data.text.shouldNotBeBlank()
        // video link
        source.loadSong(
            SearchedSong(
                "",
                "",
                "",
                SongType.UNKNOWN,
                "https://supermusic.cz/skupina.php?idpiesne=1327001"
            )
        )
            .asSuccess().data.videoLink.shouldNotBeNull()
    }

//    "just many things to load" {
//        val res = source.searchSongsByAuthor("Michal").asSuccess().data.results
//        res.forEach {
//            source.loadSong(it).asSuccess()
//        }
//    }
})
