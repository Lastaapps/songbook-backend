
pluginManagement {
    includeBuild("gradle/plugins")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Songbook"

include(
    ":backend:app",
    ":backend:base",
    ":backend:feature:song",
    ":backend:feature:song:presentation",
)

