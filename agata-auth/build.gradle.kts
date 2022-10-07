plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlinx.dateTime)
    implementation(libs.kotlinx.collection)
    implementation(libs.kmLogging)
    implementation(libs.fluidLocale)
    implementation(libs.logback)

    implementation(projects.backend.base)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.client.contentNegotiation)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}