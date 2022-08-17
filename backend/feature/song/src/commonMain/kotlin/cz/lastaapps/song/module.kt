package cz.lastaapps.song

import cz.lastaapps.base.util.LocalizedComparatorProvider
import cz.lastaapps.song.data.SongRepositoryImpl
import cz.lastaapps.song.data.SourceAggregator
import cz.lastaapps.song.data.SourceAggregatorImpl
import cz.lastaapps.song.data.agama.AgamaAuthorNameCache
import cz.lastaapps.song.data.agama.AgamaAuthorNameCacheImpl
import cz.lastaapps.song.data.agama.AgamaDataSourceImpl
import cz.lastaapps.song.data.brnkni.BrnkniDataSourceImpl
import cz.lastaapps.song.data.pisnickyakordy.PisnickyAkordyDataSourceImpl
import cz.lastaapps.song.data.supermusic.SuperMusicDataSourceImpl
import cz.lastaapps.song.data.velkyzpevnik.VelkyZpevnikDataSourceImpl
import cz.lastaapps.song.data.zpevniksakordy.ZpevnikSAkordyDataSourceImpl
import cz.lastaapps.song.domain.SongRepository
import cz.lastaapps.song.domain.sources.*
import cz.lastaapps.song.domain.usecase.LoadSongUseCase
import cz.lastaapps.song.domain.usecase.LoadSongUseCaseImpl
import cz.lastaapps.song.domain.usecase.SearchSongUseCase
import cz.lastaapps.song.domain.usecase.SearchSongUseCaseImpl
import cz.lastaapps.song.util.SearchedSongComparator
import cz.lastaapps.song.util.songBookHttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


internal expect val platformModule: Module

val songModule = module {
    @Suppress("")
    includes(platformModule)

    single { songBookHttpClient }
    factory {
        SearchedSongComparator(get<LocalizedComparatorProvider>().createDefault())
    }

    singleOf(::AgamaDataSourceImpl) { bind<AgamaDataSource>() }
    singleOf(::AgamaAuthorNameCacheImpl) { bind<AgamaAuthorNameCache>() }
    singleOf(::BrnkniDataSourceImpl) { bind<BrnkniDataSource>() }
    singleOf(::PisnickyAkordyDataSourceImpl) { bind<PisnickyAkordyDataSource>() }
    singleOf(::SuperMusicDataSourceImpl) { bind<SuperMusicDataSource>() }
    singleOf(::VelkyZpevnikDataSourceImpl) { bind<VelkyZpevnikDataSource>() }
    singleOf(::ZpevnikSAkordyDataSourceImpl) { bind<ZpevnikSAkordyDataSource>() }
    singleOf(::SourceAggregatorImpl) { bind<SourceAggregator>() }

    singleOf(::SongRepositoryImpl) { bind<SongRepository>() }

    factoryOf(::LoadSongUseCaseImpl) { bind<LoadSongUseCase>() }
    factoryOf(::SearchSongUseCaseImpl) { bind<SearchSongUseCase>() }
}
