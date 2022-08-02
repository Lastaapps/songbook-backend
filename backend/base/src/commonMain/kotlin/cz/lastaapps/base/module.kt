package cz.lastaapps.base

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformModule: Module

val module: Module = module {
    includes(platformModule)
}
