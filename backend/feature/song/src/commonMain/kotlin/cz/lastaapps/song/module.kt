package cz.lastaapps.song

import cz.lastaapps.base.util.LocalizedComparatorProvider
import cz.lastaapps.song.data.agama.AgamaAuthorNameCacheImpl
import cz.lastaapps.song.data.agama.AgamaDataSourceImpl
import cz.lastaapps.song.data.brnkni.BrnkniDataSourceImpl
import cz.lastaapps.song.data.pisnickyakordy.PisnickyAkordyByNameDataSourceImpl
import cz.lastaapps.song.data.supermusic.SuperMusicByNameDataSourceImpl
import cz.lastaapps.song.data.velkyzpevnik.VelkyZpevnikDataSourceImpl
import cz.lastaapps.song.data.zpevniksakordy.ZpevnikSAkordyByNameDataSourceImpl
import cz.lastaapps.song.util.SearchedSongComparator
import cz.lastaapps.song.util.songBookHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


internal expect val platformModule: Module

val songModule = module {
    includes(platformModule)

    single { songBookHttpClient }
    factory {
        SearchedSongComparator(get<LocalizedComparatorProvider>().createDefault())
    }

    singleOf(::AgamaDataSourceImpl)
    singleOf(::AgamaAuthorNameCacheImpl)
    singleOf(::BrnkniDataSourceImpl)
    singleOf(::PisnickyAkordyByNameDataSourceImpl)
    singleOf(::SuperMusicByNameDataSourceImpl)
    singleOf(::VelkyZpevnikDataSourceImpl)
    singleOf(::ZpevnikSAkordyByNameDataSourceImpl)
}
