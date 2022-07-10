package cz.lastaapps.common

import cz.lastaapps.common.base.util.LocalizedComparatorProvider
import cz.lastaapps.common.base.util.LocalizedComparatorProviderImpl
import org.kodein.di.DI
import org.kodein.di.bindProvider

internal actual val platformModule: DI.Module = DI.Module {
    bindProvider<LocalizedComparatorProvider> { LocalizedComparatorProviderImpl() }
}