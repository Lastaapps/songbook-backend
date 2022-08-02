plugins {
    application
    id(Plugins.Kotlin.jvm)
    id(Plugins.Java.kotlin)
    id(Plugins.Shadow.plugin) version Plugins.Shadow.version
}

application {
//    mainClass.set("io.ktor.server.netty.EngineMain")
    mainClass.set("io.ktor.server.cio.EngineMain")
}

dependencies {
    implementation(project(Modules.Backend.Feature.song))
    implementation(project(Modules.Backend.base))

    implementation(Dependency.Koin.core)
    implementation(Dependency.Koin.ktorServer)

    implementation(Dependency.Ktor.Server.core)
    implementation(Dependency.Ktor.Server.cio)
    implementation(Dependency.Ktor.Server.auth)
    implementation(Dependency.Ktor.Server.autoHeadResponse)
    implementation(Dependency.Ktor.Server.callLogging)
    implementation(Dependency.Ktor.Server.compression)
    implementation(Dependency.Ktor.Server.contentNegotiation)
    implementation(Dependency.Ktor.Server.defaultHeaders)
    implementation(Dependency.Ktor.Server.forwardedHeaders)
    implementation(Dependency.Ktor.Server.httpRedirect)
    implementation(Dependency.Ktor.Server.jsonSerialization)
    implementation(Dependency.Ktor.Server.statusPages)
    implementation(Dependency.Ktor.Server.Unofficial.apiKeyAuth)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}