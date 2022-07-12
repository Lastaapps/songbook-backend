package cz.lastaapps.common

import cz.lastaapps.common.base.util.LocalizedComparatorProvider
import cz.lastaapps.common.base.util.songBookHttpClient
import cz.lastaapps.common.song.data.pisnickyakordy.PisnickyAkordyByNameDataSourceImpl
import cz.lastaapps.common.song.data.supermusic.SuperMusicByNameDataSourceImpl
import cz.lastaapps.common.song.data.zpevniksakordy.ZpevnikSAkordyByNameDataSourceImpl
import cz.lastaapps.common.song.domain.sources.PisnickyAkordyByNameDataSource
import cz.lastaapps.common.song.domain.sources.SuperMusicByNameDataSource
import cz.lastaapps.common.song.domain.sources.ZpevnikSAkordyByNameDataSource
import cz.lastaapps.common.song.util.SearchedSongComparator
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance
import org.kodein.di.singleton

internal expect val platformModule: DI.Module

val module = DI.Module {
    import(platformModule)

    singleton { songBookHttpClient }
    bindProvider {
        SearchedSongComparator(instance<LocalizedComparatorProvider>().createDefault())
    }

    bindProvider<PisnickyAkordyByNameDataSource> { PisnickyAkordyByNameDataSourceImpl(instance(), instance()) }
    bindProvider<SuperMusicByNameDataSource> { SuperMusicByNameDataSourceImpl(instance(), instance()) }
    bindProvider<ZpevnikSAkordyByNameDataSource> { ZpevnikSAkordyByNameDataSourceImpl(instance(), instance()) }
}
