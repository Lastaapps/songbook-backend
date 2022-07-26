plugins {
    id(Plugins.Kotlin.multiplatform)
//    id(Plugins.Android.library)
    id(Plugins.Kotlin.serialization)
}

group = App.GROUP
version = App.VERSION_NAME

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperties["kotest.framework.parallelism"] = 4
}

kotlin {
    sourceSets.all {
        languageSettings.apply {
            languageVersion = Config.kotlinLanguageVersion
            apiVersion = Config.kotlinLanguageVersion
        }
    }
//    android {
//        compilations.all {
//            kotlinOptions.jvmTarget = Config.jvmTarget
//        }
//    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Config.jvmTarget
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            systemProperties["kotest.framework.parallelism"] = 4
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependency.KotlinX.dateTime)
                implementation(Dependency.KotlinX.collectionImmutable)
                implementation(Dependency.kmLogging)
                implementation(Dependency.fluidLocale)

                implementation(Dependency.Ktor.core)
                implementation(Dependency.Ktor.encoding)
                implementation(Dependency.Ktor.contentNegotiation)
                implementation(Dependency.Ktor.serialization)

                implementation(Dependency.Kodein.kodein)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Tests.Kotest.assertion)
            }
        }
//        val androidMain by getting {
//            dependencies {
//                implementation(Dependency.Ktor.cio)
//            }
//        }
//        val androidTest by getting {
//            dependencies {
//            }
//        }
        val desktopMain by getting {
            dependencies {
                implementation(Dependency.Ktor.cio)
                implementation(Dependency.logback)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(project.dependencies.platform(Tests.JUnit5.bom))
                implementation(Tests.JUnit5.jupiter)

                implementation(Tests.Kotest.jUnit5runner)
                implementation(Dependency.Kotlin.Coroutines.test)
            }
        }
    }
}

//android {
//    compileSdk = App.COMPILE_SDK
//
//    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
//
//    defaultConfig {
//        minSdk = App.MIN_SDK
//        targetSdk = App.TARGET_SDK
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        isCoreLibraryDesugaringEnabled = true
//
//        sourceCompatibility = Config.javaVersion
//        targetCompatibility = Config.javaVersion
//    }
//    dependencies {
//        coreLibraryDesugaring(Dependency.kotlinxDatetime)
//    }
//}
