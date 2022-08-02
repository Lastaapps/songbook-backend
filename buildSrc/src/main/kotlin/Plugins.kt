object Plugins {

    object Android {
        const val application = "com.android.application"
        const val library = "com.android.library"
    }

    object Kotlin {
        const val android = "org.jetbrains.kotlin.android"
        const val multiplatform = "org.jetbrains.kotlin.multiplatform"
        const val jvm = "org.jetbrains.kotlin.jvm"
        const val parcelize = "org.jetbrains.kotlin.kotlin-parcelize"
        const val serialization = "org.jetbrains.kotlin.plugin.serialization"
    }

    object Java {
        const val library = "java-library"
        const val kotlin = "kotlin"
    }

    const val ksp = "com.google.devtools.ksp"
    const val kapt = "kotlin-kapt"

    const val maven = "maven-publish"

    object Shadow {
        const val version = "7.1.2"
        const val plugin = "com.github.johnrengelman.shadow"
    }

    const val sqldelight = "com.squareup.sqldelight"
    const val aboutLibraries = "com.mikepenz.aboutlibraries.plugin"
    const val daggerHilt = "dagger.hilt.android.plugin"

    const val playServices = "com.google.gms.google-services"
    const val firebaseCrashlytics = "com.google.firebase.crashlytics"
}