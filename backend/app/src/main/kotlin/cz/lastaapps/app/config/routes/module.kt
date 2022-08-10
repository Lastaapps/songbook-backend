package cz.lastaapps.app.config.routes

import cz.lastaapps.song.presentation.SearchRoutes
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val routesModule = module(createdAtStart = true) {
    singleOf(::SearchRoutes)
}