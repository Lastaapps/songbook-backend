plugins {
    id("base-kmp-convention")
}

dependencies {
    commonMainImplementation(projects.backend.base)

    commonMainImplementation(libs.ktor.client.core)
    commonMainImplementation(libs.ktor.client.encoding)
    commonMainImplementation(libs.ktor.client.contentNegotiation)
    commonMainImplementation(libs.ktor.client.serialization)

    desktopMainImplementation(libs.ktor.client.cio)
    desktopMainImplementation(libs.ktor.server.cio)
}
