plugins {
    id("base-kmp-convention")
}

dependencies {
    commonMainImplementation(projects.backend.base)
    commonMainImplementation(projects.backend.feature.song)

    commonMainImplementation(libs.ktor.server.core)
    commonMainImplementation(libs.ktor.server.auth)
}
