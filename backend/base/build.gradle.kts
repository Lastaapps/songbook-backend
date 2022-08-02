plugins {
    id(Plugins.Kotlin.multiplatform)
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
                api(Dependency.KotlinX.dateTime)
                api(Dependency.KotlinX.collectionImmutable)
                api(Dependency.kmLogging)
                api(Dependency.fluidLocale)

                implementation(Dependency.Koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(Tests.Kotest.assertion)
            }
        }
        val desktopMain by getting {
            dependencies {
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
