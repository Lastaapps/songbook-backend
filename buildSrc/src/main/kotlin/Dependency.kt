object Dependency {

    const val desugaring = "com.android.tools:desugar_jdk_libs:1.1.5"

    const val kmLogging = "org.lighthousegames:logging:1.1.1"
    const val logback = "ch.qos.logback:logback-classic:1.2.11"
    const val qrgen = "com.github.kenglxn.QRGen:android:2.6.0" // jitpack
    const val skrapeIt = "it.skrape:skrapeit:1.2.1"
    const val fluidLocale = "io.fluidsonic.locale:fluid-locale:0.11.0"

    object Kotlin {
        const val version = "1.7.0"
        const val languageVersion = "1.7"

        const val standardLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"

        object Coroutines {
            private const val coroutinesVersion = "1.6.3"
            const val common =
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
            const val android =
                "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
            const val test =
                "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion"
        }
    }

    object KotlinX {
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"
        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3"
        const val collectionImmutable = "org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5"
    }

    object Ktor {
        private const val version = "2.0.3"

        object Client {
            const val core = "io.ktor:ktor-client-core:$version"
            const val contentNegotiation = "io.ktor:ktor-client-content-negotiation:$version"
            const val serialization = "io.ktor:ktor-serialization-kotlinx-json:$version"
            const val auth = "io.ktor:ktor-client-auth:$version"
            const val encoding = "io.ktor:ktor-client-encoding:$version"
            const val logging = "io.ktor:ktor-client-logging:$version"

            const val cio = "io.ktor:ktor-client-cio:$version"
            const val okhttp = "io.ktor:ktor-client-okhttp:$version"
        }
        object Server {
            const val core = "io.ktor:ktor-server-core:$version"

            const val auth = "io.ktor:ktor-server-auth:$version"
            const val autoHeadResponse = "io.ktor:ktor-server-auto-head-response:$version"
            const val caching = "io.ktor:ktor-server-caching-headers:$version"
            const val callLogging = "io.ktor:ktor-server-call-logging:$version"
            const val compression = "io.ktor:ktor-server-compression:$version"
            const val conditionalsHeaders = "io.ktor:ktor-server-conditional-headers:$version"
            const val contentNegotiation = "io.ktor:ktor-server-content-negotiation:$version"
            const val defaultHeaders = "io.ktor:ktor-server-default-headers:$version"
            const val forwardedHeaders = "io.ktor:ktor-server-forwarded-header:$version"
            const val httpRedirect = "io.ktor:ktor-server-http-redirect:$version"
            const val jsonSerialization = "io.ktor:ktor-serialization-kotlinx-json:$version"
            const val partialContent = "io.ktor:ktor-server-partial-content:$version"
            const val sesstions = "io.ktor:ktor-server-sessions:$version"
            const val statusPages = "io.ktor:ktor-server-status-pages:$version"

            const val netty = "io.ktor:ktor-server-netty:$version"
            const val cio = "io.ktor:ktor-server-cio:$version"

            object Unofficial {
                const val apiKeyAuth = "dev.forst:ktor-api-key:1.1.0"
            }
        }
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.6.0-alpha05"
        const val annotation = "androidx.annotation:annotation:1.4.0"
        const val collection = "androidx.collection:collection:1.3.0-alpha01"
        const val core = "androidx.core:core-ktx:1.8.0"
        const val documentFile = "androidx.documentfile:documentfile:1.0.1"
        const val fragment = "androidx.fragment:fragment-ktx:1.5.0"
        const val preferences = "androidx.preference:preference-ktx:1.2.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"
        const val splashscreen = "androidx.core:core-splashscreen:1.0.0-rc01"
        const val startup = "androidx.startup:startup-runtime:1.1.1"
        const val swipeRefreshLayout =
            "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val tracing = "androidx.tracing:tracing-ktx:1.1.0"
        const val vectorDrawables = "androidx.vectordrawable:vectordrawable:1.1.0"
        const val windowManager = "androidx.window:window:1.0.0"
        const val work = "androidx.work:work-runtime-ktx:2.7.1"

        object Activity {
            private const val version = "1.5.0"
            const val core = "androidx.activity:activity-ktx:$version"
            const val compose = "androidx.activity:activity-compose:$version"
        }

        object AppSearch {
            private const val version = "1.1.0-alpha01"
            const val core = "androidx.appsearch:appsearch:$version"
            const val compiler = "androidx.appsearch:appsearch-compiler:$version"
            const val local = "androidx.appsearch:appsearch-local-storage:$version"
            const val platform = "androidx.appsearch:appsearch-platform-storage:$version"
        }

        object Camera {
            private const val version = "1.1.0"
            const val core = "androidx.camera:camera-core:$version"
            const val camera2 = "androidx.camera:camera-camera2:$version"
            const val extensions = "androidx.camera:camera-extensions:$version"
            const val lifecycle = "androidx.camera:camera-lifecycle:$version"
            const val video = "androidx.camera:camera-video:$version"
            const val view = "androidx.camera:camera-view:$version"
            const val vision = "androidx.camera:camera-mlkit-vision:$version"
        }

        object ConstrainLayout {
            const val views = "androidx.constraintlayout:constraintlayout:2.1.4"
            const val compose = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        }

        object Dagger {
            private const val version = "2.42"
            const val hilt = "com.google.dagger:hilt-android:$version"
            const val hiltCompiler = "com.google.dagger:hilt-android-compiler:$version"
        }

        object Emoji {
            private const val version = "1.2.0-alpha04"
            const val core = "androidx.emoji2:emoji2:$version"
            const val views = "androidx.emoji2:emoji2-views:$version"
            const val helper = "androidx.emoji2:emoji2-views-helper:$version"
        }

        object Lifecycle {
            private const val version = "2.5.0"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val service = "androidx.lifecycle:lifecycle-service:$version"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val viewModelSavedState =
                "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
        }

        object Hilt {
            private const val version = "1.0.0"
            const val common = "androidx.hilt:hilt-common:$version"
            const val compiler = "androidx.hilt:hilt-compiler:$version"
            const val navigation = "androidx.hilt:hilt-navigation:$version"
            const val navigationCompose = "androidx.hilt:hilt-navigation-compose:$version"
            const val work = "androidx.hilt:hilt-work:$version"
        }

        object Navigation {
            private const val version = "2.5.0"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val compose = "androidx.navigation:navigation-compose:$version"
        }

        object Room {
            private const val version = ""
            const val runtime = "androidx.room:room-runtime:$version"
            const val compiler = "androidx.room:room-compiler:$version"
            const val test = "androidx.room:room-testing:$version"
        }
    }

    object Google {
        const val material = "com.google.android.material:material:1.6.1"
        const val playServices = "com.google.android.play:core-ktx:1.8.1"
        const val playServicesLocation = "com.google.android.gms:play-services-location:20.0.0"
        const val mlkitBarcode = "com.google.mlkit:barcode-scanning:17.0.2"
    }

    object Firebase {
        private const val version = "30.2.0"
        const val bom = "com.google.firebase:firebase-bom:$version"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val config = "com.google.firebase:firebase-config-ktx"
        const val messaging = "com.google.firebase:firebase-messaging-ktx"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val database = "com.google.firebase:firebase-database-ktx"
    }

    object Compose {
        const val compilerVersion = "1.2.0"
        private const val version = "1.2.0-rc03"
        private const val material3Version = "1.0.0-alpha13"

        const val compiler = "androidx.compose.compiler:compiler:$compilerVersion"

        const val animation = "androidx.compose.animation:animation:$version"
        const val material = "androidx.compose.material:material:$version"
        const val iconsCore = "androidx.compose.material:material-icons-core:$version"
        const val iconsExtended = "androidx.compose.material:material-icons-extended:$version"
        const val foundation = "androidx.compose.foundation:foundation:$version"
        const val ui = "androidx.compose.ui:ui:$version"
        const val material3 = "androidx.compose.material3:material3:$material3Version"
        const val material3WindowSizeClass =
            "androidx.compose.material3:material3-window-size-class:$material3Version"

        const val tooling = "androidx.compose.ui:ui-tooling:$version" // debug
        const val toolingPreview = "androidx.compose.ui:ui-tooling-preview:$version"
        const val testManifest = "androidx.compose.ui:ui-test-manifest:$version" // debug

        const val testJUnit4 = "androidx.compose.ui:ui-test-junit4:$version" // test
    }

    object Accompanist {
        private const val version = "0.24.13-rc"
        const val systemUi = "com.google.accompanist:accompanist-systemuicontroller:$version"
        const val pager = "com.google.accompanist:accompanist-pager:$version"
        const val permission = "com.google.accompanist:accompanist-permissions:$version"
        const val placeholder = "com.google.accompanist:accompanist-placeholder:$version"
        const val flowLayouts = "com.google.accompanist:accompanist-flowlayout:$version"
        const val navigationAnimation =
            "com.google.accompanist:accompanist-navigation-animation:$version"
        const val navigationMaterial =
            "com.google.accompanist:accompanist-navigation-material:$version"
        const val drawablePainters = "com.google.accompanist:accompanist-drawablepainter:$version"
        const val swipeToRefresh = "com.google.accompanist:accompanist-swiperefresh:$version"
    }

    object SqlDelight {
        private const val version = "1.5.3"
        const val android = "com.squareup.sqldelight:android-driver:$version"
        const val jvm = "com.squareup.sqldelight:sqlite-driver:$version"
        const val coroutines = "com.squareup.sqldelight:coroutines-extensions:$version"
        const val runtime = "com.squareup.sqldelight:runtime:$version"
        const val plugin = "com.squareup.sqldelight:gradle-plugin:$version"
    }

    object Coil {
        private const val version = "2.1.0"
        const val core = "io.coil-kt:coil:$version"
        const val base = "io.coil-kt:coil-base:$version"
        const val composeComplete = "io.coil-kt:coil-compose:$version"
        const val composeBase = "io.coil-kt:coil-compose-base:$version"
        const val gif = "io.coil-kt:coil-gif:$version"
        const val svg = "io.coil-kt:coil-svg:$version"
        const val video = "io.coil-kt:coil-video:$version"
    }

    object Kodein {
        private const val version = "7.12.0"
        const val kodein = "org.kodein.di:kodein-di:$version"
        const val androidCore = "org.kodein.di:kodein-di-framework-android-core:$version"
        const val androidx = "org.kodein.di:kodein-di-framework-android-x:$version"
        const val androidxViewmode =
            "org.kodein.di:kodein-di-framework-android-x-viewmodel:$version"
        const val androidxViewmodeSavedstate =
            "org.kodein.di:kodein-di-framework-android-x-viewmodel-savedstate:$version"
        const val compose = "org.kodein.di:kodein-di-framework-compose:$version"
    }

    object Koin {
        private const val version = "3.2.0"

        const val core = "io.insert-koin:koin-core:$version"

        const val annotations = "io.insert-koin:koin-annotations:$version"
        const val compiler = "io.insert-koin:koin-ksp-compiler:$version"

        const val android = "io.insert-koin:koin-android:$version"
        const val ktorServer = "io.insert-koin:koin-ktor:$version"

        const val test = "io.insert-koin:koin-test:$version"
        const val testJUnit = "io.insert-koin:koin-test-junit4:$version"
    }

    object AboutLibraries {
        private const val version = "10.3.1"
        const val core = "com.mikepenz:aboutlibraries-core:$version"
        const val compose = "com.mikepenz:aboutlibraries-compose:$version"
    }
}