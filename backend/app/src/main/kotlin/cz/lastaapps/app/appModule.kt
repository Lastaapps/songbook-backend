package cz.lastaapps.app

import cz.lastaapps.app.config.routes.routesModule
import cz.lastaapps.song.presentation.songPresentationModule
import org.koin.dsl.module

val appModule = module {
    // load routes
    includes(routesModule)

    includes(songPresentationModule)
}