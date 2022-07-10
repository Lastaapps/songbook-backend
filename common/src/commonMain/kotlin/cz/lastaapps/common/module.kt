package cz.lastaapps.common

import cz.lastaapps.common.base.util.LocalizedComparatorProvider
import cz.lastaapps.common.song.data.supermusic.SuperMusicDataSourceImpl
import cz.lastaapps.common.song.domain.sources.SuperMusicDataSource
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

internal expect val platformModule: DI.Module

val module = DI.Module {
    import(platformModule)

    bindProvider<SuperMusicDataSource> {
        SuperMusicDataSourceImpl(
            SuperMusicDataSourceImpl.createHttpClient(),
            instance<LocalizedComparatorProvider>().createDefault(),
        )
    }
}
