package cz.lastaapps.extensions

import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

fun PluginManager.alias(plugin: Provider<PluginDependency>) {
    apply(plugin.get().pluginId)
}

val Project.multiplatform: KotlinMultiplatformExtension
    get() = kotlinExtension as KotlinMultiplatformExtension

fun Project.multiplatform(block: KotlinMultiplatformExtension.() -> Unit) {
    multiplatform.apply(block)
}

//fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
//    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
//}
