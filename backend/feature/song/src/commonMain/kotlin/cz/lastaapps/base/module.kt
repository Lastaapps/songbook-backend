package cz.lastaapps.base

import cz.lastaapps.base.data.agama.AgamaDataSourceImpl
import cz.lastaapps.base.data.brnkni.BrnkniDataSourceImpl
import cz.lastaapps.base.data.pisnickyakordy.PisnickyAkordyByNameDataSourceImpl
import cz.lastaapps.base.data.supermusic.SuperMusicByNameDataSourceImpl
import cz.lastaapps.base.data.velkyzpevnik.VelkyZpevnikDataSourceImpl
import cz.lastaapps.base.data.zpevniksakordy.ZpevnikSAkordyByNameDataSourceImpl
import cz.lastaapps.base.util.LocalizedComparatorProvider
import cz.lastaapps.base.util.SearchedSongComparator
import cz.lastaapps.base.util.songBookHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


internal expect val platformModule: Module

val module = module {
    includes(platformModule)

    single { songBookHttpClient }
    factory {
        SearchedSongComparator(get<LocalizedComparatorProvider>().createDefault())
    }

    factoryOf(::AgamaDataSourceImpl)
    factoryOf(::BrnkniDataSourceImpl)
    factoryOf(::PisnickyAkordyByNameDataSourceImpl)
    factoryOf(::SuperMusicByNameDataSourceImpl)
    factoryOf(::VelkyZpevnikDataSourceImpl)
    factoryOf(::ZpevnikSAkordyByNameDataSourceImpl)
}
