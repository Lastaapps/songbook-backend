package cz.lastaapps.song.presentation

import cz.lastaapps.song.songModule
import org.koin.dsl.module

val songPresentationModule = module {
    includes(songModule)
}