package cz.lastaapps.base.data

import cz.lastaapps.base.asSuccess
import cz.lastaapps.base.data.velkyzpevnik.VelkyZpevnikDataSourceImpl
import cz.lastaapps.base.domain.model.SongType
import cz.lastaapps.base.domain.model.search.SearchedSong
import cz.lastaapps.base.util.songBookHttpClient
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

class VelkyZpevnikDataSourceTest : StringSpec({

    val source = VelkyZpevnikDataSourceImpl(songBookHttpClient)

    "searchByName" {
        val res = source.searchByName("Hrobař").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }
    "searchByNameNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data.results
        res.shouldBeEmpty()
    }

    "searchByText" {
        val res = source.searchByText("Hrobař").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }
    "searchByTextNonExisting" {
        val res = source.searchByText("asdfmovie").asSuccess().data.results
        res.shouldBeEmpty()
    }

    "searchByAuthor" {
        val res = source.searchByName("Kabát").asSuccess().data.results
        res.shouldNotBeEmpty()
        res.forEach { println("${it.name} - ${it.author}") }
    }
    "searchByAuthorNonExisting" {
        val res = source.searchByName("asdfmovie").asSuccess().data.results
        res.shouldBeEmpty()
    }

    "loadSong" {
        fun song(id: String) =
            SearchedSong("", "", "", SongType.UNKNOWN, VelkyZpevnikDataSourceImpl.velkyZpevnikLink(id))
        source.loadSong(song("/premier/hrobar")).asSuccess().data.text.apply { shouldNotBeBlank(); println(this) }
        source.loadSong(song("/kabat/pohoda")).asSuccess().data.text.apply { shouldNotBeBlank(); println(this) }
        source.loadSong(song("/kabat/kabat")).asSuccess().data.text.apply { shouldNotBeBlank(); println(this) }
    }
})