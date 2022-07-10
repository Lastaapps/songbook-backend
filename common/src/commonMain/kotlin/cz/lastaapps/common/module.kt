package cz.lastaapps.common

import cz.lastaapps.common.base.util.LocalizedComparatorProvider
import cz.lastaapps.common.base.util.songBookHttpClient
import cz.lastaapps.common.song.data.supermusic.SuperMusicDataSourceImpl
import cz.lastaapps.common.song.data.zpevniksakordy.ZpevnikSAkordyDataSourceImpl
import cz.lastaapps.common.song.domain.sources.SuperMusicDataSource
import cz.lastaapps.common.song.domain.sources.ZpevnikSAkordyDataSource
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
    bindProvider<SuperMusicDataSource> {
        SuperMusicDataSourceImpl(instance(), instance())
    }
    bindProvider<ZpevnikSAkordyDataSource> {
        ZpevnikSAkordyDataSourceImpl(instance(), instance())
    }
}
