package cz.lastaapps.base

import cz.lastaapps.base.util.LocalizedComparatorProviderImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    factoryOf(::LocalizedComparatorProviderImpl)
}