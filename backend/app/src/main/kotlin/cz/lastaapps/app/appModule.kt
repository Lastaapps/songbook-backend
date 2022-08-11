package cz.lastaapps.app

import cz.lastaapps.song.presentation.songPresentationModule
import org.koin.dsl.module

val appModule = module {

    includes(songPresentationModule)
}