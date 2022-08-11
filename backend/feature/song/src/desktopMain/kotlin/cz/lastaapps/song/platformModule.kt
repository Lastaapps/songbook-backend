package cz.lastaapps.song

import cz.lastaapps.base.util.LocalizedComparatorProvider
import cz.lastaapps.base.util.LocalizedComparatorProviderImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    factoryOf(::LocalizedComparatorProviderImpl) { bind<LocalizedComparatorProvider>() }
}