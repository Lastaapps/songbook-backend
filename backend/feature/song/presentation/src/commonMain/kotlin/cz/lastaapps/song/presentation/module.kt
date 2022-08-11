package cz.lastaapps.song.presentation

import cz.lastaapps.song.songModule
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val songPresentationModule = module {
    includes(songModule)

    singleOf(::SearchRoutes) {
        createdAtStart()
    }
}