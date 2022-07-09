buildscript {
}
plugins {
    val gradleVersion = "7.4.0-alpha07"
    id(Plugins.Android.application) version (gradleVersion) apply false
    id(Plugins.Android.library) version (gradleVersion) apply false
    id(Plugins.Kotlin.android) version Dependency.Kotlin.version apply false
}